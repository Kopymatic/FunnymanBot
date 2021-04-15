package kotBot.commands.`fun`.randomImageCommands

import com.jagrosh.jdautilities.command.CommandEvent
import kotBot.Bot

class NoContextCmd : RandomImageCommand() {
    override fun onCommandRun(event: CommandEvent) {
        this.event = event

            if (event.message.attachments.size > 0) {
                //if it has attachments, import
                if (import()) {
                    event.reactSuccess()
                } else {
                    event.reactError()
                }
            }
//            else {
//                if (event.args.toLowerCase().contains("all")) {
//                    sendWithPagination(null)
//                } else if (event.args.toLowerCase().contains("edit")) {
//                    if (editLink()) {
//                        event.reactSuccess()
//                    } else {
//                        event.reactError()
//                    }
//                } else if (event.args.toLowerCase().contains("delete")) {
//                    delete()
//                } else {
//                    sendWithLink()
//                }
//            }
    }

    init {
        this.name = "NoContext"
        this.aliases = arrayOf("nc")
        this.help = "Shows a random no context image from the group chat"
        this.arguments = "[Image request # (Optional)], [Edit], [Link to message], [delete]"
    }

    override var dbTableName: String = "NoContext"
    override var footers: Array<String> = arrayOf("Laugh. Now.", "laugh! >:(", "nice meme, very poggers")
}