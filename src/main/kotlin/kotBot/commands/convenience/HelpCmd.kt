package kotBot.commands.convenience

import kotBot.utils.Reference.Companion.defaultColor
import kotBot.Bot.Companion.allCommands
import kotBot.utils.Reference.Companion.mainPrefix
import kotBot.utils.KopyCommand
import com.jagrosh.jdautilities.command.CommandEvent
import kotBot.Bot
import kotBot.utils.EmbedPaginator
import net.dv8tion.jda.api.EmbedBuilder
import kotBot.utils.Reference
import kotBot.utils.replyWithReference
import kotlinx.coroutines.processNextEventInCurrentThread
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.exceptions.PermissionException
import java.util.Objects
import java.util.concurrent.TimeUnit

class HelpCmd : KopyCommand() {
    private val waiter = Reference.waiter

    init {
        name = "Help"
        help = "Get help with various commands"
        aliases = arrayOf("h")
        arguments = "[Command to get detailed help for]"
        guildOnly = false
        category = Reference.convenienceCategory
    }

    override fun onCommandRun(event: CommandEvent) {
        //make an eb and set it up
        //val eb = EmbedBuilder().setTitle(Reference.botName + " commands:").setColor(defaultColor)
        val commands = allCommands
        val categories = Reference.categories

        val ep = EmbedPaginator.Builder()
            .setFinalAction { m: Message ->
                try {
                    m.clearReactions().queue()
                } catch (ex: PermissionException) {
                    ex.printStackTrace()
                    m.delete().queue()
                }
            }
            .allowTextInput(true)
            .wrapPageEnds(true)
            .setText(EmbedBuilder.ZERO_WIDTH_SPACE)
            .setEventWaiter(Reference.waiter)
            .setTimeout(2, TimeUnit.MINUTES)
            .waitOnSinglePage(false)

        val categoryEmbeds: Array<EmbedBuilder?> = arrayOfNulls(categories.size)
        for (i in categories.indices) {
            val eb = EmbedBuilder()
                .setColor(defaultColor)
                .setTitle(categories[i].name)
                .setFooter("Use " + mainPrefix + "help [command] to get additional info for commands.")
            categoryEmbeds[i] = eb
        }

        if (event.args.equals("")) {
            for(currentCommand in commands) {
                for(i in categories.indices) {
                    val currentCategory = categories[i]
                    if (currentCommand.category == currentCategory && !currentCommand.isHidden) {
                        if (null == currentCommand.arguments) {
                            categoryEmbeds[i]?.addField("$mainPrefix${currentCommand.name}", currentCommand.help, false)
                        } else {
                            categoryEmbeds[i]?.addField("$mainPrefix${currentCommand.name} `${currentCommand.arguments}`", currentCommand.help, false)
                        }
                    }
                }
            }

            for(currentEmbed in categoryEmbeds) {
                if (currentEmbed != null) {
                    ep.addItems(currentEmbed.build())
                }
            }

            ep.build().paginate(event.channel, 1)
        } else { //If we have args, return custom help command for the provided command
            val args = event.args.toLowerCase()
            val matchList: MutableList<KopyCommand> = mutableListOf()

            for(command in commands) {
                val currentAliases = command.getAllAliases()
                for(currentAlias in currentAliases) {
                    if(currentAlias.contains(args.split(" ")[0], true)) {
                        matchList.add(command)
                    }
                }
            }

            var epSize = 0
            val ep = EmbedPaginator.Builder()
                .setFinalAction { m: Message ->
                    try {
                        m.clearReactions().queue()
                    } catch (ex: PermissionException) {
                        ex.printStackTrace()
                        m.delete().queue()
                    }
                }
                .allowTextInput(true)
                .wrapPageEnds(true)
                .setText(EmbedBuilder.ZERO_WIDTH_SPACE)
                .setEventWaiter(Reference.waiter)
                .setTimeout(2, TimeUnit.MINUTES)
                .waitOnSinglePage(false)

            for(currentCommand in matchList) {
                if(currentCommand.isHidden) continue //If command is hidden then move on

                if(currentCommand.getAdvancedHelp() != null && !args.contains("debug", false)) {
                    ep.addItems(currentCommand.getAdvancedHelp()!!.build())
                    epSize++

                } else {
                    val eb = EmbedBuilder().setColor(defaultColor).setTitle("${currentCommand.name} help")
                        .setFooter("Use " + mainPrefix + "help [command] to get additional info for other commands.")

                    if (currentCommand.arguments == null) {
                        eb.addField(mainPrefix + currentCommand.name, currentCommand.help, false)
                    } else {
                        eb.addField(
                            mainPrefix + currentCommand.name + " `" + currentCommand.arguments + "`",
                            currentCommand.help,
                            false
                        )
                    }

                    val aliases = currentCommand.aliases
                    if (aliases.isNotEmpty()) {
                        eb.addField("Aliases", currentCommand.getAllAliases().toString().removePrefix("[").removeSuffix("]"), false)
                    }

                    eb.addField("Advanced info:",
                    """
                        Category: ${currentCommand.category.name}
                        Allowed in dms: ${!currentCommand.isGuildOnly}
                        Cooldown: ${currentCommand.cooldown} seconds
                        Owner only: ${currentCommand.isOwnerCommand}
                    """.trimIndent(), false)
                    ep.addItems(eb.build())
                    epSize++
                }
            }
            if(epSize == 0) {
                event.replyWithReference("Not found!")
            } else {
                ep.build().paginate(event.channel, 1)
            }
        }
    }
}