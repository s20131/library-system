package pja.s20131.librarysystem.domain.resource

data class Ebook(
    val resource: Resource,
    val format: Format,
    val content: Content,
    val size: Size,
    val sizeUnit: SizeUnit
)

@JvmInline
value class Format(val raw: String)

@JvmInline
value class Content(val raw: ByteArray)

@JvmInline
value class Size(val raw: Double)

enum class SizeUnit {
    kB, MB
}
