package kotBot.commands.util

import com.jagrosh.jdautilities.command.CommandEvent
import dev.minn.jda.ktx.Embed
import kotBot.utils.GuildSettings
import kotBot.utils.KopyCommand
import kotBot.utils.Reference
import java.time.LocalDateTime

class AboutCmd : KopyCommand() {
    init {
        name = "About"
        aliases = arrayOf("a")
        help = "Shows details about the bot - currently only has uptime and version"
        guildOnly = false
        category = Reference.utilityCategory
    }

    override suspend fun onCommandRun(event: CommandEvent, guildSettings: GuildSettings) {
        event.reply(
            Embed(
                title = "About FunnymanBot",
                description = "This is a bot made by Kopymatic",
                fields = arrayListOf(
                    makeField(
                        "Uptime",
                        ("" + (LocalDateTime.ofEpochSecond(Reference.bootTime - System.currentTimeMillis(), 0, null)))
                    )
                )
            )
        )
    }
}