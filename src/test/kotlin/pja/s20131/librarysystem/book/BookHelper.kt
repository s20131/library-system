package pja.s20131.librarysystem.book

import com.github.javafaker.Faker
import java.time.LocalDate
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import pja.s20131.librarysystem.adapter.database.book.BookTable
import pja.s20131.librarysystem.adapter.database.shared.ResourceTable
import pja.s20131.librarysystem.adapter.database.shared.SeriesTable
import pja.s20131.librarysystem.domain.resource.Description
import pja.s20131.librarysystem.domain.resource.ReleaseDate
import pja.s20131.librarysystem.domain.resource.ResourceId
import pja.s20131.librarysystem.domain.resource.ResourceStatus
import pja.s20131.librarysystem.domain.resource.Series
import pja.s20131.librarysystem.domain.resource.Title
import pja.s20131.librarysystem.domain.resource.book.Book
import pja.s20131.librarysystem.domain.resource.book.ISBN


val faker = Faker()

fun book(
    resourceId: ResourceId = ResourceId.generate(),
    title: Title = Title(faker.book().title()),
    releaseDate: ReleaseDate = ReleaseDate(LocalDate.now()),
    description: Description? = Description(faker.yoda().quote()),
    series: Series? = Series(faker.book().title()),
    resourceStatus: ResourceStatus = ResourceStatus.AVAILABLE,
    isbn: ISBN = ISBN(faker.idNumber().valid())
) = Book(resourceId, title, releaseDate, description, series, resourceStatus, isbn)

fun addBook(book: Book) {
    transaction {
        book.series?.let { series ->
            SeriesTable.insert {
                it[id] = series.raw
            }
        }
        ResourceTable.insert {
            it[id] = book.resourceId.raw
            it[title] = book.title.raw
            it[releaseDate] = book.releaseDate.raw
            it[description] = book.description?.raw
            it[series] = book.series?.raw
            it[resourceStatus] = book.resourceStatus
        }
        BookTable.insert {
            it[id] = book.resourceId.raw
            it[isbn] = book.isbn.raw
        }
    }
}
