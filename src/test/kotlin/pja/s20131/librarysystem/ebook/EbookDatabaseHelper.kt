package pja.s20131.librarysystem.ebook

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.adapter.database.resource.EbookTable
import pja.s20131.librarysystem.adapter.database.resource.EbookTable.toEbook
import pja.s20131.librarysystem.adapter.database.resource.ResourceTable
import pja.s20131.librarysystem.domain.resource.model.Ebook
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.model.SizeUnit
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
            it[content] = ExposedBlob(ebook.content.value)
            it[format] = ebook.format
            it[size] = ebook.size.value
            it[sizeUnit] = SizeUnit.kB
        }
    }

    fun assertEbookIsSaved(ebookId: ResourceId) {
        val ebook = EbookTable
            .innerJoin(ResourceTable)
            .select { EbookTable.id eq ebookId.value }
            .singleOrNull()
            ?.toEbook()

        assertThat(ebook).isNotNull
        assertThat(ebook?.resourceId).isEqualTo(ebookId)
    }
}
