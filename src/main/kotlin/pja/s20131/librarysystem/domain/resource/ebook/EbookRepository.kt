package pja.s20131.librarysystem.domain.resource.ebook

interface EbookRepository {
    fun getAll(): List<Ebook>
}
