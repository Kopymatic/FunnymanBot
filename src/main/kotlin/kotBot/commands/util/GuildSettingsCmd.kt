package kotBot.commands.util

import com.jagrosh.jdautilities.command.CommandEvent
import kotBot.utils.GuildSettings
import kotBot.utils.KopyCommand
import kotBot.utils.Reference
import kotBot.utils.replyWithReference
import net.dv8tion.jda.api.Permission
import java.awt.Color

class GuildSettingsCmd : KopyCommand() {
    init {
        name = "Settings"
        arguments = "Run the command for help"
        help = "Lets you change settings about your guild"
        userPermissions = arrayOf(Permission.ADMINISTRATOR)
        category = Reference.utilityCategory
    }

    override suspend fun onCommandRun(event: CommandEvent, guildSettings: GuildSettings) {
        if (event.args.isBlank()) {
            event.reply(
                """
                    Current variables:
                    ```kotlin
                    defaultColor: Color (rgb format) = ${guildSettings.defaultColor.red}, ${guildSettings.defaultColor.green}, ${guildSettings.defaultColor.blue}
                    partneredGuilds: List (comma seperated) = ${
                    guildSettings.partneredGuilds.toString().removePrefix("[").removeSuffix("]")
                }
                    doSexAlarm: Boolean = ${guildSettings.doSexAlarm}
                    ```
                    Change them with `${Reference.mainPrefix}${this.name} [variable] = [assignment]`
                """.trimIndent()
            )
        } else {
            val args = event.args.split("=")
            if (args.size < 2) {
                event.reply("Not enough args - make sure to remember the equal sign"); return
            }
            when (args[0].trim().toLowerCase()) {
                "defaultcolor" -> {
                    try {
                        val rgb = args[1].split(",")
                        guildSettings.defaultColor =
                            Color(rgb[0].trim().toInt(), rgb[1].trim().toInt(), rgb[2].trim().toInt())
                    } catch (e: Exception) {
                        event.replyWithReference("Invalid color! Remember that colors need to be in RGB format (like `255, 111, 255`) and the maximum value is 255")
                        return
                    }

                }
                "partneredguilds" -> {
                    val guilds = args[1].split(",")
                    for (guild in guilds) {
                        if (guild.trim().length != 18) {
                            event.reply("Invalid guild id!")
                            return
                        }
                    }
                    guildSettings.partneredGuilds = guilds
                }
                "dosexalarm" -> {
                    //try {
                    guildSettings.doSexAlarm = args[1].toBoolean()
                    //} catch () {

                    //}
                }
                else -> event.reply("Variable not found")
            }
            guildSettings.push()
            event.reactSuccess()
        }
    }
}