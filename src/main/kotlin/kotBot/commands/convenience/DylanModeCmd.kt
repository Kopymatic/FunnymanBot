package kotBot.commands.convenience

import com.jagrosh.jdautilities.command.CommandEvent
import kotBot.utils.GuildSettings
import kotBot.utils.KopyCommand
import kotBot.utils.Reference
import net.dv8tion.jda.api.Permission

class DylanModeCmd : KopyCommand() {
    init {
        name = "DylanMode"
        aliases = arrayOf("")
        help = "Dylan mode makes all replies into quotes, this toggles it on and off"
        guildOnly = false
        userPermissions = arrayOf(
            Permission.ADMINISTRATOR
        )
        category = Reference.convenienceCategory
    }

    override suspend fun onCommandRun(event: CommandEvent, guildSettings: GuildSettings) {
        Reference.everyMessageManager.dylanMode = !Reference.everyMessageManager.dylanMode
        if (Reference.everyMessageManager.dylanMode) {
            event.reply("Dylan mode active")
        } else {
            event.reply("Dylan mode inactive")
        }
    }
}