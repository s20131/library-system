package pja.s20131.librarysystem.domain.resource

import java.time.LocalDate
import java.util.UUID

abstract class Resource {
    abstract val resourceId: ResourceId
    abstract val title: Title
    abstract val releaseDate: ReleaseDate
    abstract val description: Description?
    abstract val series: Series?
    abstract val status: ResourceStatus

    fun isAvailable() = status == ResourceStatus.AVAILABLE
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
