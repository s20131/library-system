package pja.s20131.librarysystem.domain.resource

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.domain.resource.ResourceWithAuthorBasicData.Companion.withAuthors
import pja.s20131.librarysystem.domain.resource.model.Author
import pja.s20131.librarysystem.domain.resource.model.AuthorBasicData
import pja.s20131.librarysystem.domain.resource.model.AuthorId
import pja.s20131.librarysystem.domain.resource.model.Book
import pja.s20131.librarysystem.domain.resource.model.Description
import pja.s20131.librarysystem.domain.resource.model.ISBN
import pja.s20131.librarysystem.domain.resource.model.ReleaseDate
import pja.s20131.librarysystem.domain.resource.model.Resource
import pja.s20131.librarysystem.domain.resource.model.ResourceBasicData
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.model.SearchQuery
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
    fun getAllActiveBooks(): List<ResourceWithAuthorBasicData> {
        val books = bookRepository.getAllActive()
        val authors = authorRepository.getAll()
        return books.withAuthors(authors)
    }

    fun getActiveBook(bookId: ResourceId): Book {
        return bookRepository.getActive(bookId)
    }

    fun getBook(isbn: ISBN): ResourceWithAuthorBasicData {
        val book = bookRepository.get(isbn)
        val author = authorRepository.get(book.authorId)
        return ResourceWithAuthorBasicData(book.toBasicData(), author.toBasicData())
    }

    fun addBook(dto: AddBookDto): ResourceId {
        checkIfAuthorExists(dto.authorId)
        val newBook = Book.from(dto)
        bookRepository.save(newBook)
        return newBook.resourceId
    }

    fun search(query: SearchQuery): List<ResourceWithAuthorBasicData> {
        val tokens = query.tokenize()
        val books = bookRepository.searchActive(tokens)
        val authors = authorRepository.getAll()
        return books.withAuthors(authors)
    }

    private fun checkIfAuthorExists(authorId: AuthorId) {
        authorRepository.get(authorId)
    }
}

data class ResourceWithAuthorBasicData(
    val resource: ResourceBasicData,
    val author: AuthorBasicData,
) {
    companion object {
        fun List<Resource>.withAuthors(authors: List<Author>): List<ResourceWithAuthorBasicData> {
            return map { book ->
                ResourceWithAuthorBasicData(
                    book.toBasicData(),
                    authors.first { it.authorId == book.authorId }.toBasicData()
                )
            }
        }
    }
}

data class AddBookDto(
    val title: Title,
    val authorId: AuthorId,
    val releaseDate: ReleaseDate,
    val description: Description?,
    val series: Series?,
    val isbn: ISBN,
)
