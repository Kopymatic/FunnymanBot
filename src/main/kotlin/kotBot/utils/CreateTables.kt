package kotBot.utils

import java.sql.DriverManager

fun main() {
    try {
        val c = DriverManager.getConnection(Config().url, Config().userName, Config().password)
        val stmt = c.createStatement()
        val sql = "CREATE TABLE LoveCommands " +
             "(SenderID           TEXT  NOT NULL," +
             " ReceiverID         TEXT  NOT NULL, " +
             " ActionIdentifier   TEXT  NOT NULL, " +
             " TimesPerformed     INT   NOT NULL)"
        stmt.executeUpdate(sql)
        stmt.close()
        c.close()
        println("Success!")
    } catch ( e: Exception) {
        println(e.message)
    }

    try {
        val c = DriverManager.getConnection(Config().url, Config().userName, Config().password)
        val stmt = c.createStatement()
        val sql =
            """
                CREATE TABLE NoContext(
                    id SERIAL PRIMARY KEY,
                    guildID TEXT NOT NULL,
                    ImageLink TEXT NOT NULL,
                    LinkTag TEXT,
                    TextTag TEXT,
                    ImporterID TEXT NOT NULL,
                    ImportMessageID TEXT NOT NULL
                );
            """.trimIndent()
        stmt.executeUpdate(sql)
        stmt.close()
        c.close()
        println("Success!")
    } catch ( e: Exception) {
        println(e.message)
    }
}