package pja.s20131.librarysystem.adapter.database.exposed

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.ExpressionWithColumnType
import org.jetbrains.exposed.sql.Function
import org.jetbrains.exposed.sql.QueryBuilder
import org.jetbrains.exposed.sql.Table

class VectorColumnType : ColumnType() {
    override fun sqlType(): String {
        return "TSVECTOR"
    }
}

class TsQuery(
    private val expr: ExpressionWithColumnType<String>,
    private val query: String
) : Function<Boolean>(expr.columnType) {
    override fun toQueryBuilder(queryBuilder: QueryBuilder) = queryBuilder {
        append("${(expr as Column).name} @@ to_tsquery(\'$query\')")
    }
}

fun Table.tsvector(name: String): Column<String> = registerColumn(name, VectorColumnType())
