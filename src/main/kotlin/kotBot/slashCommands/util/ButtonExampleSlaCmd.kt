package kotBot.slashCommands.util

import kotBot.slashCommands.SlashCommand
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.components.Button

class ButtonExampleSlaCmd : SlashCommand() {
    init {
        commandData = CommandData("button-example", "An example of how the buttons work")
//            .addOptions(
//                OptionData(OptionType.STRING, "option1", "desc")
//                    .setRequired(true)
//            )
        name = commandData.name
        hasButtons = true
    }

    override fun onRunEvent(event: SlashCommandEvent) {
        val userId = event.user.id

        event.reply("Button test")
            .addActionRow( // this means "<style>(<id>, <label>)" the id can be spoofed by the user so setup some kinda verification system
                Button.success(
                    "${getButtonIDPrefix()}$userId:success",
                    "Success"
                ),  // the first parameter is the component id we use in onButtonClick above
                Button.danger("${getButtonIDPrefix()}$userId:danger", "Danger")
            )
            .addActionRow( // this means "<style>(<id>, <label>)" the id can be spoofed by the user so setup some kinda verification system
                Button.primary(
                    "${getButtonIDPrefix()}$userId:primary",
                    "Primary"
                ),  // the first parameter is the component id we use in onButtonClick above
                Button.secondary("${getButtonIDPrefix()}$userId:secondary", "Secondary")
            )
            .addActionRow( // this means "<style>(<id>, <label>)" the id can be spoofed by the user so setup some kinda verification system
                Button.link(
                    "https://youtu.be/dQw4w9WgXcQ",
                    "Link"
                )
            ).queue()
    }

    override fun onButtonPressEvent(event: ButtonClickEvent) {
        val id = event.componentId.split(":")

        when (id[2]) {
            "success" -> event.reply("success button pressed").setEphemeral(true).queue()
            "danger" -> event.reply("danger button pressed").setEphemeral(true).queue()
            "primary" -> event.reply("primary button pressed").setEphemeral(true).queue()
            "secondary" -> event.reply("secondary button pressed").setEphemeral(true).queue()
        }
    }
}