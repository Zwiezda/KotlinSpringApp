package pl.techdra.rsq.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import pl.techdra.rsq.domain.Doctor
import pl.techdra.rsq.domain.Patient
import pl.techdra.rsq.domain.Visit
import java.time.LocalDate
import java.time.LocalTime
import java.util.*


interface VisitRepository : PagingAndSortingRepository<Visit, Long> {
    @Query("SELECT v FROM Visit v WHERE v.doctor = :doctor AND (v.visitDate < :visitDate OR v.visitTime <= :visitTime) ORDER BY v.visitDate DESC, v.visitTime DESC")
    fun getLastVisitBeforeDate(@Param("doctor") doctor: Doctor,
                               @Param("visitDate") visitDate: LocalDate,
                               @Param("visitTime") visitTime: LocalTime) : Iterable<Visit>

    fun getVisitsByPatient(patient: Patient?, pageable: Pageable) : Page<Visit>
}