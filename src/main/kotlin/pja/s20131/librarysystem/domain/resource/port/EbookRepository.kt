package pja.s20131.librarysystem.domain.resource.port

import pja.s20131.librarysystem.domain.resource.model.Ebook
import pja.s20131.librarysystem.domain.resource.model.EbookContent
import pja.s20131.librarysystem.domain.resource.model.ResourceId

interface EbookRepository {
    fun getAllActive(): List<Ebook>
    fun get(ebookId: ResourceId): Ebook
    fun getContent(ebookId: ResourceId): EbookContent
    fun insert(ebook: Ebook)
    fun searchActive(tokens: List<String>): List<Ebook>
}
