package pja.s20131.librarysystem.assertions

import org.springframework.stereotype.Component

@Component
class Assertions(
    val user: UserAssertions,
    val resource: ResourceAssertions,
    val book: BookAssertions,
    val ebook: EbookAssertions,
    val author: AuthorAssertions,
    val series: SeriesAssertions,
)
