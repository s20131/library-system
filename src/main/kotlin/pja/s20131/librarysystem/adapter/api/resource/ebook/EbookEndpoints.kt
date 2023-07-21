package pja.s20131.librarysystem.adapter.api.resource.ebook

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_PDF_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pja.s20131.librarysystem.adapter.api.resource.resource.GetResourceWithAuthorBasicDataResponse
import pja.s20131.librarysystem.domain.resource.EbookService
import pja.s20131.librarysystem.domain.resource.ResourceWithAuthorBasicData
import pja.s20131.librarysystem.domain.resource.model.Ebook
import pja.s20131.librarysystem.domain.resource.model.Format
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.model.SearchQuery
import pja.s20131.librarysystem.domain.resource.model.SearchQuery.Companion.isNullOrEmpty
import pja.s20131.librarysystem.domain.user.AuthService

@RestController
@RequestMapping("/ebooks")
class EbookEndpoints(
    val ebookService: EbookService,
    val authService: AuthService,
) {

    @GetMapping
    fun getAllEbooks(@RequestParam(required = false) search: SearchQuery?): List<GetResourceWithAuthorBasicDataResponse> {
        val result = if (search.isNullOrEmpty()) {
            ebookService.getAllActiveEbooks()
        } else {
            requireNotNull(search)
            ebookService.search(search)
        }
        return result.toResponse()
    }

    @GetMapping("/{ebookId}")
    fun getEbook(@PathVariable ebookId: ResourceId): GetEbookInfoResponse {
        return ebookService.getEbook(ebookId).toResponse()
    }

    @GetMapping("/{ebookId}/content", produces = [APPLICATION_EPUB_VALUE, APPLICATION_PDF_VALUE])
    fun getEbookContent(@PathVariable ebookId: ResourceId): ResponseEntity<ByteArray> {
        val content = authService.withUserContext {
            ebookService.getEbookContent(ebookId, it)
        }
        val contentType = when (content.format) {
            Format.EPUB -> APPLICATION_EPUB_VALUE
            Format.PDF -> APPLICATION_PDF_VALUE
        }
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf(contentType)).body(content.bytes)
    }

    @PostMapping
    @Secured("ROLE_LIBRARIAN")
    fun addEbook(@RequestBody addEbookRequest: AddEbookRequest): ResourceId {
        return ebookService.addEbook(addEbookRequest.toDto())
    }

    companion object {
        const val APPLICATION_EPUB_VALUE = "application/epub+zip"
    }
}

private fun List<ResourceWithAuthorBasicData>.toResponse() = map { GetResourceWithAuthorBasicDataResponse(it.resource, it.author) }
private fun Ebook.toResponse() = GetEbookInfoResponse(title, authorId, releaseDate, description, series, status, content.format, size)
