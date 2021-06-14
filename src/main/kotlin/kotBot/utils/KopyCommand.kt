package kotBot.utils

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import java.util.*

abstract class KopyCommand : Command() {
    companion object {
        val times: Queue<Int> = LinkedList()
        var commandsRun: Int = 0
    }

    val connection = Reference.connection
    var doTyping = true
    var logStatement: String? = null

    /**
     * -- DO NOT OVERRIDE --
     * Use onCommandRun instead
     */
    override fun execute(event: CommandEvent?) {
        if (event == null) return
        if (Reference.doTyping && this.doTyping) event.channel.sendTyping().queue()
        trackStats(event)
        val guildSettings =
            if (event.channelType != ChannelType.PRIVATE) GuildSettings(event.guild.id) else GuildSettings()
        GlobalScope.launch {
            val start = System.currentTimeMillis()
            onCommandRun(event, guildSettings)
            val end = System.currentTimeMillis()
            times.add((end - start).toInt())
            if (times.size > 100) { //So the size never exceeds 100
                times.poll() //so we dont get an exception if it is somehow empty
            }
            if (end - start > 500 || Reference.experimental) {
                println("$name took ${end - start}ms to run${if (logStatement != null) " | $logStatement" else ""}")
            }
            logStatement = null
        }
        commandsRun++
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

    fun makeField(title: String, text: String, inline: Boolean = false): MessageEmbed.Field {
        return MessageEmbed.Field(title, text, inline)
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