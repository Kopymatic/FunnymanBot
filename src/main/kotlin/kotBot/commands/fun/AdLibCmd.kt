package kotBot.commands.`fun`

import com.jagrosh.jdautilities.command.CommandEvent
import dev.minn.jda.ktx.await
import kotBot.utils.GuildSettings
import kotBot.utils.KopyCommand
import kotBot.utils.Reference
import kotlinx.coroutines.withTimeoutOrNull
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.Button
import java.security.SecureRandom
import java.util.*

class AdLibCmd : KopyCommand() {
    init {
        name = "AdLib"
        help = "Make a silly story by blindly filling in blanks"
        guildOnly = false
        category = Reference.funCategory
    }

    override suspend fun onCommandRun(event: CommandEvent, guildSettings: GuildSettings) {
        val nonceGen = ByteArray(32)
        SecureRandom().nextBytes(nonceGen)
        val nonce = Base64.getEncoder().encodeToString(nonceGen)

        val eb = EmbedBuilder().setColor(guildSettings.defaultColor)
            .setTitle("Pick a story")
            .addField("Story #1", "When invited to a party...", false)
            .addField("Random", "Pick a random story", false)
        event.channel.sendMessage(eb.build())
            .setActionRows(ActionRow.of(Button.primary("$nonce:1", "1"), Button.primary("$nonce:Random", "Random")))
            .queue()
        var buttonEvent: ButtonClickEvent? = null
        withTimeoutOrNull(60000) {
            buttonEvent = event.jda.await { it.componentId.startsWith(nonce) }
        } ?: event.reply("Timed out!") //If it times out we go here
        if (buttonEvent == null) return

        buttonEvent!!.deferReply()
        val choice = when (buttonEvent!!.componentId.split(":")[1]) {
            "Random" -> Random().nextInt(adLibArray.size)
            else -> buttonEvent!!.componentId.split(":")[1].toInt() - 1
        }
        var story = ""
        val storyList = adLibArray[choice].split("|")

        for (i in storyList.indices step (2)) {
            story += storyList[i]
            try {
                event.reply("I need a **${storyList[i + 1]}**")
                val messageEvent =
                    event.jda.await<MessageReceivedEvent> { it.channel == event.channel && it.member == event.member }
                story += messageEvent.message.contentRaw
            } catch (e: Exception) {
                break
            }
        }
        event.reply(story)

    }

    val adLibArray = arrayOf(
        "When invited to a party at a |noun|'s house, you should always bring a |noun|. This will make you seem " +
                "especially |adjective|, and may even get you some |plural noun|. Don't talk too |adverb|, and don't " +
                "ever sit on the |noun|. Don't dance too |adverb|, and don't |verb| anything that's in the |noun|. " +
                "At the end of the party, be sure to |verb| your host before saying \"|exclaimation|\" and driving home.",

        )
}