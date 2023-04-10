package pja.s20131.librarysystem.domain.resource.port

import pja.s20131.librarysystem.domain.resource.model.Ebook
import pja.s20131.librarysystem.domain.resource.model.ResourceId

interface EbookRepository {
    fun getAll(): List<Ebook>
    fun get(ebookId: ResourceId): Ebook
    fun insert(ebook: Ebook)
}
