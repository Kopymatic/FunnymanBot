package kotBot.commands.util

import com.jagrosh.jdautilities.command.CommandEvent
import dev.minn.jda.ktx.Embed
import dev.minn.jda.ktx.await
import kotBot.utils.GuildSettings
import kotBot.utils.KopyCommand
import kotBot.utils.Reference
import kotBot.utils.replyWithReference
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import java.awt.Color

class PartnerCmd : KopyCommand() {
    init {
        name = "Partner"
        arguments = "[the id of the guild to partner with]"
        help =
            "Allows you to \"Partner\" with guilds, which will allow them to see your NoContext entries and you to see theirs."
        guildOnly = false
        category = Reference.utilityCategory
    }

    override suspend fun onCommandRun(event: CommandEvent, guildSettings: GuildSettings) {
        if (event.args.isBlank()) { //Quit if no args
            event.reply("Supply a guild id!")
            return
        }
        val guild: Guild? = try {
            event.jda.getGuildById(event.args)
        } catch (e: NumberFormatException) {
            event.reply("Invalid guild id!"); return
        }
        if (guild == null) {
            event.replyWithReference("Invalid guild!")
            return
        }

        var channel = guild.textChannels.find { channel -> channel.name.contains("funnyman", true) }
        if (channel == null) {
            channel = guild.defaultChannel
        }
        if (channel == null) {
            event.reactError()
            return
        }
        channel.sendMessage(Embed {
            title = "${event.guild.name} (id: ${event.guild.id}) wants to partner with you!"
            description =
                "This will let them see all of your entries to NoContext, People, Pet, and Meme. React with ✅ to accept.\n" +
                        "***THIS IS CURRENTLY IRREVERSIBLE***.\n" +
                        "Only admins can react to this message."
            color = guildSettings.rgb
        }).queue { message: Message ->
            message.addReaction("✅").queue()
            message.addReaction("❌").queue()
            event.replyWithReference("Your partnership request has been successfully posted! Now just wait for it to be accepted or rejected.")
            GlobalScope.launch {
                val reactEvent = event.jda.await<GuildMessageReactionAddEvent> {
                    (it.reaction.reactionEmote.name == "✅" || it.reaction.reactionEmote.name == "❌")
                            && (it.member.permissions.contains(Permission.ADMINISTRATOR)) && (it.messageId == message.id)
                            && !it.member.user.isBot
                }
                reactEvent.retrieveMessage().queue { reactedMessage: Message ->
                    when (reactEvent.reactionEmote.name) {
                        "✅" -> {
                            val embed = reactedMessage.embeds[0]
                            reactedMessage.editMessage(Embed {
                                title = embed.title
                                description = embed.description
                                color = Color.green.rgb
                            }).queue()
                            event.replyWithReference("Your request was accepted!")

                            val otherGuildSettings = GuildSettings(reactedMessage.guild.id)

                            guildSettings.partneredGuilds?.add(otherGuildSettings.guildID)
                            otherGuildSettings.partneredGuilds?.add(guildSettings.guildID)
                            guildSettings.push()
                            otherGuildSettings.push()
                        }
                        "❌" -> {
                            reactedMessage.delete().reason("Rejected").queue()
                            event.replyWithReference("Your request was rejected")
                        }
                    }
                }

            }
        }
    }
}