package pja.s20131.librarysystem.adapter.api.resource.ebook

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pja.s20131.librarysystem.adapter.api.resource.ebook.GetEbookResponse.Companion.toResponse
import pja.s20131.librarysystem.domain.resource.port.EbookService

@RestController
@RequestMapping("/ebooks")
class EbookEndpoints(
    val ebookService: EbookService
) {

    @GetMapping
    fun getAllEbooks(): List<GetEbookResponse> =
        ebookService.getAllEbooks().map { it.toResponse() }
}
