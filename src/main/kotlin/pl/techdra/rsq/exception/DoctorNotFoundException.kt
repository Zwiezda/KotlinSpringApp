package pl.techdra.rsq.exception

class DoctorNotFoundException(id: Long) : RuntimeException("Could not find doctor: $id")