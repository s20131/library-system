package pja.s20131.librarysystem.adapter.database.exposed

import net.postgis.jdbc.PGgeography
import net.postgis.jdbc.geometry.Point
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.CustomFunction
import org.jetbrains.exposed.sql.DoubleColumnType
import org.jetbrains.exposed.sql.QueryParameter


class GeographyColumnType : ColumnType() {
    override fun sqlType(): String {
        return "GEOGRAPHY"
    }
}

infix fun Column<Point>.stDistance(from: Point) =
    CustomFunction<Double>("ST_DISTANCE", DoubleColumnType(), geographyParam(PGgeography(from)), this)

private fun geographyParam(value: PGgeography) = QueryParameter(value, GeographyColumnType())
