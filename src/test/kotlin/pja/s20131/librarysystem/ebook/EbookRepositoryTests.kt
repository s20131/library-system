package pja.s20131.librarysystem.ebook

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.deleteAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.adapter.database.resource.AuthorTable
import pja.s20131.librarysystem.adapter.database.resource.EbookTable
import pja.s20131.librarysystem.adapter.database.resource.ResourceTable
import pja.s20131.librarysystem.adapter.database.resource.SeriesTable
import pja.s20131.librarysystem.domain.resource.model.Size
import pja.s20131.librarysystem.domain.resource.port.EbookRepository
import pja.s20131.librarysystem.ebook.EbookGen.ebook
import java.sql.SQLException

@SpringBootTest
@Transactional
class EbookRepositoryTests @Autowired constructor(
    val ebookRepository: EbookRepository,
    val ebookDatabaseHelper: EbookDatabaseHelper,
) {

    @AfterEach
    fun clear() {
        EbookTable.deleteAll()
        ResourceTable.deleteAll()
        AuthorTable.deleteAll()
        SeriesTable.deleteAll()
    }

    // TODO: currently not working because of byte arrays of contents 'inequality' (a value class cannot override 'equals')
    // https://github.com/Kotlin/KEEP/blob/master/proposals/inline-classes.md#methods-from-kotlinany
    @Disabled("byte array inequality")
    @Test
    fun `should get all ebooks`() {
        val (ebook1, ebook2) = ebook() to ebook()
        ebookDatabaseHelper.insertEbook(ebook1)
        ebookDatabaseHelper.insertEbook(ebook2)

        val response = ebookRepository.getAll()

        assertThat(response).containsExactly(ebook1, ebook2)
    }

    // TODO: check also postgres exception
    @Test
    @Disabled("added domain check")
    fun `should return SQL error when trying to insert ebook with size lower than 0`() {
        val ebook = ebook(size = Size(-.1))

        assertThrows<SQLException> { ebookDatabaseHelper.insertEbook(ebook) }
    }

}
