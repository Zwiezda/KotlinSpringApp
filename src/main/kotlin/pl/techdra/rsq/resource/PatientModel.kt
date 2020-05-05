package pl.techdra.rsq.resource

import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation
import pl.techdra.rsq.domain.Patient

@Relation(itemRelation = "patient", collectionRelation = "patients")
class PatientModel(patient: Patient)  : RepresentationModel<DoctorModel>()   {
    val firstName: String? = patient.firstName
    val lastName: String? = patient.lastName
    val address: String? = patient.address
}