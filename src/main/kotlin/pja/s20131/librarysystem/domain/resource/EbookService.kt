package pja.s20131.librarysystem.domain.resource

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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
import pja.s20131.librarysystem.domain.resource.model.Title
import pja.s20131.librarysystem.domain.resource.port.AuthorRepository
import pja.s20131.librarysystem.domain.resource.port.EbookRepository

@Service
@Transactional
class EbookService(
    private val ebookRepository: EbookRepository,
    private val authorRepository: AuthorRepository,
) {

    fun getAllEbooks(): List<ResourceWithAuthorBasicData> {
        val ebooks = ebookRepository.getAll()
        val authors = authorRepository.getAll()
        return ebooks.map { ebook ->
            ResourceWithAuthorBasicData(
                ebook.toBasicData(),
                authors.first { it.authorId == ebook.authorId }.toBasicData()
            )
        }
    }

    fun getEbook(ebookId: ResourceId): Ebook {
        return ebookRepository.get(ebookId)
    }

    fun addEbook(dto: AddEbookDto): ResourceId {
        checkIfAuthorExists(dto.authorId)
        val newEbook = Ebook.from(dto)
        ebookRepository.insert(newEbook)
        return newEbook.resourceId
    }

    private fun checkIfAuthorExists(authorId: AuthorId) {
        authorRepository.get(authorId)
    }
}

data class AddEbookDto(
    val title: Title,
    val authorId: AuthorId,
    val releaseDate: ReleaseDate,
    val description: Description?,
    val series: Series?,
    val status: ResourceStatus,
    val content: Content,
    val format: Format,
    val size: Size,
)
