package pja.s20131.librarysystem.ebook

import com.github.javafaker.Faker
import java.time.LocalDate
import java.util.UUID
import kotlin.random.Random
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction
import pja.s20131.librarysystem.adapter.database.resource.EbookTable
import pja.s20131.librarysystem.adapter.database.resource.SeriesTable
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
import pja.s20131.librarysystem.domain.resource.model.SizeUnit
import pja.s20131.librarysystem.domain.resource.model.Title
import pja.s20131.librarysystem.resource.author
import pja.s20131.librarysystem.resource.insertAuthor
import pja.s20131.librarysystem.resource.insertResource

val faker = Faker()

fun ebook(
    id: ResourceId = ResourceId.generate(),
    title: Title = Title(faker.book().title()),
    author: Author = author(),
    releaseDate: ReleaseDate = ReleaseDate(LocalDate.now()),
    description: Description? = Description(faker.witcher().quote()),
    series: Series? = Series(faker.elderScrolls().dragon()),
    status: ResourceStatus = ResourceStatus.AVAILABLE,
    content: Content = Content(Random.nextBytes(10)),
    ebookFormat: EbookFormat = EbookFormat.EPUB,
    size: Size = Size(faker.number().randomDouble(2, 0, 800)),
) = Ebook(id, title, author, releaseDate, description, series, status, content, ebookFormat, size)

fun insertEbook(ebook: Ebook) {
    val authorId = UUID.randomUUID()
    // TODO replace with repo call
    transaction {
        ebook.series?.let { series ->
            SeriesTable.insert {
                it[id] = series.value
            }
        }
        insertAuthor(authorId, ebook.author)
        insertResource(ebook, authorId)
        EbookTable.insert {
            it[id] = ebook.resourceId.value
            it[content] = ExposedBlob(ebook.content.value)
            it[format] = ebook.ebookFormat
            it[size] = ebook.size.value
            it[sizeUnit] = SizeUnit.kB
        }
    }
}
