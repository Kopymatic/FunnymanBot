package kotBot

import com.jagrosh.jdautilities.command.CommandClientBuilder
import kotBot.commands.`fun`.LoveCommand
import kotBot.commands.`fun`.randomImageCommands.MemeCmd
import kotBot.commands.`fun`.randomImageCommands.NoContextCmd
import kotBot.commands.`fun`.randomImageCommands.PeopleCmd
import kotBot.commands.`fun`.randomImageCommands.PetCmd
import kotBot.commands.util.EmbedCmd
import kotBot.utils.Config
import kotBot.utils.Reference
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import com.jagrosh.jdautilities.commons.waiter.EventWaiter as EventWaiter

fun main() {
    Bot().createBot(Config().token)
    //TODO make this load from a JSON config file
}

class Bot {
    fun createBot(token: String) {
        Reference.waiter = EventWaiter()
        Reference.cmdClient = CommandClientBuilder()
            .setActivity(Reference.status)
            .setOwnerId("326489320980611075")
            .setEmojis("\u2705", "\uD83D\uDE2E", "\uD83D\uDE26")
            .setPrefix(Reference.mainPrefix)
            .setAlternativePrefix(Reference.alternativePrefix)
            .addCommands(
                NoContextCmd(),
                PeopleCmd(),
                PetCmd(),
                MemeCmd(),
                LoveCommand.Hug(),
                LoveCommand.Kiss(),
                LoveCommand.Cuddle(),
                EmbedCmd()
            )
            .build()

        Reference.jda = JDABuilder.createDefault(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS)
            .disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOTE)
            .addEventListeners(Reference.waiter, Reference.cmdClient)
            .build()
    }
}