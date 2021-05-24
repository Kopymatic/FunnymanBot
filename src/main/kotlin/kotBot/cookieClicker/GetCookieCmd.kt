package kotBot.cookieClicker

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
import java.math.BigInteger

class GetCookieCmd : KopyCommand() {
    init {
        name = "GetCookie"
        aliases = arrayOf("c")
        arguments = ""
        help = "Gives you a cookie"
        category = Reference.cookieClickerCategory
    }

    override suspend fun onCommandRun(event: CommandEvent, guildSettings: GuildSettings) {
        val cProfile = ClickerProfile(event.member.id)
        cProfile.cookies += BigInteger("1")
        println(cProfile.cookies)
        cProfile.push()
        println(ClickerProfile(event.member.id).cookies)
        event.channel.sendMessage(Embed {
            title = "Cookies: ${cProfile.cookies}"
            description = "React with ✅ to get more\nYour progress wont save until you hit ❌ (To save bot resources)"
            color = Color.green.rgb
        }).queue { message: Message ->
            GlobalScope.launch {
                message.addReaction("✅").queue()
                message.addReaction("❌").queue()
                val reactEvent = event.jda.await<GuildMessageReactionAddEvent> {
                    (it.reaction.reactionEmote.name == "✅" || it.reaction.reactionEmote.name == "❌")
                            && (it.member.id == "326489320980611075") && (it.messageId == message.id)
                }
                reactEvent.retrieveMessage().queue { reactedMessage: Message ->
                    when (reactEvent.reactionEmote.name) {
                        "✅" -> {
                            val embed = reactedMessage.embeds[0]
                            cProfile.cookies += BigInteger("1")
                            reactedMessage.editMessage(Embed {
                                title = "Cookies ${cProfile.cookies}"
                                description = embed.description
                                color = embed.colorRaw
                            }).queue()
                            cProfile.push()
                            event.reply("+1 cookie! (now ${cProfile.cookies}")
                        }
                        "❌" -> {
                            reactedMessage.delete().reason("Rejected").queue()
                            event.replyWithReference("Your suggestion was denied", true)
                        }
                    }
                }
            }
        }
    }
}