package me.ngrid.philosocrawler

import me.ngrid.philosocrawler.crawler.EnglishWikipediaCrawler
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.{Bean, Primary}
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

object PhilosoCrawlerApp extends App {
  SpringApplication.run(classOf[PhilosoCrawlerApp], args: _*)
}

@SpringBootApplication
@EnableJpaRepositories
class PhilosoCrawlerApp {
  @Bean
  @Primary
  @Qualifier("english")
  def defaultCrawler = new EnglishWikipediaCrawler()
}
