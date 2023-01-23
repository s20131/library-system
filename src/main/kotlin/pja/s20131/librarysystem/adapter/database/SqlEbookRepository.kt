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
import pja.s20131.librarysystem.domain.resource.ebook.Content
//import pja.s20131.librarysystem.domain.resource.ebook.ContentType
import pja.s20131.librarysystem.domain.resource.ebook.Ebook
import pja.s20131.librarysystem.domain.resource.ebook.EbookRepository
import pja.s20131.librarysystem.domain.resource.ebook.Size

@Repository
class SqlEbookRepository : EbookRepository {

    override fun getAll(): List<Ebook> =
        EbookTable
            .innerJoin(ResourceTable)
            .selectAll()
            .map { it.toEbook() }
}

private fun ResultRow.toEbook() =
    Ebook(
        resourceId = ResourceId(this[ResourceTable.id].value),
        title = Title(this[ResourceTable.title]),
        releaseDate = ReleaseDate(this[ResourceTable.releaseDate]),
        description = this[ResourceTable.description]?.let { Description(it) },
        series = this[ResourceTable.series]?.let { Series(it) },
        resourceStatus = this[ResourceTable.resourceStatus],
        content = Content(this[EbookTable.content].bytes),
        //contentType = this[EbookTable.contentType],
        size = Size(this[EbookTable.size]),
    )

object EbookTable : IdTable<UUID>("ebook") {
    override val id = reference("resource_id", ResourceTable)
    val content = blob("content")
    //val contentType = enumeration<ContentType>("content_type")
    val size = double("size")
    override val primaryKey = PrimaryKey(id/*, format*/)
}
