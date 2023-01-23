package pja.s20131.librarysystem.domain.resource.book

interface BookRepository {
    fun getAll(): List<Book>
}
