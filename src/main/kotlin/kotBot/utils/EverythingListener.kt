package kotBot.utils

import club.minnced.discord.webhook.WebhookClientBuilder
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Icon
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.net.URL
import java.util.*

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
                val targetMember = event.member!!
                event.textChannel.createWebhook(targetMember.effectiveName)
                    .setAvatar(Icon.from(URL(targetMember.user.avatarUrl!!).openStream()))
                    .queue {
                        val webhook = it
                        val client = WebhookClientBuilder.fromJDA(webhook).buildJDA()
                        client.send(
                            MessageBuilder().setContent(event.message.contentRaw).setEmbed(
                                EmbedBuilder().setColor(guildSettings.defaultColor)
                                    .setAuthor(
                                        "Reply to ${referencedMessage.author.asTag}",
                                        referencedMessage.jumpUrl,
                                        referencedMessage.author.avatarUrl
                                    )
                                    .setDescription(referencedMessage.contentRaw)
                                    .build()
                            )
                                .build()
                        ).get()
                        client.close()
                        event.message.delete().queue()
                        webhook.delete().queue()
                    }

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

            if (guildSettings.joeMode && event.message.contentRaw.contains("joe", true)) {
                if (Random().nextBoolean()) {
                    event.channel.sendMessage("Joe Biden, the president of this god blessed country :flag_us:").queue()
                } else {
                    event.channel.sendMessage("Joe mamma!!!").queue()
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