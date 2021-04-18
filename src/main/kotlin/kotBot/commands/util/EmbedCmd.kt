package kotBot.commands.util

import com.jagrosh.jdautilities.command.CommandEvent
import kotBot.utils.KopyCommand
import kotBot.utils.Reference
import net.dv8tion.jda.api.EmbedBuilder
import java.awt.Color

class EmbedCmd : KopyCommand() {

    init {
        name = "Embed"
        help = "Allows you to make complex embeds with a command"
        arguments = "Run the command with no arguments for detailed help."
    }

    override fun onCommandRun(event: CommandEvent) {
        val eb: EmbedBuilder = EmbedBuilder().setColor(Reference.defaultColor)
        when (event.args.toLowerCase()) {
            "" -> event.reply(getAdvancedHelp().build())
            "example" -> event.reply(
                """
                ```${Reference.mainPrefix}embed 
                Title: Hi there//
                Desc: This is an example//
                Field: This is a field, they're really useful / That slash indicates that this is the text under the field//
                Field: You can have multiple fields at once, up to 25 if you need / Cool huh?//
                Field: The double forward slashes indicate the next argument, so that they can be easily interpreted by the code//
                Footer: This is the footer//
                Image: https://cdn.discordapp.com/attachments/793295208137621505/814964138258071553/unknown.png//
                Color: 255 0 255```
                """.trimIndent()
            )
            else -> {
                val args = event.args.split("//").toTypedArray()
                for (i in args.indices) {
                    val currentArg = args[i].trim()
                    if (currentArg.startsWith("Title:")) {
                        val titleAndLink = currentArg.split("/").toTypedArray()
                        if (titleAndLink.size >= 2) {
                            val title = titleAndLink[0].replace("Title:", "").trim()
                            val link = titleAndLink[1].trim() + "//" + args[i + 1].trim()
                            eb.setTitle(title, link)
                        } else if (titleAndLink.size == 1) {
                            eb.setTitle(currentArg.replace("Title:", "").trim())
                        }
                    } else if (currentArg.startsWith("Desc:")) {
                        eb.setDescription(currentArg.replace("Desc:", "").trim())
                    } else if (currentArg.startsWith("Field:")) {
                        val fieldTexts = currentArg.split("/").toTypedArray()
                        if (fieldTexts.size == 2) {
                            eb.addField(fieldTexts[0].replace("Field:", "").trim(), fieldTexts[1].trim(), false)
                        } else if (fieldTexts.size == 1) {
                            eb.addField(fieldTexts[0].replace("Field:", "").trim(), "", false)
                        }
                    } else if (currentArg.startsWith("Footer:")) {
                        eb.setFooter(currentArg.replace("Footer:", "").trim())
                    } else if (currentArg.startsWith("Image:")) {
                        eb.setImage(currentArg.replace("Image:", "").trim() + "//" + args[i + 1].trim())
                    } else if (currentArg.startsWith("Color:")) {
                        val colors = currentArg.replace("Color:", "").trim().split(" ").toTypedArray()
                        try {
                            val r = colors[0].toInt()
                            val g = colors[1].toInt()
                            val b = colors[2].toInt()
                            eb.setColor(Color(r, g, b))
                        } catch (e: NumberFormatException) {
                            event.reply("Error parsing your color - Make sure there are no letters and only 3 numbers between 0-255")
                            return
                        } catch (e: ArrayIndexOutOfBoundsException) {
                            event.reply("Error parsing your color - Make sure there are no letters and only 3 numbers between 0-255")
                            return
                        }
                    }
                }
                event.reply(eb.build())
            }
        }
    }

        override fun getAdvancedHelp(): EmbedBuilder {
            val eb: EmbedBuilder = EmbedBuilder().setColor(Reference.defaultColor)
            eb.setTitle("How to make an embed with ${Reference.mainPrefix}embed")
            eb.setDescription(
                "Embeds are really cool and good at getting information across. This command allows you to construct embeds "
                        + "(relatively) quickly and easily. Here's a tutorial."
            )
            eb.addField(
                "You need to format it properly.", """
     End every argument with a double slash `//` 
     All arguments should start with a capital letter and end in a colon like `Title:`
     """.trimIndent(), false
            )
            eb.addField("**List of Arguments**", "", false)
            eb.addField(
                "Title:", """
     Use this to set the title of your embed. Optionally, you can also set a link in your title, separate that with a slash. 
     Example with link: `Title: Hello there / https://youtu.be/dQw4w9WgXcQ//`
     """.trimIndent(), false
            )
            eb.addField(
                "Desc:", """
     Use this to set your embed's description. 
     Example: `Desc: Hello World!//`
     """.trimIndent(), false
            )
            eb.addField(
                "Field:", """
     You can add as many fields as you like, and they usually (but don't have to) have subtext. Separate that with a slash. 
     Example: `Field: Hello / World!//`
     """.trimIndent(), false
            )
            eb.addField(
                "Footer:", """
     Allows you to set your embed's footer. 
     Example: `Footer: Hello World!//`
     """.trimIndent(), false
            )
            eb.addField(
                "Image:", """
                        Allows you to set an image for an embed. Use a valid link. 
                        Example: `Image: https://cdn.discordapp.com/attachments/793295208137621505/814964138258071553/unknown.png//`
                        """.trimIndent(), false
            )
            eb.addField(
                "Color:", """
     This lets you set the embed's color. If not set, it defaults to pink. Use RGB. 
     Example: `Color: 255 0 0//`
     """.trimIndent(), false
            )
            eb.setFooter("A full example can be found by doing ${Reference.mainPrefix}embed example")
            return eb
        }
    }
