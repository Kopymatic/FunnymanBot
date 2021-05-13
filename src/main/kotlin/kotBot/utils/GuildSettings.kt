package kotBot.utils

import org.postgresql.util.PSQLException
import java.awt.Color
import java.sql.PreparedStatement
import java.sql.ResultSet

data class GuildSettings(
    var guildID: String,
    var defaultColor: Color,
    var partneredGuilds: List<String>?,
    var doSexAlarm: Boolean,
) {
    val rgb = defaultColor.rgb

    /**
     * Constructor using a ResultSet - make sure that
     */
    constructor(resultSet: ResultSet) : this(
        resultSet.getString("guildID"),
        Color(resultSet.getInt("defaultColor")),
        resultSet.getString("partneredGuilds")?.split(","),
        resultSet.getBoolean("doSexAlarm")
    )

    constructor(guildID: String) : this(
        try { //Try to get guild, if we fail make it and then continue
            Reference.connection.prepareStatement(
                """
                SELECT * FROM GuildSettings 
                WHERE guildID = ?;
                """.trimIndent()
            ).setStringAndReturnThis(1, guildID).executeQueryAndNext()
        } catch (e: PSQLException) {
            val ps = Reference.connection.prepareStatement(
                """
                    INSERT INTO GuildSettings
                    VALUES(?, DEFAULT, NULL, DEFAULT);
                    """.trimIndent()
            )
            ps.setString(1, guildID)
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
                SET defaultColor = ?, partneredGuilds = ?, doSexAlarm = ?
                WHERE guildID = ?;
            """.trimIndent()
        )
        ps.setInt(1, defaultColor.rgb)
        ps.setString(2, partneredGuilds.toString())
        ps.setBoolean(3, doSexAlarm)

        ps.setString(4, guildID) //REMEMBER TO UPDATE THIS INDEX WHEN ADDING NEW VARIABLES!!!!!!!!!!!!!!
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