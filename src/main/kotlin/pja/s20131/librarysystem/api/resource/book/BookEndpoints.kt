package pja.s20131.librarysystem.api.resource.book

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pja.s20131.librarysystem.api.resource.book.GetBookResponse.Companion.toGetBookResponse
import pja.s20131.librarysystem.domain.resource.book.BookService

@RestController
@RequestMapping("/books")
class BookEndpoints(
    val bookService: BookService
) {

    @GetMapping
    fun getAllBooks(): List<GetBookResponse> =
        bookService.getAllBooks().map { it.toGetBookResponse() }

}
