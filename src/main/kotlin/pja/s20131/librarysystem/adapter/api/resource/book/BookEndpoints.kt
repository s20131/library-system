package pja.s20131.librarysystem.adapter.api.resource.book

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pja.s20131.librarysystem.domain.resource.BookService
import pja.s20131.librarysystem.domain.resource.ResourceWithAuthorBasicData
import pja.s20131.librarysystem.domain.resource.model.Book
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.model.SearchQuery
import pja.s20131.librarysystem.domain.resource.model.SearchQuery.Companion.isNullOrEmpty

@RestController
@RequestMapping("/books")
class BookEndpoints(
    val bookService: BookService
) {
    @GetMapping
    fun getAllBooks(@RequestParam(required = false) search: SearchQuery?): List<GetResourceWithAuthorBasicDataResponse> {
        val result = if (search.isNullOrEmpty()) {
            bookService.getAllBooks()
        } else {
            requireNotNull(search)
            bookService.search(search)
        }
        return result.toResponse()
    }

    @GetMapping("/{bookId}")
    fun getBook(@PathVariable bookId: ResourceId): GetBookResponse {
        return bookService.getBook(bookId).toResponse()
    }

    @PostMapping
    fun addBook(@RequestBody addBookRequest: AddBookRequest): ResourceId {
        return bookService.addBook(addBookRequest.toDto())
    }
}

private fun List<ResourceWithAuthorBasicData>.toResponse() = map { GetResourceWithAuthorBasicDataResponse(it.resource, it.author) }
private fun Book.toResponse() = GetBookResponse(title, authorId, releaseDate, description, series, status, isbn)
