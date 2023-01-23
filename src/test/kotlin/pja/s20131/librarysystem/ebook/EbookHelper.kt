package pja.s20131.librarysystem.ebook

import com.github.javafaker.Faker
import java.time.LocalDate
import kotlin.random.Random
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction
import pja.s20131.librarysystem.adapter.database.EbookTable
import pja.s20131.librarysystem.adapter.database.shared.ResourceTable
import pja.s20131.librarysystem.adapter.database.shared.SeriesTable
import pja.s20131.librarysystem.domain.resource.Description
import pja.s20131.librarysystem.domain.resource.ReleaseDate
import pja.s20131.librarysystem.domain.resource.ResourceId
import pja.s20131.librarysystem.domain.resource.ResourceStatus
import pja.s20131.librarysystem.domain.resource.Series
import pja.s20131.librarysystem.domain.resource.Title
import pja.s20131.librarysystem.domain.resource.ebook.Content
//import pja.s20131.librarysystem.domain.resource.ebook.ContentType
import pja.s20131.librarysystem.domain.resource.ebook.Ebook
import pja.s20131.librarysystem.domain.resource.ebook.Size

val faker = Faker()

fun ebook(
    id: ResourceId = ResourceId.generate(),
    title: Title = Title(faker.book().title()),
    releaseDate: ReleaseDate = ReleaseDate(LocalDate.now()),
    description: Description? = Description(faker.witcher().quote()),
    series: Series? = Series(faker.elderScrolls().dragon()),
    resourceStatus: ResourceStatus = ResourceStatus.AVAILABLE,
    content: Content = Content(Random.nextBytes(10)),
    //contentType: ContentType = ContentType.EPUB,
    size: Size = Size(faker.number().randomDouble(2, 0, 800)),
) = Ebook(id, title, releaseDate, description, series, resourceStatus, content, /*contentType,*/ size)

fun addEbook(ebook: Ebook) {
    transaction {
        ebook.series?.let { series ->
            SeriesTable.insert {
                it[id] = series.raw
            }
        }
        ResourceTable.insert {
            it[id] = ebook.resourceId.raw
            it[title] = ebook.title.raw
            it[releaseDate] = ebook.releaseDate.raw
            it[description] = ebook.description?.raw
            it[series] = ebook.series?.raw
            it[resourceStatus] = ebook.resourceStatus
        }
        EbookTable.insert {
            it[id] = ebook.resourceId.raw
            it[content] = ExposedBlob(ebook.content.raw)
            //it[contentType] = ebook.contentType
            it[size] = ebook.size.raw
        }
    }
}
