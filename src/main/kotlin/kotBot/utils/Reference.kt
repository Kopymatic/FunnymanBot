package kotBot.utils

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandClient
import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import jdk.jfr.Event
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Activity
import java.awt.Color
import java.time.format.DateTimeFormatterBuilder

import java.time.format.DateTimeFormatter

class Reference {
    companion object {
        const val experimental = true
        const val botName = "KotBot"
        const val version = "4.1"
        val token = if (!experimental) Config().mainToken else Config().devToken
        val status = Activity.watching("V$version ${if (experimental) "Experimental" else ""}")
        val dateFormatter: DateTimeFormatter = DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("L/d/yy h:mm a").toFormatter()

        //Categories!
        val convenienceCategory = Command.Category("Convenience")
        val funCategory = Command.Category("Fun")
        val utilityCategory = Command.Category("Utility")
        val quickStringCategory = Command.Category("QuickStringCommands")
        val categories = arrayOf(funCategory, convenienceCategory, utilityCategory, quickStringCategory)

        val mainPrefix = if(!experimental) "pp" else "dd"
        val alternativePrefix = if(!experimental) "p!" else "d!"
        val prefixes = arrayOf(mainPrefix, alternativePrefix)
        val defaultColor: Color = Color(255, 111, 255)
        val rgb: Int = Color(255, 111, 255).rgb

        val kdb = KopyDB()
        lateinit var jda: JDA
        val waiter: EventWaiter = EventWaiter()
        lateinit var cmdClient: CommandClient
        val everyMessageManager = EveryMessageManager()
    }
}
