package pja.s20131.librarysystem.domain.resource.port

import pja.s20131.librarysystem.domain.resource.model.Book
import pja.s20131.librarysystem.domain.resource.model.ISBN
import pja.s20131.librarysystem.domain.resource.model.ResourceId

interface BookRepository {
    fun getAllActive(): List<Book>
    fun getActive(bookId: ResourceId): Book
    fun get(isbn: ISBN): Book
    fun save(book: Book)
    fun searchActive(tokens: List<String>): List<Book>
}
