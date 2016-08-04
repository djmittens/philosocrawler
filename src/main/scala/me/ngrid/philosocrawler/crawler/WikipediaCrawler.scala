package me.ngrid.philosocrawler.crawler

import com.typesafe.scalalogging.LazyLogging
import org.jsoup.nodes.Element
import org.jsoup.{HttpStatusException, Jsoup}

import scala.collection.JavaConverters._
import scala.util.{Failure, Try}

/**
  * Crawls the wikipedia pages by following the first link until it reaches a page with the target Id.
  */
trait WikipediaCrawler {
  /**
    *
    * @param pageId id of the page to start crawling on.
    * @param target stop the traversal when a page with this id has been reached.
    * @param cache some results may have been calculated before, this function may provide an answer in case that occurs
    * @return path from target to page id, beginning at target.
    */
  def crawl(pageId: String, target: String, cache: (String) => Option[List[WikipediaPage]]): Option[List[WikipediaPage]]
}

object WikipediaPage {
  def apply(getUrl: (String) => String)(e: Element): WikipediaPage = {
    val pageId = e.attr("href").replaceFirst("/wiki/", "")
    WikipediaPage(pageId, e.attr("title"), getUrl(pageId))
  }
}

case class WikipediaPage(id: String, title: String, fullUrl: String)

class EnglishWikipediaCrawler(wikiUrl: String = "http://en.wikipedia.org/wiki/",
                              maxDepth: Int = 100) extends WikipediaCrawler with LazyLogging {

  override def crawl(pageId: String,
                     target: String,
                     cache: (String) => Option[List[WikipediaPage]]): Option[List[WikipediaPage]] = {
    for {
      page <- findPage(pageId)
      path <- crawl(page, target, cache)
    } yield path
  }

  @scala.annotation.tailrec
  final def crawl(page: WikipediaPage,
                  stopOnPage: String,
                  cache: (String) => Option[List[WikipediaPage]],
                  visited: List[WikipediaPage] = Nil,
                  depth: Int = 0): Option[List[WikipediaPage]] = {
    if (page.id == stopOnPage) return Some(page +: visited)

    if (depth > maxDepth) return None

    val c = cache(page.id).map(stored => stored ++ visited  )
    if (c.nonEmpty) return c

    val p = findNextPage(page, validateLink)
    if (p.isEmpty) {
      logger.error(s"Could not find the next link for this page $page")
      return None
    }
    if(visited.contains(p.get)) {
      logger.error(s"Found a loop for page $p")
      return None
    }

    crawl(p.get, stopOnPage, cache, page +: visited, depth + 1)
  }

  def findPage(pageId: String): Option[WikipediaPage] = {
    Try(Jsoup.connect(getUrl(pageId)).get().body().getElementById("firstHeading")).
      recoverWith(logHttpError(getUrl(pageId))).
      toOption.
      map(_.text).
      map(title => WikipediaPage(pageId, title, getUrl(pageId)))
  }

  def findNextPage(page: WikipediaPage, validator: (Element) => Boolean): Option[WikipediaPage] = {
    val html = Try(Jsoup.connect(page.fullUrl).timeout(10000).get().body()).
      recoverWith(logHttpError(page.fullUrl))

    for {
      page <- html.toOption
      link <- searchNormalPage(page).orElse({searchDisambiguationPage(page)})
    } yield WikipediaPage(getUrl)(link)
  }

  def searchNormalPage(es: Element): Option[Element] = {
    def nonCapitalValidator(e: Element): Boolean = {
      if (!e.text().charAt(0).isLower) {
        logger.trace(s"Ignoring non lower case alpha characters $e")
        return false
      }
      true
    }

    val a = es.select("div#mw-content-text>p>a").select(":not(.mw-redirect)").asScala.toList

    a.find(x => validateLink(x) && nonCapitalValidator(x))
  }

  def searchDisambiguationPage(es: Element): Option[Element] = {
    val a = es.select("div#mw-content-text>p ~ ul").
      select("li>a").
      asScala.toList

    a.find(validateLink)
  }

  def getUrl(pageId: String): String = s"$wikiUrl$pageId"

  private[EnglishWikipediaCrawler] def logHttpError[E](url: String): PartialFunction[Throwable, Try[E]] = {
    case e: HttpStatusException =>
      logger.warn(s"Problem crawling url: $url : $e")
      Failure(e)
    case e =>
      logger.error(s"Unexpected error occured crawling: $url : $e")
      Failure(e)
  }

  def validateLink(link: Element): Boolean = {
    if (link.tagName() != "a") {
      logger.trace(s"Was expecting an anchor tag for link but found $link")
      return false
    }

    if (!link.attr("href").startsWith("/wiki/")) {
      logger.trace(s"Ignoring a link that doesn't start with /wiki $link")
      return false
    }

    if (!link.hasText) {
      logger.trace(s"Ignoring link that has no contents $link")
      return false
    }

    logger.trace(s"Found valid link $link")

    true
  }
}
