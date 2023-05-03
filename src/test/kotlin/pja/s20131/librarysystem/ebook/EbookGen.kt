package pja.s20131.librarysystem.ebook

import net.datafaker.Faker
import pja.s20131.librarysystem.domain.resource.AddEbookCommand
import pja.s20131.librarysystem.domain.resource.model.Author
import pja.s20131.librarysystem.domain.resource.model.AuthorId
import pja.s20131.librarysystem.domain.resource.model.Content
import pja.s20131.librarysystem.domain.resource.model.Description
import pja.s20131.librarysystem.domain.resource.model.Ebook
import pja.s20131.librarysystem.domain.resource.model.Format
import pja.s20131.librarysystem.domain.resource.model.ReleaseDate
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.model.ResourceStatus
import pja.s20131.librarysystem.domain.resource.model.Series
import pja.s20131.librarysystem.domain.resource.model.Size
import pja.s20131.librarysystem.domain.resource.model.Title
import pja.s20131.librarysystem.resource.ResourceGen
import java.time.LocalDate
import kotlin.random.Random

object EbookGen {
    private val faker = Faker()

    fun ebook(
        id: ResourceId = ResourceId.generate(),
        title: Title = Title(faker.book().title()),
        author: Author = ResourceGen.author(),
        releaseDate: ReleaseDate = ReleaseDate(LocalDate.now()),
        description: Description? = Description(faker.witcher().quote()),
        series: Series? = Series(faker.elderScrolls().dragon()),
        status: ResourceStatus = ResourceStatus.AVAILABLE,
        content: Content = Content(Random.nextBytes(10)),
        format: Format = Format.EPUB,
        size: Size = Size(faker.number().randomDouble(2, 0, 800)),
    ) = Ebook(id, title, author.authorId, releaseDate, description, series, status, content, format, size)

    fun addEbookCommand(
        title: Title = Title(faker.book().title()),
        authorId: AuthorId = AuthorId.generate(),
        releaseDate: ReleaseDate = ReleaseDate(LocalDate.now()),
        description: Description? = Description(faker.witcher().quote()),
        series: Series? = Series(faker.elderScrolls().dragon()),
        status: ResourceStatus = ResourceStatus.AVAILABLE,
        content: Content = Content(Random.nextBytes(10)),
        format: Format = Format.PDF,
        size: Size = Size(faker.number().randomDouble(2, 0, 800)),
    ) = AddEbookCommand(title, authorId, releaseDate, description, series, status, content, format, size)
}
