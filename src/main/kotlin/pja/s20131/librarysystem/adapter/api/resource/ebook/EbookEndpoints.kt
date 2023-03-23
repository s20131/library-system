package pja.s20131.librarysystem.adapter.api.resource.ebook

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import pja.s20131.librarysystem.domain.resource.model.Ebook
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.port.EbookService

@RestController
@RequestMapping("/ebooks")
class EbookEndpoints(
    val ebookService: EbookService
) {

    @GetMapping
    fun getAllEbooks(): List<GetEbookResponse> =
        ebookService.getAllEbooks().map { it.toResponse() }

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun addEbook(@RequestBody addEbookRequest: AddEbookRequest): ResourceId {
        return ebookService.addEbook(addEbookRequest.toCommand())
    }
}

private fun Ebook.toResponse() = GetEbookResponse(title, releaseDate, description, series, status, content, size)
