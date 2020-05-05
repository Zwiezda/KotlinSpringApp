package pl.techdra.rsq.assembler

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport
import org.springframework.stereotype.Component
import pl.techdra.rsq.controller.VisitController
import pl.techdra.rsq.domain.Visit
import pl.techdra.rsq.resource.VisitModel

@Component
class VisitModelAssembler : RepresentationModelAssemblerSupport<Visit, VisitModel>(
        VisitController::class.java, VisitModel::class.java
) {
    override fun toModel(entity: Visit): VisitModel {
        return createModelWithId(entity.id!!, entity)
    }

    override fun instantiateModel(entity: Visit): VisitModel {
        return VisitModel(entity)
    }


}