package pl.techdra.rsq.validator

import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass
/*
 * Annotation prepared for checking collisions in visits
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [VisitCollisionValidation::class])
annotation class VisitCollisionCheck(
        val message: String = "Visit conflicts with another",
        val groups: Array<KClass<*>> = [],
        val payload: Array<KClass<out Payload>> = [])
