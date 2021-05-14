package kotBot.commands.convenience

import com.jagrosh.jdautilities.command.CommandEvent
import kotBot.utils.GuildSettings
import kotBot.utils.KopyCommand
import kotBot.utils.Reference

class PollCmd : KopyCommand() {
    init {
        name = "Poll"
        help = "Creates a poll with reactions"
        arguments = "[# of reacts (Default 2, max 10)], [Message]"
        category = Reference.convenienceCategory
        doTyping = false
    }

    override suspend fun onCommandRun(event: CommandEvent, guildSettings: GuildSettings) {
        val args = event.args
        val splitArgs = args.split(" ")
        try {
            val reactAmount = splitArgs[0].toInt()
            if (reactAmount == 1) {
                event.message.addReaction("U+1F44D").queue() //thumbs up
                return
            }
            for (i in 1..reactAmount) {
                if (i == 10) {
                    event.message.addReaction("\uD83D\uDD1F")
                        .queue() //10 -- unicode got from https://www.iemoji.com/view/emoji/777/symbols/keycap-10
                    break
                }
                event.message.addReaction("U+003$i U+FE0F U+20E3").queue()
            }
        } catch (e: Exception) {
            when (e) { //Kotlin doesn't have multi catch blocks :(
                is NumberFormatException, is IndexOutOfBoundsException -> {
                    event.message.addReaction("U+1F44D").queue() //thumbs up
                    event.message.addReaction("U+1F44E").queue() //thumbs down
                }
                else -> throw e
            }
        }
    }
}