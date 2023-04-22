package pja.s20131.librarysystem.domain.resource.model

import java.time.LocalDate
import java.util.UUID

sealed class Resource {
    abstract val resourceId: ResourceId
    abstract val title: Title
    abstract val authorId: AuthorId
    abstract val releaseDate: ReleaseDate
    abstract val description: Description?
    abstract val series: Series?
    abstract val status: ResourceStatus

    fun isAvailable(): Boolean = status == ResourceStatus.AVAILABLE

    fun toBasicData(): ResourceBasicData = ResourceBasicData(resourceId, title)
}

@JvmInline
value class ResourceId(val value: UUID) {
    companion object {
        fun generate() = ResourceId(UUID.randomUUID())
    }
}

@JvmInline
value class Title(val value: String)

@JvmInline
value class ReleaseDate(val value: LocalDate)

@JvmInline
value class Description(val value: String)

@JvmInline
value class Series(val value: String)

enum class ResourceStatus {
    AVAILABLE, WITHDRAWN
}

enum class ResourceType {
    BOOK, EBOOK
}

data class ResourceBasicData(
    val id: ResourceId,
    val title: Title,
)
