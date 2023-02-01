package pja.s20131.librarysystem.adapter.database.ebook

import pja.s20131.librarysystem.domain.resource.ebook.Ebook

interface EbookRepository {
    fun getAll(): List<Ebook>
}
