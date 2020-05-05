package pl.techdra.rsq.exception

import java.time.LocalDateTime

class ErrorResponseEntity(val errorMessage: String, val statusCode: Int, val errorDate: LocalDateTime = LocalDateTime.now())