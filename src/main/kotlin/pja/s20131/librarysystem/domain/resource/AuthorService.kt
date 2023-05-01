package pja.s20131.librarysystem.domain.resource

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.domain.person.FirstName
import pja.s20131.librarysystem.domain.person.LastName
import pja.s20131.librarysystem.domain.resource.model.Author
import pja.s20131.librarysystem.domain.resource.model.AuthorId
import pja.s20131.librarysystem.domain.resource.port.AuthorRepository

@Service
@Transactional
class AuthorService(
    private val authorRepository: AuthorRepository,
) {

    fun getAuthor(authorId: AuthorId): Author {
        return authorRepository.get(authorId)
    }

    fun addAuthor(addAuthorCommand: AddAuthorCommand): AuthorId {
        val newAuthor = addAuthorCommand.toAuthor()
        authorRepository.insert(newAuthor)
        return newAuthor.authorId
    }
}

data class AddAuthorCommand(
    val firstName: FirstName,
    val lastName: LastName,
) {
    fun toAuthor() = Author(AuthorId.generate(), firstName, lastName)
}