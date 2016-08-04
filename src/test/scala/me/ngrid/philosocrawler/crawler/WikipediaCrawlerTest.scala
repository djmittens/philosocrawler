package me.ngrid.philosocrawler.crawler

import java.util

import com.fasterxml.jackson.databind.ObjectMapper
import org.scalatest.{FlatSpec, Matchers}
import scala.collection.JavaConverters._

/**
  *
  */
class WikipediaCrawlerTest extends FlatSpec with Matchers {

}

object TestBasicCrawl extends App {
  val crawler = new EnglishWikipediaCrawler(maxDepth = 10000)
//  val res = crawler.crawl(pageId = "Vladimir_Putin", (s: String) => None)
  val res = crawler.crawl(pageId = "Charles_V", (s: String) => None)
  System.out.println(res)
}
