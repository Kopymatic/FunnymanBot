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
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import java.awt.Color


class SuggestCmd : KopyCommand() {
    init {
        name = "Suggest"
        help = "Gives a suggestion to the dev"
        arguments = "[suggestion]"
        guildOnly = false
        category = Reference.utilityCategory
        cooldown = 10
    }

    override suspend fun onCommandRun(event: CommandEvent, guildSettings: GuildSettings) { //This is fucking AWFUL code.
        if (event.args.isBlank()) {
            event.reply("You need to actually suggest something!")
            return
        }
        val suggestChannel = event.jda.getTextChannelById("794090631743406100")

        suggestChannel?.sendMessage(Embed {
            title = "Suggestion from ${event.author.asTag}"
            description = event.args
        })?.queue { message: Message ->
            message.addReaction("✅").queue()
            message.addReaction("❌").queue()
            event.replyWithReference("Your suggestion has been successfully posted! Now just wait for it to be accepted or rejected.")
            GlobalScope.launch {
                val reactEvent = event.jda.await<GuildMessageReactionAddEvent> {
                    (it.reaction.reactionEmote.name == "✅" || it.reaction.reactionEmote.name == "❌")
                            && (it.member.id == "326489320980611075") && (it.messageId == message.id)
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
                            event.replyWithReference("Your suggestion was approved!")
                        }
                        "❌" -> {
                            reactedMessage.delete().reason("Rejected").queue()
                            event.replyWithReference("Your suggestion was denied")
                        }
                    }
                }

            }
        }

    }
}

