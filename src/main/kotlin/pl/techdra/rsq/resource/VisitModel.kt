package pl.techdra.rsq.resource

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.hateoas.Link
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation
import org.springframework.hateoas.server.mvc.linkTo
import pl.techdra.rsq.assembler.DoctorModelAssembler
import pl.techdra.rsq.assembler.PatientModelAssembler
import pl.techdra.rsq.domain.Visit
import java.time.LocalDate
import java.time.LocalTime

@Relation(itemRelation = "visit", collectionRelation = "visits")
class VisitModel(visit: Visit) : RepresentationModel<VisitModel>() {
    val id: Long? = visit.id
    val doctor: DoctorModel = doctorModelAssembler.toModel(visit.doctor)
    val patient: PatientModel = patientModelAssembler.toModel(visit.patient)
    @field:JsonFormat(pattern = "HH:mm") val visitTime: LocalTime = visit.visitTime
    @field:JsonFormat(pattern = "dd-MM-yyyy") val visitDate: LocalDate = visit.visitDate
    val expectedVisitDuration: Int = visit.expectedVisitDuration


    companion object {
        private val doctorModelAssembler: DoctorModelAssembler = DoctorModelAssembler()
        private val patientModelAssembler: PatientModelAssembler = PatientModelAssembler()
    }

}