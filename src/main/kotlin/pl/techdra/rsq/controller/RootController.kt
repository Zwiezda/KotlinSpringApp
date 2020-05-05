package pl.techdra.rsq.controller

import org.springframework.data.domain.PageRequest
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.DummyInvocationUtils.methodOn
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RestControllerAdvice
import pl.techdra.rsq.resource.RootModel


@RestController
@RequestMapping(path = ["/"], produces = ["application/json"])
class RootController {

    @GetMapping("/")
    fun root(): ResponseEntity<RepresentationModel<*>>? {
        val root = RootModel()

        root.add(linkTo(methodOn(RootController::class.java).root()!!).withSelfRel())
        root.add(linkTo(methodOn(DoctorController::class.java)).withRel("doctors"))
        root.add(linkTo(methodOn(PatientController::class.java)).withRel("patients"))
        root.add(linkTo(methodOn(VisitController::class.java)).withRel("visits"))

        return ResponseEntity.ok(root)
    }

}