package pl.techdra.rsq.controller

import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.PagedModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.techdra.rsq.assembler.VisitModelAssembler
import pl.techdra.rsq.domain.Visit
import pl.techdra.rsq.exception.ChangePatientException
import pl.techdra.rsq.exception.DoctorNotFoundException
import pl.techdra.rsq.exception.PatientNotFoundException
import pl.techdra.rsq.exception.VisitNotFoundException
import pl.techdra.rsq.repository.DoctorRepository
import pl.techdra.rsq.repository.PatientRepository
import pl.techdra.rsq.repository.VisitRepository

import pl.techdra.rsq.resource.VisitModel
import javax.validation.Valid


@RestControllerAdvice
@RequestMapping(path = ["/visits"], produces = ["application/json"])
class VisitController(
        private val visitRepository: VisitRepository,
        private val patientRepository: PatientRepository,
        private val doctorRepository: DoctorRepository,
        private val visitModelAssembler: VisitModelAssembler
) {
    @GetMapping
    fun getAllVisits(pageable: Pageable, assembler: PagedResourcesAssembler<Visit>) : PagedModel<VisitModel> {
        val visits = visitRepository.findAll(pageable)
        return assembler.toModel(visits, visitModelAssembler)
    }

    @GetMapping("patient/{id}")
    fun getPatientVisits(@PathVariable id: Long, pageable: Pageable, assembler: PagedResourcesAssembler<Visit>) : PagedModel<VisitModel> {
        if (!patientRepository.existsById(id)) {
            throw PatientNotFoundException(id)
        }

        val visits = visitRepository.getVisitsByPatient(patientRepository.findById(id).get(), pageable)
        return assembler.toModel(visits, visitModelAssembler)
    }

    @GetMapping("/{id}")
    fun getVisit(@PathVariable id: Long) : VisitModel {
        return visitModelAssembler.toModel(visitRepository.findById(id).orElseThrow { VisitNotFoundException(id) })
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addVisit(@Valid @RequestBody visit: Visit) : VisitModel {
        if (!doctorRepository.existsById(visit.doctor.id!!) ) {
            throw DoctorNotFoundException(visit.doctor.id!!)
        }

        if (!patientRepository.existsById(visit.patient.id!!)) {
            throw PatientNotFoundException(visit.patient.id!!)
        }

        visit.doctor = doctorRepository.findById(visit.doctor.id!!).get();
        visit.patient = patientRepository.findById(visit.patient.id!!).get();

        val result = visitRepository.save(visit)
        return visitModelAssembler.toModel(result)
    }

    @PutMapping("/{id}")
    fun changeVisit(@PathVariable id: Long, @RequestBody @Valid visit: Visit) : ResponseEntity<VisitModel> {
        if (!doctorRepository.existsById(visit.doctor.id!!) ) {
            throw DoctorNotFoundException(visit.doctor.id!!)
        }

        if (!patientRepository.existsById(visit.patient.id!!)) {
            throw PatientNotFoundException(visit.patient.id!!)
        }

        var result = visitRepository.findById(id).map {
            if (it.patient.id != visit.patient.id) throw ChangePatientException()   //If user try to change patient

            it.doctor = visit.doctor
            it.patient = visit.patient
            it.visitDate = visit.visitDate
            it.visitTime = visit.visitTime
            it.expectedVisitDuration = visit.expectedVisitDuration
            return@map visitRepository.save(it)
        }.orElseGet {
            visit.id = id
            return@orElseGet visitRepository.save(visit)
        }

        result = visitRepository.findById(result.id!!).get()

        return if (result.id != id) {
            ResponseEntity(visitModelAssembler.toModel(result), HttpStatus.CREATED)
        } else {
            ResponseEntity(visitModelAssembler.toModel(result), HttpStatus.OK)
        }

    }

    @DeleteMapping("/{id}")
    fun deleteVisit(@PathVariable id: Long) {
        if (!visitRepository.existsById(id)) throw VisitNotFoundException(id)
        visitRepository.deleteById(id)
    }



}