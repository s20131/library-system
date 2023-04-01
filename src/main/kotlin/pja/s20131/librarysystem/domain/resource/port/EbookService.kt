package pja.s20131.librarysystem.domain.resource.port

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.domain.resource.model.Author
import pja.s20131.librarysystem.domain.resource.model.AuthorId
import pja.s20131.librarysystem.domain.resource.model.Content
import pja.s20131.librarysystem.domain.resource.model.Description
import pja.s20131.librarysystem.domain.resource.model.Ebook
import pja.s20131.librarysystem.domain.resource.model.EbookFormat
import pja.s20131.librarysystem.domain.resource.model.ReleaseDate
import pja.s20131.librarysystem.domain.resource.model.ResourceBasicData
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

    fun getAllEbooks(): List<ResourceBasicData> =
        ebookRepository.getAll().map { ResourceBasicData(it.title, it.author) }

    fun addEbook(addEbookCommand: AddEbookCommand): ResourceId {
        val author = authorRepository.get(addEbookCommand.authorId)
        val newEbook = addEbookCommand.toEbook(author)
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
    val ebookFormat: EbookFormat,
    val size: Size,
) {
    fun toEbook(author: Author) = Ebook(ResourceId.generate(), title, author, releaseDate, description, series, status, content, ebookFormat, size)
}
