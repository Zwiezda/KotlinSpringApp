package pl.techdra.rsq.exception

import java.lang.RuntimeException

class ChangePatientException : RuntimeException("Can't change patient in existing visit!") {
}