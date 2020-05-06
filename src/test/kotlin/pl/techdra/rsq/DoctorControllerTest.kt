package pl.techdra.rsq

import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.hateoas.MediaTypes
import org.springframework.http.HttpHeaders
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import pl.techdra.rsq.domain.Doctor
import pl.techdra.rsq.repository.DoctorRepository

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("dev")
class DoctorControllerTest
{
    @Autowired
    private var mockMvc: MockMvc? = null

    @Autowired
    private var doctorRepository: DoctorRepository? = null

    @BeforeEach
    private fun clearDatabase() {
        doctorRepository!!.deleteAll()
    }


    @Test
    fun `GET all doctors via REST`() {
        var firstDoctor = Doctor("Jan", "Kowalski", "Kardiolog")
        var secondDoctor = Doctor("Mateusz", "Nowak", "Neurolog")

        firstDoctor = doctorRepository!!.save(firstDoctor)
        secondDoctor = doctorRepository!!.save(secondDoctor)

        mockMvc!!.perform(get("/doctors").accept("application/json"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$._embedded.doctors[0].firstName", equalTo(firstDoctor.firstName) ))
                .andExpect(jsonPath("$._embedded.doctors[0].lastName", equalTo(firstDoctor.lastName) ))
                .andExpect(jsonPath("$._embedded.doctors[0].specialization", equalTo(firstDoctor.specialization) ))
                .andExpect(jsonPath("$._embedded.doctors[1].firstName", equalTo(secondDoctor.firstName) ))
                .andExpect(jsonPath("$._embedded.doctors[1].lastName", equalTo(secondDoctor.lastName) ))
                .andExpect(jsonPath("$._embedded.doctors[1].specialization", equalTo(secondDoctor.specialization) ))
                .andReturn()
    }

    @Test
    fun `POST doctor via REST`() {
        val doctor = Doctor("Jan", "Kowalski", "Kardiolog")

        mockMvc!!.perform(post("/doctors").accept("application/json").content(
                """
                    {
                        "firstName": "${doctor.firstName}",
                        "lastName": "${doctor.lastName}",
                        "specialization": "${doctor.specialization}"
                    }
                """.trimIndent()
        ).contentType("application/json")).andExpect(status().isCreated)
                .andReturn();


        val result = doctorRepository!!.findAll().first()
        Assertions.assertEquals(doctor.firstName, result.firstName)
        Assertions.assertEquals(doctor.lastName, result.lastName)
        Assertions.assertEquals(doctor.specialization, result.specialization)
        Assertions.assertNotEquals(0, result.id)
    }


    @Test
    fun `PUT doctor via REST`() {
        var doctor = Doctor("Jan", "Kowalski", "Kardiolog")
        doctor = doctorRepository!!.save(doctor)

        mockMvc!!.perform(put("/doctors/${doctor.id}").accept("application/json").content(
                """
                    {
                        "firstName": "Jacek",
                        "lastName": "${doctor.lastName}",
                        "specialization": "${doctor.specialization}"
                    }
                """.trimIndent()
        ).contentType("application/json")).andExpect(status().isOk)
                .andReturn();


        val result = doctorRepository!!.findById(doctor.id!!.toLong()).get()
        Assertions.assertEquals(doctor.id!!.toLong(), result.id!!.toLong())
        Assertions.assertEquals("Jacek", result.firstName)
    }


    @Test
    fun `DELETE doctor via REST`() {
        var firstDoctor = Doctor("Jan", "Kowalski", "Kardiolog")
        val secondDoctor = Doctor("Mateusz", "Nowak", "Neurolog")

        firstDoctor = doctorRepository!!.save(firstDoctor)
        doctorRepository!!.save(secondDoctor)

        mockMvc!!.perform(delete("/doctors/${firstDoctor.id}").accept("application/json"))
                .andExpect(status().isOk)
                .andReturn();

        Assertions.assertFalse(doctorRepository!!.findById(firstDoctor.id!!).isPresent)


    }
}

