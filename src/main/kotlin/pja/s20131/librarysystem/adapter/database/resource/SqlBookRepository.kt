package pja.s20131.librarysystem.adapter.database.resource

import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import pja.s20131.librarysystem.adapter.database.exposed.TsQuery
import pja.s20131.librarysystem.adapter.database.exposed.tsvector
import pja.s20131.librarysystem.adapter.database.resource.BookSearchView.toBookView
import pja.s20131.librarysystem.adapter.database.resource.BookTable.toBook
import pja.s20131.librarysystem.domain.resource.model.AuthorId
import pja.s20131.librarysystem.domain.resource.model.Book
import pja.s20131.librarysystem.domain.resource.model.Description
import pja.s20131.librarysystem.domain.resource.model.ISBN
import pja.s20131.librarysystem.domain.resource.model.ReleaseDate
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.model.ResourceStatus
import pja.s20131.librarysystem.domain.resource.model.Series
import pja.s20131.librarysystem.domain.resource.model.Title
import pja.s20131.librarysystem.domain.resource.port.BookRepository
import pja.s20131.librarysystem.exception.BaseException
import java.util.UUID

@Repository
class SqlBookRepository : BookRepository {

    override fun getAll(): List<Book> =
        BookTable
            .innerJoin(ResourceTable)
            .selectAll()
            .orderBy(ResourceTable.title)
            .map { it.toBook() }

    override fun get(bookId: ResourceId): Book =
        BookTable
            .innerJoin(ResourceTable)
            .select { BookTable.id eq bookId.value }
            .singleOrNull()
            ?.toBook() ?: throw BookNotFoundException(bookId)

    override fun get(isbn: ISBN): Book =
        BookTable
            .innerJoin(ResourceTable)
            .select { BookTable.isbn eq isbn.value }
            .singleOrNull()
            ?.toBook() ?: throw BookNotFoundException(isbn)

    override fun save(book: Book) {
        ResourceTable.insert {
            it.from(book)
        }
        BookTable.insert {
            it[id] = book.resourceId.value
            it[isbn] = book.isbn.value
        }
    }

    override fun search(tokens: List<String>): List<Book> {
        val joinedTokens = tokens.joinToString(" | ")
        return BookSearchView
            .select { TsQuery(BookSearchView.tokens, joinedTokens) eq true }
            .orderBy(BookSearchView.title)
            .map { it.toBookView() }
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

object BookSearchView : UUIDTable("books_search_view") {
    val title = text("title")
    val author = uuid("author").references(AuthorTable.id)
    val releaseDate = date("release_date")
    val description = text("description").nullable()
    val series = text("series").references(SeriesTable.id).nullable()
    val status = enumerationByName<ResourceStatus>("status", 255)
    val isbn = text("isbn")
    val tokens = tsvector("tokens")

    fun ResultRow.toBookView() = Book(
        ResourceId(this[id].value),
        Title(this[title]),
        AuthorId(this[author]),
        ReleaseDate(this[releaseDate]),
        this[description]?.let { Description(it) },
        this[series]?.let { Series(it) },
        this[status],
        ISBN(this[isbn]),
    )
}

class BookNotFoundException : BaseException {
    constructor(bookId: ResourceId) : super("Book with id ${bookId.value} doesn't exist")
    constructor(isbn: ISBN) : super("Book with isbn ${isbn.value} doesn't exist")
}
