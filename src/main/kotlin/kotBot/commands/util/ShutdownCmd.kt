package kotBot.commands.util

import com.jagrosh.jdautilities.command.CommandEvent
import kotBot.utils.GuildSettings
import kotBot.utils.KopyCommand
import kotBot.utils.Reference

class ShutdownCmd : KopyCommand() {
    init {
        name = "Shutdown"
        aliases = arrayOf("stop")
        help = "safely shuts off the bot"
        ownerCommand = true
        hidden = true
        guildOnly = false
        category = Reference.utilityCategory
        doTyping = false
    }

    override suspend fun onCommandRun(event: CommandEvent, guildSettings: GuildSettings) {
        event.reactWarning()
        event.jda.shutdown()
    }
}