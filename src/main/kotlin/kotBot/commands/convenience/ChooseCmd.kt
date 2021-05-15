package kotBot.commands.convenience

import com.jagrosh.jdautilities.command.CommandEvent
import kotBot.utils.GuildSettings
import kotBot.utils.KopyCommand
import kotBot.utils.Reference
import java.util.*

class ChooseCmd : KopyCommand() {
    init {
        name = "Choose"
        aliases = arrayOf("pick")
        help = "Pick from a list you supply. Separate arguments with `/`"
        arguments = "[Thing1 / Thing2 / Thing3...]"
        guildOnly = false
        category = Reference.convenienceCategory
    }

    override suspend fun onCommandRun(event: CommandEvent, guildSettings: GuildSettings) {
        //Get the options then split and pick one to send
        val args = event.args.split("/").toTypedArray()
        val chosenOne = args[Random().nextInt(args.size)].trim()
        event.reply("I pick `$chosenOne`")
    }
}