package kotBot.commands.`fun`

import kotBot.utils.KopyCommand
import com.jagrosh.jdautilities.command.CommandEvent
import dev.minn.jda.ktx.Embed
import kotBot.utils.Reference
import java.util.*

class OneVOneCmd : KopyCommand() {
    init {
        name = "1v1"
        aliases = arrayOf("OneVsOne", "kill")
        help = "Battle between two things you choose (or a thing and yourself)"
        arguments = "[Thing1 Thing2]"
        guildOnly = false
        category = Reference.funCategory
    }

    override fun onCommandRun(event: CommandEvent) {
        val args = event.args.split(" ").toTypedArray()
        var first: String
        var second: String
        when {
            args.isEmpty() -> {
                event.reply("You need to supply arguments!")
                return
            }
            args.size == 1 -> {
                first = event.member.asMention
                second = args[0]
            }
            else -> {
                first = args[0]
                second = args[1]
            }
        }

        if(Random().nextBoolean()) { //shuffle
            val temp = first
            first = second
            second = temp
        }
        event.reply(Embed {
            title = titles[(Random().nextInt(titles.size))]
            description = "$first **${actions[Random().nextInt(actions.size)]}** $second ${descriptors[Random().nextInt(descriptors.size)]}"
            color = Reference.rgb
        })
    }

    private val titles = arrayOf("OOOF", "Oooh that looks like it hurt", "damn bro that hurt", "ouchie", "xX_1v1_R3s7lts_Xx",
        "1v1 results", "Mothertrucker that must've hurt like a butcheek on a stick", "can we get an f in the chat")
    private val actions = arrayOf("rekt", "quickscoped", "trickshot", "multiscoped", "slowscoped", "720 noscoped",
        "shrekt", "360 noscoped", "420 noscoped", "deckt", "headshot", "murdered", "game ended", "poked", "killed", "coughed on",
        "shot", "quarantined", "gibbed", "explained quantum mechanics to", "hugged", "sucked the soul out of")
    private val descriptors = arrayOf("a little too hard", "with a poke", "by throwing a grand piano at them",
        "using a S&W Model 460 XVR Magnum", "with a Railgun", "with a george foreman grill", "using a Cheytac M200 Intervention Sniper Rifle",
        "using a salt cannon", "just by their sheer swagness", "using a lightsaber", "by throwing 9001 fedoras at them",
        "by tipping their fedora too hard", "by posting cringe", "by posting nsfw in general",
        "by slamming a fridge door at the speed of light on their face", "using a donut", "with a bowling ball",
        "with a poke", "with a broom", "with their dank memes", "by poking them with a bullet at the speed of sound",
        "using a watering hose", "using a billion airhorns", "by launching 50 nukes taped together at them",
        "using their  m a g n u m   d o n g", "using their dump truck ass", "using their absolutely massive tiddies",
        "with covid-19", "measles", "with boredom", "by taking their phone", "by saying it was their turn on the xbox",
        "with a null pointer exception"
    )
}