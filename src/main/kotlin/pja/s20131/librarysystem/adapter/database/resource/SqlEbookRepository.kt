package pja.s20131.librarysystem.adapter.database.resource

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.springframework.stereotype.Repository
import pja.s20131.librarysystem.domain.resource.model.Content
import pja.s20131.librarysystem.domain.resource.model.Description
import pja.s20131.librarysystem.domain.resource.model.Ebook
import pja.s20131.librarysystem.domain.resource.model.EbookFormat
import pja.s20131.librarysystem.domain.resource.model.ReleaseDate
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.model.Series
import pja.s20131.librarysystem.domain.resource.model.Size
import pja.s20131.librarysystem.domain.resource.model.SizeUnit
import pja.s20131.librarysystem.domain.resource.model.Title
import pja.s20131.librarysystem.domain.resource.port.EbookRepository

@Repository
class SqlEbookRepository : EbookRepository {
    override fun getAll(): List<Ebook> =
        EbookTable
            .innerJoin(ResourceTable)
            .innerJoin(AuthorTable)
            .selectAll()
            .map { it.toEbook() }

    override fun insert(ebook: Ebook) {
        insertResourcePropertiesFrom(ebook)
        EbookTable.insert {
            it[id] = ebook.resourceId.value
            it[content] = ExposedBlob(ebook.content.value)
            it[format] = ebook.ebookFormat
            it[size] = ebook.size.value
            it[sizeUnit] = SizeUnit.kB
        }
    }
}

private fun ResultRow.toEbook() =
    Ebook(
        resourceId = ResourceId(this[ResourceTable.id].value),
        title = Title(this[ResourceTable.title]),
        releaseDate = ReleaseDate(this[ResourceTable.releaseDate]),
        description = this[ResourceTable.description]?.let { Description(it) },
        series = this[ResourceTable.series]?.let { Series(it) },
        status = this[ResourceTable.status],
        content = Content(this[EbookTable.content].bytes),
        ebookFormat = EbookFormat.valueOf(this[EbookTable.format].name),
        size = Size(this[EbookTable.size]),
        author = toAuthor()
    )

object EbookTable : Table("ebook") {
    val id = reference("resource_id", ResourceTable)
    val content = blob("content")
    val format = enumerationByName<EbookFormat>("format", 255)
    val size = double("size")
    val sizeUnit = enumerationByName<SizeUnit>("size_unit", 255)
    override val primaryKey = PrimaryKey(id, format)
}
