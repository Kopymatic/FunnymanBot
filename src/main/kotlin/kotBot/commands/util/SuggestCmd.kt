package kotBot.commands.util

import com.jagrosh.jdautilities.command.CommandEvent
import dev.minn.jda.ktx.Embed
import kotBot.utils.KopyCommand
import kotBot.utils.Reference

class SuggestCmd : KopyCommand() {
    init {
        name = "Suggest"
        help = "Gives a suggestion to the dev"
        arguments = "[suggestion]"
        guildOnly = false
        category = Reference.utilityCategory
        cooldown = 10
    }

    override fun onCommandRun(event: CommandEvent) {
        val suggestChannel = event.jda.getTextChannelById("794090631743406100")
        suggestChannel?.sendMessage(Embed {
            title = "Suggestion from ${event.author.asTag}"
            description = event.args
        })?.queue()
        event.reactSuccess()
    }
}