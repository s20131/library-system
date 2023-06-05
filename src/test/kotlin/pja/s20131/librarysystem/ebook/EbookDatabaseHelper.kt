package pja.s20131.librarysystem.ebook

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.adapter.database.resource.EbookTable
import pja.s20131.librarysystem.adapter.database.resource.EbookTable.toEbook
import pja.s20131.librarysystem.adapter.database.resource.ResourceTable
import pja.s20131.librarysystem.domain.resource.model.AuthorId
import pja.s20131.librarysystem.domain.resource.model.Ebook
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.resource.ResourceDatabaseHelper

@Component
@Transactional
class EbookDatabaseHelper @Autowired constructor(
    val resourceDatabaseHelper: ResourceDatabaseHelper,
) {
    fun insertEbook(ebook: Ebook) {
        resourceDatabaseHelper.insertResource(ebook)
        EbookTable.insert {
            it[id] = ebook.resourceId.value
            it[content] = ExposedBlob(ebook.content.bytes)
            it[format] = ebook.content.format
        }
    }

    fun findBy(ebookId: ResourceId): Ebook? {
        return EbookTable
            .innerJoin(ResourceTable)
            .select { EbookTable.id eq ebookId.value }
            .singleOrNull()
            ?.toEbook()
    }

    fun getBy(authorId: AuthorId): List<Ebook> {
        return EbookTable
            .innerJoin(ResourceTable)
            .select { ResourceTable.author eq authorId.value }
            .map { it.toEbook() }
    }

    fun refreshSearchView() {
        TransactionManager.current().exec("REFRESH MATERIALIZED VIEW ebooks_search_view;")
    }
}
