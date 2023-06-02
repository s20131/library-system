package pja.s20131.librarysystem.adapter.api.resource.resource

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.IMAGE_JPEG_VALUE
import org.springframework.http.MediaType.IMAGE_PNG_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pja.s20131.librarysystem.domain.resource.AuthorService
import pja.s20131.librarysystem.domain.resource.ResourceService
import pja.s20131.librarysystem.domain.resource.SeriesService
import pja.s20131.librarysystem.domain.resource.model.Author
import pja.s20131.librarysystem.domain.resource.model.AuthorId
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

    @GetMapping("/authors/{authorId}")
    fun getAuthor(@PathVariable authorId: AuthorId): GetAuthorResponse {
        return authorService.getAuthor(authorId).toResponse()
    }

    @PostMapping(Paths.AUTHORS)
    fun addAuthor(@RequestBody addAuthorRequest: AddAuthorRequest): AuthorId {
        return authorService.addAuthor(addAuthorRequest.toDto())
    }

    @GetMapping(Paths.SERIES)
    fun getAllSeries(): List<Series> {
        return seriesService.getAllSeries()
    }

    @PostMapping(Paths.SERIES)
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
