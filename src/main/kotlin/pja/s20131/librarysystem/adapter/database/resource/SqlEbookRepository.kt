package pja.s20131.librarysystem.adapter.database.resource

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.springframework.stereotype.Repository
import pja.s20131.librarysystem.adapter.exceptions.NotFoundException
import pja.s20131.librarysystem.domain.resource.model.AuthorId
import pja.s20131.librarysystem.domain.resource.model.Content
import pja.s20131.librarysystem.domain.resource.model.Description
import pja.s20131.librarysystem.domain.resource.model.Ebook
import pja.s20131.librarysystem.domain.resource.model.Format
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
            .selectAll()
            .map { it.toEbook() }

    override fun get(ebookId: ResourceId): Ebook =
        EbookTable
            .innerJoin(ResourceTable)
            .select { EbookTable.id eq ebookId.value }
            .singleOrNull()
            ?.toEbook() ?: throw EbookNotFoundException(ebookId)

    override fun insert(ebook: Ebook) {
        ResourceTable.insert {
            it.from(ebook)
        }
        EbookTable.insert {
            it[id] = ebook.resourceId.value
            it[content] = ExposedBlob(ebook.content.value)
            it[format] = ebook.format
            it[size] = ebook.size.value
            it[sizeUnit] = SizeUnit.kB
        }
    }
}

private fun ResultRow.toEbook() =
    Ebook(
        ResourceId(this[ResourceTable.id].value),
        Title(this[ResourceTable.title]),
        AuthorId(this[ResourceTable.author]),
        ReleaseDate(this[ResourceTable.releaseDate]),
        this[ResourceTable.description]?.let { Description(it) },
        this[ResourceTable.series]?.let { Series(it) },
        this[ResourceTable.status],
        Content(this[EbookTable.content].bytes),
        Format.valueOf(this[EbookTable.format].name),
        Size(this[EbookTable.size]),
    )

object EbookTable : Table("ebook") {
    val id = reference("resource_id", ResourceTable)
    val content = blob("content")
    val format = enumerationByName<Format>("format", 255)
    val size = double("size")
    val sizeUnit = enumerationByName<SizeUnit>("size_unit", 255)
    override val primaryKey = PrimaryKey(id, format)
}

class EbookNotFoundException(ebookId: ResourceId): NotFoundException("Ebook with id=${ebookId} was not found")
