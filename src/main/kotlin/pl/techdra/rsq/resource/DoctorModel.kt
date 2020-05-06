package pl.techdra.rsq.resource

import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation
import pl.techdra.rsq.domain.Doctor

@Relation(itemRelation = "doctor", collectionRelation = "doctors")
class DoctorModel(doctor: Doctor) : RepresentationModel<DoctorModel>() {
    val id: Long? = doctor.id
    val firstName: String? = doctor.firstName
    val lastName: String? = doctor.lastName
    val specialization: String? = doctor.specialization
}