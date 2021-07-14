package kotBot.commands.`fun`

import com.jagrosh.jdautilities.command.CommandEvent
import dev.minn.jda.ktx.await
import kotBot.utils.GuildSettings
import kotBot.utils.KopyCommand
import kotBot.utils.Reference
import kotlinx.coroutines.withTimeoutOrNull
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.InteractionHook
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

        val adLibArray = arrayOf(
            AdLib(
                makeField("Story #1", "When invited to a party... [Length 11]", false),
                Button.primary("$nonce:1", "1"),
                "When invited to a party at a |noun|'s house, you should always bring a |noun|. This will make you seem " +
                        "especially |adjective|, and may even get you some |plural noun|. Don't talk too |adverb|, and don't " +
                        "ever sit on the |noun|. Don't dance too |adverb|, and don't |verb| anything that's in the |noun|. " +
                        "At the end of the party, be sure to |verb| your host before saying \"|exclaimation|\" and driving home.",
            ),
            AdLib(
                makeField("Story #2", "Let me tell you about my favorite place... [Length 30]", false),
                Button.primary("$nonce:2", "2"),
                """
                    Let me tell you about my favorite place. It is called |Your Name|ville. Everyone there always dresses in |Favorite Color|, and all the cars and the |Favorite Animal (Plural)| are |Favorite Color|, too.
    
                    |Favorite Band| came to do a concert in |Your Name|ville once, and the band liked it so much they never left. Now every |Day of the Week| night, all the people who live in |Your Name|ville put on their |adjective|, |Favorite Color| |Plural Clothing| and walk their |Favorite Animal (Plural)| to the town square. Then they sit on the grass, listen to |Favorite Band| play |Favorite Music Genre| music, and eat |Snack|.
                
                    No one has to go to school in |Your Name|ville unless they want to. Of course, everybody wants to because |Famous Singer| and |Famous Actor| are two of the teachers. |Famous Singer| teaches |Favorite School Subject| and |Famous Actor| teaches |Hobby|.
                    One day |Famous Singer| said to |Famous Actor|, "Maybe we should take the students on a field trip." "That's a |adjective| idea, |Famous Singer|," said |Famous Actor|. "Let's take them to the most fun place we can think of." "But that would be |Your Name|ville," said |Famous Singer|. "You're right!" |Famous Actor| exclaimed. "Call off the field trip! We're already here!"  
                """.trimIndent()
            ),
        )

        val eb = EmbedBuilder().setColor(guildSettings.defaultColor)
            .setTitle("Pick a story")
        for (adLib in adLibArray) {
            eb.addField(adLib.field)
        }
        eb.addField("Random", "Pick a random story", false)

        val actionRow1 = ActionRow.of(adLibArray[0].button, adLibArray[1].button)
//        val actionRow2 = ActionRow.of()
//        val actionRow3 = ActionRow.of()
//        val actionRow4 = ActionRow.of()
        val actionRow5 = ActionRow.of(Button.primary("$nonce:Random", "Random"))

        event.channel.sendMessage(eb.build())
            .setActionRows(
                actionRow1, /*actionRow2, actionRow3, actionRow4,*/
                actionRow5
            ) //Uncomment this at some point
            .queue()
        var buttonEvent: ButtonClickEvent? = null
        withTimeoutOrNull(60000) {
            buttonEvent = event.jda.await { it.componentId.startsWith(nonce) }
        } ?: event.reply("Timed out!") //If it times out we go here
        if (buttonEvent == null) return

        val choice = when (buttonEvent!!.componentId.split(":")[1]) {
            "Random" -> Random().nextInt(adLibArray.size)
            else -> buttonEvent!!.componentId.split(":")[1].toInt() - 1
        }
        var story = ""
        val storyList = adLibArray[choice].story.split("|")

        //This is inefficient
        var hook: InteractionHook? = null
        var wordCount = 1

        for (i in storyList.indices step (2)) {
            story += storyList[i]
            try {
                if (i == 0) buttonEvent!!.reply("[$wordCount] I need a **${storyList[i + 1]}**").setEphemeral(true)
                    .queue { hook = it }
                else hook?.editOriginal("[$wordCount] I need a **${storyList[i + 1]}**")?.queue()

                val messageEvent = withTimeoutOrNull(60000) {
                    event.jda.await<MessageReceivedEvent> { it.channel == event.channel && it.member == event.member }
                }
                if (messageEvent == null) {
                    event.reply("Timed out! Please try again.")
                    return
                }
                messageEvent.message.delete().queue()
                story += messageEvent.message.contentRaw
                wordCount++
            } catch (e: Exception) {
                break
            }
        }
        event.reply("||$story||")

    }

    data class AdLib(val field: MessageEmbed.Field, val button: Button, val story: String)
}