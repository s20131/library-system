package pja.s20131.librarysystem.adapter.api.reservations

import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.library.model.LibraryName
import pja.s20131.librarysystem.domain.resource.model.AuthorBasicData
import pja.s20131.librarysystem.domain.resource.model.FinishDate
import pja.s20131.librarysystem.domain.resource.model.ResourceBasicData
import pja.s20131.librarysystem.domain.resource.model.ResourceType

data class GetReservationHistoryResponse(
    val libraryId: LibraryId,
    val resource: ResourceBasicData,
    val author: AuthorBasicData,
    val finishDate: FinishDate,
    val resourceType: ResourceType,
)

data class GetReservationShortInfoResponse(
    val finish: FinishDate,
    val library: LibraryName
)
