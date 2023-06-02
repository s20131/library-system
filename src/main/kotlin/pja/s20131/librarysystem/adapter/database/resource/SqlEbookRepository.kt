package pja.s20131.librarysystem.adapter.database.resource

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.springframework.stereotype.Repository
import pja.s20131.librarysystem.adapter.database.exposed.TsQuery
import pja.s20131.librarysystem.adapter.database.exposed.tsvector
import pja.s20131.librarysystem.adapter.database.resource.EbookSearchView.toEbookView
import pja.s20131.librarysystem.adapter.database.resource.EbookTable.toEbook
import pja.s20131.librarysystem.domain.resource.model.AuthorId
import pja.s20131.librarysystem.domain.resource.model.Content
import pja.s20131.librarysystem.domain.resource.model.Description
import pja.s20131.librarysystem.domain.resource.model.Ebook
import pja.s20131.librarysystem.domain.resource.model.Format
import pja.s20131.librarysystem.domain.resource.model.ReleaseDate
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.model.ResourceStatus
import pja.s20131.librarysystem.domain.resource.model.Series
import pja.s20131.librarysystem.domain.resource.model.Size
import pja.s20131.librarysystem.domain.resource.model.SizeUnit
import pja.s20131.librarysystem.domain.resource.model.Title
import pja.s20131.librarysystem.domain.resource.port.EbookRepository
import pja.s20131.librarysystem.exception.BaseException

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
            // TODO calculate
            it[size] = ebook.size.value
            it[sizeUnit] = SizeUnit.kB
        }
    }

    override fun search(tokens: List<String>): List<Ebook> {
        val joinedTokens = tokens.joinToString(" | ")
        return EbookSearchView.select {
            TsQuery(EbookSearchView.tokens, joinedTokens) eq true
        }.map { it.toEbookView() }
    }
}

object EbookTable : Table("ebook") {
    val id = reference("resource_id", ResourceTable)
    val content = blob("content")
    val format = enumerationByName<Format>("format", 255)
    val size = double("size")
    val sizeUnit = enumerationByName<SizeUnit>("size_unit", 255)
    override val primaryKey = PrimaryKey(id, format)

    fun ResultRow.toEbook() = Ebook(
        ResourceId(this[ResourceTable.id].value),
        Title(this[ResourceTable.title]),
        AuthorId(this[ResourceTable.author]),
        ReleaseDate(this[ResourceTable.releaseDate]),
        this[ResourceTable.description]?.let { Description(it) },
        this[ResourceTable.series]?.let { Series(it) },
        this[ResourceTable.status],
        Content(this[content].bytes),
        this[format],
        Size(this[size]),
    )
}

object EbookSearchView : UUIDTable("ebooks_search_view") {
    val title = text("title")
    val author = uuid("author").references(AuthorTable.id)
    val releaseDate = date("release_date")
    val description = text("description").nullable()
    val series = text("series").references(SeriesTable.id).nullable()
    val status = enumerationByName<ResourceStatus>("status", 255)
    val content = blob("content")
    val format = enumerationByName<Format>("format", 255)
    val size = double("size")
    val sizeUnit = enumerationByName<SizeUnit>("size_unit", 255)
    val tokens = tsvector("tokens")

    fun ResultRow.toEbookView() = Ebook(
        ResourceId(this[id].value),
        Title(this[title]),
        AuthorId(this[author]),
        ReleaseDate(this[releaseDate]),
        this[description]?.let { Description(it) },
        this[series]?.let { Series(it) },
        this[status],
        Content(this[content].bytes),
        this[format],
        Size(this[size]),
    )
}

class EbookNotFoundException(ebookId: ResourceId) : BaseException("Ebook with id=${ebookId} was not found")
