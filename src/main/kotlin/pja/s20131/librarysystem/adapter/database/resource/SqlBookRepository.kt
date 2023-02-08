package pja.s20131.librarysystem.adapter.database.resource

import java.util.UUID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository
import pja.s20131.librarysystem.domain.resource.model.Description
import pja.s20131.librarysystem.domain.resource.model.ReleaseDate
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.model.Series
import pja.s20131.librarysystem.domain.resource.model.Title
import pja.s20131.librarysystem.domain.resource.model.Book
import pja.s20131.librarysystem.domain.resource.model.ISBN
import pja.s20131.librarysystem.domain.resource.port.BookRepository

@Repository
class SqlBookRepository : BookRepository {

    // TODO select only available?
    override fun getAll(): List<Book> =
        BookTable
            .innerJoin(ResourceTable)
            .innerJoin(AuthorTable)
            .selectAll()
            .map { it.toBook() }
}

private fun ResultRow.toBook() =
    Book(
        resourceId = ResourceId(this[ResourceTable.id].value),
        title = Title(this[ResourceTable.title]),
        releaseDate = ReleaseDate(this[ResourceTable.releaseDate]),
        description = this[ResourceTable.description]?.let { Description(it) },
        series = this[ResourceTable.series]?.let { Series(it) },
        status = this[ResourceTable.status],
        isbn = ISBN(this[BookTable.isbn]),
        author = toAuthor(),
    )

object BookTable : IdTable<UUID>("book") {
    override val id = reference("resource_id", ResourceTable)
    override val primaryKey = PrimaryKey(id)
    val isbn = text("isbn")
}
