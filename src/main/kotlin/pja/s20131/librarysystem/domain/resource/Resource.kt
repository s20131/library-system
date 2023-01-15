package pja.s20131.librarysystem.domain.resource

import java.time.LocalDate
import java.util.UUID

@JvmInline
value class ResourceId(val raw: UUID) {

    companion object {
        fun generate() = ResourceId(UUID.randomUUID())
    }
}

@JvmInline
value class Title(val raw: String)

@JvmInline
value class ReleaseDate(val raw: LocalDate)

@JvmInline
value class Description(val raw: String)

@JvmInline
value class Series(val raw: String)

enum class ResourceStatus {
    AVAILABLE, WITHDRAWN
}
