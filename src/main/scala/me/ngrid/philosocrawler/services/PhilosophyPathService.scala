package me.ngrid.philosocrawler.services

import me.ngrid.philosocrawler.crawler.{WikipediaCrawler, WikipediaPage}
import me.ngrid.philosocrawler.models.{PathPage, PathToPhilosophy}
import org.springframework.stereotype.Service

/**
  *
  */
trait PhilosophyPathService {
  def findPathForPageId(pageId: String): Option[PathToPhilosophy]
}

@Service
class PhilosophyPathServiceImpl(wikipediaCrawler: WikipediaCrawler) extends PhilosophyPathService {
  override def findPathForPageId(pageId: String): Option[PathToPhilosophy] = {
    wikipediaCrawler.crawl(pageId, (s: String) => None).map(convertCrawlerPath)
  }

  def convertCrawlerPath(path: List[WikipediaPage]) = PathToPhilosophy(path.length, path.map(wikipediaToPathPage))
  def wikipediaToPathPage(p: WikipediaPage): PathPage = PathPage(p.id, p.title, p.fullUrl)
}


