package kotBot.utils

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandClient
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Activity
import java.awt.Color
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder

class Reference {
    companion object {
        const val experimental = true
        const val botName = "Funnyman"
        var version: String = "4.3"
        val token = if (!experimental) Config().mainToken else Config().devToken
        val status = Activity.watching("V$version ${if (experimental) "Experimental" else ""}")
        val ownerID = "326489320980611075"
        val dateFormatter: DateTimeFormatter =
            DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("L/d/yy h:mm a").toFormatter()
        var doTyping: Boolean = true //This determines if the bot will send a typing status on every command

        //Categories!
        val convenienceCategory = Command.Category("Convenience")
        val funCategory = Command.Category("Fun")
        val utilityCategory = Command.Category("Utility")
        val quickStringCategory = Command.Category("QuickStringCommands")
        val categories = arrayOf(funCategory, convenienceCategory, utilityCategory, quickStringCategory)

        val mainPrefix = if (!experimental) "pp" else "dd"
        val alternativePrefix = if (!experimental) "p!" else "d!"
        val prefixes = arrayOf(mainPrefix, alternativePrefix)
        var defaultColor: Color = Color(255, 111, 255)
        var rgb: Int = defaultColor.rgb

        val kdb = KopyDB()
        val connection = kdb.connection
        lateinit var jda: JDA
        val waiter: EventWaiter = EventWaiter()
        lateinit var cmdClient: CommandClient
        val everyMessageManager = EverythingListener()
    }

    class ConfigCmd : KopyCommand() {
        init {
            name = "Config"
            arguments = "no args for help"
            help = "Change configurable variables"
            guildOnly = false
            hidden = true
            ownerCommand = true
            category = utilityCategory
        }

        override suspend fun onCommandRun(event: CommandEvent, guildSettings: GuildSettings) {
            if (event.args.isBlank()) {
                event.reply(
                    """
                    Current variables:
                    ```kotlin
                    doTyping: Boolean = $doTyping
                    version: String = $version
                    defaultColor: Color (rgb format) = ${defaultColor.red}, ${defaultColor.green}, ${defaultColor.blue}
                    ```
                    Change them with `${mainPrefix}${this.name} [variable] = [assignment]`
                """.trimIndent()
                )
            } else {
                val args = event.args.split("=")
                if (args.size < 2) {
                    event.reply("Not enough args - make sure to remember the equal sign"); return
                }
                when (args[0].trim()) {
                    "doTyping" -> doTyping = args[1].toBoolean()
                    "version" -> version = args[1]
                    "defaultColor" -> {
                        val rgb = args[1].split(",")
                        defaultColor = Color(rgb[0].trim().toInt(), rgb[1].trim().toInt(), rgb[2].trim().toInt())
                        Reference.rgb = defaultColor.rgb
                    }
                    else -> event.reply("Variable not found")
                }
                event.reactSuccess()
            }
        }
    }

}
