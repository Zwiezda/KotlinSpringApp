package pl.techdra.rsq.controller

import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.PagedModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.techdra.rsq.assembler.DoctorModelAssembler
import pl.techdra.rsq.domain.Doctor
import pl.techdra.rsq.exception.DoctorNotFoundException
import pl.techdra.rsq.repository.DoctorRepository
import pl.techdra.rsq.resource.DoctorModel
import javax.validation.Valid

@RestController
@RequestMapping(path = ["/doctors"], produces = ["application/json"])
class DoctorController(private val doctorRepository: DoctorRepository,
                       private val doctorModelAssembler: DoctorModelAssembler
) {

    @GetMapping
    fun getAllDoctors(pageable: Pageable, assembler: PagedResourcesAssembler<Doctor>) : PagedModel<DoctorModel> {
        val doctors = doctorRepository.findAll(pageable)

        return assembler.toModel(doctors, doctorModelAssembler)
     }

    @GetMapping("/{id}")
    fun getDoctor( @PathVariable id: Long) : DoctorModel {
        return doctorModelAssembler.toModel(doctorRepository.findById(id).orElseThrow { DoctorNotFoundException(id) })
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addDoctor(@Valid @RequestBody doctor: Doctor) : DoctorModel {
        return doctorModelAssembler.toModel(doctorRepository.save(doctor))
    }

    @PutMapping("/{id}")
    fun changeDoctor(@PathVariable id: Long, @RequestBody @Valid doctor: Doctor) : ResponseEntity<DoctorModel> {
        val result = doctorRepository.findById(id).map {
             it.firstName = doctor.firstName
             it.lastName = doctor.lastName
             it.specialization = doctor.specialization
            return@map doctorRepository.save(it)
         }.orElseGet {
             doctor.id = id
            return@orElseGet doctorRepository.save(doctor)
         }


        return if (result.id != id) {
            ResponseEntity(doctorModelAssembler.toModel(result), HttpStatus.CREATED)
        } else {
            ResponseEntity(doctorModelAssembler.toModel(result), HttpStatus.OK)
        }
    }

    @DeleteMapping("/{id}")
    fun deleteDoctor(@PathVariable id: Long) {
        if (!doctorRepository.existsById(id)) throw DoctorNotFoundException(id)
        doctorRepository.deleteById(id)
    }

}