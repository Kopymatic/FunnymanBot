package kotBot.commands.util

import com.jagrosh.jdautilities.command.CommandEvent
import kotBot.utils.GuildSettings
import kotBot.utils.KopyCommand
import kotBot.utils.Reference

class ShutdownCmd : KopyCommand() {
    init {
        name = "Shutdown"
        aliases = arrayOf("kill", "stop")
        help = "safely shuts off the bot"
        ownerCommand = true
        hidden = true
        guildOnly = false
        category = Reference.utilityCategory
    }

    override suspend fun onCommandRun(event: CommandEvent, guildSettings: GuildSettings) {
        event.reactWarning()
        event.jda.shutdown()
    }
}