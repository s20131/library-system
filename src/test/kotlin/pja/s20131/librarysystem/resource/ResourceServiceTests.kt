package pja.s20131.librarysystem.resource

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pja.s20131.librarysystem.Assertions
import pja.s20131.librarysystem.BaseTestConfig
import pja.s20131.librarysystem.Preconditions
import pja.s20131.librarysystem.domain.resource.ResourceService

@SpringBootTest
class ResourceServiceTests @Autowired constructor(
    private val resourceService: ResourceService,
    private val given: Preconditions,
    private val assert: Assertions,
    ) : BaseTestConfig() {

    @Test
    fun `should get a cover`() {
        val cover = ResourceGen.cover()
        val book = given.author.exists().withBook(cover = cover).build().second[0]

        val response = resourceService.getResourceCover(book.resourceId)

        assertThat(cover.content).isEqualTo(response.content)
        assertThat(cover.mediaType).isEqualTo(response.mediaType)
    }

    @Test
    fun `should add a cover`() {
        val cover = ResourceGen.cover()
        val book = given.author.exists().withBook().build().second[0]

        resourceService.addResourceCover(book.resourceId, cover)

        assert.book.hasCover(book.resourceId, cover)
    }
}
