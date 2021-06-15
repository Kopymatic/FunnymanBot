package kotBot.utils

import java.awt.Color
import java.sql.PreparedStatement
import java.sql.ResultSet

data class GuildSettings(
    var guildID: String,
    var defaultColor: Color,
    var partneredGuilds: MutableList<String>?,
    var doSexAlarm: Boolean,
    var dylanMode: Boolean,
    var joeMode: Boolean
) {
    val rgb = defaultColor.rgb

    /**
     * Constructor using a ResultSet - make sure that
     */
    constructor(resultSet: ResultSet) : this(
        resultSet.getString("guildID"),
        Color(resultSet.getInt("defaultColor")),
        makeList(resultSet),
        resultSet.getBoolean("doSexAlarm"),
        resultSet.getBoolean("dylanMode"),
        resultSet.getBoolean("JoeMode")
    )

    constructor(guildID: String) : this(
        try { //Try to get guild, if we fail make it and then continue
            Reference.connection.prepareStatement(
                """
                SELECT * FROM GuildSettings 
                WHERE guildID = '$guildID';
                """.trimIndent()
            ).executeQueryAndNext()
        } catch (e: IndexOutOfBoundsException) {
            val ps = Reference.connection.prepareStatement(
                """
                    INSERT INTO GuildSettings
                    VALUES('$guildID', DEFAULT, '$guildID', DEFAULT, DEFAULT);
                    """.trimIndent() //WHEN YOU ADD NEW THINGS UPDATE THIS
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

    constructor() : this(
        "000000000000000000", Color(255, 111, 255), mutableListOf(""), false, false, false //Defaults
    )

    fun push() {
        val ps = Reference.connection.prepareStatement(
            """
                UPDATE GuildSettings
                SET defaultColor = ?, partneredGuilds = ?, doSexAlarm = ?, dylanMode = ?, JoeMode = ?
                WHERE guildID = '$guildID';
            """.trimIndent()
        )
        ps.setInt(1, defaultColor.rgb)
        ps.setString(2, partneredGuilds.toString().removePrefix("[").removeSuffix("]"))
        ps.setBoolean(3, doSexAlarm)
        ps.setBoolean(4, dylanMode)
        ps.setBoolean(5, joeMode)

        ps.executeUpdate()
    }

    companion object {
        private fun makeList(resultSet: ResultSet): MutableList<String>? {
            val list = resultSet.getString("partneredGuilds")?.split(",")?.toMutableList()
            if (list != null) {
                for (i in 0 until list.size) {
                    list[i] = list[i].trim()
                }
            }
            return list
        }
    }
}

private fun PreparedStatement.setStringAndReturnThis(parameterIndex: Int, value: String): PreparedStatement {
    this.setString(parameterIndex, value)
    return this
}

private fun PreparedStatement.executeQueryAndNext(): ResultSet {
    val rs = this.executeQuery()
    //Throw an exception if it is empty. Because i am lazy.
    if (!rs.next()) throw ArrayIndexOutOfBoundsException()
    return rs
}