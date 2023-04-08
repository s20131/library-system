package pja.s20131.librarysystem.adapter.api.resource.resource

import pja.s20131.librarysystem.domain.person.FirstName
import pja.s20131.librarysystem.domain.person.LastName
import pja.s20131.librarysystem.domain.resource.model.AuthorId
import pja.s20131.librarysystem.domain.resource.port.AddAuthorCommand

data class AddAuthorRequest(
    val firstName: FirstName,
    val lastName: LastName,
) {
    fun toCommand() = AddAuthorCommand(firstName, lastName)
}

data class GetAuthorResponse(
    val authorId: AuthorId,
    val firstName: FirstName,
    val lastName: LastName,
)
