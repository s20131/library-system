package pja.s20131.librarysystem.api.resource.ebook

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/ebooks")
class EbookEndpoints {

    @GetMapping
    fun getAllEbooks(): List<GetEbookResponse> = TODO()
}
