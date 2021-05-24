package kotBot

import com.jagrosh.jdautilities.command.CommandClientBuilder
import kotBot.commands.`fun`.*
import kotBot.commands.convenience.*
import kotBot.commands.util.*
import kotBot.utils.Reference
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag

fun main() {
    Bot().createBot(Reference.token)
    //TODO make this load from a JSON config file
}

class Bot {
    companion object {
        val allCommands = arrayOf(
            NoContextCmd(),
            PeopleCmd(),
            PetCmd(),
            MemeCmd(),
            LoveCommand.Hug(),
            LoveCommand.Kiss(),
            LoveCommand.Cuddle(),
            EmbedCmd(),
            DayTrackerCmd(),
            ChooseCmd(),
            HelpCmd(),
            PollCmd(),
            ChatDeadCmd(),
            ComplimentCmd(),
            InsultCmd(),
            OneVOneCmd(),
            RateCmd(),
            QuickStringCommand.Lenny(),
            QuickStringCommand.LennyConcern(),
            QuickStringCommand.Rick(),
            QuickStringCommand.LennyHug(),
            QuickStringCommand.LennyKiss(),
            QuickStringCommand.AddSpaces(),
            QuickStringCommand.Owoifier(),
            QuickStringCommand.Scrambler(),
            QuickStringCommand.Reverser(),
            QuickStringCommand.Say(),
            QuickStringCommand.Alphabetizer(),
            QuickStringCommand.RandomCaps(),
            QuickStringCommand.BeQuiet(),
            QuickStringCommand.AddClap(),
            DylanModeCmd(),
            RestartCmd(),
            UpdateStatusCmd(),
            SuggestCmd(),
            TestCmd(),
            Reference.ConfigCmd(),
            GuildSettingsCmd(),
            ShutdownCmd(),
            PartnerCmd(),

            )
    }

    fun createBot(token: String) {
        //Reference.waiter = EventWaiter()
        val cmdClientBuilder = CommandClientBuilder()
            .setActivity(Reference.status)
            .setOwnerId(Reference.ownerID)
            .setEmojis("\u2705", "\uD83D\uDE2E", "\uD83D\uDE26")
            .setPrefix(Reference.mainPrefix)
            .setAlternativePrefix(Reference.alternativePrefix)
            .useHelpBuilder(false)

        for (allCommand in allCommands) { //This is an enhanced for loop, tbh i'm not sure how it works
            cmdClientBuilder.addCommand(allCommand)
        }

        Reference.cmdClient = cmdClientBuilder.build()

        Reference.jda = JDABuilder.createDefault(
            token,
            GatewayIntent.GUILD_MESSAGES,
            GatewayIntent.DIRECT_MESSAGES,
            GatewayIntent.GUILD_MESSAGE_REACTIONS,
            GatewayIntent.DIRECT_MESSAGE_TYPING,
            GatewayIntent.GUILD_MESSAGE_TYPING,
            GatewayIntent.GUILD_EMOJIS
        )
            .disableCache(CacheFlag.VOICE_STATE)
            .addEventListeners(Reference.waiter, Reference.cmdClient, Reference.everyMessageManager)
            .build()
    }
}