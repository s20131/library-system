package pja.s20131.librarysystem.resource

import net.datafaker.Faker
import org.springframework.http.MediaType
import pja.s20131.librarysystem.domain.person.FirstName
import pja.s20131.librarysystem.domain.person.LastName
import pja.s20131.librarysystem.domain.resource.AddAuthorDto
import pja.s20131.librarysystem.domain.resource.model.Author
import pja.s20131.librarysystem.domain.resource.model.AuthorId
import pja.s20131.librarysystem.domain.resource.model.ResourceCover
import pja.s20131.librarysystem.domain.resource.model.Series
import kotlin.random.Random

object ResourceGen {
    private val faker = Faker()

    val defaultSeries = Series("series")

    fun author(
        authorId: AuthorId = AuthorId.generate(),
        firstName: FirstName = FirstName(faker.name().firstName()),
        lastName: LastName = LastName(faker.name().lastName()),
    ) = Author(authorId, firstName, lastName)

    fun addAuthorDto(
        firstName: FirstName = FirstName(faker.name().firstName()),
        lastName: LastName = LastName(faker.name().lastName()),
    ) = AddAuthorDto(firstName, lastName)

    fun cover() = ResourceCover(Random.nextBytes(10), MediaType.IMAGE_JPEG)
}
