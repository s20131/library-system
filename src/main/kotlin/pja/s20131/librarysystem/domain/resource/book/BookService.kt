package pja.s20131.librarysystem.domain.resource.book

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.adapter.database.book.BookRepository

@Service
@Transactional
class BookService(
    val bookRepository: BookRepository
) {

    fun getAllBooks() = bookRepository.getAll()
}
