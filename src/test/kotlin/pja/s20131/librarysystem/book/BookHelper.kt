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
    status: ResourceStatus = ResourceStatus.AVAILABLE,
    isbn: ISBN = ISBN(faker.idNumber().valid())
) = Book(resourceId, title, releaseDate, description, series, status, isbn)

fun addBook(book: Book) {
    transaction {
        book.series?.let { series ->
            SeriesTable.insert {
                it[id] = series.value
            }
        }
        ResourceTable.insert {
            it[id] = book.resourceId.value
            it[title] = book.title.value
            it[releaseDate] = book.releaseDate.value
            it[description] = book.description?.value
            it[series] = book.series?.value
            it[status] = book.status
        }
        BookTable.insert {
            it[id] = book.resourceId.value
            it[isbn] = book.isbn.value
        }
    }
}
