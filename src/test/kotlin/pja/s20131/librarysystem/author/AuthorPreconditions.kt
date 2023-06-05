package pja.s20131.librarysystem.author

import net.datafaker.Faker
import org.springframework.stereotype.Component
import pja.s20131.librarysystem.book.BookDatabaseHelper
import pja.s20131.librarysystem.domain.person.FirstName
import pja.s20131.librarysystem.domain.person.LastName
import pja.s20131.librarysystem.domain.resource.model.Author
import pja.s20131.librarysystem.domain.resource.model.Book
import pja.s20131.librarysystem.domain.resource.model.Description
import pja.s20131.librarysystem.domain.resource.model.Ebook
import pja.s20131.librarysystem.domain.resource.model.EbookContent
import pja.s20131.librarysystem.domain.resource.model.Format
import pja.s20131.librarysystem.domain.resource.model.ISBN
import pja.s20131.librarysystem.domain.resource.model.ReleaseDate
import pja.s20131.librarysystem.domain.resource.model.ResourceStatus
import pja.s20131.librarysystem.domain.resource.model.Series
import pja.s20131.librarysystem.domain.resource.model.Size
import pja.s20131.librarysystem.domain.resource.model.Title
import pja.s20131.librarysystem.ebook.EbookDatabaseHelper
import pja.s20131.librarysystem.series.SeriesDatabaseHelper
import java.time.LocalDate
import kotlin.random.Random

@Component
class AuthorPreconditions(
    private val authorDatabaseHelper: AuthorDatabaseHelper,
    private val seriesDatabaseHelper: SeriesDatabaseHelper,
    private val bookDatabaseHelper: BookDatabaseHelper,
    private val ebookDatabaseHelper: EbookDatabaseHelper,
) {
    private val faker = Faker()

    private lateinit var author: Author

    fun exists(
        firstName: FirstName = FirstName(faker.name().firstName()),
        lastName: LastName = LastName(faker.name().lastName()),
    ): AuthorPreconditions {
        author = Author.from(firstName, lastName)
        authorDatabaseHelper.insertAuthor(author)
        return this
    }

    fun withBook(
        title: Title = Title(faker.book().title()),
        releaseDate: ReleaseDate = ReleaseDate(LocalDate.now()),
        description: Description? = Description(faker.familyGuy().quote()),
        series: Series? = null,
        status: ResourceStatus = ResourceStatus.AVAILABLE,
        isbn: ISBN = ISBN(faker.idNumber().valid()),
    ): AuthorPreconditions {
        val book = Book.from(title, author.authorId, releaseDate, description, series, status, isbn)
        seriesDatabaseHelper.insertSeries(series)
        bookDatabaseHelper.insertBook(book)
        return this
    }

    fun withEbook(
        title: Title = Title(faker.book().title()),
        releaseDate: ReleaseDate = ReleaseDate(LocalDate.now()),
        description: Description? = Description(faker.familyGuy().quote()),
        series: Series? = null,
        status: ResourceStatus = ResourceStatus.AVAILABLE,
        content: EbookContent = EbookContent(Random.nextBytes(10), Format.PDF),
        size: Size = Size(faker.number().randomDigit()),
    ): AuthorPreconditions {
        val ebook = Ebook.from(title, author.authorId, releaseDate, description, series, status, content, size)
        seriesDatabaseHelper.insertSeries(series)
        ebookDatabaseHelper.insertEbook(ebook)
        return this
    }

    fun build(): Triple<Author, List<Book>, List<Ebook>> {
        val books = bookDatabaseHelper.getBy(author.authorId)
        val ebooks = ebookDatabaseHelper.getBy(author.authorId)
        return Triple(author, books, ebooks)
    }
}