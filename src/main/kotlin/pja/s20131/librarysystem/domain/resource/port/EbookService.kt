package pja.s20131.librarysystem.domain.resource.port

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.domain.resource.model.AuthorBasicData
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

@Service
@Transactional
class EbookService(
    val ebookRepository: EbookRepository,
    val authorRepository: AuthorRepository,
) {

    fun getAllEbooks(): List<ResourceWithAuthorBasicData> {
        val ebooks = ebookRepository.getAll()
        val authors = authorRepository.getAll()
        return ebooks.map { ebook ->
            ResourceWithAuthorBasicData(
                ebook.toBasicData(),
                authors.first { it.authorId == ebook.authorId }.let { AuthorBasicData(it.firstName, it.lastName) }
            )
        }
    }

    fun getEbook(ebookId: ResourceId): Ebook {
        return ebookRepository.get(ebookId)
    }

    fun addEbook(addEbookCommand: AddEbookCommand): ResourceId {
        val author = authorRepository.get(addEbookCommand.authorId)
        val newEbook = addEbookCommand.toEbook(author.authorId)
        ebookRepository.insert(newEbook)
        return newEbook.resourceId
    }
}

data class AddEbookCommand(
    val title: Title,
    val authorId: AuthorId,
    val releaseDate: ReleaseDate,
    val description: Description?,
    val series: Series?,
    val status: ResourceStatus,
    val content: Content,
    val format: Format,
    val size: Size,
) {
    fun toEbook(authorId: AuthorId) = Ebook(ResourceId.generate(), title, authorId, releaseDate, description, series, status, content, format, size)
}
