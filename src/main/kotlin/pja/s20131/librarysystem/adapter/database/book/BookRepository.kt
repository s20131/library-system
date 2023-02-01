package pja.s20131.librarysystem.adapter.database.book

import pja.s20131.librarysystem.domain.resource.book.Book

interface BookRepository {
    fun getAll(): List<Book>
}
