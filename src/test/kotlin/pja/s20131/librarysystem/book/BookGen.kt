package pja.s20131.librarysystem.book

import com.github.javafaker.Faker
import pja.s20131.librarysystem.domain.resource.model.Author
import pja.s20131.librarysystem.domain.resource.model.Book
import pja.s20131.librarysystem.domain.resource.model.Description
import pja.s20131.librarysystem.domain.resource.model.ISBN
import pja.s20131.librarysystem.domain.resource.model.ReleaseDate
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.model.ResourceStatus
import pja.s20131.librarysystem.domain.resource.model.Series
import pja.s20131.librarysystem.domain.resource.model.Title
import pja.s20131.librarysystem.resource.ResourceGen
import java.time.LocalDate

object BookGen {
    private val faker = Faker()

    fun book(
        resourceId: ResourceId = ResourceId.generate(),
        title: Title = Title(faker.book().title()),
        author: Author = ResourceGen.author(),
        releaseDate: ReleaseDate = ReleaseDate(LocalDate.now()),
        description: Description? = Description(faker.yoda().quote()),
        series: Series? = Series(faker.book().title()),
        status: ResourceStatus = ResourceStatus.AVAILABLE,
        isbn: ISBN = ISBN(faker.idNumber().valid())
    ) = Book(resourceId, title, author.authorId, releaseDate, description, series, status, isbn)
}
