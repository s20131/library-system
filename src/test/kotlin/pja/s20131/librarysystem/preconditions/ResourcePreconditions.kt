package pja.s20131.librarysystem.preconditions

import net.datafaker.Faker
import org.springframework.stereotype.Component
import pja.s20131.librarysystem.book.BookDatabaseHelper
import pja.s20131.librarysystem.domain.person.FirstName
import pja.s20131.librarysystem.domain.person.LastName
import pja.s20131.librarysystem.domain.resource.model.Author
import pja.s20131.librarysystem.domain.resource.model.Book
import pja.s20131.librarysystem.domain.resource.model.Content
import pja.s20131.librarysystem.domain.resource.model.Description
import pja.s20131.librarysystem.domain.resource.model.Ebook
import pja.s20131.librarysystem.domain.resource.model.Format
import pja.s20131.librarysystem.domain.resource.model.ISBN
import pja.s20131.librarysystem.domain.resource.model.ReleaseDate
import pja.s20131.librarysystem.domain.resource.model.ResourceStatus
import pja.s20131.librarysystem.domain.resource.model.Series
import pja.s20131.librarysystem.domain.resource.model.Size
import pja.s20131.librarysystem.domain.resource.model.Title
import pja.s20131.librarysystem.ebook.EbookDatabaseHelper
import pja.s20131.librarysystem.resource.AuthorDatabaseHelper
import pja.s20131.librarysystem.resource.SeriesDatabaseHelper
import java.time.LocalDate
import kotlin.random.Random

@Component
class ResourcePreconditions(
    private val bookDatabaseHelper: BookDatabaseHelper,
    private val ebookDatabaseHelper: EbookDatabaseHelper,
    private val authorDatabaseHelper: AuthorDatabaseHelper,
    private val seriesDatabaseHelper: SeriesDatabaseHelper,
) {
    private val faker = Faker()

    fun authorExists(
        firstName: FirstName = FirstName(faker.name().firstName()),
        lastName: LastName = LastName(faker.name().lastName()),
    ): AuthorPreconditions {
        val author = Author.from(firstName, lastName)
        authorDatabaseHelper.insertAuthor(author)
        return AuthorPreconditions(author)
    }

    fun seriesExists(series: Series = Series(faker.familyGuy().location())): Series {
        return series.also { insertSeries(series) }
    }

    private fun insertSeries(series: Series?) {
        if (series != null) {
            seriesDatabaseHelper.insertSeriesWithoutConflict(series)
        }
    }

    inner class AuthorPreconditions(
        private val author: Author
    ) {

        fun withBook(
            title: Title = Title(faker.book().title()),
            releaseDate: ReleaseDate = ReleaseDate(LocalDate.now()),
            description: Description? = Description(faker.familyGuy().quote()),
            series: Series? = null,
            status: ResourceStatus = ResourceStatus.AVAILABLE,
            isbn: ISBN = ISBN(faker.idNumber().valid()),
        ): AuthorPreconditions {
            val book = Book.from(title, author.authorId, releaseDate, description, series, status, isbn)
            insertSeries(series)
            bookDatabaseHelper.insertBook(book)
            return this
        }

        fun withEbook(
            title: Title = Title(faker.book().title()),
            releaseDate: ReleaseDate = ReleaseDate(LocalDate.now()),
            description: Description? = Description(faker.familyGuy().quote()),
            series: Series? = null,
            status: ResourceStatus = ResourceStatus.AVAILABLE,
            content: Content = Content(Random.nextBytes(10)),
            format: Format = Format.MOBI,
            size: Size = Size(faker.number().randomDouble(2, 0, 800)),
        ): AuthorPreconditions {
            val ebook = Ebook.from(title, author.authorId, releaseDate, description, series, status, content, format, size)
            insertSeries(series)
            ebookDatabaseHelper.insertEbook(ebook)
            return this
        }

        fun build(): Triple<Author, List<Book>, List<Ebook>> {
            val books = bookDatabaseHelper.getBy(author.authorId)
            val ebooks = ebookDatabaseHelper.getBy(author.authorId)
            return Triple(author, books, ebooks)
        }
    }
}
