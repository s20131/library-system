package pja.s20131.librarysystem.api.resource.book

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/books")
class BookEndpoints {

    @GetMapping
    fun getAllBooks(): List<GetBookResponse> = TODO()

}
