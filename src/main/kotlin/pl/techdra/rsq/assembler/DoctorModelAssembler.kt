package pl.techdra.rsq.assembler

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport
import org.springframework.stereotype.Component
import pl.techdra.rsq.controller.DoctorController
import pl.techdra.rsq.domain.Doctor
import pl.techdra.rsq.resource.DoctorModel

@Component
class DoctorModelAssembler : RepresentationModelAssemblerSupport<Doctor, DoctorModel>(
        DoctorController::class.java, DoctorModel::class.java)
{
    override fun toModel(entity: Doctor): DoctorModel {
        return createModelWithId(entity.id!!, entity)
    }

    override fun instantiateModel(entity: Doctor): DoctorModel {
        return DoctorModel(entity)
    }
}