package kotBot.utils

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import kotBot.Bot

abstract class KopyCommand: Command() {
    val kdb = Bot().kdb
    /**
     * -- DO NOT OVERRIDE --
     * Use onCommandRun instead
     */
    override fun execute(event: CommandEvent?) {
        if(event == null) return
        trackStats(event)
        onCommandRun(event)
    }

    /**
     * KopyCommand's implementation of execute, except event is nonnull and stats are tracked
     */
    abstract fun onCommandRun(event: CommandEvent)

    /*
    For tracking bot statistics
     */
    private fun trackStats(event: CommandEvent) {
        //TODO("Not yet implemented! - needs database")
    }
}