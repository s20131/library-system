package pja.s20131.librarysystem.domain.resource.port

import pja.s20131.librarysystem.adapter.exceptions.NotFoundException
import pja.s20131.librarysystem.domain.resource.model.Author
import pja.s20131.librarysystem.domain.resource.model.AuthorId

interface AuthorRepository {
    fun find(authorId: AuthorId): Author?
    fun get(authorId: AuthorId): Author = find(authorId) ?: throw AuthorNotFound(authorId)
    fun getAll(): List<Author>
    fun insert(author: Author)
}

class AuthorNotFound(authorId: AuthorId) : NotFoundException("Author with id=${authorId.value} doesn't exist")
