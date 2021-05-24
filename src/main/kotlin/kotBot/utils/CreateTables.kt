package kotBot.utils

import java.sql.DriverManager

fun main() {
    create( //Create LoveCommands
        """
            CREATE TABLE LoveCommands(
            SenderID TEXT NOT NULL,
            ReceiverID TEXT NOT NULL,
            ActionIdentifier TEXT NOT NULL,
            TimesPerformed INT NOT NULL,
            primaryKey SERIAL PRIMARY KEY
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

    create( //Create dayLogger
        """
            CREATE TABLE DayLogger (
               logTime TIMESTAMP NOT NULL,
               userID TEXT NOT NULL,
               logRating INT NOT NULL,
               logTextRating TEXT,
               logSummary TEXT
            );
        """.trimIndent()
    )

    create( //Create guildSettings //DEFAULTCOLOR IS USING COLOR.RGB
        """
            CREATE TABLE GuildSettings (
               guildID TEXT PRIMARY KEY,
               defaultColor INT NOT NULL DEFAULT -36865,
               partneredGuilds TEXT,
               doSexAlarm BOOLEAN NOT NULL DEFAULT true,
               dylanMode boolean NOT NULL DEFAULT false
            );
        """.trimIndent()
    )

    create(
        """
            CREATE TABLE ClickerUsers (
               userID TEXT PRIMARY KEY,
               lastKnownUsername TEXT,
               cookies TEXT DEFAULT '0',
               cursors INT DEFAULT 0,
               grandmas INT DEFAULT 0,
               farms INT DEFAULT 0,
               mines INT DEFAULT 0,
               factories INT DEFAULT 0,
               banks INT DEFAULT 0,
               temples INT DEFAULT 0,
               wizardTowers INT DEFAULT 0,
               shipments INT DEFAULT 0,
               alchemyLabs INT DEFAULT 0,
               portals INT DEFAULT 0,
               timeMachines INT DEFAULT 0,
               antimatterCondensers INT DEFAULT 0,
               prisms INT DEFAULT 0,
               chancemakers INT DEFAULT 0,
               fractalEngines INT DEFAULT 0,
               javascriptConsoles INT DEFAULT 0,
               idleverses INT DEFAULT 0
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

fun update(sql: String) {
    try {
        val c = DriverManager.getConnection(Config().url, Config().userName, Config().password)
        val stmt = c.createStatement()
        stmt.executeUpdate(sql)
        stmt.close()
        c.close()
        println("Edit Success!")
    } catch (e: Exception) {
        println(e.message)
    }
}