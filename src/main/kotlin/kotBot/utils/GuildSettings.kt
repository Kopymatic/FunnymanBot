package kotBot.utils

import org.postgresql.util.PSQLException
import java.awt.Color
import java.sql.PreparedStatement
import java.sql.ResultSet

data class GuildSettings(
    var guildID: String,
    var defaultColor: Color,
    var partneredGuilds: MutableList<String>?,
    var doSexAlarm: Boolean,
    var dylanMode: Boolean,
) {
    val rgb = defaultColor.rgb

    /**
     * Constructor using a ResultSet - make sure that
     */
    constructor(resultSet: ResultSet) : this(
        resultSet.getString("guildID"),
        Color(resultSet.getInt("defaultColor")),
        resultSet.getString("partneredGuilds")?.split(",")?.toMutableList(),
        resultSet.getBoolean("doSexAlarm"),
        resultSet.getBoolean("dylanMode")
    )

    constructor(guildID: String) : this(
        try { //Try to get guild, if we fail make it and then continue
            Reference.connection.prepareStatement(
                """
                SELECT * FROM GuildSettings 
                WHERE guildID = '$guildID';
                """.trimIndent()
            ).executeQueryAndNext()
        } catch (e: PSQLException) {
            val ps = Reference.connection.prepareStatement(
                """
                    INSERT INTO GuildSettings
                    VALUES('$guildID', DEFAULT, '$guildID', DEFAULT);
                    """.trimIndent()
            )
            ps.executeUpdate()

            Reference.connection.prepareStatement(
                """
                SELECT * FROM GuildSettings 
                WHERE guildID = ?;
                """.trimIndent()
            ).setStringAndReturnThis(1, guildID).executeQueryAndNext()
        }
    )

    fun push() {
        val ps = Reference.connection.prepareStatement(
            """
                UPDATE GuildSettings
                SET defaultColor = ?, partneredGuilds = ?, doSexAlarm = ?, dylanMode = ?
                WHERE guildID = '$guildID';
            """.trimIndent()
        )
        ps.setInt(1, defaultColor.rgb)
        ps.setString(2, partneredGuilds.toString().removePrefix("[").removeSuffix("]"))
        ps.setBoolean(3, doSexAlarm)
        ps.setBoolean(4, dylanMode)

        ps.executeUpdate()
    }
}

private fun PreparedStatement.setStringAndReturnThis(parameterIndex: Int, value: String): PreparedStatement {
    this.setString(parameterIndex, value)
    return this
}

private fun PreparedStatement.executeQueryAndNext(): ResultSet {
    val rs = this.executeQuery()
    rs.next()
    return rs
}