package kotBot.cookieClicker

import com.jagrosh.jdautilities.command.CommandEvent
import kotBot.utils.EmbedPaginator
import kotBot.utils.GuildSettings
import kotBot.utils.KopyCommand
import kotBot.utils.Reference
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.exceptions.PermissionException
import java.util.concurrent.TimeUnit

class ShopCmd : KopyCommand() {
    init {
        name = "Shop"
        aliases = arrayOf("Buy", "b")
        arguments = "TBD " // TODO finish arguments
        help = "For buying buildings for Cookie Clicker"
        category = Reference.cookieClickerCategory
    }

    override suspend fun onCommandRun(event: CommandEvent, guildSettings: GuildSettings) {
        val cp = ClickerProfile(event.member.id) //cp is ClickerProfile
        if (event.args.isBlank()) {
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

            var pageNum = 1
            for (building in ClickerReference.buildings) {
                val userBuilding = cp.getUserBuildingByName(building.name)
                val eb = EmbedBuilder().setColor(guildSettings.defaultColor)
                    .setTitle("$pageNum | ${building.name}:")
                    .addField("Cost:", "${userBuilding.getCost()}", true)
                    .addField("Amount Owned:", "${userBuilding.amount}", true)
                    .addField("Max you can buy", "${userBuilding.getMaxBuy(cp.cookies)}", true)
                    .setFooter("Your cookies: ${cp.cookies} | Page $pageNum")
                ep.addItems(eb.build())
                pageNum++
            }
            ep.build().paginate(event.channel, 0)
        }
    }
}