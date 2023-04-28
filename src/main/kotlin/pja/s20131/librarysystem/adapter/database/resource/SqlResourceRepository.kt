package pja.s20131.librarysystem.adapter.database.resource

import java.time.Instant
import org.jetbrains.exposed.sql.ISqlExpressionBuilder
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.replace
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.union
import org.springframework.stereotype.Repository
import pja.s20131.librarysystem.adapter.database.resource.StorageTable.eqKey
import pja.s20131.librarysystem.adapter.database.resource.StorageTable.toStoredResource
import pja.s20131.librarysystem.adapter.database.user.UserTable
import pja.s20131.librarysystem.domain.person.FirstName
import pja.s20131.librarysystem.domain.person.LastName
import pja.s20131.librarysystem.domain.resource.model.AuthorBasicData
import pja.s20131.librarysystem.domain.resource.model.ResourceBasicData
import pja.s20131.librarysystem.domain.resource.model.ResourceId
import pja.s20131.librarysystem.domain.resource.model.ResourceType
import pja.s20131.librarysystem.domain.resource.model.Title
import pja.s20131.librarysystem.domain.resource.port.ResourceRepository
import pja.s20131.librarysystem.domain.resource.port.StoredResource
import pja.s20131.librarysystem.domain.user.model.UserId

@Repository
class SqlResourceRepository : ResourceRepository {

    override fun getFromStorageBy(userId: UserId): List<StoredResource> {
        val sinceAlias = StorageTable.since.alias("timestamps")
        val columns = listOf(sinceAlias, *AuthorTable.columns.toTypedArray(), ResourceTable.id, ResourceTable.title)
        val booksQuery = StorageTable
            .innerJoin(UserTable)
            .innerJoin(ResourceTable)
            .innerJoin(BookTable)
            .innerJoin(AuthorTable)
            .slice(columns)
            .select { StorageTable.userId eq userId.value }
        val ebooksQuery = StorageTable
            .innerJoin(UserTable)
            .innerJoin(ResourceTable)
            .innerJoin(EbookTable)
            .innerJoin(AuthorTable)
            .slice(columns)
            .select { StorageTable.userId eq userId.value }
        return booksQuery
            .union(ebooksQuery)
            .orderBy(sinceAlias, SortOrder.DESC)
            .map { resultRow ->
                when {
                    booksQuery.any { resultRow[ResourceTable.id] == it[ResourceTable.id] } ->
                        resultRow.toStoredResource(ResourceType.BOOK)

                    else -> resultRow.toStoredResource(ResourceType.EBOOK)
                }
            }
    }

    override fun getIsInUserStorage(userId: UserId, resourceId: ResourceId): Boolean {
        // TODO with database query
        //val existsOp = exists(StorageTable.select { eqKey(userId, resourceId) })
        return StorageTable.select {
            eqKey(userId, resourceId)
        }.any()
    }

    override fun addToUserStorage(userId: UserId, resourceId: ResourceId, since: Instant) {
        StorageTable.replace {
            it[this.userId] = userId.value
            it[this.resourceId] = resourceId.value
            it[this.since] = since
        }
    }

    override fun removeFromUserStorage(userId: UserId, resourceId: ResourceId) {
        StorageTable.deleteWhere {
            it.eqKey(userId, resourceId)
        }
    }

}

object StorageTable : Table("storage") {
    val userId = reference("user_id", UserTable)
    val resourceId = reference("resource_id", ResourceTable)
    val since = timestamp("since")
    override val primaryKey = PrimaryKey(userId, resourceId)

    fun ISqlExpressionBuilder.eqKey(userId: UserId, resourceId: ResourceId): Op<Boolean> =
        StorageTable.userId eq userId.value and (StorageTable.resourceId eq resourceId.value)

    fun ResultRow.toStoredResource(resourceType: ResourceType) = StoredResource(
        ResourceBasicData(
            ResourceId(this[ResourceTable.id].value),
            Title(this[ResourceTable.title]),
        ),
        AuthorBasicData(
            FirstName(this[AuthorTable.firstName]),
            LastName(this[AuthorTable.lastName])
        ),
        resourceType,
        this[since]
    )
}
