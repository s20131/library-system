package pja.s20131.librarysystem.api.resource.book

import pja.s20131.librarysystem.domain.resource.book.Book
import pja.s20131.librarysystem.domain.resource.Description
import pja.s20131.librarysystem.domain.resource.book.ISBN
import pja.s20131.librarysystem.domain.resource.ReleaseDate
import pja.s20131.librarysystem.domain.resource.ResourceStatus
import pja.s20131.librarysystem.domain.resource.Series
import pja.s20131.librarysystem.domain.resource.Title

data class GetBookResponse(
    val title: Title,
    val releaseDate: ReleaseDate,
    val description: Description?,
    val series: Series?,
    val status: ResourceStatus,
    val isbn: ISBN,
) {

    companion object {
        fun Book.toGetBookResponse() = GetBookResponse(title, releaseDate, description, series, status, isbn)
    }
}
