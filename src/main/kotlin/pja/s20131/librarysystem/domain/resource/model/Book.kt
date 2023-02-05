package pja.s20131.librarysystem.domain.resource.model

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
