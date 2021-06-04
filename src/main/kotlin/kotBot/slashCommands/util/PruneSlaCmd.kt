package kotBot.slashCommands.util

import kotBot.slashCommands.SlashCommand
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.components.Button

class PruneSlaCmd : SlashCommand() {
    init {
        commandData = CommandData("prune", "Prune messages from this channel")
            .addOptions(
                OptionData(
                    OptionType.INTEGER,
                    "amount",
                    "How many messages to prune (Default 10)"
                )
            )
        name = commandData.name
        hasButtons = true
    }

    override fun onRunEvent(event: SlashCommandEvent) {
        val amountOption = event.getOption("amount") // This is configured to be optional so check for null

        val amount = if (amountOption == null) 10 // default 100
        else Math.min(200, Math.max(2, amountOption.asLong)).toInt() // enforcement: must be between 2-200

        val userId = event.user.id
        event.reply("This will delete $amount messages.\nAre you sure?") // prompt the user with a button menu
            .addActionRow( // this means "<style>(<id>, <label>)" the id can be spoofed by the user so setup some kinda verification system
                Button.success(
                    "${getButtonIDPrefix()}$userId:prune:$amount",
                    "Yes!"
                ),  // the first parameter is the component id we use in onButtonClick above
                Button.danger("${getButtonIDPrefix()}$userId:delete", "Nevermind!")
            )
            .queue()
    }

    override fun onButtonPressEvent(event: ButtonClickEvent) {

        // users can spoof this id so be careful what you do with this
        val id = event.componentId.split(":").toTypedArray() // this is the custom id we specified in our button

        val authorId = id[1]
        val type = id[2]

        // When storing state like this is it is highly recommended to do some kind of verification that it was generated by you, for instance a signature or local cache
        if (authorId != event.user.id) return
        val channel = event.channel
        event.deferEdit().queue() // acknowledge the button was clicked, otherwise the interaction will fail

        when (type) {
            "prune" -> {
                val amount = id[3].toInt()
                event.channel.iterableHistory
                    .skipTo(event.messageIdLong)
                    .takeAsync(amount)
                    .thenAccept { messages: List<Message?>? ->
                        channel.purgeMessages(
                            messages!!
                        )
                    }
                event.hook.deleteOriginal().queue()
            }
            "delete" -> event.hook.deleteOriginal().queue()
        }

    }
}