package pl.techdra.rsq.domain

import net.minidev.json.annotate.JsonIgnore
import javax.persistence.*
import javax.validation.constraints.NotBlank


@Entity
class Doctor(
        @field:NotBlank(message = "firstName can't be empty!")
        var firstName: String?,
        @field:NotBlank(message = "lastName can't be empty!")
        var lastName: String?,
        @field:NotBlank(message = "specialization can't be empty!")
        var specialization: String?,

        @field:OneToMany(mappedBy = "doctor", cascade = [CascadeType.REMOVE])
        @field:JsonIgnore
        var visits: MutableList<Visit> = ArrayList<Visit>(),

        @field:Id
        @field:GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long? = null
)


