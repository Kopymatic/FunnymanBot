package kotBot.commands.util

import com.jagrosh.jdautilities.command.CommandEvent
import kotBot.utils.GuildSettings
import kotBot.utils.KopyCommand
import kotBot.utils.Reference
import net.dv8tion.jda.api.entities.Activity

class UpdateStatusCmd : KopyCommand() {
    init {
        name = "UpdateStatus"
        aliases = arrayOf("us")
        help = "Updates the bot's status"
        arguments = "[Playing, Competing, Listening, Watching, Default, none] [text]"
        ownerCommand = true
        hidden = true
        guildOnly = false
        category = Reference.utilityCategory
        doTyping = false
    }

    override suspend fun onCommandRun(event: CommandEvent, guildSettings: GuildSettings) {
        val args = event.args
        try {
            event.jda.presence.activity = when {
                args == "" -> null
                args.startsWith("Playing", true) -> Activity.playing(args.replace("Playing", "", true))
                args.startsWith("Competing", true) -> Activity.competing(args.replace("Competing", "", true))
                args.startsWith("Listening", true) -> Activity.listening(args.replace("Listening", "", true))
                args.startsWith("Watching", true) -> Activity.watching(args.replace("Watching", "", true))
                args.startsWith("Default", true) -> Reference.status
                else -> Activity.playing(args)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            event.reactError()
            return
        }
        event.reactSuccess()
    }
}