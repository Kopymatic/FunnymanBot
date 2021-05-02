package kotBot.utils

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import dev.minn.jda.ktx.Embed
import kotBot.Bot
import kotlinx.coroutines.GlobalScope
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Category
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import java.util.*

abstract class KopyCommand: Command() {
    val kdb = Reference.kdb
    /**
     * -- DO NOT OVERRIDE --
     * Use onCommandRun instead
     */
    override fun execute(event: CommandEvent?) {
        if(event == null) return
        trackStats(event)
        GlobalScope.run {
            onCommandRun(event)
        }
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

    fun getAllAliases(): List<String> {
        val aliases: MutableList<String> = ArrayList()
        aliases.add(name)
        aliases.addAll(listOf(*getAliases()))
        return aliases
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

fun Category.getDescription(): String { //TODO FINISH THIS
    when(this.name) {

    }
    return ""
}