package pja.s20131.librarysystem.ebook

import com.github.javafaker.Faker
import pja.s20131.librarysystem.domain.resource.model.Author
import pja.s20131.librarysystem.domain.resource.model.Content
import pja.s20131.librarysystem.domain.resource.model.Description
import pja.s20131.librarysystem.domain.resource.model.Ebook
import pja.s20131.librarysystem.domain.resource.model.EbookFormat
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
        ebookFormat: EbookFormat = EbookFormat.EPUB,
        size: Size = Size(faker.number().randomDouble(2, 0, 800)),
    ) = Ebook(id, title, author, releaseDate, description, series, status, content, ebookFormat, size)
}
