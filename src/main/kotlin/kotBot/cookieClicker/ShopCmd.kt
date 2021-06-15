package kotBot.cookieClicker

import com.jagrosh.jdautilities.command.CommandEvent
import dev.minn.jda.ktx.Message
import dev.minn.jda.ktx.await
import kotBot.utils.GuildSettings
import kotBot.utils.KopyCommand
import kotBot.utils.Reference
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.exceptions.ErrorHandler
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.hooks.SubscribeEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.Button
import net.dv8tion.jda.api.interactions.components.ButtonInteraction
import net.dv8tion.jda.api.requests.ErrorResponse
import java.math.BigInteger
import java.security.SecureRandom
import java.util.*
import java.util.concurrent.TimeUnit

class ShopCmd : KopyCommand() {
    init {
        name = "Shop"
        aliases = arrayOf("Buy", "b")
        arguments = "TBD " // TODO finish arguments
        help = "For buying buildings for Cookie Clicker"
        category = Reference.cookieClickerCategory
    }

    lateinit var cp: ClickerProfile

    override suspend fun onCommandRun(event: CommandEvent, guildSettings: GuildSettings) {
        cp = ClickerProfile(event.member.id) //cp is clickerprofile
        cp.updateLastUsername(event)
        val embeds: MutableList<MessageEmbed> = mutableListOf()
        var pageNum = 1
        for (building in ClickerReference.buildings) {
            val userBuilding = cp.getUserBuildingByName(building.name)
            val eb = EmbedBuilder().setColor(guildSettings.defaultColor)
                .setTitle("$pageNum | ${building.name}:")
                .addField("Cost:", "${userBuilding.getCost()}", true)
                .addField("Amount Owned:", "${userBuilding.amount}", true)
                //.addField("Max you can buy", "${userBuilding.getMaxBuy(cp.cookies)}", true) //This lags the bot making it take more than 6 seconds sometimes
                .setFooter("Your cookies: ${cp.cookies} | Page $pageNum")
            embeds.add(eb.build())
            pageNum++
        }
        event.channel.sendShopPaginator(*embeds.toTypedArray()).queue()
    }

    private inner class ShopPaginator(private val nonce: String, private val ttl: Long) :
        EventListener {
        private val defaultPrev =
            Button.primary("prev", Emoji.fromUnicode("⬅️"))
        private val defaultNext =
            Button.primary("next", Emoji.fromUnicode("➡️"))
        private val DEFAULT_BUY =
            Button.success("buy", "Buy This")
        private val DEFAULT_DELETE =
            Button.danger("delete", Emoji.fromUnicode("\uD83D\uDEAE"))

        private var expiresAt: Long = System.currentTimeMillis() + ttl

        private var index = 0
        private val pageCache = mutableListOf<Message>()
        private val nextPage: Message get() = pageCache[++index] //TODO remove disabling

        //        {
//            val num = if(index + 1 > pageCache.size) 0 else index + 1
//            return pageCache[num]
//        }
        private val prevPage: Message get() = pageCache[--index]
//{
//            val num = if(index - 1 == -1) pageCache.size + 1 else index - 1
//            return pageCache[num]
//        }

        var filter: (ButtonInteraction) -> Boolean = { true }

        fun filterBy(filter: (ButtonInteraction) -> Boolean): ShopPaginator {
            this.filter = filter
            return this
        }

        var prev: Button = defaultPrev
        var next: Button = defaultNext
        var buy: Button = DEFAULT_BUY
        var delete: Button = DEFAULT_DELETE

        val controls: ActionRow
            get() = ActionRow.of(
                prev.withDisabled(index == 0).withId("$nonce:prev"), //TODO remove disabling
                next.withDisabled(index == pageCache.size - 1).withId("$nonce:next"),
                buy.withId("$nonce:buy"),
                delete.withId("$nonce:delete")
            )

        val pages: List<Message> get() = pageCache.toList()

        fun addPages(vararg page: Message) {
            pageCache.addAll(page)
        }

        fun addPages(vararg page: MessageEmbed) {
            addPages(*page.map { Message(embed = it) }.toTypedArray())
        }

        @SubscribeEvent
        override fun onEvent(event: GenericEvent) {
            if (expiresAt < System.currentTimeMillis())
                return unregister(event.jda)
            if (event !is ButtonInteraction) return
            val buttonId = event.componentId
            if (!buttonId.startsWith(nonce) || !filter(event)) return
            expiresAt = System.currentTimeMillis() + ttl
            val (_, operation) = buttonId.split(":")
            when (operation) {
                "prev" -> {
                    event.editMessage(prevPage)
                        .setActionRows(controls)
                        .queue(null, ErrorHandler().handle(ErrorResponse.UNKNOWN_MESSAGE) { unregister(event.jda) })
                }
                "next" -> {
                    event.editMessage(nextPage)
                        .setActionRows(controls)
                        .queue(null, ErrorHandler().handle(ErrorResponse.UNKNOWN_MESSAGE) { unregister(event.jda) })
                }
                "buy" -> {
                    if (event.user.id != cp.userID) {
                        event.reply("You arent the right person!")
                        return
                    }
                    val userBuilding = cp.buildings[index]
                    event.reply("How many ${userBuilding.building.name}s to buy?").addActionRow(
                        Button.secondary("$nonce::1", "1").withDisabled(userBuilding.getCost() > cp.cookies),
                        Button.secondary("$nonce::10", "10").withDisabled(userBuilding.getMultiCost(10) > cp.cookies),
                        Button.secondary("$nonce::100", "100")
                            .withDisabled(userBuilding.getMultiCost(100) > cp.cookies),
                        Button.secondary("$nonce::max", "${userBuilding.getMaxBuy(cp.cookies)} (Max)")
                    ).setEphemeral(true).queue()
                    GlobalScope.launch {
                        val e = event.jda.await { e: ButtonClickEvent -> e.componentId.startsWith("$nonce::") }
                        val id = e.componentId.split("::")[1]
                        var toBuy = 0
                        when (id) {
                            "1" -> {
                                toBuy = 1
                            }
                            "10" -> {
                                toBuy = 10
                            }
                            "100" -> {
                                toBuy = 100
                            }
                            "max" -> {
                                toBuy = userBuilding.getMaxBuy(cp.cookies)
                            }
                        }
                        val cost = userBuilding.getMultiCost(toBuy)
                        if (cp.cookies - cost < BigInteger("0")) {
                            e.reply("You can't afford $toBuy ${userBuilding.building.name}s!").setEphemeral(true)
                                .queue()
                        } else {
                            userBuilding.amount += toBuy
                            cp.cookies -= cost
                            cp.push()
                            e.reply("Successfully bought $toBuy ${userBuilding.building.name}s").setEphemeral(true)
                                .queue()
                        }
                    }
                }
                "delete" -> {
                    unregister(event.jda)
                    event.deferEdit().queue()
                    if (event.message == null)
                        event.hook.editOriginal(pageCache[index])
                            .setActionRows(emptyList())
                            .queue()
                    else
                        event.hook.deleteOriginal().queue(null, ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE))
                }
            }
        }

        private fun unregister(jda: JDA) {
            jda.removeEventListener(this)
        }
    }

    private fun paginator(vararg pages: Message, expireAfter: Long = TimeUnit.MINUTES.toMillis(15)): ShopPaginator {
        val nonce = ByteArray(32)
        SecureRandom().nextBytes(nonce)
        return ShopPaginator(Base64.getEncoder().encodeToString(nonce), expireAfter).also { it.addPages(*pages) }
    }

    private fun paginator(
        vararg pages: MessageEmbed,
        expireAfter: Long = TimeUnit.MINUTES.toMillis(15)
    ): ShopPaginator {
        return paginator(*pages.map { Message(embed = it) }.toTypedArray(), expireAfter = expireAfter)
    }

    private fun MessageChannel.sendShopPaginator(shopPaginator: ShopPaginator) =
        sendMessage(shopPaginator.also { jda.addEventListener(it) }.pages[0]).setActionRows(shopPaginator.controls)

    private fun MessageChannel.sendShopPaginator(
        vararg pages: MessageEmbed,
        expireAfter: Long = TimeUnit.MINUTES.toMillis(5),
        filter: (ButtonInteraction) -> Boolean = { true }
    ) = sendShopPaginator(paginator(*pages, expireAfter = expireAfter).filterBy(filter))
}