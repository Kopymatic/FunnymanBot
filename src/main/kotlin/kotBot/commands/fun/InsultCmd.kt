package kotBot.commands.`fun`

import com.jagrosh.jdautilities.command.CommandEvent
import kotBot.utils.GuildSettings
import kotBot.utils.KopyCommand
import kotBot.utils.Reference
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class InsultCmd : KopyCommand() {
    init {
        name = "Insult"
        aliases = arrayOf("ISV2", "insultSimulator")
        help = "Gives you a random insult"
        arguments = "[User as mention]"
        category = Reference.funCategory
    }

    override suspend fun onCommandRun(event: CommandEvent, guildSettings: GuildSettings) {

        val insults: List<String> = loadFile("resources/Insults.txt")!!
        var insult = insults[Random().nextInt(insults.size)]
        val name: String = if (event.message.mentionedMembers.size > 0) {
            event.message.mentionedMembers[0].effectiveName
        } else {
            event.member.effectiveName
        }
        insult = insult.replace("~", name)
        event.reply(insult)
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