package kotBot.commands.util

import com.jagrosh.jdautilities.command.CommandEvent
import dev.minn.jda.ktx.Embed
import kotBot.utils.GuildSettings
import kotBot.utils.KopyCommand
import kotBot.utils.Reference
import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.Button

class AboutCmd : KopyCommand() {
    init {
        name = "About"
        aliases = arrayOf("a")
        help = "Shows details about the bot - currently only has uptime and version"
        guildOnly = false
        category = Reference.utilityCategory
    }

    override suspend fun onCommandRun(event: CommandEvent, guildSettings: GuildSettings) {
        val secondsRun = (System.currentTimeMillis() - Reference.bootTime) / 1000

        var avg = 0
        try {
            for (time in times) {
                avg += time
            }
            avg /= times.size
        } catch (e: ArithmeticException) {
            avg = 0
        }

        val twitter = event.jda.getEmoteById("853144562179637328")
        val youtube = event.jda.getEmoteById("853144562516492308")
        val discord = event.jda.getEmoteById("853144562222104596")
        val github = event.jda.getEmoteById("853144562201133066")


        event.channel.sendMessage(
            Embed(
                color = guildSettings.rgb,
                title = "About FunnymanBot",
                description = "This is a bot made by Kopymatic",
                fields = arrayListOf(
                    makeField(
                        "Uptime",
                        if (secondsRun / 60 > 60) "${secondsRun / 60 / 60} hours, ${(secondsRun % 60) / 60} minutes, ${secondsRun % 60} seconds" else if (secondsRun > 60) "${secondsRun / 60} minutes, ${secondsRun % 60} seconds" else "$secondsRun seconds"
                    ),
                    makeField("Commands Run", "$commandsRun"),
                    makeField("Average Command Run Time", "${if (avg > 0) "$avg" else "N/A"}ms")
                )
            )
        ).setActionRows(
            ActionRow.of( //TODO potentially add emojis
                Button.link("https://twitter.com/Kopymatic", "Twitter").withEmoji(Emoji.fromEmote(twitter!!)),
                Button.link("https://www.youtube.com/channel/UCF5_Evvi_JP-j12t-8YbOEA/", "Youtube")
                    .withEmoji(Emoji.fromEmote(youtube!!))
            ),
            ActionRow.of(
                Button.link("https://discord.gg/YBcveMYeDU", "Discord").withEmoji(Emoji.fromEmote(discord!!)),
                Button.link("https://github.com/Kopymatic/FunnymanBot", "Source Code")
                    .withEmoji(Emoji.fromEmote(github!!))
            )
        ).queue()
    }
}