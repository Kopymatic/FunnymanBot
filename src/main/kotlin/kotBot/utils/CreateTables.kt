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
    } catch ( e: Exception) {
        println("Already Exists!")
    }
}