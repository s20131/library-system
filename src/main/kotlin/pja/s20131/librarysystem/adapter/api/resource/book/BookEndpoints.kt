package pja.s20131.librarysystem.adapter.api.resource.book

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pja.s20131.librarysystem.domain.resource.model.ResourceBasicData
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.port.BookService

@RestController
@RequestMapping("/books")
class BookEndpoints(
    val bookService: BookService
) {

    @GetMapping
    fun getAllBooks(): List<ResourceBasicData> {
        return bookService.getAllBooks()
    }

    @PostMapping
    fun addBook(@RequestBody addBookRequest: AddBookRequest): ResourceId {
        return bookService.addBook(addBookRequest.toCommand())
    }

}
