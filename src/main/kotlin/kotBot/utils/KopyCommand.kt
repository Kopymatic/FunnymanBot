package kotBot.utils

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import dev.minn.jda.ktx.Embed
import kotBot.Bot
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed

abstract class KopyCommand: Command() {
    val kdb = Reference.kdb
    /**
     * -- DO NOT OVERRIDE --
     * Use onCommandRun instead
     */
    override fun execute(event: CommandEvent?) {
        if(event == null) return
        trackStats(event)
        onCommandRun(event)
    }

    /**
     * KopyCommand's implementation of execute, except event is nonnull and stats are tracked
     */
    abstract fun onCommandRun(event: CommandEvent)

    /*
    For tracking bot statistics
     */
    private fun trackStats(event: CommandEvent) {
        //TODO("Not yet implemented! - needs database")
    }

    open fun getAdvancedHelp(): EmbedBuilder? {
        return null
    }
}

fun Message.hasAttachments(): Boolean {
    return this.attachments.size > 0
}

fun CommandEvent.replyWithReference(message: String) {
    this.channel.sendMessage(message).reference(this.message).queue()
}

fun CommandEvent.replyWithReference(embed: MessageEmbed) {
    this.channel.sendMessage(embed).reference(this.message).queue()
}