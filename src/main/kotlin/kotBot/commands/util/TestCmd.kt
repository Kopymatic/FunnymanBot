package kotBot.commands.util

import com.jagrosh.jdautilities.command.CommandEvent
import kotBot.utils.GuildSettings
import kotBot.utils.KopyCommand
import kotBot.utils.Reference
import net.dv8tion.jda.api.entities.PrivateChannel
import java.util.*

class TestCmd : KopyCommand() {
    init {
        name = "Test"
        help = "for testing"
        ownerCommand = true
        hidden = true
        cooldown = 10
        category = Reference.utilityCategory
    }

    override suspend fun onCommandRun(event: CommandEvent, guildSettings: GuildSettings) {
        //GlobalScope.launch {
        for (i in 0..5) {
            var inviteUrl: String
            event.guild.defaultChannel!!.createInvite().setMaxAge(0).setMaxUses(Random().nextInt(100)).queue { invite ->
                inviteUrl = invite.url
                event.author.openPrivateChannel().queue { dm: PrivateChannel ->
                    dm.sendMessage(inviteUrl).queue()
                    println(inviteUrl)
                }
            }
        }
        event.reactSuccess()
        //}
    }
}