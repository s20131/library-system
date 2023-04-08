package pja.s20131.librarysystem.book

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.adapter.database.resource.BookTable
import pja.s20131.librarysystem.adapter.database.resource.ResourceTable
import pja.s20131.librarysystem.domain.resource.model.AuthorId
import pja.s20131.librarysystem.domain.resource.model.Book
import pja.s20131.librarysystem.domain.resource.model.Description
import pja.s20131.librarysystem.domain.resource.model.ISBN
import pja.s20131.librarysystem.domain.resource.model.ReleaseDate
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.model.Series
import pja.s20131.librarysystem.domain.resource.model.Title
import pja.s20131.librarysystem.resource.ResourceDatabaseHelper
import pja.s20131.librarysystem.resource.ResourceGen

@Component
@Transactional
class BookDatabaseHelper @Autowired constructor(
    val resourceDatabaseHelper: ResourceDatabaseHelper,
) {
    fun getAllBooks() =
        BookTable
            .innerJoin(ResourceTable)
            .selectAll()
            .map { it.toBook() }

    fun insertBook(book: Book) {
        book.series?.let { series -> resourceDatabaseHelper.insertSeries(series) }
        resourceDatabaseHelper.insertAuthor(ResourceGen.author(book.authorId))
        resourceDatabaseHelper.insertResource(book)
        BookTable.insert {
            it[id] = book.resourceId.value
            it[isbn] = book.isbn.value
        }
    }

    private fun ResultRow.toBook() = Book(
        resourceId = ResourceId(this[ResourceTable.id].value),
        title = Title(this[ResourceTable.title]),
        releaseDate = ReleaseDate(this[ResourceTable.releaseDate]),
        description = this[ResourceTable.description]?.let { Description(it) },
        series = this[ResourceTable.series]?.let { Series(it) },
        status = this[ResourceTable.status],
        isbn = ISBN(this[BookTable.isbn]),
        authorId = AuthorId(this[ResourceTable.author]),
    )

}
