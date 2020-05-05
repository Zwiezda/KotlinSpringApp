package pl.techdra.rsq

import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import pl.techdra.rsq.domain.Doctor
import pl.techdra.rsq.domain.Patient
import pl.techdra.rsq.repository.PatientRepository

@AutoConfigureMockMvc
@SpringBootTest
class PatientControllerTest
{
    @Autowired
    private var mockMvc: MockMvc? = null

    @Autowired
    private var patientRepository: PatientRepository? = null

    @BeforeEach
    private fun clearDatabase() {
        patientRepository!!.deleteAll()
    }


    @Test
    fun `GET all patients via REST`() {
        var firstPatient = Patient("Jan", "Kowalski", "Wroclaw")
        var secondPatient = Patient("Mateusz", "Nowak", "Krakow")

        firstPatient = patientRepository!!.save(firstPatient)
        secondPatient = patientRepository!!.save(secondPatient)

        mockMvc!!.perform(get("/patients").accept("application/json"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$._embedded.patients[0].firstName", equalTo(firstPatient.firstName) ))
                .andExpect(jsonPath("$._embedded.patients[0].lastName", equalTo(firstPatient.lastName) ))
                .andExpect(jsonPath("$._embedded.patients[0].address", equalTo(firstPatient.address) ))
                .andExpect(jsonPath("$._embedded.patients[1].firstName", equalTo(secondPatient.firstName) ))
                .andExpect(jsonPath("$._embedded.patients[1].lastName", equalTo(secondPatient.lastName) ))
                .andExpect(jsonPath("$._embedded.patients[1].address", equalTo(secondPatient.address) ))
                .andReturn()
    }

    @Test
    fun `POST patient via REST`() {
        val patient = Patient("Jan", "Kowalski", "ul. Klonowa 6 60-001 Poznan")

        mockMvc!!.perform(post("/patients").accept("application/json").content(
                """
                    {
                        "firstName": "${patient.firstName}",
                        "lastName": "${patient.lastName}",
                        "address": "${patient.address}"
                    }
                """.trimIndent()
        ).contentType("application/json")).andExpect(status().isCreated)
                .andReturn();

        val result = patientRepository!!.findAll().first()

        Assertions.assertEquals(patient.firstName, result.firstName)
        Assertions.assertEquals(patient.lastName, result.lastName)
        Assertions.assertEquals(patient.address, result.address)
        Assertions.assertNotEquals(0, result.id)
    }


    @Test
    fun `PUT patient via REST`() {
        var patient = Patient("Jan", "Kowalski", "Kardiolog")
        patient = patientRepository!!.save(patient)

        mockMvc!!.perform(put("/patients/${patient.id}").accept("application/json").content(
                """
                    {
                        "firstName": "Jacek",
                        "lastName": "${patient.lastName}",
                        "address": "${patient.address}"
                    }
                """.trimIndent()
        ).contentType("application/json")).andExpect(status().isOk)
                .andReturn();


        val result = patientRepository!!.findById(patient.id!!.toLong()).get()
        Assertions.assertEquals(patient.id!!.toLong(), result.id!!.toLong())
        Assertions.assertEquals("Jacek", result.firstName)
    }


    @Test
    fun `DELETE patient via REST`() {
        var patient = Patient("Jan", "Kowalski", "Kardiolog")

        patient = patientRepository!!.save(patient)

        mockMvc!!.perform(delete("/patients/${patient.id}").accept("application/json"))
                .andExpect(status().isOk)
                .andReturn();

        Assertions.assertFalse(patientRepository!!.findById(patient.id!!).isPresent)


    }
}

