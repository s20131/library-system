package pja.s20131.librarysystem.domain.resource.port

import net.postgis.jdbc.geometry.Point
import pja.s20131.librarysystem.domain.library.ResourceCopy
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.resource.model.Availability
import pja.s20131.librarysystem.domain.resource.model.ResourceId

interface CopyRepository {
    fun getAllBy(resourceId: ResourceId, userLocation: Point?): List<ResourceCopy>
    fun getAvailability(resourceId: ResourceId, libraryId: LibraryId): Availability
    fun upsertAvailability(resourceId: ResourceId, libraryId: LibraryId, availability: Availability)
    fun increaseAvailability(resourceId: ResourceId, libraryId: LibraryId)
    fun decreaseAvailability(resourceId: ResourceId, libraryId: LibraryId)
}
