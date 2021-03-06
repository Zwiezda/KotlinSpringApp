package pl.techdra.rsq.exception

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


@RestControllerAdvice
class ErrorHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(DoctorNotFoundException::class, PatientNotFoundException::class, VisitNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFoundException(e: RuntimeException): ErrorResponseEntity {
        return ErrorResponseEntity(e.message!!, 404)
    }

    @ExceptionHandler(ChangePatientException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handlePatientChangeInExistingVisit(e: ChangePatientException) : ErrorResponseEntity {
        return ErrorResponseEntity(e.message!!, 400)
    }

    override fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException, headers: HttpHeaders, status: HttpStatus, request: WebRequest): ResponseEntity<Any> {
        val result = ex.bindingResult
        val errors = result.allErrors

        return ResponseEntity(ErrorResponseEntity(errors.joinToString {"${it.objectName} - ${it.defaultMessage}"}, 400), HttpStatus.BAD_REQUEST)
    }

}