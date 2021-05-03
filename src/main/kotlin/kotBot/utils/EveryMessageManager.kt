package kotBot.utils

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class EveryMessageManager: ListenerAdapter() {
    var dylanMode = false

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return

        if (dylanMode && event.message.referencedMessage != null) {
            val referencedMessage = event.message.referencedMessage!!
            event.channel.sendMessage(
                "**${event.member?.effectiveName}** replied to **${referencedMessage.member?.effectiveName}**\n" +
                        "> *${
                            referencedMessage.contentStripped.replace("\n", "\n> ").replace("@everyone", "everyone")
                                .replace("@here", "here")
                        }*\n" + event.message.contentRaw
            ).queue()
        }

        if (event.message.contentRaw.contains("sex")) {
            val channel = event.jda.textChannels.find { channel -> channel.name == "sex-alarm" }
            channel?.sendMessage("${event.member?.effectiveName} has sexed in <#${event.channel.id}>!!!! :flushed:")
                ?.queue()
        }
    }
}