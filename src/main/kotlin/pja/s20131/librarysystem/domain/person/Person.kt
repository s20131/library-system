package pja.s20131.librarysystem.domain.person

interface Person {
    val firstName: FirstName
    val lastName: LastName
}

@JvmInline
value class FirstName(val value: String)

@JvmInline
value class LastName(val value: String)
