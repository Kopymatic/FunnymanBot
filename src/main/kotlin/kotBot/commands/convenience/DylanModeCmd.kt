package kotBot.commands.convenience

import com.jagrosh.jdautilities.command.CommandEvent
import kotBot.utils.KopyCommand
import kotBot.utils.Reference

class DylanModeCmd : KopyCommand() {
    init {
        name = "DylanMode"
        aliases = arrayOf("")
        help = "Dylan mode makes all replies into quotes, this toggles it on and off"
        guildOnly = false
        category = Reference.convenienceCategory
    }

    override fun onCommandRun(event: CommandEvent) {
        Reference.everyMessageManager.dylanMode = !Reference.everyMessageManager.dylanMode
        if(Reference.everyMessageManager.dylanMode) {
            event.reply("Dylan mode active")
        } else {
            event.reply("Dylan mode inactive")
        }
    }
}