package pl.techdra.rsq.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import net.minidev.json.annotate.JsonIgnore
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
class Patient(
        @field:NotBlank(message = "firstName can't be empty!")
        var firstName: String?,

        @field:NotBlank(message = "lastName can't be empty!")
        var lastName: String?,

        @field:NotBlank(message = "address can't be empty!")
        var address: String?,

        @field:OneToMany(mappedBy = "patient", cascade = [CascadeType.REMOVE])
        @field:JsonIgnore
        var visits: MutableList<Visit> = ArrayList<Visit>(),

        @field:Id
        @field:GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long? = null
)
