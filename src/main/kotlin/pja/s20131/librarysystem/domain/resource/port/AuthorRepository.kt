package pja.s20131.librarysystem.domain.resource.port

import pja.s20131.librarysystem.domain.resource.model.Author
import pja.s20131.librarysystem.domain.resource.model.AuthorId

interface AuthorRepository {
    fun get(authorId: AuthorId): Author
    fun insert(author: Author)
}