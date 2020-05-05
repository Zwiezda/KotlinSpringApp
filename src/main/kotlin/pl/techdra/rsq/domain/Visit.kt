package pl.techdra.rsq.domain

import com.fasterxml.jackson.annotation.JsonFormat
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import pl.techdra.rsq.validator.VisitCollisionCheck
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import javax.persistence.*
import javax.validation.constraints.Max
import javax.validation.constraints.Min

@Entity
@VisitCollisionCheck(message = "Another visit is already registered")
class Visit (
        @field:ManyToOne
        var doctor: Doctor,

        @field:ManyToOne
        var patient: Patient,

        @field:Column(columnDefinition = "TIME")
        @field:JsonFormat(pattern = "HH:mm")
        var visitTime: LocalTime,

        @field:Column(columnDefinition = "DATE")
        @field:JsonFormat(pattern = "dd-MM-yyyy")
        var visitDate: LocalDate,

        @field:Min(10, message = "Visit can not be shorter than 10 minutes")
        @field:Max(180, message= "Visit can not be longer than 180 minutes")
        var expectedVisitDuration: Int = 60,

        @field:Id @field:GeneratedValue(strategy = GenerationType.AUTO) var id: Long? = null
)