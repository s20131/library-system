package pja.s20131.librarysystem.domain.resource.port

import net.postgis.jdbc.geometry.Point
import pja.s20131.librarysystem.domain.library.ResourceCopy
import pja.s20131.librarysystem.domain.library.model.LibraryId
import pja.s20131.librarysystem.domain.resource.model.Available
import pja.s20131.librarysystem.domain.resource.model.ResourceId

interface CopyRepository {
    fun getAllBy(resourceId: ResourceId, userLocation: Point?): List<ResourceCopy>
    fun getAvailability(resourceId: ResourceId, libraryId: LibraryId): Available
    fun decreaseAvailability(resourceId: ResourceId, libraryId: LibraryId)
}
