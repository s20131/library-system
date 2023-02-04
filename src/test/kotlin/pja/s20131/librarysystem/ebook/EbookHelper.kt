package pja.s20131.librarysystem.ebook

import com.github.javafaker.Faker
import java.time.LocalDate
import kotlin.random.Random
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction
import pja.s20131.librarysystem.adapter.database.ebook.EbookTable
import pja.s20131.librarysystem.adapter.database.shared.ResourceTable
import pja.s20131.librarysystem.adapter.database.shared.SeriesTable
import pja.s20131.librarysystem.domain.resource.Description
import pja.s20131.librarysystem.domain.resource.ReleaseDate
import pja.s20131.librarysystem.domain.resource.ResourceId
import pja.s20131.librarysystem.domain.resource.ResourceStatus
import pja.s20131.librarysystem.domain.resource.Series
import pja.s20131.librarysystem.domain.resource.Title
import pja.s20131.librarysystem.domain.resource.ebook.Content
import pja.s20131.librarysystem.domain.resource.ebook.EbookFormat
import pja.s20131.librarysystem.domain.resource.ebook.Ebook
import pja.s20131.librarysystem.domain.resource.ebook.Size
import pja.s20131.librarysystem.domain.resource.ebook.SizeUnit

val faker = Faker()

fun ebook(
    id: ResourceId = ResourceId.generate(),
    title: Title = Title(faker.book().title()),
    releaseDate: ReleaseDate = ReleaseDate(LocalDate.now()),
    description: Description? = Description(faker.witcher().quote()),
    series: Series? = Series(faker.elderScrolls().dragon()),
    resourceStatus: ResourceStatus = ResourceStatus.AVAILABLE,
    content: Content = Content(Random.nextBytes(10)),
    ebookFormat: EbookFormat = EbookFormat.EPUB,
    size: Size = Size(faker.number().randomDouble(2, 0, 800)),
) = Ebook(id, title, releaseDate, description, series, resourceStatus, content, ebookFormat, size)

fun addEbook(ebook: Ebook) {
    transaction {
        ebook.series?.let { series ->
            SeriesTable.insert {
                it[id] = series.value
            }
        }
        ResourceTable.insert {
            it[id] = ebook.resourceId.value
            it[title] = ebook.title.value
            it[releaseDate] = ebook.releaseDate.value
            it[description] = ebook.description?.value
            it[series] = ebook.series?.value
            it[resourceStatus] = ebook.resourceStatus
        }
        EbookTable.insert {
            it[id] = ebook.resourceId.value
            it[content] = ExposedBlob(ebook.content.value)
            it[format] = ebook.ebookFormat
            it[size] = ebook.size.value
            it[sizeUnit] = SizeUnit.kB
        }
    }
}
