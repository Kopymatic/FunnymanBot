package kotBot.commands.util

import com.jagrosh.jdautilities.command.CommandEvent
import kotBot.utils.GuildSettings
import kotBot.utils.KopyCommand
import kotBot.utils.Reference
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Emoji

class EmojiInfoCmd : KopyCommand() {
    init {
        name = "EmojiInfo"
        aliases = arrayOf("emoji")
        arguments = "Emoji to get info for"
        help = "Gets technical info for an emoji"
        guildOnly = false
        category = Reference.utilityCategory
        hidden = true
    }

    override suspend fun onCommandRun(event: CommandEvent, guildSettings: GuildSettings) {
        if (event.args.isEmpty()) {
            event.reply("Supply an emoji!")
        }
        try {
            val emoji = Emoji.fromMarkdown(event.args.split(" ")[0])
            val eb = EmbedBuilder().setColor(guildSettings.defaultColor)
                .setTitle("Emoji ${emoji.name}")
            if (emoji.isCustom) {
                val emote = event.jda.getEmoteById(emoji.id)!!
                eb.addField("As mention", "```${emote.asMention}```", false)
                eb.addField("Id", "```${emoji.id}```", false)
                //eb.addField("Guild", "${emote.guild.name} (Id: ${emote.guild.id}")
                eb.addField("Time created", "```${emote.timeCreated.format(Reference.dateFormatter)} (MST)```", false)
                eb.setImage(emote.imageUrl)
            } else if (emoji.isUnicode) {
                eb.addField("Unicode Character", "```${emoji.name}```", false)
            }
            event.reply(eb.build())
        } catch (e: IllegalArgumentException) {
            event.reactError()
            event.reply("Invalid emoji!")
        }
    }
}