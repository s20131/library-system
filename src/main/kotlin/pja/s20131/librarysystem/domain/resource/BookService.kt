package pja.s20131.librarysystem.domain.resource

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.domain.resource.model.AuthorBasicData
import pja.s20131.librarysystem.domain.resource.model.AuthorId
import pja.s20131.librarysystem.domain.resource.model.Book
import pja.s20131.librarysystem.domain.resource.model.Description
import pja.s20131.librarysystem.domain.resource.model.ISBN
import pja.s20131.librarysystem.domain.resource.model.ReleaseDate
import pja.s20131.librarysystem.domain.resource.model.ResourceBasicData
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.model.ResourceStatus
import pja.s20131.librarysystem.domain.resource.model.Series
import pja.s20131.librarysystem.domain.resource.model.Title
import pja.s20131.librarysystem.domain.resource.port.AuthorRepository
import pja.s20131.librarysystem.domain.resource.port.BookRepository

@Service
@Transactional
class BookService(
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository,
) {
    fun getAllBooks(): List<ResourceWithAuthorBasicData> {
        val books = bookRepository.getAll()
        val authors = authorRepository.getAll()
        return books.map { book ->
            ResourceWithAuthorBasicData(
                book.toBasicData(),
                authors.first { it.authorId == book.authorId }.toBasicData()
            )
        }
    }

    fun getBook(bookId: ResourceId): Book {
        return bookRepository.get(bookId)
    }

    fun addBook(addBookCommand: AddBookCommand): ResourceId {
        val author = authorRepository.get(addBookCommand.authorId)
        val newBook = addBookCommand.toBook(author.authorId)
        bookRepository.save(newBook)
        return newBook.resourceId
    }
}

data class ResourceWithAuthorBasicData(
    val resource: ResourceBasicData,
    val author: AuthorBasicData,
)

data class AddBookCommand(
    val title: Title,
    // TODO get or create
    val authorId: AuthorId,
    val releaseDate: ReleaseDate,
    val description: Description?,
    val series: Series?,
    val status: ResourceStatus,
    val isbn: ISBN,
) {
    // TODO create in domain class
    fun toBook(authorId: AuthorId) =
        Book(ResourceId.generate(), title, authorId, releaseDate, description, series, status, isbn)
}
