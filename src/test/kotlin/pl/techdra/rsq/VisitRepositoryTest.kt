package pl.techdra.rsq

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.TransactionSystemException
import pl.techdra.rsq.domain.Doctor
import pl.techdra.rsq.domain.Patient
import pl.techdra.rsq.domain.Visit
import pl.techdra.rsq.repository.DoctorRepository
import pl.techdra.rsq.repository.PatientRepository
import pl.techdra.rsq.repository.VisitRepository
import java.time.LocalDate
import java.time.LocalTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VisitRepositoryTest(
        @Autowired val doctorRepository: DoctorRepository,
        @Autowired val patientRepository: PatientRepository,
        @Autowired val visitRepository: VisitRepository
) {
    private fun createDoctor() : Doctor {
        return doctorRepository.save(Doctor("Jan", "Kowalski", "Trycholog"))
    }

    private fun createPatient() : Patient {
        return patientRepository.save(Patient("Marek", "Miodowicz", "Kielce"))
    }

    @BeforeEach
    private fun clearDatabase() {
        visitRepository.deleteAll()
    }


    @Test
    fun `Register visit`() {
        val time = LocalTime.now()
        val date = LocalDate.now()
        val doctor = createDoctor()
        val patient = createPatient()

        val result = visitRepository.save(Visit(doctor, patient, time, date))

        Assertions.assertEquals(doctor.id, result.doctor.id)
        Assertions.assertEquals(patient.id, result.patient.id)
        Assertions.assertEquals(time, result.visitTime)
        Assertions.assertEquals(date, result.visitDate)
        Assertions.assertNotEquals(0L, result.id)
    }

    @Test
    fun `Register concurrent visit`() {
        val time = LocalTime.parse("12:00:00")
        val date = LocalDate.parse("1999-03-01")
        val doctor = createDoctor()
        val patient = createPatient()
        val patient2 = createPatient()

        visitRepository.save(Visit(doctor, patient, time, date, 50))

        Assertions.assertThrows(TransactionSystemException::class.java) {
            visitRepository.save(Visit(doctor, patient2, time.plusMinutes(30), date, 30))
        }
    }

    @Test
    fun `Get patient visits`() {
        val time = LocalTime.parse("12:00:00")
        val date = LocalDate.parse("1999-03-01")
        val doctor = createDoctor()
        val patient = createPatient()
        val patient2 = createPatient()

        visitRepository.save(Visit(doctor, patient, time, date, 50))
        visitRepository.save(Visit(doctor, patient2, time.plusHours(1), date, 50))

        val patientVisits = visitRepository.getVisitsByPatient(patient2, PageRequest.of(0, 10))

        Assertions.assertEquals(1, patientVisits.count() )
    }

    @Test
    fun `Change visit date`() {
        val time = LocalTime.parse("12:00:00")
        val date = LocalDate.parse("1999-03-01")
        val changedDate = LocalDate.parse("1999-03-03")
        val doctor = createDoctor()
        val patient = createPatient()

        val visit = visitRepository.save(Visit(doctor, patient, time, date, 30))
        visit.visitDate = changedDate

        val result= visitRepository.save(visit)

        Assertions.assertEquals(visit.id, result.id)
        Assertions.assertEquals(changedDate, result.visitDate)
    }

    @Test
    fun `Delete visit`() {
        val time = LocalTime.parse("12:00:00")
        val date = LocalDate.parse("1999-03-01")
        val changedDate = LocalDate.parse("1999-03-03")
        val doctor = createDoctor()
        val patient = createPatient()

        val visit = visitRepository.save(Visit(doctor, patient, time, date, 30))
        visitRepository.delete(visit)

        Assertions.assertEquals(0,visitRepository.count() )
    }
}