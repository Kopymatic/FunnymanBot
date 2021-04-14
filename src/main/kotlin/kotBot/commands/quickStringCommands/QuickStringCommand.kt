package kotBot.commands.quickStringCommands

import com.jagrosh.jdautilities.command.CommandEvent
import kotBot.utils.KopyCommand

abstract class QuickStringCommand: KopyCommand() {
    /**
     * The text that will be sent by default with this command
     */
    protected var quickString: String? = null

    /**
     * Sets whether or not the command can respond with an embed -- default true
     */
    protected var isEmbeddable: Boolean = true

    /**
     * Sets whether or not the user should have to put anon in their command
     */
    protected var showUserByDefault: Boolean = false

    override fun onCommandRun(event: CommandEvent) {
        val text = event.message.contentRaw
        if(isEmbeddable and text.contains("embed", true)) {

        } else if(showUserByDefault and !text.contains("anon", true)) {

        } else {
            if(quickString != null) event.reply(quickString)
            else event.reply(formatText(text))
        }
    }

    protected open fun formatText(text: String): String {
        return text
    }
}