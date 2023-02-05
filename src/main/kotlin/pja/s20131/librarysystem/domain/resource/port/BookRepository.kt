package pja.s20131.librarysystem.domain.resource.port

import pja.s20131.librarysystem.domain.resource.model.Book

interface BookRepository {
    fun getAll(): List<Book>
}
