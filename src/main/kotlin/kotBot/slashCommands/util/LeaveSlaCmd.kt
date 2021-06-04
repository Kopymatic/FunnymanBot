package kotBot.slashCommands.util

import kotBot.slashCommands.SlashCommand
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData

class LeaveSlaCmd : SlashCommand() {
    init {
        commandData = CommandData("leave", "Make the bot leave the server")
        name = commandData.name
    }

    override fun onRunEvent(event: SlashCommandEvent) {
        if (!event.member!!.hasPermission(Permission.KICK_MEMBERS)) event.reply("You do not have permissions to kick me.")
            .setEphemeral(true).queue() else event.reply("Leaving the server... :wave:") // Yep we received it
            .flatMap {
                event.guild!!.leave()
            } // Leave server after acknowledging the command
            .queue()
    }
}