package pja.s20131.librarysystem.domain.resource.port

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BookService(
    val bookRepository: BookRepository
) {

    fun getAllBooks() = bookRepository.getAll()
}
