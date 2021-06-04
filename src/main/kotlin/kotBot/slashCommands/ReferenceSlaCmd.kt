package kotBot.slashCommands

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.OptionData

class ReferenceSlaCmd : SlashCommand() {
    init {
        commandData = CommandData("name", "description")
            .addOptions(
                OptionData(OptionType.STRING, "option1", "desc")
                    .setRequired(true)
            )
        name = commandData.name
    }

    override fun onRunEvent(event: SlashCommandEvent) {
        event.deferReply(false).queue()
        event.interaction.hook.editOriginal(event.getOption("content")!!.asString).queue()
    }
}