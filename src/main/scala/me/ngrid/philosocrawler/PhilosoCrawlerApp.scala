package me.ngrid.philosocrawler

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

object PhilosoCrawlerApp extends App {
  SpringApplication.run(classOf[PhilosoCrawlerApp], args: _*)
}

@SpringBootApplication
@EnableJpaRepositories
class PhilosoCrawlerApp {
}
