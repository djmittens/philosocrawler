package me.ngrid.philosocrawler.controllers

import java.util.Collections
import javax.validation.Valid

import me.ngrid.philosocrawler.models.{PathPage, PathToPhilosophy}
import me.ngrid.philosocrawler.services.PhilosophyPathService
import org.hibernate.validator.constraints.NotEmpty
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.util.StringUtils
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.{RequestMapping, RequestMethod}

import scala.beans.BeanProperty
import scala.collection.JavaConverters._

/**
  *
  */
@Controller
class PathToPhilosophyController(philosophyPathService: PhilosophyPathService) {


  @RequestMapping(path = Array("/"), method = Array(RequestMethod.GET))
  def findPath(searchForm: SearchForm, bindingResult: BindingResult, model: Model): String = {
    model.addAttribute("pathToPhilosophy", PathToPhilosophy(0, Nil))

    val path = searchForm.getPageId.flatMap(philosophyPathService.findPathForPageId)
    model.addAttribute("philosophyPath", extractPath(path))

    "path-to-philosophy"
  }

  def extractPath(path: Option[PathToPhilosophy]): java.util.List[PathPage] = {
    path.map(_.pages.reverse.asJava).getOrElse({Collections.emptyList[PathPage]()})
  }
}

class SearchForm {
  @NotEmpty
  @BeanProperty
  var searchForUrl: String = ""

  def getPageId: Option[String] = {
    if(StringUtils.isEmpty(searchForUrl)) return None

    val http = "^http://en.wikipedia.org/wiki/(.*)$".r
    val https = "^https://en.wikipedia.org/wiki/(.*)$".r
    Some(searchForUrl match {
      case http(id) => id
      case https(id) => id
      case other => other
    })
  }
}