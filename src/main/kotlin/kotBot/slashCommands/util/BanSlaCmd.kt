package kotBot.slashCommands.util

import kotBot.slashCommands.SlashCommand
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.OptionData

class BanSlaCmd : SlashCommand() {
    init {
        commandData = CommandData("ban", "Ban a user from this server. Requires permission to ban users.")
            .addOptions(
                OptionData(
                    OptionType.USER,
                    "user",
                    "The user to ban"
                ) // USER type allows to include members of the server or other users by id
                    .setRequired(true)
            ) // This command requires a parameter
            .addOptions(
                OptionData(
                    OptionType.INTEGER,
                    "del_days",
                    "Delete messages from the past days."
                )
            )
        name = commandData.name
    }

    override fun onRunEvent(event: SlashCommandEvent) {
        val member =
            event.getOption("user")!!.asMember // the "user" option is required so it doesn't need a null-check here
        val user = event.getOption("user")!!.asUser
        event.deferReply(true).queue() // Let the user know we received the command before doing anything else

        val hook =
            event.hook // This is a special webhook that allows you to send messages without having permissions in the channel and also allows ephemeral messages

        hook.setEphemeral(true) // All messages here will now be ephemeral implicitly

        if (!event.member!!.hasPermission(Permission.BAN_MEMBERS)) {
            hook.sendMessage("You do not have the required permissions to ban users from this server.").queue()
            return
        }

        val selfMember = event.guild!!.selfMember
        if (!selfMember.hasPermission(Permission.BAN_MEMBERS)) {
            hook.sendMessage("I don't have the required permissions to ban users from this server.").queue()
            return
        }

        if (member != null && !selfMember.canInteract(member)) {
            hook.sendMessage("This user is too powerful for me to ban.").queue()
            return
        }

        var delDays = 0
        val option = event.getOption("del_days")
        if (option != null) // null = not provided
            delDays = Math.max(0, Math.min(7, option.asLong)).toInt()
        // Ban the user and send a success response
        // Ban the user and send a success response
        event.guild!!.ban(user, delDays)
            .flatMap { v: Void? ->
                hook.sendMessage(
                    "Banned user " + user.asTag
                )
            }
            .queue()
    }
}