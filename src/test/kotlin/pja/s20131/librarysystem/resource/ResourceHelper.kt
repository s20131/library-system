package pja.s20131.librarysystem.resource

import com.github.javafaker.Faker
import org.jetbrains.exposed.sql.insert
import pja.s20131.librarysystem.adapter.database.resource.AuthorTable
import pja.s20131.librarysystem.adapter.database.resource.ResourceTable
import pja.s20131.librarysystem.domain.person.FirstName
import pja.s20131.librarysystem.domain.person.LastName
import pja.s20131.librarysystem.domain.resource.model.Author
import pja.s20131.librarysystem.domain.resource.model.Resource
import java.util.UUID

val faker = Faker()

fun author(
    firstName: FirstName = FirstName(faker.name().firstName()),
    lastName: LastName = LastName(faker.name().lastName()),
) = Author(firstName, lastName)

fun insertAuthor(authorId: UUID, author: Author) =
    AuthorTable.insert {
        it[id] = authorId
        it[firstName] = author.firstName.value
        it[lastName] = author.lastName.value
    }

fun insertResource(resource: Resource, authorId: UUID) =
    ResourceTable.insert {
        it[id] = resource.resourceId.value
        it[title] = resource.title.value
        it[author] = authorId
        it[releaseDate] = resource.releaseDate.value
        it[description] = resource.description?.value
        it[series] = resource.series?.value
        it[status] = resource.status
    }
