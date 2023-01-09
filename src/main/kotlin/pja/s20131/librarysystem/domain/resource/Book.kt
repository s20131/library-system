package pja.s20131.librarysystem.domain.resource

data class Book (val resource: Resource, val isbn: ISBN)

@JvmInline
value class ISBN(val raw: String)
