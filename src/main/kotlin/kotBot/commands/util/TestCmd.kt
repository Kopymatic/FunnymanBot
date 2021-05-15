package kotBot.commands.util

import com.jagrosh.jdautilities.command.CommandEvent
import kotBot.utils.GuildSettings
import kotBot.utils.KopyCommand
import kotBot.utils.Reference

class TestCmd : KopyCommand() {
    init {
        name = "Test"
        help = "for testing"
        ownerCommand = true
        hidden = true
        category = Reference.utilityCategory
    }

    override fun execute(event: CommandEvent?) {
        val guilds = Reference.jda.guilds
        for (guild in guilds) {
            val ps = Reference.connection.prepareStatement(
                """
            INSERT INTO GuildSettings
            VALUES(?, DEFAULT, NULL, DEFAULT);
            """.trimIndent()
            )
            ps.setString(1, guild.id) //Set the first ? in the prepared statement to guildID
            ps.executeUpdate()
        }
    }

    override suspend fun onCommandRun(event: CommandEvent, guildSettings: GuildSettings) {
        TODO("Not yet implemented")
    }
}