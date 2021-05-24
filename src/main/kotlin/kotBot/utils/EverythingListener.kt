package kotBot.utils

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class EverythingListener : ListenerAdapter() {

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return
        if (Reference.emergencyDisable) return

        if (event.message.contentStripped == "k") {
            val emote = event.jda.getEmoteById("685602497733328924")
            if (emote != null) {
                event.message.addReaction(emote).queue()
            }
        }

        if (event.isFromGuild) {
            val guildSettings = GuildSettings(event.guild.id)

            if (guildSettings.dylanMode && event.message.referencedMessage != null) {
                val referencedMessage = event.message.referencedMessage!!
                event.channel.sendMessage(
                    "**${event.member?.effectiveName}** replied to **${referencedMessage.member?.effectiveName}**\n" +
                            "> *${
                                referencedMessage.contentStripped.replace("\n", "\n> ").replace("@everyone", "everyone")
                                    .replace("@here", "here")
                            }*\n" + event.message.contentRaw
                ).queue()
            }

            if (guildSettings.doSexAlarm) {
                if (event.message.contentRaw.contains("sex", true)) {
                    var channel = event.guild.textChannels.find { channel -> channel.name == "sex-alarm" }
                    if (channel == null) {
                        channel =
                            event.guild.textChannels.find { channel1 -> channel1.name == Reference.feedChannelName }
                    }
                    channel?.sendMessage("${event.member?.effectiveName} has sexed in <#${event.channel.id}>!!!! :flushed:")
                        ?.queue()
                }
            }
        }
    }

//    override fun onGuildJoin(event: GuildJoinEvent) { //This might be useful one day, but not now
//        val ps = Reference.connection.prepareStatement(
//            """
//            INSERT INTO GuildSettings
//            VALUES(?, DEFAULT, NULL, DEFAULT);
//            """.trimIndent()
//        )
//        ps.setString(1, event.guild.id) //Set the first ? in the prepared statement to guildID
//        ps.executeUpdate()
//    }
}