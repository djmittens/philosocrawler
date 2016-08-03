package me.ngrid.philosocrawler.models

import scala.beans.BeanProperty

case class PathToPhilosophy (count: Int, pages: List[PathPage])
case class PathPage(id: String, @BeanProperty title: String, @BeanProperty url: String)
