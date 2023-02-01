package pja.s20131.librarysystem.ebook

import java.sql.SQLException
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pja.s20131.librarysystem.adapter.database.ebook.EbookRepository
import pja.s20131.librarysystem.adapter.database.ebook.EbookTable
import pja.s20131.librarysystem.adapter.database.shared.ResourceTable
import pja.s20131.librarysystem.adapter.database.shared.SeriesTable
import pja.s20131.librarysystem.domain.resource.ebook.Size

@SpringBootTest
class EbookRepositoryTests @Autowired constructor(
    val ebookRepository: EbookRepository
) {

    @AfterEach
    fun clear() {
        transaction {
            EbookTable.deleteAll()
            ResourceTable.deleteAll()
            SeriesTable.deleteAll()
        }
    }

    // TODO: currently not working because of byte arrays of contents 'inequality'
    @Disabled
    @Test
    fun `should get all ebooks`() {
        val ebook1 = ebook()
        val ebook2 = ebook()

        addEbook(ebook1)
        addEbook(ebook2)

        val response = transaction { ebookRepository.getAll() }

        assertThat(response).containsExactly(
            ebook1, ebook2
        )
    }

    @Test
    fun `should return SQL error when trying to insert ebook with size lower than 0`() {
        val ebook = ebook(size = Size(-.1))

        assertThrows<SQLException> { addEbook(ebook) }
    }

}
