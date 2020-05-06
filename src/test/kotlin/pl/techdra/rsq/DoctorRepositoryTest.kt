package pl.techdra.rsq

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.TransactionSystemException
import pl.techdra.rsq.domain.Doctor
import pl.techdra.rsq.repository.DoctorRepository


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
class DoctorRepositoryTest(@Autowired val doctorRepository: DoctorRepository) {

    @BeforeEach
    private fun clearDatabase() {
        doctorRepository.deleteAll()
    }

    @Test
    fun `Create doctor and save to database`() {
        val doctor = Doctor("Jan", "Kowalski", "Trycholog")
        val result = doctorRepository.save(doctor)

        Assertions.assertEquals(doctor.firstName, result.firstName)
        Assertions.assertEquals(doctor.lastName, result.lastName)
        Assertions.assertEquals(doctor.specialization, result.specialization)
        Assertions.assertNotEquals(0L, result.id)
    }

    @Test
    fun `Constraint exception creating doctor with empty firstName and lastName`() {
        val doctor = Doctor("", "", "Trycholog")
        Assertions.assertThrows(TransactionSystemException::class.java) {
            doctorRepository.save(doctor)
        }
    }

    @Test
    fun `Update doctor`() {
        val doctor = Doctor("Jan", "Kowalski", "Trycholog")
        var result = doctorRepository.save(doctor)
        result.firstName = "Marek"
        result.specialization = "Kardiolog"
        result = doctorRepository.save(result)

        Assertions.assertEquals("Marek", result.firstName)
        Assertions.assertEquals(doctor.lastName, result.lastName)
        Assertions.assertEquals("Kardiolog", result.specialization)
        Assertions.assertNotEquals(0L, result.id)
        Assertions.assertEquals(1L, doctorRepository.count())
    }

    @Test
    fun `Delete all doctors`() {
        doctorRepository.save( Doctor("Jan", "Kowalski", "Trycholog"))
        doctorRepository.save( Doctor("Marek", "Miodowicz", "Urolog"))
        doctorRepository.save( Doctor("Jacek", "Kowalski", "Pulmonolog"))
        doctorRepository.deleteAll()

        Assertions.assertEquals(0L, doctorRepository.count())
    }

}