package pja.s20131.librarysystem.adapter.database.book

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
        resourceId = ResourceId(this[ResourceTable.id].value),
        title = Title(this[ResourceTable.title]),
        releaseDate = ReleaseDate(this[ResourceTable.releaseDate]),
        description = this[ResourceTable.description]?.let { Description(it) },
        series = this[ResourceTable.series]?.let { Series(it) },
        resourceStatus = this[ResourceTable.resourceStatus],
        isbn = ISBN(this[BookTable.isbn])
    )

object BookTable : IdTable<UUID>("book") {
    override val id = reference("resource_id", ResourceTable)
    override val primaryKey = PrimaryKey(id)
    val isbn = text("isbn")
}
