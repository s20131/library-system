package pja.s20131.librarysystem.domain.resource.book

import pja.s20131.librarysystem.domain.resource.Description
import pja.s20131.librarysystem.domain.resource.ReleaseDate
import pja.s20131.librarysystem.domain.resource.ResourceId
import pja.s20131.librarysystem.domain.resource.ResourceStatus
import pja.s20131.librarysystem.domain.resource.Series
import pja.s20131.librarysystem.domain.resource.Title

data class Book (
    val resourceId: ResourceId,
    val title: Title,
    val releaseDate: ReleaseDate,
    val description: Description,
    val series: Series,
    val resourceStatus: ResourceStatus,
    val isbn: ISBN,
)

@JvmInline
value class ISBN(val raw: String)
