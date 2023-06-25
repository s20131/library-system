package pja.s20131.librarysystem.adapter.api.rentals

import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.library.model.LibraryName
import pja.s20131.librarysystem.domain.resource.model.AuthorBasicData
import pja.s20131.librarysystem.domain.resource.model.FinishTime
import pja.s20131.librarysystem.domain.resource.model.Penalty
import pja.s20131.librarysystem.domain.resource.model.RentalStatus
import pja.s20131.librarysystem.domain.resource.model.ResourceBasicData
import pja.s20131.librarysystem.domain.resource.model.ResourceType
import pja.s20131.librarysystem.domain.resource.model.StartDate

data class GetRentalHistoryResponse(
    val libraryId: LibraryId,
    val resource: ResourceBasicData,
    val author: AuthorBasicData,
    val startDate: StartDate,
    val rentalStatus: RentalStatus,
    val resourceType: ResourceType,
)

data class GetRentalShortInfoResponse(
    val rentalStatus: RentalStatus,
    val finish: FinishTime,
    val library: LibraryName,
    val penalty: Penalty?,
)

data class GetRentalPenaltyInfoResponse(
    val finish: FinishTime,
    val status: RentalStatus,
    val penalty: Penalty?,
)
