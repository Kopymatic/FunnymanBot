package kotBot.commands.convenience

import com.jagrosh.jdautilities.command.CommandEvent
import kotBot.utils.KopyCommand
import kotBot.utils.Reference
import net.dv8tion.jda.api.entities.Message

class PollCmd : KopyCommand() {
    init {
        this.name = "Poll"
        this.help = "Creates a poll with reactions"
        this.arguments = "[# of reacts (Default 2, max 9)], [Message]"
        category = Reference.convenienceCategory
    }

    override fun onCommandRun(event: CommandEvent) {
        if (event.args.equals("", ignoreCase = true)) {
            event.reply("You must provide arguments!")
        } else {
            val args: Array<String> = event.args.split(" ").toTypedArray()
            val message: Message = event.message
            try {
                val pollOptions = args[0].toInt()
                if (pollOptions in 3..9) {
                    for (i in 1..pollOptions) {
                        message.addReaction("U+003" + i + "U+FE0F U+20E3").queue()
                    }
                } else {
                    when (pollOptions) {
                        1 -> message.addReaction("U+1F44D").queue() //thumbs up
                        2 -> {
                            message.addReaction("U+1F44D").queue() //thumbs up
                            message.addReaction("U+1F44E").queue() //thumbs down
                        }
                    }
                }
            } catch (e: NumberFormatException) { // if the first arg can't be parsed to an int, run this.
                message.addReaction("U+1F44D").queue() //thumbs up
                message.addReaction("U+1F44E").queue() //thumbs down
            }
        }
    }
}