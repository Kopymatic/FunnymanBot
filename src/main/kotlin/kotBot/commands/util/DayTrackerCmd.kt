package kotBot.commands.util

import com.jagrosh.jdautilities.command.CommandEvent
import dev.minn.jda.ktx.Embed
import dev.minn.jda.ktx.await
import kotBot.utils.GuildSettings
import kotBot.utils.KopyCommand
import kotBot.utils.Reference
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.time.LocalDateTime

class DayTrackerCmd : KopyCommand() {
    init {
        name = "DayLogger"
        aliases = arrayOf("log")
        help = "Keep track of how your days are going! ${Reference.mainPrefix}log"
        guildOnly = false
        hidden = true
        category = Reference.utilityCategory
        //this.arguments = "Do ${Reference.mainPrefix}help ${this.name} for advanced help."
    }

    //TODO finish this
    override suspend fun onCommandRun(event: CommandEvent, guildSettings: GuildSettings) {
        GlobalScope.launch { //Start a new coroutine so that this doesn't interrupt the thread
            val time = LocalDateTime.now()

            var dayRating: Int
            do {
                event.reply("How was your day on a scale of 1-10?")
                dayRating = getInput(event).toInt()
            } while (dayRating < 1 || dayRating > 10)

            event.reply("Give a summary of your day:")
            val summary: String = getInput(event)
            if (summary.startsWith("cancel", true)) {event.reply("Cancelled!"); return@launch}

            event.reply(Embed {
                title = time.format(Reference.dateFormatter)
                description = "**Rating:** $dayRating\n**Summary:** $summary"
            })
        }
    }

    private suspend fun getInput(event: CommandEvent): String {
        val e = Reference.jda.await { e: MessageReceivedEvent -> (e.author.id == event.author.id) && (e.channel.id == event.channel.id) }
        return e.message.contentRaw
    }
}