package pja.s20131.librarysystem.domain.resource.port

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.domain.resource.model.Author
import pja.s20131.librarysystem.domain.resource.model.AuthorId
import pja.s20131.librarysystem.domain.resource.model.Book
import pja.s20131.librarysystem.domain.resource.model.Description
import pja.s20131.librarysystem.domain.resource.model.ISBN
import pja.s20131.librarysystem.domain.resource.model.ReleaseDate
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.model.ResourceStatus
import pja.s20131.librarysystem.domain.resource.model.Series
import pja.s20131.librarysystem.domain.resource.model.Title

@Service
@Transactional
class BookService(
    val bookRepository: BookRepository,
    val authorRepository: AuthorRepository,
) {
    fun getAllBooks() = bookRepository.getAll()

    fun addBook(addBookCommand: AddBookCommand): ResourceId {
        val author = authorRepository.get(addBookCommand.authorId)
        val newBook = addBookCommand.toBook(author)
        bookRepository.insert(newBook)
        return newBook.resourceId
    }
}

data class AddBookCommand(
    val title: Title,
    val authorId: AuthorId,
    val releaseDate: ReleaseDate,
    val description: Description?,
    val series: Series?,
    val status: ResourceStatus,
    val isbn: ISBN,
) {
    fun toBook(author: Author) = Book(ResourceId.generate(), title, author, releaseDate, description, series, status, isbn)
}
