package pja.s20131.librarysystem.domain.resource.port

import pja.s20131.librarysystem.domain.resource.model.Ebook

interface EbookRepository {
    fun getAll(): List<Ebook>
    fun insert(ebook: Ebook)
}
