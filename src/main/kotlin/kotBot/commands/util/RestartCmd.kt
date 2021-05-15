package kotBot.commands.util

import com.jagrosh.jdautilities.command.CommandEvent
import kotBot.main
import kotBot.utils.GuildSettings
import kotBot.utils.KopyCommand
import kotBot.utils.Reference

class RestartCmd : KopyCommand() {
    init {
        name = "Restart"
        help = "Restarts the bot"
        ownerCommand = true
        hidden = true
        guildOnly = false
        category = Reference.utilityCategory
        doTyping = false
    }

    override suspend fun onCommandRun(event: CommandEvent, guildSettings: GuildSettings) {
        event.reactSuccess()
        println("Restarted - This may cause issues")
        event.jda.shutdown()
        main()
    }
}