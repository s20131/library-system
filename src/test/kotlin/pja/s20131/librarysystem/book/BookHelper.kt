package pja.s20131.librarysystem.book

import com.github.javafaker.Faker
import java.time.LocalDate
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import pja.s20131.librarysystem.adapter.database.resource.BookTable
import pja.s20131.librarysystem.adapter.database.resource.ResourceTable
import pja.s20131.librarysystem.adapter.database.resource.SeriesTable
import pja.s20131.librarysystem.domain.resource.model.Description
import pja.s20131.librarysystem.domain.resource.model.ReleaseDate
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.model.ResourceStatus
import pja.s20131.librarysystem.domain.resource.model.Series
import pja.s20131.librarysystem.domain.resource.model.Title
import pja.s20131.librarysystem.domain.resource.model.Book
import pja.s20131.librarysystem.domain.resource.model.ISBN


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
