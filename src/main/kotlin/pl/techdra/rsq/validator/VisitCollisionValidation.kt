package pl.techdra.rsq.validator

import pl.techdra.rsq.domain.Visit
import pl.techdra.rsq.repository.VisitRepository
import pl.techdra.rsq.service.BeanProvider
import javax.persistence.EntityManager
import javax.persistence.FlushModeType
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class VisitCollisionValidation
    : ConstraintValidator<VisitCollisionCheck, Visit> {

    private var visitRepository: VisitRepository? = null
    private var entytyManager: EntityManager? = null

    override fun initialize(constraintAnnotation: VisitCollisionCheck?) {
        visitRepository = BeanProvider.getBean(VisitRepository::class.java)
        entytyManager = BeanProvider.getBean(EntityManager::class.java)
    }

    override fun isValid(value: Visit?, context: ConstraintValidatorContext?): Boolean {
        try {
            entytyManager!!.flushMode = FlushModeType.COMMIT

            if (value == null) return false

            val startOfVisit = value.visitDate.atTime(value.visitTime)
            val endOfVisit = startOfVisit.plusMinutes(value.expectedVisitDuration.toLong())

            val nextVisit = visitRepository?.getLastVisitBeforeDate(value.doctor, endOfVisit.toLocalDate(), endOfVisit.toLocalTime())!!.firstOrNull()
            val beforeVisit = visitRepository?.getLastVisitBeforeDate(value.doctor, value.visitDate, value.visitTime)!!.firstOrNull()
            if (beforeVisit != null) {
                val endOfBeforeVisit = beforeVisit.visitDate.atTime(beforeVisit.visitTime).plusMinutes(beforeVisit.expectedVisitDuration.toLong())
                if (beforeVisit.patient.id != value.patient.id && endOfBeforeVisit.isAfter(startOfVisit)) {
                    return false
                }
            }

            if (nextVisit != null) {
                val startOfNextVisit = nextVisit.visitDate.atTime(nextVisit.visitTime)
                if (nextVisit.id != beforeVisit!!.id && nextVisit.patient.id != value.patient.id && endOfVisit.isAfter(startOfNextVisit)) {
                    return false
                }
            }

            return true
        } finally {
            entytyManager!!.flushMode = FlushModeType.AUTO
        }


    }
}

