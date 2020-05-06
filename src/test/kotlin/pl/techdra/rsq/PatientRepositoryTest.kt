package pl.techdra.rsq

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.TransactionSystemException
import pl.techdra.rsq.domain.Patient
import pl.techdra.rsq.repository.PatientRepository

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
class PatientRepositoryTest(@Autowired val patientRepository: PatientRepository) {

    @BeforeEach
    private fun clearDatabase() {
        patientRepository.deleteAll()
    }

    @Test
    fun `Create patient and save to database`() {
        val doctor = Patient("Jan", "Kowalski", "Poznań")
        val result = patientRepository.save(doctor)

        Assertions.assertEquals(doctor.firstName, result.firstName)
        Assertions.assertEquals(doctor.lastName, result.lastName)
        Assertions.assertEquals(doctor.address, result.address)
        Assertions.assertNotEquals(0L, result.id)
    }

    @Test
    fun `Constraint exception creating patient with empty firstName and lastName`() {
        val patient = Patient("", "", "Bydgoszcz")
        Assertions.assertThrows(TransactionSystemException::class.java) {
            patientRepository.save(patient)
        }
    }

    @Test
    fun `Update patient`() {
        val patient = Patient("Jan", "Kowalski", "Bydgoszcz")
        var result = patientRepository.save(patient)
        result.firstName = "Marek"
        result.address = "Lublin"
        result = patientRepository.save(patient)

        Assertions.assertEquals("Marek", result.firstName)
        Assertions.assertEquals(patient.lastName, result.lastName)
        Assertions.assertEquals("Lublin", result.address)
        Assertions.assertNotEquals(0L, result.id)
        Assertions.assertEquals(1L, patientRepository.count())
    }

    @Test
    fun `Delete all patients`() {
        patientRepository.save( Patient("Jan", "Kowalski", "Lubin"))
        patientRepository.save( Patient("Marek", "Miodowicz", "Kielce"))
        patientRepository.save( Patient("Jacek", "Kowalski", "Szczecin"))
        patientRepository.deleteAll()

        Assertions.assertEquals(0L, patientRepository.count())
    }
}