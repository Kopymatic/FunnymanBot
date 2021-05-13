package kotBot.commands.`fun`

import com.jagrosh.jdautilities.command.CommandEvent
import kotBot.utils.GuildSettings
import kotBot.utils.KopyCommand
import kotBot.utils.Reference
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class ComplimentCmd : KopyCommand() {
    init {
        name = "Compliment"
        aliases = arrayOf("ComplimentSimulator")
        help = "Gives you a random compliment"
        arguments = "[User as mention]"
        category = Reference.funCategory
    }

    override suspend fun onCommandRun(event: CommandEvent, guildSettings: GuildSettings) {
        //command code here
        val compliments: List<String> = loadFile("Compliments.txt")!!
        var compliment = compliments[Random().nextInt(compliments.size)]
        val name: String = if (event.message.mentionedMembers.size > 0) {
            event.message.mentionedMembers[0].effectiveName
        } else {
            event.member.effectiveName
        }
        compliment = compliment.replace("~", name)
        event.reply(compliment)
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