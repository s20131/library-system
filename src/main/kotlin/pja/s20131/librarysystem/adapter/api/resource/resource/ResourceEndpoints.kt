package pja.s20131.librarysystem.adapter.api.resource.resource

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pja.s20131.librarysystem.domain.resource.model.AuthorId
import pja.s20131.librarysystem.domain.resource.model.Series
import pja.s20131.librarysystem.domain.resource.port.AuthorService
import pja.s20131.librarysystem.domain.resource.port.SeriesService

@RestController
@RequestMapping("/resources")
class ResourceEndpoints(
    val authorService: AuthorService,
    val seriesService: SeriesService,
) {

    // TODO add endpoints tests

    @PostMapping(Paths.AUTHORS)
    fun addAuthor(@RequestBody addAuthorRequest: AddAuthorRequest):  AuthorId {
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

    object Paths {
        const val AUTHORS = "/authors"
        const val SERIES = "/series"
    }
}
