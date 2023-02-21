package pja.s20131.librarysystem.ebook

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.adapter.database.resource.EbookTable
import pja.s20131.librarysystem.domain.resource.model.Ebook
import pja.s20131.librarysystem.domain.resource.model.SizeUnit
import pja.s20131.librarysystem.resource.ResourceDatabaseHelper

@Component
@Transactional
class EbookDatabaseHelper @Autowired constructor(
    val resourceDatabaseHelper: ResourceDatabaseHelper,
) {
    fun insertEbook(ebook: Ebook) {
        ebook.series?.let { series -> resourceDatabaseHelper.insertSeries(series) }
        resourceDatabaseHelper.insertAuthor(ebook.author)
        resourceDatabaseHelper.insertResource(ebook)
        EbookTable.insert {
            it[id] = ebook.resourceId.value
            it[content] = ExposedBlob(ebook.content.value)
            it[format] = ebook.ebookFormat
            it[size] = ebook.size.value
            it[sizeUnit] = SizeUnit.kB
        }
    }
}
