package kotBot.commands.`fun`

import club.minnced.discord.webhook.WebhookClientBuilder
import com.jagrosh.jdautilities.command.CommandEvent
import kotBot.utils.GuildSettings
import kotBot.utils.KopyCommand
import kotBot.utils.Reference
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Icon
import java.net.URL

class MimicCmd : KopyCommand() {
    init {
        name = "Mimic"
        arguments = "[User to mimic as @mention] [Text to say]"
        help = "Mimics a user"
        guildOnly = true
        category = Reference.funCategory
        hidden = true
        allowedGuilds = arrayOf("654578321543266305", "793293945437814797")
        botPermissions = arrayOf(Permission.MANAGE_WEBHOOKS)
        doTyping = false
    }

    override suspend fun onCommandRun(event: CommandEvent, guildSettings: GuildSettings) {
        val targetMember = event.message.mentionedMembers[0]
        event.textChannel.createWebhook(targetMember.effectiveName)
            .setAvatar(Icon.from(URL(targetMember.user.avatarUrl!!).openStream()))
            .queue {
                val webhook = it
                val client = WebhookClientBuilder.fromJDA(webhook).buildJDA()
                client.send(event.args.removePrefix(event.message.mentionedMembers[0].asMention)).get()
                client.close()
                event.message.delete().queue()
                webhook.delete().queue()
            }

    }
}