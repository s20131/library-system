package pja.s20131.librarysystem.domain.resource

import org.springframework.stereotype.Service
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.user.model.UserId

@Service
class ReservationService {

    fun reserveResource(resourceId: ResourceId, libraryId: LibraryId, userId: UserId) {

    }
}
