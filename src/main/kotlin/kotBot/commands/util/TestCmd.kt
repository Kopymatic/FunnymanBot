package kotBot.commands.util

import com.jagrosh.jdautilities.command.CommandEvent
import kotBot.utils.GuildSettings
import kotBot.utils.KopyCommand
import kotBot.utils.Reference
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.Button

class TestCmd : KopyCommand() {
    init {
        name = "Test"
        help = "for testing"
        ownerCommand = true
        hidden = true
        category = Reference.utilityCategory
    }

    override fun execute(event: CommandEvent?) {
        if (event == null) return
        event.channel.sendMessage("pretend theres shop stuff here").reference(event.message)
            .setActionRows(
                ActionRow.of(
                    Button.primary("test", "<-"),
                    Button.success("test", "Buy This"),
                    Button.primary("test", "->")
                )
            ).queue()
    }

    override suspend fun onCommandRun(event: CommandEvent, guildSettings: GuildSettings) {
        TODO("Not yet implemented")
    }
}