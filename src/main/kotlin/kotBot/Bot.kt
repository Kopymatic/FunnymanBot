package kotBot

import com.jagrosh.jdautilities.command.CommandClient
import com.jagrosh.jdautilities.command.CommandClientBuilder
import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag

fun main(args: Array<String>) {
    Bot().createBot(args[0])
}

class Bot() {
    lateinit var jda: JDA
    lateinit var commandClient: CommandClient
    val waiter = EventWaiter()

    fun createBot(token: String) {
        jda = JDABuilder.createDefault(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS)
            .disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOTE)
            .addEventListeners(waiter)
            .build()

        commandClient = CommandClientBuilder()
            .setActivity(Activity.watching("A")) //This don't work
            .setOwnerId("326489320980611075")
            .setEmojis("\u2705", "\uD83D\uDE2E", "\uD83D\uDE26")
            .setPrefix("kk")
            .setAlternativePrefix("k!")
            .addCommands()
            .build()

        jda.addEventListener(commandClient)
    }
}