package pja.s20131.librarysystem.domain.resource.book

import pja.s20131.librarysystem.domain.resource.Description
import pja.s20131.librarysystem.domain.resource.ReleaseDate
import pja.s20131.librarysystem.domain.resource.Resource
import pja.s20131.librarysystem.domain.resource.ResourceId
import pja.s20131.librarysystem.domain.resource.ResourceStatus
import pja.s20131.librarysystem.domain.resource.Series
import pja.s20131.librarysystem.domain.resource.Title

data class Book (
    override val resourceId: ResourceId,
    override val title: Title,
    override val releaseDate: ReleaseDate,
    override val description: Description?,
    override val series: Series?,
    override val status: ResourceStatus,
    val isbn: ISBN,
) : Resource()

@JvmInline
value class ISBN(val value: String)
