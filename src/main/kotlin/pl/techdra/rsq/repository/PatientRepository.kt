package pl.techdra.rsq.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import pl.techdra.rsq.domain.Patient

interface PatientRepository : PagingAndSortingRepository<Patient, Long> {
}