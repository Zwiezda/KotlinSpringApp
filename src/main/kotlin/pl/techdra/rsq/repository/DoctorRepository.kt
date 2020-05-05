package pl.techdra.rsq.repository

import org.springframework.data.repository.PagingAndSortingRepository
import pl.techdra.rsq.domain.Doctor

interface DoctorRepository : PagingAndSortingRepository<Doctor, Long> {
}