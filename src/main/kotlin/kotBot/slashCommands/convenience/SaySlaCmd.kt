package kotBot.slashCommands.convenience

import kotBot.slashCommands.SlashCommand
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.OptionData

class SaySlaCmd : SlashCommand() {
    init {
        commandData = CommandData("say", "Makes the bot say what you tell it to")
            .addOptions(
                OptionData(OptionType.STRING, "content", "What the bot should say")
                    .setRequired(true)
            )
        name = commandData.name
    }

    override fun onRunEvent(event: SlashCommandEvent) {
        event.deferReply(false).queue()
        event.interaction.hook.editOriginal(event.getOption("content")!!.asString).queue()
    }
}