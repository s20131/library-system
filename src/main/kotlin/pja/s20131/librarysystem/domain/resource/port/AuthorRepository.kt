package pja.s20131.librarysystem.domain.resource.port

import pja.s20131.librarysystem.domain.resource.model.Author
import pja.s20131.librarysystem.domain.resource.model.AuthorId

interface AuthorRepository {
    fun find(authorId: AuthorId): Author?
    fun get(authorId: AuthorId): Author
    fun getAll(): List<Author>
    fun insert(author: Author)
}
