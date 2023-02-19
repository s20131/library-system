package pja.s20131.librarysystem.book

import com.github.javafaker.Faker
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import pja.s20131.librarysystem.adapter.database.resource.BookTable
import pja.s20131.librarysystem.adapter.database.resource.SeriesTable
import pja.s20131.librarysystem.domain.resource.model.Author
import pja.s20131.librarysystem.domain.resource.model.Book
import pja.s20131.librarysystem.domain.resource.model.Description
import pja.s20131.librarysystem.domain.resource.model.ISBN
import pja.s20131.librarysystem.domain.resource.model.ReleaseDate
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.model.ResourceStatus
import pja.s20131.librarysystem.domain.resource.model.Series
import pja.s20131.librarysystem.domain.resource.model.Title
import pja.s20131.librarysystem.resource.author
import pja.s20131.librarysystem.resource.insertAuthor
import pja.s20131.librarysystem.resource.insertResource
import java.time.LocalDate
import java.util.UUID


val faker = Faker()

fun book(
    resourceId: ResourceId = ResourceId.generate(),
    title: Title = Title(faker.book().title()),
    author: Author = author(),
    releaseDate: ReleaseDate = ReleaseDate(LocalDate.now()),
    description: Description? = Description(faker.yoda().quote()),
    series: Series? = Series(faker.book().title()),
    status: ResourceStatus = ResourceStatus.AVAILABLE,
    isbn: ISBN = ISBN(faker.idNumber().valid())
) = Book(resourceId, title, author, releaseDate, description, series, status, isbn)

fun insertBook(book: Book) {
    val authorId = UUID.randomUUID()
    // TODO change to repo call
    transaction {
        book.series?.let { series ->
            SeriesTable.insert {
                it[id] = series.value
            }
        }
        insertAuthor(authorId, book.author)
        insertResource(book, authorId)
        BookTable.insert {
            it[id] = book.resourceId.value
            it[isbn] = book.isbn.value
        }
    }
}
