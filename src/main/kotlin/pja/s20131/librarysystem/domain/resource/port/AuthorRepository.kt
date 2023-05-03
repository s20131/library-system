package pja.s20131.librarysystem.domain.resource.port

import pja.s20131.librarysystem.domain.resource.model.Author
import pja.s20131.librarysystem.domain.resource.model.AuthorId
import pja.s20131.librarysystem.exception.BaseException

interface AuthorRepository {
    fun find(authorId: AuthorId): Author?
    fun get(authorId: AuthorId): Author = find(authorId) ?: throw AuthorNotFoundException(authorId)
    fun getAll(): List<Author>
    fun insert(author: Author)
}

class AuthorNotFoundException(authorId: AuthorId) : BaseException("Author with id=${authorId.value} doesn't exist")
