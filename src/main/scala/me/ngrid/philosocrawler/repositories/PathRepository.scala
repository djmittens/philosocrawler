package me.ngrid.philosocrawler.repositories

import me.ngrid.philosocrawler.entities.PathToPhilosophy
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
  *
  */
@Repository
trait PathRepository extends CrudRepository[PathToPhilosophy, String]{

}
