package kotBot.commands.convenience

import kotBot.utils.KopyCommand
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.doc.standard.CommandInfo
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

    override fun onCommandRun(event: CommandEvent) {
        //Get the options then split and pick one to send
        val args = event.args.split("/").toTypedArray()
        val chosenOne = args[Random().nextInt(args.size)].trim()
        event.reply("I pick `$chosenOne`")
    }
}