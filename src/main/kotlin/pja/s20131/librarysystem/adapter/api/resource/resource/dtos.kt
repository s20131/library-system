package pja.s20131.librarysystem.adapter.api.resource.resource

import pja.s20131.librarysystem.domain.person.FirstName
import pja.s20131.librarysystem.domain.person.LastName
import pja.s20131.librarysystem.domain.resource.AddAuthorDto
import pja.s20131.librarysystem.domain.resource.model.AuthorBasicData
import pja.s20131.librarysystem.domain.resource.model.AuthorId
import pja.s20131.librarysystem.domain.resource.model.ResourceBasicData
import pja.s20131.librarysystem.domain.resource.model.ResourceType
import java.time.Instant

data class AddAuthorRequest(
    val firstName: FirstName,
    val lastName: LastName,
) {
    fun toDto(): AddAuthorDto = AddAuthorDto(firstName, lastName)
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
