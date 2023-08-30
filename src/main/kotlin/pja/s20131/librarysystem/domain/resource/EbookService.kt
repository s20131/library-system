package pja.s20131.librarysystem.domain.resource

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.domain.resource.ResourceWithAuthorBasicData.Companion.withAuthors
import pja.s20131.librarysystem.domain.resource.model.AuthorId
import pja.s20131.librarysystem.domain.resource.model.Description
import pja.s20131.librarysystem.domain.resource.model.Ebook
import pja.s20131.librarysystem.domain.resource.model.EbookContent
import pja.s20131.librarysystem.domain.resource.model.Format
import pja.s20131.librarysystem.domain.resource.model.ReleaseDate
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.model.SearchQuery
import pja.s20131.librarysystem.domain.resource.model.Series
import pja.s20131.librarysystem.domain.resource.model.Size
import pja.s20131.librarysystem.domain.resource.model.Title
import pja.s20131.librarysystem.domain.resource.port.AuthorRepository
import pja.s20131.librarysystem.domain.resource.port.EbookRepository
import pja.s20131.librarysystem.domain.resource.port.RentalRepository
import pja.s20131.librarysystem.domain.user.model.UserId

@Service
@Transactional
class EbookService(
    private val ebookRepository: EbookRepository,
    private val authorRepository: AuthorRepository,
    private val rentalRepository: RentalRepository,
) {

    fun getAllActiveEbooks(): List<ResourceWithAuthorBasicData> {
        val ebooks = ebookRepository.getAllActive()
        val authors = authorRepository.getAll()
        return ebooks.withAuthors(authors)
    }

    fun getEbook(ebookId: ResourceId): Ebook {
        return ebookRepository.get(ebookId)
    }

    fun getEbookContent(ebookId: ResourceId, userId: UserId): EbookContent {
        val rental = rentalRepository
            .getLatest(ebookId, userId)
            .also { it.validateCanBeDownloaded() }
        return ebookRepository.getContent(rental.resourceId)
    }

    fun addEbook(dto: AddEbookDto): ResourceId {
        checkIfAuthorExists(dto.authorId)
        val newEbook = Ebook.from(dto)
        ebookRepository.insert(newEbook)
        return newEbook.resourceId
    }

    fun search(query: SearchQuery): List<ResourceWithAuthorBasicData> {
        val tokens = query.tokenize()
        val ebooks = ebookRepository.searchActive(tokens)
        val authors = authorRepository.getAll()
        return ebooks.withAuthors(authors)
    }

    private fun checkIfAuthorExists(authorId: AuthorId) {
        authorRepository.get(authorId)
    }
}

@Suppress("ArrayInDataClass")
data class AddEbookDto(
    val title: Title,
    val authorId: AuthorId,
    val releaseDate: ReleaseDate,
    val description: Description?,
    val series: Series?,
    val content: ByteArray,
    val format: Format,
    val size: Size,
)
