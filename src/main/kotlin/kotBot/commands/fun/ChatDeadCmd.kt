package kotBot.commands.`fun`

import com.jagrosh.jdautilities.command.CommandEvent
import kotBot.utils.GuildSettings
import kotBot.utils.KopyCommand
import kotBot.utils.Reference
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class ChatDeadCmd : KopyCommand() {
    init {
        name = "ChatDead"
        aliases = arrayOf("ded", "dead", "chatded")
        help = "Sends a random question / conversation starter"
        arguments = "Question type: [Would You Rather / wyr], [General questions / gq] (optional)"
        guildOnly = false
        category = Reference.funCategory
    }

    override suspend fun onCommandRun(event: CommandEvent, guildSettings: GuildSettings) {
        val finalString: String = if (event.args.equals("would you rather", ignoreCase = true) || event.args.equals(
                "wyr",
                ignoreCase = true
            )
        ) {
            wouldYouRather()
        } else if (event.args.equals("general questions", ignoreCase = true) || event.args.equals(
                "gq",
                ignoreCase = true
            )
        ) {
            generalQuestions()
        } else {
            if (Random().nextBoolean()) {
                wouldYouRather()
            } else {
                generalQuestions()
            }
        }
        event.reply(finalString)
    }

    companion object {
        private fun wouldYouRather(): String {
            val wyrQuestions = loadFile("resources/WouldYouRather.txt")!!
            return wyrQuestions[Random().nextInt(wyrQuestions.size)]
        }

        private fun generalQuestions(): String {
            val generalQuestions = loadFile("resources/GeneralQuestions.txt")!!
            return generalQuestions[Random().nextInt(generalQuestions.size)]
        }

        private fun loadFile(path: String): List<String>? {
            val loadedFile: List<String>
            return try {
                loadedFile = Files.readAllLines(Paths.get(Paths.get(path).toFile().absolutePath))
                loadedFile
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
    }
}