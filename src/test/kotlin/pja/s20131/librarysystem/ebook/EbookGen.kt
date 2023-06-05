package pja.s20131.librarysystem.ebook

import net.datafaker.Faker
import pja.s20131.librarysystem.domain.resource.AddEbookDto
import pja.s20131.librarysystem.domain.resource.model.Author
import pja.s20131.librarysystem.domain.resource.model.AuthorId
import pja.s20131.librarysystem.domain.resource.model.Description
import pja.s20131.librarysystem.domain.resource.model.Ebook
import pja.s20131.librarysystem.domain.resource.model.EbookContent
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
        series: Series? = null,
        status: ResourceStatus = ResourceStatus.AVAILABLE,
        content: EbookContent = EbookContent(Random.nextBytes(10), Format.EPUB),
        size: Size = Size(faker.number().randomDigit()),
    ) = Ebook(id, title, author.authorId, releaseDate, description, series, status, content, size)

    fun addEbookDto(
        title: Title = Title(faker.book().title()),
        authorId: AuthorId = AuthorId.generate(),
        releaseDate: ReleaseDate = ReleaseDate(LocalDate.now()),
        description: Description? = Description(faker.witcher().quote()),
        series: Series? = null,
        status: ResourceStatus = ResourceStatus.AVAILABLE,
        content: ByteArray = Random.nextBytes(10),
        format: Format = Format.PDF,
        size: Size = Size(faker.number().randomDigit()),
    ) = AddEbookDto(title, authorId, releaseDate, description, series, status, content, format, size)
}
