package kotBot.slashCommands

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData

abstract class SlashCommand {
    var commandData = CommandData("null", "This is not supposed to be seen.")
    var name = commandData.name
    var guildOnly = true
    var hasButtons = false

    //This will send a <Bot> is thinking... message in chat that will be updated later through either InteractionHook.editOriginal(String) or InteractionHook.sendMessage(String).
    var sendEphemeral = false

    abstract fun onRunEvent(event: SlashCommandEvent)

    open fun onButtonPressEvent(event: ButtonClickEvent) {}

    /**
     * For buttons it is important to have a standard way of lying out ids.
     * this prefix lets the SlashCommandManager know that this button belongs here
     */
    fun getButtonIDPrefix(): String {
        return "${this.name}:"
    }

    fun getButtonID(suffix: String): String {
        return "${getButtonIDPrefix()}$suffix"
    }
}