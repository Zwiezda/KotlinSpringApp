package pl.techdra.rsq.exception

class VisitNotFoundException(id: Long) : RuntimeException("Could not find visit: $id")