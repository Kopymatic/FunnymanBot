package kotBot.utils

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Category
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed

abstract class KopyCommand : Command() {
    val kdb = Reference.kdb
    var doTyping = true
    /**
     * -- DO NOT OVERRIDE --
     * Use onCommandRun instead
     */
    override fun execute(event: CommandEvent?) {
        if (event == null) return
        if (Reference.doTyping && this.doTyping) event.channel.sendTyping().queue()
        trackStats(event)
        val guildSettings = GuildSettings(event.guild.id)
        GlobalScope.launch {
            onCommandRun(event, guildSettings)
        }
    }

    /**
     * KopyCommand's implementation of execute, except event is nonnull and stats are tracked
     */
    abstract suspend fun onCommandRun(event: CommandEvent, guildSettings: GuildSettings)

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

fun CommandEvent.replyWithReference(message: String, mention: Boolean = false) {
    this.channel.sendMessage(message).reference(this.message).mentionRepliedUser(mention).queue()
}

fun CommandEvent.replyWithReference(embed: MessageEmbed, mention: Boolean = false) {
    this.channel.sendMessage(embed).reference(this.message).mentionRepliedUser(mention).queue()
}

fun Category.getDescription(): String { //TODO FINISH THIS
    when (this.name) {

    }
    return ""
}