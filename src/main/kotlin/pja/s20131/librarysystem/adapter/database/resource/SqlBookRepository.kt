package pja.s20131.librarysystem.adapter.database.resource

import java.util.UUID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import pja.s20131.librarysystem.adapter.database.resource.BookTable.toBook
import pja.s20131.librarysystem.adapter.exceptions.NotFoundException
import pja.s20131.librarysystem.domain.resource.model.AuthorId
import pja.s20131.librarysystem.domain.resource.model.Book
import pja.s20131.librarysystem.domain.resource.model.Description
import pja.s20131.librarysystem.domain.resource.model.ISBN
import pja.s20131.librarysystem.domain.resource.model.ReleaseDate
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.model.Series
import pja.s20131.librarysystem.domain.resource.model.Title
import pja.s20131.librarysystem.domain.resource.port.BookRepository

@Repository
class SqlBookRepository : BookRepository {

    override fun getAll(): List<Book> =
        BookTable
            .innerJoin(ResourceTable)
            .selectAll()
            .map { it.toBook() }

    override fun get(bookId: ResourceId): Book =
        BookTable
            .innerJoin(ResourceTable)
            .select { BookTable.id eq bookId.value }
            .singleOrNull()
            ?.toBook() ?: throw BookNotFoundException(bookId)

    override fun save(book: Book) {
        ResourceTable.insert {
            it.from(book)
        }
        BookTable.insert {
            it[id] = book.resourceId.value
            it[isbn] = book.isbn.value
        }
    }
}

object BookTable : IdTable<UUID>("book") {
    override val id = reference("resource_id", ResourceTable)
    override val primaryKey = PrimaryKey(id)
    val isbn = text("isbn")

    fun ResultRow.toBook() = Book(
        ResourceId(this[ResourceTable.id].value),
        Title(this[ResourceTable.title]),
        AuthorId(this[ResourceTable.author]),
        ReleaseDate(this[ResourceTable.releaseDate]),
        this[ResourceTable.description]?.let { Description(it) },
        this[ResourceTable.series]?.let { Series(it) },
        this[ResourceTable.status],
        ISBN(this[isbn]),
    )
}

class BookNotFoundException(bookId: ResourceId) : NotFoundException("Book with id=${bookId.value} doesn't exist")
