package pja.s20131.librarysystem.adapter.api.resource.book

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pja.s20131.librarysystem.domain.resource.model.Book
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.port.BookService

@RestController
@RequestMapping("/books")
class BookEndpoints(
    val bookService: BookService
) {

    @GetMapping
    fun getAllBooks(): List<GetBookResponse> =
        bookService.getAllBooks().map { it.toResponse() }

    @PostMapping
    fun addBook(@RequestBody addBookRequest: AddBookRequest): ResourceId {
        return bookService.addBook(addBookRequest.toCommand())
    }

}

private fun Book.toResponse() = GetBookResponse(title, releaseDate, description, series, status, isbn)

