package pja.s20131.librarysystem.resource

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import pja.s20131.librarysystem.adapter.database.resource.AuthorTable
import pja.s20131.librarysystem.adapter.database.resource.ResourceTable
import pja.s20131.librarysystem.adapter.database.resource.SeriesTable
import pja.s20131.librarysystem.adapter.database.resource.toAuthor
import pja.s20131.librarysystem.domain.resource.model.Author
import pja.s20131.librarysystem.domain.resource.model.AuthorId
import pja.s20131.librarysystem.domain.resource.model.Resource
import pja.s20131.librarysystem.domain.resource.model.Series

@Component
@Transactional
class ResourceDatabaseHelper {

    fun getAuthor(authorId: AuthorId) =
        AuthorTable
            .select { AuthorTable.id eq authorId.value }
            .single()
            .toAuthor()

    fun insertAuthor(author: Author) =
        AuthorTable.insert {
            it[id] = author.authorId.value
            it[firstName] = author.firstName.value
            it[lastName] = author.lastName.value
        }

    fun insertSeries(series: Series) =
        SeriesTable.insert {
            it[SeriesTable.id] = series.value
        }

    fun insertResource(resource: Resource) =
        ResourceTable.insert {
            it[id] = resource.resourceId.value
            it[title] = resource.title.value
            it[author] = resource.author.authorId.value
            it[releaseDate] = resource.releaseDate.value
            it[description] = resource.description?.value
            it[series] = resource.series?.value
            it[status] = resource.status
        }

    fun insertResourceDependencies(author: Author, series: Series) {
        insertAuthor(author)
        insertSeries(series)
    }
}
