package me.ngrid.philosocrawler.services

import com.fasterxml.jackson.databind.ObjectMapper
import me.ngrid.philosocrawler.crawler.{WikipediaCrawler, WikipediaPage}
import me.ngrid.philosocrawler.entities.{PageEntity, PathEntity}
import me.ngrid.philosocrawler.models.{PathPage, PathToPhilosophy}
import me.ngrid.philosocrawler.repositories.PathRepository
import org.springframework.stereotype.Service

/**
  *
  */
trait PhilosophyPathService {
  def findPathForPageId(pageId: String): Option[PathToPhilosophy]
  def savePath(pathToPhilosophy: PathToPhilosophy)
}

@Service
class PhilosophyPathServiceImpl(wikipediaCrawler: WikipediaCrawler,
                                pathRepository: PathRepository) extends PhilosophyPathService {
  import PhilosophyPathServiceImpl._

  import scala.collection.JavaConverters._

  val jsonMapper = new ObjectMapper()
  val jsonPageReader = jsonMapper.readerFor(classOf[PageEntity])

  override def findPathForPageId(pageId: String): Option[PathToPhilosophy] = {
    def pathCache(id: String): Option[List[WikipediaPage]] = {
      if(pathRepository.exists(id)) {
        val e = pathRepository.findOne(id)
        Option(e.path).map(readJsonCrawlerPath)
      } else {
        None
      }
    }

    val path = wikipediaCrawler.crawl(pageId, pathCache).map(convertCrawlerPath)
    path.foreach(savePath)
    path
  }

  override def savePath(p: PathToPhilosophy): Unit = {
    if(p.pages.length > 1 && !pathRepository.exists(p.pages(1).id))
      pathRepository.save(convertPathToEntity(p.pages.dropRight(1)))
  }

  def convertPathToEntity(pages: List[PathPage]): PathEntity = {
    val e = new PathEntity
    val path = pages.map(x => new PageEntity(x.id, x.title, x.url))
    e.pageId = pages.last.id
    e.pageUrl = pages.last.url
    e.path = jsonMapper.writeValueAsString(path.asJava)
    e
  }

  def readJsonCrawlerPath(path: String): List[WikipediaPage] = readJsonPath(path).map(convertPageEntityToCrawler)
  def readJsonPath(path: String): List[PageEntity] = jsonPageReader.readValues(path).asScala.toList

}

object PhilosophyPathServiceImpl {
  def convertPageEntityToCrawler(p: PageEntity) = WikipediaPage(p.id, p.title, p.url)
  def convertCrawlerPath(path: List[WikipediaPage]) = PathToPhilosophy(path.length, path.map(wikipediaToPathPage))
  def wikipediaToPathPage(p: WikipediaPage): PathPage = PathPage(p.id, p.title, p.fullUrl)
}

