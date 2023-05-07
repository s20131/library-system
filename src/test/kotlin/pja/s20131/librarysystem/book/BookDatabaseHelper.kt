package pja.s20131.librarysystem.book

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.adapter.database.resource.BookTable
import pja.s20131.librarysystem.adapter.database.resource.BookTable.toBook
import pja.s20131.librarysystem.adapter.database.resource.ResourceTable
import pja.s20131.librarysystem.domain.resource.model.AuthorId
import pja.s20131.librarysystem.domain.resource.model.Book
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.resource.ResourceDatabaseHelper

@Component
@Transactional
class BookDatabaseHelper @Autowired constructor(
    val resourceDatabaseHelper: ResourceDatabaseHelper,
) {

    fun insertBook(book: Book) {
        resourceDatabaseHelper.insertResource(book)
        BookTable.insert {
            it[id] = book.resourceId.value
            it[isbn] = book.isbn.value
        }
    }

    fun assertBookIsSaved(bookId: ResourceId) {
        val book = BookTable
            .innerJoin(ResourceTable)
            .select { BookTable.id eq bookId.value }
            .singleOrNull()
            ?.toBook()

        assertThat(book).isNotNull
        assertThat(book?.resourceId).isEqualTo(bookId)
    }

    fun getBy(authorId: AuthorId): List<Book> {
        return BookTable
            .innerJoin(ResourceTable)
            .select { ResourceTable.author eq authorId.value }
            .map { it.toBook() }
    }

}
