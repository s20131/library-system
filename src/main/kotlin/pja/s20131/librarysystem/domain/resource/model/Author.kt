package pja.s20131.librarysystem.domain.resource.model

data class Author(
    val firstName: FirstName,
    val lastName: LastName,
)

@JvmInline
value class FirstName(val value: String)

@JvmInline
value class LastName(val value: String)
