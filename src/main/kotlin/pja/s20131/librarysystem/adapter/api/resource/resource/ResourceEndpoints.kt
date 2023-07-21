package pja.s20131.librarysystem.adapter.api.resource.resource

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.MediaType.IMAGE_JPEG_VALUE
import org.springframework.http.MediaType.IMAGE_PNG_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import pja.s20131.librarysystem.domain.resource.AuthorService
import pja.s20131.librarysystem.domain.resource.ResourceService
import pja.s20131.librarysystem.domain.resource.SeriesService
import pja.s20131.librarysystem.domain.resource.model.Author
import pja.s20131.librarysystem.domain.resource.model.AuthorId
import pja.s20131.librarysystem.domain.resource.model.ResourceCover
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.model.Series

@RestController
@RequestMapping("/resources")
class ResourceEndpoints(
    private val resourceService: ResourceService,
    private val authorService: AuthorService,
    private val seriesService: SeriesService,
) {

    @GetMapping("/{resourceId}/cover", produces = [IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_WEBP_VALUE])
    fun getCover(@PathVariable resourceId: ResourceId): ResponseEntity<ByteArray> {
        val cover = resourceService.getResourceCover(resourceId)
        return ResponseEntity.status(HttpStatus.OK).contentType(cover.mediaType).body(cover.content)
    }

    @PostMapping("/{resourceId}/cover", consumes = [IMAGE_PNG_VALUE, IMAGE_JPEG_VALUE, IMAGE_WEBP_VALUE])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured("ROLE_LIBRARIAN")
    fun addCover(@PathVariable resourceId: ResourceId, @RequestHeader(HttpHeaders.CONTENT_TYPE) contentType: MediaType, @RequestBody cover: ByteArray) {
        resourceService.addResourceCover(resourceId, ResourceCover(cover, contentType))
    }

    @GetMapping("${Paths.AUTHORS}/{authorId}")
    fun getAuthor(@PathVariable authorId: AuthorId): GetAuthorResponse {
        return authorService.getAuthor(authorId).toResponse()
    }

    @GetMapping(Paths.AUTHORS)
    fun getAuthors(): List<GetAuthorResponse> {
        return authorService.getAuthors().map { it.toResponse() }
    }

    @PostMapping(Paths.AUTHORS)
    @Secured("ROLE_LIBRARIAN")
    fun addAuthor(@RequestBody addAuthorRequest: AddAuthorRequest): AuthorId {
        return authorService.addAuthor(addAuthorRequest.toDto())
    }

    @GetMapping(Paths.SERIES)
    fun getAllSeries(): List<Series> {
        return seriesService.getAllSeries()
    }

    @PostMapping(Paths.SERIES)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured("ROLE_LIBRARIAN")
    fun addSeries(@RequestBody series: Series) {
        return seriesService.addSeries(series)
    }

    object Paths {
        const val AUTHORS = "/authors"
        const val SERIES = "/series"
    }

    companion object {
        const val IMAGE_WEBP_VALUE = "image/webp"
    }
}

private fun Author.toResponse() = GetAuthorResponse(authorId, firstName, lastName)
