package pl.techdra.rsq

import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import pl.techdra.rsq.domain.Doctor
import pl.techdra.rsq.domain.Patient
import pl.techdra.rsq.domain.Visit
import pl.techdra.rsq.repository.DoctorRepository
import pl.techdra.rsq.repository.PatientRepository
import pl.techdra.rsq.repository.VisitRepository
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("dev")
class VisitControllerTest(
        @Autowired val doctorRepository: DoctorRepository,
        @Autowired val patientRepository: PatientRepository
)
{
    @Autowired
    private var mockMvc: MockMvc? = null

    @Autowired
    private var visitRepository: VisitRepository? = null


    @BeforeEach
    private fun clearDatabase() {
        visitRepository!!.deleteAll()
    }

    private fun createDoctor() : Doctor {
        return doctorRepository.save(Doctor("Jan", "Kowalski", "Trycholog"))
    }

    private fun createPatient() : Patient {
        return patientRepository.save(Patient("Marek", "Miodowicz", "Kielce"))
    }

    @Test
    fun `GET all visits via REST`() {
        val patient = createPatient()
        val doctor = createDoctor()

        var firstVisit = Visit(doctor, patient, LocalTime.parse("12:00:00"), LocalDate.parse("1999-03-01"), 60)
        var secondVisit = Visit(doctor, patient, LocalTime.parse("12:00:00"), LocalDate.parse("1999-03-02"), 60)

        firstVisit = visitRepository!!.save(firstVisit)
        secondVisit = visitRepository!!.save(secondVisit)

        mockMvc!!.perform(get("/visits").accept("application/json"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$._embedded.visits[0].doctor.firstName", equalTo(doctor.firstName) ))
                .andExpect(jsonPath("$._embedded.visits[0].doctor.lastName", equalTo(doctor.lastName) ))
                .andExpect(jsonPath("$._embedded.visits[0].doctor.specialization", equalTo(doctor.specialization) ))
                .andExpect(jsonPath("$._embedded.visits[0].patient.firstName", equalTo(patient.firstName) ))
                .andExpect(jsonPath("$._embedded.visits[0].patient.lastName", equalTo(patient.lastName) ))
                .andExpect(jsonPath("$._embedded.visits[0].patient.address", equalTo(patient.address) ))
                .andExpect(jsonPath("$._embedded.visits[0].visitTime", equalTo(firstVisit.visitTime.format(DateTimeFormatter.ofPattern("HH:mm"))) ))
                .andExpect(jsonPath("$._embedded.visits[0].visitDate", equalTo(firstVisit.visitDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))) ))
                .andExpect(jsonPath("$._embedded.visits[0].expectedVisitDuration", equalTo(firstVisit.expectedVisitDuration) ))
                .andExpect(jsonPath("$._embedded.visits[1].doctor.firstName", equalTo(doctor.firstName) ))
                .andExpect(jsonPath("$._embedded.visits[1].doctor.lastName", equalTo(doctor.lastName) ))
                .andExpect(jsonPath("$._embedded.visits[1].doctor.specialization", equalTo(doctor.specialization) ))
                .andExpect(jsonPath("$._embedded.visits[1].patient.firstName", equalTo(patient.firstName) ))
                .andExpect(jsonPath("$._embedded.visits[1].patient.lastName", equalTo(patient.lastName) ))
                .andExpect(jsonPath("$._embedded.visits[1].patient.address", equalTo(patient.address) ))
                .andExpect(jsonPath("$._embedded.visits[1].visitTime", equalTo(secondVisit.visitTime.format(DateTimeFormatter.ofPattern("HH:mm"))) ))
                .andExpect(jsonPath("$._embedded.visits[1].visitDate", equalTo(secondVisit.visitDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))) ))
                .andExpect(jsonPath("$._embedded.visits[1].expectedVisitDuration", equalTo(secondVisit.expectedVisitDuration) ))
                .andReturn()
    }

    @Test
    fun `GET patient visits`() {
        val time = LocalTime.parse("12:00:00")
        val date = LocalDate.parse("1999-03-01")
        val doctor = createDoctor()
        val patient = createPatient()
        val patient2 = createPatient()

        visitRepository!!.save(Visit(doctor, patient, time, date, 50))
        val visit = visitRepository!!.save(Visit(doctor, patient2, time.plusHours(1), date, 50))

        mockMvc!!.perform(get("/visits/patient/${patient2.id}").accept("application/json"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$._embedded.visits[0].doctor.firstName", equalTo(doctor.firstName) ))
                .andExpect(jsonPath("$._embedded.visits[0].doctor.lastName", equalTo(doctor.lastName) ))
                .andExpect(jsonPath("$._embedded.visits[0].doctor.specialization", equalTo(doctor.specialization) ))
                .andExpect(jsonPath("$._embedded.visits[0].patient.firstName", equalTo(patient2.firstName) ))
                .andExpect(jsonPath("$._embedded.visits[0].patient.lastName", equalTo(patient2.lastName) ))
                .andExpect(jsonPath("$._embedded.visits[0].patient.address", equalTo(patient2.address) ))
                .andExpect(jsonPath("$._embedded.visits[0].visitTime", equalTo(visit.visitTime.format(DateTimeFormatter.ofPattern("HH:mm"))) ))
                .andExpect(jsonPath("$._embedded.visits[0].visitDate", equalTo(visit.visitDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))) ))
                .andExpect(jsonPath("$._embedded.visits[0].expectedVisitDuration", equalTo(visit.expectedVisitDuration) ))
    }

    @Test
    fun `POST visit via REST`() {
        val patient = createPatient()
        val doctor = createDoctor()

        val visit = Visit(doctor, patient, LocalTime.parse("12:00:00"), LocalDate.parse("1999-03-01"), 60)

        mockMvc!!.perform(post("/visits").accept("application/json").content(
                """
                    {
                        "doctor": {
                            "id": ${doctor.id}
                        },
                        "patient": {
                            "id": ${patient.id}
                        },
                        "visitTime": "${visit.visitTime.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                        "visitDate": "${visit.visitDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))}",
                        "expectedVisitDuration": ${visit.expectedVisitDuration}
                    }
                """.trimIndent()
        ).contentType("application/json")).andExpect(status().isCreated)
                .andReturn();

        val result = visitRepository!!.findAll().first()

        Assertions.assertEquals(visit.doctor.id, result.doctor.id)
        Assertions.assertEquals(visit.patient.id, result.patient.id)
        Assertions.assertEquals(visit.visitDate, result.visitDate)
        Assertions.assertEquals(visit.visitTime, result.visitTime)
    }

    @Test
    fun `PUT visit via REST - change visit date`() {
        val patient = createPatient()
        val doctor = createDoctor()

        var visit = Visit(doctor, patient, LocalTime.parse("12:00:00"), LocalDate.parse("1999-03-01"), 60)
        visit = visitRepository!!.save(visit)

        mockMvc!!.perform(put("/visits/${visit.id}").accept("application/json").content(
                """
                    {
                        "doctor": {
                            "id": ${doctor.id}
                        },
                        "patient": {
                            "id": ${patient.id}
                        },
                        "visitTime": "${visit.visitTime.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                        "visitDate": "20-03-1999",
                        "expectedVisitDuration": ${visit.expectedVisitDuration}
                    }
                """.trimIndent()
        ).contentType("application/json")).andExpect(status().isOk)
                .andReturn();

        val result = visitRepository!!.findAll().first()

        Assertions.assertEquals(visit.doctor.id, result.doctor.id)
        Assertions.assertEquals(visit.patient.id, result.patient.id)
        Assertions.assertEquals(LocalDate.parse("1999-03-20"), result.visitDate)
        Assertions.assertEquals(visit.visitTime, result.visitTime)
        Assertions.assertEquals(visit.id, result.id)
    }


    @Test
    fun `PUT visit via REST - check visit collision validation`() {
        val patient1 = createPatient()
        val patient2 = createPatient()
        val patient3 = createPatient()
        val doctor = createDoctor()

        visitRepository!!.save(Visit(doctor, patient1, LocalTime.parse("12:00:00"), LocalDate.parse("1999-03-01"), 60))
        val visit2 = visitRepository!!.save(Visit(doctor, patient2, LocalTime.parse("13:00:00"), LocalDate.parse("1999-03-01"), 60))
        visitRepository!!.save(Visit(doctor, patient3, LocalTime.parse("14:20:00"), LocalDate.parse("1999-03-01"), 60))

        mockMvc!!.perform(put("/visits/${visit2.id}").accept("application/json").content(
                """
                    {
                        "doctor": {
                            "id": ${doctor.id}
                        },
                        "patient": {
                            "id": ${patient2.id}
                        },
                        "visitTime": "13:00",
                        "visitDate": "01-03-1999",
                        "expectedVisitDuration": 80
                    }
                """.trimIndent()
        ).contentType("application/json")).andExpect(status().isOk)
                .andReturn();

    }


    @Test
    fun `DELETE visit via REST`() {
        val patient1 = createPatient()
        val patient2 = createPatient()
        val patient3 = createPatient()
        val doctor = createDoctor()

        visitRepository!!.save(Visit(doctor, patient1, LocalTime.parse("12:00:00"), LocalDate.parse("1999-03-01"), 60))
        val visit2 = visitRepository!!.save(Visit(doctor, patient2, LocalTime.parse("13:00:00"), LocalDate.parse("1999-03-01"), 60))
        visitRepository!!.save(Visit(doctor, patient3, LocalTime.parse("14:20:00"), LocalDate.parse("1999-03-01"), 60))


        mockMvc!!.perform(delete("/visits/${visit2.id}").accept("application/json"))
                .andExpect(status().isOk)
                .andReturn();

        Assertions.assertFalse(visitRepository!!.findById(visit2.id!!).isPresent)


    }



}

