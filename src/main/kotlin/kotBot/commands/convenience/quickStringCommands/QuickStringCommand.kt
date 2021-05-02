package kotBot.commands.convenience.quickStringCommands

import com.jagrosh.jdautilities.command.CommandEvent
import kotBot.utils.KopyCommand
import kotBot.utils.Reference
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.ChannelType
import java.util.*


abstract class QuickStringCommand: KopyCommand() {
    /**
     * The text that will be sent by default with this command
     */
    protected lateinit var quickString: String

    /**
     * Sets whether or not the command can respond with an embed -- default true
     */
    protected var isEmbeddable: Boolean = true

    /**
     * Sets whether or not the user should have to put anon in their command
     *
     * Defaults to false
     */
    protected var showUserByDefault: Boolean = false

    override fun onCommandRun(event: CommandEvent) {
        if (event.args.contains("anon")) {
            if (event.args.contains("embed") && isEmbeddable) {
                val eb = EmbedBuilder()
                eb.setColor(Reference.defaultColor)

                val quote = formatText(event.args.replace("embed", "").replace("anon", "").trim())

                if (quote.length > 256) {
                    eb.setDescription(quote)
                } else {
                    eb.setTitle(quote)
                }
                event.reply(eb.build())
            } else {
                event.reply(formatText(event.args.replace("anon", "").trim()))
            }

        } else if (event.args.contains("embed") && isEmbeddable) {
            val eb = EmbedBuilder()
            eb.setColor(Reference.defaultColor)
            if (showUserByDefault) eb.setFooter(event.author.name, event.author.effectiveAvatarUrl)

            val quote = formatText(event.args.replace("embed", "").trim())

            if (quote.length > 256) {
                eb.setDescription(quote)
            } else {
                eb.setTitle(quote)
            }
            event.reply(eb.build())

        } else {
            event.reply(formatText(event.args.trim()))
        }

        if (event.channelType.equals(ChannelType.TEXT)) {
            event.message.delete().queue()
        }
    }

    /**
     * Override this with any custom formatting rules.
     */
    protected open fun formatText(text: String): String {
        return this.quickString
    }

    class Lenny : QuickStringCommand() {
        init {
            name = "Lenny"
            quickString = "( ͡° ͜ʖ ͡°)"
            help = quickString
            guildOnly = false
            category = Reference.quickStringCategory
        }
    }

    class LennyConcern : QuickStringCommand() {
        init {
            name = "hmmm"
            aliases = arrayOf("hmm", "altlenny")
            quickString = "( ͠° ͟ʖ ͡°)"
            help = quickString
            guildOnly = false
            category = Reference.quickStringCategory
        }
    }

    class Rick : QuickStringCommand() {
        init {
            name = "RickRoll"
            aliases = arrayOf("rick")
            quickString = "https://youtu.be/dQw4w9WgXcQ"
            this.help = "May or may not be a rickroll"
            guildOnly = false
            category = Reference.quickStringCategory
        }
    }

    class LennyHug : QuickStringCommand() {
        init {
            name = "LennyHug"
            quickString = "(つ ♥灬 ͜ʖ 灬♥)つ"
            help = quickString
            guildOnly = false
            category = Reference.quickStringCategory
        }
    }

    class LennyKiss : QuickStringCommand() {
        init {
            name = "LennyKiss"
            quickString = "( 灬♥ 3 ♥灬)"
            help = quickString
            guildOnly = false
            category = Reference.quickStringCategory
        }
    }

    class AddSpaces : QuickStringCommand() {
        init {
            name = "Expander"
            aliases = arrayOf("addspace", "as", "expand")
            help = "Adds spaces to your supplied text"
            guildOnly = false
            category = Reference.quickStringCategory
            showUserByDefault = true
        }

        override fun formatText(text: String): String {
            var withSpaces = ""
            for (element in text) {
                withSpaces += "$element "
            }
            return withSpaces
        }
    }

    class Owoifier : QuickStringCommand() {
        init {
            name = "OwOifier"
            aliases = arrayOf("owo", "uwu")
            help = "OwOifies your supplied text"
            guildOnly = false
            category = Reference.quickStringCategory
            showUserByDefault = true
        }

        override fun formatText(text: String): String {
            return text
                .replace("o ", "owo ")
                .replace("O ", "OwO ")
                .replace("u ", "uwu ")
                .replace("U ", "UwU ")
                .replace(" o", " owo")
                .replace(" O", " OwO")
                .replace(" u", " uwu")
                .replace(" U", " UwU")
                .replace("l", "w")
                .replace("L", "W")
                .replace("y ", "ie ")
                .replace("Y ", "IE ")
                .replace("r", "w")
                .replace("R", "W")
        }
    }

    class Scrambler : QuickStringCommand() {
        init {
            name = "Scrambler"
            aliases = arrayOf("scram", "scramble")
            help = "Scrambles your given text"
            guildOnly = false
            category = Reference.quickStringCategory
            showUserByDefault = true
        }

        override fun formatText(text: String): String {
            var newText = ""
            val charList: MutableList<Char> = ArrayList()
            for (i in text.length downTo 1) {
                charList.add(text[i - 1])
            }
            while (charList.size > 0) {
                val randInt: Int = Random().nextInt(charList.size)
                newText += charList[randInt]
                charList.removeAt(randInt)
            }
            return newText
        }
    }

    class Reverser : QuickStringCommand() {
        init {
            name = "Reverser"
            aliases = arrayOf("reverse", "rev")
            help = "Reverses your given text"
            guildOnly = false
            category = Reference.quickStringCategory
            showUserByDefault = true
        }

        override fun formatText(text: String): String {
            var newText = ""
            val charList: MutableList<Char> = ArrayList()
            for (i in text.length downTo 1) {
                charList.add(text[i - 1])
            }
            while (charList.size > 0) {
                val lastItem = 0
                //Since the list is created backwards, the last letter is at zero.
                newText += charList[lastItem]
                charList.removeAt(lastItem)
            }
            return newText
        }
    }

    class Say : QuickStringCommand() {
        init {
            name = "Say"
            help = "Repeats your given text."
            arguments = "[embed] (makes it fancy) [anon] (anonymous) Both will do both, obviously"
            guildOnly = false
            category = Reference.quickStringCategory
            showUserByDefault = true
        }

        override fun formatText(text: String): String {
            return text
        }
    }

    class Alphabetizer : QuickStringCommand() {
        init {
            name = "Alphabetize"
            aliases = arrayOf("alph")
            help = "Alphabetizes your given text"
            guildOnly = false
            category = Reference.quickStringCategory
            showUserByDefault = true
        }

        override fun formatText(text: String): String {
            val list = text.toList()
            var final = ""
            list.sorted().forEach { currentChar -> final += currentChar}
            return final
        }
    }

    class RandomCaps : QuickStringCommand() {
        init {
            name = "RandomCaps"
            aliases = arrayOf("rc")
            help = "Adds random capitalization to your given text."
            guildOnly = false
            category = Reference.quickStringCategory
            showUserByDefault = true
        }

        override fun formatText(text: String): String {
            val list = text.toList()
            var final = ""
            list.forEach { currentChar ->
                final += if(Random().nextBoolean()) {
                    currentChar.toUpperCase()
                } else {
                    currentChar.toLowerCase()
                }
            }
            return final
        }
    }
}