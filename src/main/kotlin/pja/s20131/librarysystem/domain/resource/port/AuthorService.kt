package pja.s20131.librarysystem.domain.resource.port

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.domain.person.FirstName
import pja.s20131.librarysystem.domain.person.LastName
import pja.s20131.librarysystem.domain.resource.model.Author
import pja.s20131.librarysystem.domain.resource.model.AuthorId

@Service
@Transactional
class AuthorService(
    val authorRepository: AuthorRepository,
) {
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
