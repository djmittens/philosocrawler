package me.ngrid.philosocrawler.controllers

import java.util
import java.util.Collections
import javax.validation.Valid

import me.ngrid.philosocrawler.models.{PathPage, PathToPhilosophy}
import me.ngrid.philosocrawler.services.PhilosophyPathService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
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
  def emptyForm(model: Model): String = {
    model.addAttribute("pathToPhilosophy", PathToPhilosophy(0, Nil))
    model.addAttribute("searchForm", new SearchForm())
    model.addAttribute("philosophyPath", Collections.emptyList[PathPage]())
    "path-to-philosophy"
  }

  @RequestMapping(path = Array("/"), method = Array(RequestMethod.POST))
  def findPath(@Valid searchForm: SearchForm, model: Model): String = {
    model.addAttribute("pathToPhilosophy", PathToPhilosophy(0, Nil))
//    model.addAttribute("searchForm", new SearchForm())

    val path = philosophyPathService.findPathForPageId(searchForm.searchForUrl)

    model.addAttribute("philosophyPath", extractPath(path))
    "path-to-philosophy"
  }

  def extractPath(path: Option[PathToPhilosophy]): java.util.List[PathPage] = {
    path.map(_.pages.reverse.asJava).getOrElse({Collections.emptyList[PathPage]()})
  }
}

class SearchForm {
  @BeanProperty
  var searchForUrl: String = ""
}