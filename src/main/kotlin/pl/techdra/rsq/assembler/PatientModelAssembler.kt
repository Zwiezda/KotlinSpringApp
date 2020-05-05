package pl.techdra.rsq.assembler

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport
import org.springframework.stereotype.Component
import pl.techdra.rsq.controller.PatientController
import pl.techdra.rsq.domain.Patient
import pl.techdra.rsq.resource.PatientModel

@Component
class PatientModelAssembler : RepresentationModelAssemblerSupport<Patient, PatientModel>(
PatientController::class.java, PatientModel::class.java) {
    override fun toModel(entity: Patient): PatientModel {
        return createModelWithId(entity.id!!, entity)
    }

    override fun instantiateModel(entity: Patient): PatientModel {
        return PatientModel(entity)
    }
}
