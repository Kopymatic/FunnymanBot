package kotBot

import com.jagrosh.jdautilities.command.CommandClientBuilder
import kotBot.commands.`fun`.*
import kotBot.commands.`fun`.randomImageCommands.MemeCmd
import kotBot.commands.`fun`.randomImageCommands.NoContextCmd
import kotBot.commands.`fun`.randomImageCommands.PeopleCmd
import kotBot.commands.`fun`.randomImageCommands.PetCmd
import kotBot.commands.convenience.ChooseCmd
import kotBot.commands.convenience.HelpCmd
import kotBot.commands.convenience.PollCmd
import kotBot.commands.util.DayTrackerCmd
import kotBot.commands.util.EmbedCmd
import kotBot.utils.Config
import kotBot.utils.Reference
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag

fun main() {
    Bot().createBot(Config().token)
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
        )
    }

    fun createBot(token: String) {
        //Reference.waiter = EventWaiter()
        val cmdClientBuilder = CommandClientBuilder()
            .setActivity(Reference.status)
            .setOwnerId("326489320980611075")
            .setEmojis("\u2705", "\uD83D\uDE2E", "\uD83D\uDE26")
            .setPrefix(Reference.mainPrefix)
            .setAlternativePrefix(Reference.alternativePrefix)
            .useHelpBuilder(false)

        for (allCommand in allCommands) { //This is an enhanced for loop, tbh i'm not sure how it works
            cmdClientBuilder.addCommand(allCommand)
        }

        Reference.cmdClient = cmdClientBuilder.build()

        Reference.jda = JDABuilder.createDefault(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS)
            .disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOTE)
            .addEventListeners(Reference.waiter, Reference.cmdClient)
            .build()
    }
}