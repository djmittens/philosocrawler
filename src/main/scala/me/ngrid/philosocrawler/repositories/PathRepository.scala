package me.ngrid.philosocrawler.repositories

import me.ngrid.philosocrawler.entities.PathEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
trait PathRepository extends CrudRepository[PathEntity, String]{ }
