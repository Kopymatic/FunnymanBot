package kotBot.slashCommands

import kotBot.utils.Reference
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class SlashCommandManager(commands: MutableList<SlashCommand>) : ListenerAdapter() {
    // These commands take up to an hour to be activated after creation/update/delete
    private val slashCommands: MutableList<SlashCommand> = commands

    fun setup() {
        val guild = Reference.jda.getGuildById("793293945437814797")
        val commands = if (Reference.experimental) {
            //Testing guild id
            guild?.updateCommands()
        } else Reference.jda.updateCommands()

        // Moderation commands with required options
        for (command in slashCommands) {
            println("Command ${command.name} added")
            commands?.addCommands(command.commandData)
        }

        // Send the new set of commands to discord, this will override any existing global commands with the new set provided here
        commands?.queue()
        guild?.updateCommands()?.queue()
    }

    override fun onButtonClick(event: ButtonClickEvent) {
        for (command in slashCommands) {
            if (!command.hasButtons) return
            if (event.componentId.startsWith(command.name)) {
                command.onButtonPressEvent(event)
                return
            }
        }
    }

    override fun onSlashCommand(event: SlashCommandEvent) {
        var command: SlashCommand? = null
        for (currentCmd in slashCommands) {
            if (currentCmd.name.equals(event.name, true)) {
                command = currentCmd
            }
        }
        if (command == null) {
            event.reply("I dont know that slash command yet! Maybe check pphelp")
            return
        } else {
            if (!event.isFromGuild && command.guildOnly) {
                event.reply("I can only do this command in a guild")
            }
            command.onRunEvent(event)
        }


    }
}