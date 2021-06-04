package kotBot.slashCommands.convenience

import kotBot.commands.convenience.QuickStringCommand
import kotBot.slashCommands.SlashCommand
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.OptionData

abstract class QuickFormatSlaCommands : SlashCommand() {
    override fun onRunEvent(event: SlashCommandEvent) {
        event.deferReply(false).queue()
        event.interaction.hook.editOriginal(formatText(event.getOption("content")!!.asString)).queue()
    }

    abstract fun formatText(text: String): String
}

class AddSpacesSlaCmd : QuickFormatSlaCommands() {
    init {
        commandData = CommandData("addspaces", "Adds spaces to your supplied text")
            .addOptions(
                OptionData(OptionType.STRING, "content", "Text to format")
                    .setRequired(true)
            )
        name = commandData.name
    }

    override fun formatText(text: String): String {
        return QuickStringCommand.AddSpaces().formatText(text)
    }
}

class OwoifierSlaCmd : QuickFormatSlaCommands() {
    init {
        commandData = CommandData("owo", "OwO-Ifies your given text")
            .addOptions(
                OptionData(OptionType.STRING, "content", "Text to format")
                    .setRequired(true)
            )
        name = commandData.name
    }

    override fun formatText(text: String): String {
        return QuickStringCommand.Owoifier().formatText(text)
    }
}

class ScramblerSlaCmd : QuickFormatSlaCommands() {
    init {
        commandData = CommandData("scramble", "Scrambles your given text")
            .addOptions(
                OptionData(OptionType.STRING, "content", "Text to format")
                    .setRequired(true)
            )
        name = commandData.name
    }

    override fun formatText(text: String): String {
        return QuickStringCommand.Scrambler().formatText(text)
    }
}

class ReverserSlaCmd : QuickFormatSlaCommands() {
    init {
        commandData = CommandData("reverser", "Reverses your given text")
            .addOptions(
                OptionData(OptionType.STRING, "content", "Text to format")
                    .setRequired(true)
            )
        name = commandData.name
    }

    override fun formatText(text: String): String {
        return QuickStringCommand.Reverser().formatText(text)
    }
}

class AlphabetizerSlaCmd : QuickFormatSlaCommands() {
    init {
        commandData = CommandData("alphabetizer", "Alphabetizes your given text")
            .addOptions(
                OptionData(OptionType.STRING, "content", "Text to format")
                    .setRequired(true)
            )
        name = commandData.name
    }

    override fun formatText(text: String): String {
        return QuickStringCommand.Alphabetizer().formatText(text)
    }
}

class RandomCapsSlaCmd : QuickFormatSlaCommands() {
    init {
        commandData = CommandData("randomcaps", "Adds random capitalization to your given text")
            .addOptions(
                OptionData(OptionType.STRING, "content", "Text to format")
                    .setRequired(true)
            )
        name = commandData.name
    }

    override fun formatText(text: String): String {
        return QuickStringCommand.RandomCaps().formatText(text)
    }
}

class AddClapSlaCmd : QuickFormatSlaCommands() {
    init {
        commandData = CommandData("clap", "Adds claps in between your words")
            .addOptions(
                OptionData(OptionType.STRING, "content", "Text to format")
                    .setRequired(true)
            )
        name = commandData.name
    }

    override fun formatText(text: String): String {
        return QuickStringCommand.AddClap().formatText(text)
    }
}