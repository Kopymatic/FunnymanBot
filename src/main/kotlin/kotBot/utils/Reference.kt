package kotBot.utils

import com.jagrosh.jdautilities.command.CommandClient
import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Activity
import java.awt.Color

class Reference () {
    companion object {
        const val botName = "KotBot"
        const val version = "Alpha"
        val status = Activity.watching(version)

        val mainPrefix = "kk"
        val alternativePrefix = "k!"
        val defaultColor: Color = Color(255, 111, 255)
        val rgb: Int = Color(255, 111, 255).rgb

        val kdb = KopyDB()
        lateinit var jda: JDA
        lateinit var waiter: EventWaiter
        lateinit var cmdClient: CommandClient
    }
}
