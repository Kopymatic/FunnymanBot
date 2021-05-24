package kotBot.utils

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandClient
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Activity
import java.awt.Color
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import kotlin.system.exitProcess

class Reference {
    companion object {
        const val experimental = true
        const val botName = "Funnyman"
        var version: String = "4.5"
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

        val connection = connect()
        lateinit var jda: JDA
        val waiter: EventWaiter = EventWaiter()
        lateinit var cmdClient: CommandClient
        val everyMessageManager = EverythingListener()
        val feedChannelName = "funnyman-feed"

        var emergencyDisable = false //Disables EverythingListener

        private fun connect(): Connection {
            try {
                val conn = DriverManager.getConnection(Config().url, Config().userName, Config().password)
                println("Connected to the PostgreSQL server successfully.")
                return conn
            } catch (e: SQLException) {
                println(e.message)
                println("The database failed to connect, exiting process")
                exitProcess(0)
            }
        }
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
                    emergencyDisable: Boolean = $emergencyDisable
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
                    "emergencyDisable" -> emergencyDisable = args[1].toBoolean()
                    else -> event.reply("Variable not found")
                }
                event.reactSuccess()
            }
        }
    }
}