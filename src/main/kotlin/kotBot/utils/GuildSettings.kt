package kotBot.utils

import java.awt.Color
import java.sql.ResultSet

data class GuildSettings(
    val guildID: String,
    val defaultColor: Color,
    val partneredGuilds: List<String>,
    val doSexAlarm: Boolean,
) {
    /**
     * Constructor using a ResultSet - make sure that
     */
    constructor(resultSet: ResultSet) : this(
        resultSet.getString("guildID"),
        Color(resultSet.getInt("defaultColor")),
        resultSet.getString("partneredGuilds").split(","),
        resultSet.getBoolean("doSexAlarm")
    )

    constructor(guildID: String) : this(
        Reference.connection.prepareStatement(
            """
            SELECT * FROM GuildSettings WHERE guildID = "$guildID";
        """.trimIndent()
        ).executeQuery()
    )
}