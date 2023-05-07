package pja.s20131.librarysystem.adapter.api.resource.resource

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pja.s20131.librarysystem.domain.resource.model.Author
import pja.s20131.librarysystem.domain.resource.model.AuthorId
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.model.Series
import pja.s20131.librarysystem.domain.resource.AuthorService
import pja.s20131.librarysystem.domain.resource.ResourceCopy
import pja.s20131.librarysystem.domain.resource.ResourceService
import pja.s20131.librarysystem.domain.resource.SeriesService

@RestController
@RequestMapping("/resources")
class ResourceEndpoints(
    val resourceService: ResourceService,
    val authorService: AuthorService,
    val seriesService: SeriesService,
) {

    @GetMapping("/authors/{authorId}")
    fun getAuthor(@PathVariable authorId: AuthorId): GetAuthorResponse {
        return authorService.getAuthor(authorId).toResponse()
    }

    @PostMapping(Paths.AUTHORS)
    fun addAuthor(@RequestBody addAuthorRequest: AddAuthorRequest): AuthorId {
        return authorService.addAuthor(addAuthorRequest.toCommand())
    }

    @GetMapping(Paths.SERIES)
    fun getAllSeries(): List<Series> {
        return seriesService.getAllSeries()
    }

    @PostMapping(Paths.SERIES)
    fun addSeries(@RequestBody series: Series) {
        return seriesService.addSeries(series)
    }

    @GetMapping("${Paths.COPIES}/{resourceId}/libraries")
    fun getResourceCopyInLibraries(@PathVariable resourceId: ResourceId): List<GetResourceCopyResponse> {
        return resourceService.getResourceCopiesInLibraries(resourceId).toResponse()
    }

    object Paths {
        const val AUTHORS = "/authors"
        const val SERIES = "/series"
        const val COPIES = "/copies"
    }
}

private fun Author.toResponse() = GetAuthorResponse(authorId, firstName, lastName)
private fun List<ResourceCopy>.toResponse() = map { GetResourceCopyResponse(it.libraryId, it.available) }
