package pja.s20131.librarysystem.adapter.database.exposed

import net.postgis.jdbc.PGgeometry
import net.postgis.jdbc.geometry.Point
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table


class PointColumnType(private val srid: Int = 4326) : ColumnType() {
    override fun sqlType() = "GEOMETRY(Point, $srid)"

    override fun valueFromDB(value: Any): Any = if (value is PGgeometry) value.geometry else value
}

fun Table.point(name: String): Column<Point> = registerColumn(name, PointColumnType())
