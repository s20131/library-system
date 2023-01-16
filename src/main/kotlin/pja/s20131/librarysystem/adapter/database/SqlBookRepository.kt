package pja.s20131.librarysystem.adapter.database

import java.util.UUID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import pja.s20131.librarysystem.adapter.database.shared.ResourceTable
import pja.s20131.librarysystem.domain.resource.Description
import pja.s20131.librarysystem.domain.resource.ReleaseDate
import pja.s20131.librarysystem.domain.resource.ResourceId
import pja.s20131.librarysystem.domain.resource.Series
import pja.s20131.librarysystem.domain.resource.Title
import pja.s20131.librarysystem.domain.resource.book.Book
import pja.s20131.librarysystem.domain.resource.book.BookRepository
import pja.s20131.librarysystem.domain.resource.book.ISBN

@Repository
class SqlBookRepository : BookRepository {

    override fun getAll(): List<Book> =
        BookTable
            .innerJoin(ResourceTable)
            .selectAll()
            .map { it.toBook() }
}

private fun ResultRow.toBook() =
    Book(
        ResourceId(this[ResourceTable.id].value),
        Title(this[ResourceTable.title]),
        ReleaseDate(this[ResourceTable.releaseDate]),
        this[ResourceTable.description]?.let { Description(it) },
        Series(this[ResourceTable.series]),
        this[ResourceTable.resourceStatus],
        ISBN(this[BookTable.isbn])
    )

object BookTable : IdTable<UUID>("book") {
    override val id = reference("resource_id", ResourceTable)
    override val primaryKey = PrimaryKey(id)
    val isbn = text("isbn")
}
