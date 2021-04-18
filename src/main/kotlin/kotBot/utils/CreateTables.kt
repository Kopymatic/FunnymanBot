package kotBot.utils

import java.sql.DriverManager

fun main() {
    create( //Create LoveCommands
        """
            CREATE TABLE LoveCommands(
            SenderID TEXT NOT NULL,
            ReceiverID TEXT NOT NULL,
            ActionIdentifier TEXT NOT NULL,
            TimesPerformed INT NOT NULL
            );
        """.trimIndent()
    )

    create( //Create NoContext
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
    )

    create( //Create People
        """
                CREATE TABLE People(
                    id SERIAL PRIMARY KEY,
                    guildID TEXT NOT NULL,
                    ImageLink TEXT NOT NULL,
                    LinkTag TEXT,
                    TextTag TEXT,
                    ImporterID TEXT NOT NULL,
                    ImportMessageID TEXT NOT NULL
                );
            """.trimIndent()
    )

    create( //Create Pets
        """
                CREATE TABLE Pets(
                    id SERIAL PRIMARY KEY,
                    guildID TEXT NOT NULL,
                    ImageLink TEXT NOT NULL,
                    LinkTag TEXT,
                    TextTag TEXT,
                    ImporterID TEXT NOT NULL,
                    ImportMessageID TEXT NOT NULL
                );
            """.trimIndent()
    )

    create( //Create Meme
        """
                CREATE TABLE Memes(
                    id SERIAL PRIMARY KEY,
                    guildID TEXT NOT NULL,
                    ImageLink TEXT NOT NULL,
                    LinkTag TEXT,
                    TextTag TEXT,
                    ImporterID TEXT NOT NULL,
                    ImportMessageID TEXT NOT NULL
                );
            """.trimIndent()
    )
}

fun create(sql: String) {
    try {
        val c = DriverManager.getConnection(Config().url, Config().userName, Config().password)
        val stmt = c.createStatement()
        stmt.executeUpdate(sql)
        stmt.close()
        c.close()
        println("Success!")
    } catch (e: Exception) {
        println(e.message)
    }
}