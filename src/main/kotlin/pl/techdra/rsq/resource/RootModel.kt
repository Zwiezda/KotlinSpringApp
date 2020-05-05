package pl.techdra.rsq.resource

import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation

@Relation(itemRelation = "root")
class RootModel : RepresentationModel<RootModel>() {
}