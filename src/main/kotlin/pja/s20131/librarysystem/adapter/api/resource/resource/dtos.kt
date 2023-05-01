package pja.s20131.librarysystem.adapter.api.resource.resource

import java.time.Instant
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.person.FirstName
import pja.s20131.librarysystem.domain.person.LastName
import pja.s20131.librarysystem.domain.resource.model.AuthorBasicData
import pja.s20131.librarysystem.domain.resource.model.AuthorId
import pja.s20131.librarysystem.domain.resource.model.Available
import pja.s20131.librarysystem.domain.resource.model.ResourceBasicData
import pja.s20131.librarysystem.domain.resource.model.ResourceType
import pja.s20131.librarysystem.domain.resource.port.AddAuthorCommand

data class AddAuthorRequest(
    val firstName: FirstName,
    val lastName: LastName,
) {
    fun toCommand(): AddAuthorCommand = AddAuthorCommand(firstName, lastName)
}

data class GetAuthorResponse(
    val authorId: AuthorId,
    val firstName: FirstName,
    val lastName: LastName,
)

data class GetStoredResourceResponse(
    val resource: ResourceBasicData,
    val author: AuthorBasicData,
    val resourceType: ResourceType,
    val since: Instant,
)

data class GetCopyResponse(
    val libraryId: LibraryId,
    val available: Available,
)
