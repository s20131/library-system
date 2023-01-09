package pja.s20131.librarysystem.api.resource

import java.time.LocalDate
import java.util.UUID
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pja.s20131.librarysystem.api.resource.GetBookResponse.Companion.toGetBookResponse
import pja.s20131.librarysystem.domain.resource.Book
import pja.s20131.librarysystem.domain.resource.ISBN
import pja.s20131.librarysystem.domain.resource.ReleaseDate
import pja.s20131.librarysystem.domain.resource.Resource
import pja.s20131.librarysystem.domain.resource.ResourceId
import pja.s20131.librarysystem.domain.resource.ResourceStatus
import pja.s20131.librarysystem.domain.resource.Series
import pja.s20131.librarysystem.domain.resource.Title

@RestController
@RequestMapping("/books")
class BookEndpoints {

    @GetMapping
    fun getAllBooks() = listOf(
        Book(
            Resource(ResourceId.create(), Title("Wieża Jaskółki"), ReleaseDate(LocalDate.of(1997, 10, 10)), description = null, Series("Wiedźmin"), ResourceStatus.AVAILABLE),
            ISBN(UUID.randomUUID().toString())
        )
    ).map { it.toGetBookResponse() }
}
