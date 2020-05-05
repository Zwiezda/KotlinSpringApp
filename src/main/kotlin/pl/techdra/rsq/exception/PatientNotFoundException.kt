package pl.techdra.rsq.exception

class PatientNotFoundException(id: Long) : RuntimeException("Could not find patient: $id")