package me.ngrid.philosocrawler.crawler

import org.scalatest.{FlatSpec, Matchers}

/**
  *
  */
class WikipediaCrawlerTest extends FlatSpec with Matchers {

}

object TestBasicCrawl extends App {
  val crawler = new EnglishWikipediaCrawler(maxDepth = 10000)
  val res = crawler.crawl(pageId = "Baseball", (s: String) => None)
  System.out.println(res)
}
