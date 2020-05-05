package pl.techdra.rsq.controller

import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.PagedModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.techdra.rsq.assembler.PatientModelAssembler
import pl.techdra.rsq.domain.Patient
import pl.techdra.rsq.exception.PatientNotFoundException
import pl.techdra.rsq.repository.PatientRepository
import pl.techdra.rsq.resource.PatientModel
import javax.validation.Valid

@RestController
@RequestMapping(path = ["/patients"], produces = ["application/json"])
class PatientController(
        private val patientRepository: PatientRepository,
        private val patientModelAssembler: PatientModelAssembler
) {
    @GetMapping
    fun getAllPatients(pageable: Pageable, assembler: PagedResourcesAssembler<Patient>) : PagedModel<PatientModel> {
        val patients = patientRepository.findAll(pageable)
        return assembler.toModel(patients, patientModelAssembler)
    }

    @GetMapping("/{id}")
    fun getPatient(@PathVariable id: Long) : PatientModel {
        return patientModelAssembler.toModel(patientRepository.findById(id).orElseThrow { PatientNotFoundException(id) })
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addPatient(@RequestBody @Valid patient: Patient) : PatientModel {
        return patientModelAssembler.toModel(patientRepository.save(patient))
    }

    @PutMapping("/{id}")
    fun changePatient(@PathVariable id: Long, @RequestBody @Valid patient: Patient) : ResponseEntity<PatientModel> {
        val result = patientRepository.findById(id).map {
            it.firstName = patient.firstName
            it.lastName = patient.lastName
            it.address = patient.address
            return@map patientRepository.save(it)
        }.orElseGet {
            patient.id = id
            return@orElseGet patientRepository.save(patient)
        }

        return if (result.id != id) {
            ResponseEntity(patientModelAssembler.toModel(result), HttpStatus.CREATED)
        } else {
            ResponseEntity(patientModelAssembler.toModel(result), HttpStatus.OK)
        }
    }

    @DeleteMapping("/{id}")
    fun deletePatient(@PathVariable id: Long) {
        if (!patientRepository.existsById(id)) throw PatientNotFoundException(id)
        patientRepository.deleteById(id)
    }
}