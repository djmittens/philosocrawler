package me.ngrid.philosocrawler.crawler

import com.typesafe.scalalogging.LazyLogging
import org.jsoup.{HttpStatusException, Jsoup}
import org.jsoup.nodes.Element

import scala.annotation.tailrec
import scala.collection.JavaConverters._
import scala.util.{Failure, Try}

trait WikipediaCrawler {
  def crawl(pageId: String, cache: (String) => Option[List[WikipediaPage]]): Option[List[WikipediaPage]]
}

object WikipediaPage {
  def apply(e: Element): WikipediaPage =
    WikipediaPage(e.attr("href").replaceFirst("/wiki/", ""), e.attr("title"), e.attr("href"))

}

case class WikipediaPage(id: String, title: String, fullUrl: String)

class EnglishWikipediaCrawler(wikiUrl: String = "http://en.wikipedia.org/wiki/",
                              maxDepth: Int = 100,
                              stopOnPage: String = "Philosophy") extends WikipediaCrawler with LazyLogging {

  override def crawl(pageId: String,
                     cache: (String) => Option[List[WikipediaPage]]): Option[List[WikipediaPage]] = {
    for {
      page <- findPage(pageId)
      path <- crawl(page, cache)
    } yield path
  }

  @tailrec
  final def crawl(page: WikipediaPage,
                  cache: (String) => Option[List[WikipediaPage]],
                  visited: List[WikipediaPage] = Nil,
                  depth: Int = 0): Option[List[WikipediaPage]] = {
    if (page.id == stopOnPage) return Some(page +: visited)

    if (depth > maxDepth) return None

    val c = cache(page.id).map(x => visited ++ (page +: x))
    if (c.nonEmpty) return c

    val p = findNextPage(page.id, validateLink)
    if (p.isEmpty || visited.contains(p.get)) {
      logger.error(s"Could not find the next link for this page $page")
      return None
    }

    crawl(p.get, cache, page +: visited, depth + 1)
  }

  def findPage(pageId: String): Option[WikipediaPage] = {
    val url = getUrl(pageId)
    Try(Jsoup.connect(s"$wikiUrl$pageId").get().body().getElementById("firstHeading")).
      recoverWith(logHttpError(url)).
      toOption.
      map(_.text).
      map(title => WikipediaPage(pageId, title, getUrl(pageId)))
  }

  def findNextPage(pageId: String, validator: (Element) => Boolean): Option[WikipediaPage] = {
    val url = getUrl(pageId)
    val html = Try(Jsoup.connect(url).
      get().body().
      select("div#mw-content-text>p>a").
      select(":not(.mw-redirect, .mw-disambig)").
      asScala.toList).
      recoverWith(logHttpError(url))

    for {
      links <- html.toOption
      nextPage <- links.find(validator).map(WikipediaPage.apply)
    } yield nextPage
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
      logger.warn(s"Was expecting an anchor tag for link but found $link")
      return false
    }

    if (!link.attr("href").startsWith("/wiki")) {
      logger.trace(s"Ignoring a link that doesn't start with /wiki $link")
      return false
    }

    if (!link.hasText) {
      logger.trace(s"Ignoring link that has no contents $link")
      return false
    }

    if (!link.text().charAt(0).isLower) {
      logger.trace(s"Ignoring non lower case alpha characters $link")
      return false
    }

    logger.info(s"Found next page link $link")

    true
  }

}
