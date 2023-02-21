package pja.s20131.librarysystem.resource

import com.github.javafaker.Faker
import pja.s20131.librarysystem.domain.person.FirstName
import pja.s20131.librarysystem.domain.person.LastName
import pja.s20131.librarysystem.domain.resource.model.Author
import pja.s20131.librarysystem.domain.resource.model.AuthorId

object ResourceGen {
    private val faker = Faker()

    fun author(
        authorId: AuthorId = AuthorId.generate(),
        firstName: FirstName = FirstName(faker.name().firstName()),
        lastName: LastName = LastName(faker.name().lastName()),
    ) = Author(authorId, firstName, lastName)
}
