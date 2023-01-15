package pja.s20131.librarysystem.domain.resource.book

import org.springframework.stereotype.Service

@Service
class BookService(
    val bookRepository: BookRepository
) {

    fun getAllBooks() = bookRepository.getAll()
}
