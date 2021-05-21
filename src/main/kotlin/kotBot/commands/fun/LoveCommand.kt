package kotBot.commands.`fun`

import com.jagrosh.jdautilities.command.CommandEvent
import dev.minn.jda.ktx.Embed
import kotBot.utils.GuildSettings
import kotBot.utils.KopyCommand
import kotBot.utils.Reference
import kotBot.utils.replyWithReference
import net.dv8tion.jda.api.entities.Message
import java.util.*

/**
 * This abstract class is for LoveCommands, which is the underlying code for all of the Valentine's day commands.
 */
abstract class LoveCommand : KopyCommand() {
    /**
     * The gifs the command can pick from.
     */
    protected abstract var gifs: Array<String>

    /**
     * The actionIdentifier is 4 characters that will be used to identify this command in the database
     */
    protected abstract var actionIdentifier: String

    /**
     * The title text used in the embed
     * Example:
     * "(user) (embedTitleText) (otheruser)"
     * "That's (number) (embedFooterText) now!"
     */
    protected abstract var embedTitleText: String

    /**
     * The footer text used in the embed
     * Example:
     * "(user) (embedTitleText) (otheruser)"
     * "That's (number) (embedFooterText) now!"
     */
    protected abstract var embedFooterText: String

    /**
     * When a command is issued, there's a percent chance that it will react with an emoji. This is where those emojis are stored.
     * Must be properly formatted. Ask Kopy. Defaults to a bunch of hearts.
     */
    protected var possibleReactions =
        arrayOf("U+2764", "U+1F496", "U+1F497", "U+1F49F", "U+2763", "U+1F49D", "U+1F49E", "U+1F495", "U+1F493")

    /**
     * The percentage chance of a reaction. Set to 0 to never have any, set to 100 to always have them.
     * Default of 33
     */
    protected var reactionPercent = 33

    /**
     * Because funny secks number.
     */
    private val sixtyNineGifs = arrayOf("https://media1.tenor.com/images/552432b67854256e7b51ab96c86d8b80/tenor.gif")

    override suspend fun onCommandRun(event: CommandEvent, guildSettings: GuildSettings) {
        if (event.isOwner && event.args.startsWith("set")) {
            if (event.args.split(",").size == 1) {
                event.reply("Format like `senderID,recieverID,newAmount`")
            } else {
                val args = event.args.split(" ")[1].split(",")
                if (args.size != 3) return

                val ps = connection.prepareStatement(
                    "UPDATE LoveCommands " +
                            "SET TimesPerformed = ${args[2]} " +
                            "WHERE SenderID = '${args[0]}' AND ReceiverID = '${args[1]}' AND ActionIdentifier = '${this.actionIdentifier}';"
                )
                val affected = ps.executeUpdate()
                event.replyWithReference("$affected rows affected")
                return
            }
        }
        //on command run, if we have mentioned users enter the command
        if (event.message.mentionedMembers.size > 0) {
            val userID: String = event.author.id //Get the IDs we need
            val mentionID: String = event.message.mentionedUsers[0].id

            var ps = Reference.connection.prepareStatement(
                "SELECT * FROM LoveCommands WHERE SenderID = '$userID' AND ReceiverID = '$mentionID' AND ActionIdentifier = '${this.actionIdentifier}';"
            )
            val rs = ps.executeQuery()

            val timesPerformed: Int
            if(rs.next()) {
                timesPerformed = rs.getInt("TimesPerformed") + 1
                ps = connection.prepareStatement(
                    "UPDATE LoveCommands " +
                            "SET TimesPerformed = $timesPerformed " +
                            "WHERE SenderID = '$userID' AND ReceiverID = '$mentionID' AND ActionIdentifier = '${this.actionIdentifier}';"
                )
                ps.executeUpdate()
            } else {
                ps = connection.prepareStatement(
                    "INSERT INTO LoveCommands VALUES ('$userID', '$mentionID', '${this.actionIdentifier}', 1);"
                )
                timesPerformed = 1
                ps.executeUpdate()
            }

            val gif: String = if (timesPerformed.toString().contains("69")) {
                sixtyNineGifs[Random().nextInt(sixtyNineGifs.size)]
            } else {
                gifs[Random().nextInt(gifs.size)]
            }

            event.channel.sendMessage(
                Embed(
                    color = guildSettings.rgb,
                    title = "${event.member.effectiveName} $embedTitleText ${event.message.mentionedMembers[0].effectiveName}",
                    image = gif,
                    footerText = "That's ${"%,d".format(timesPerformed)} $embedFooterText now!"
                )
            ).queue { message: Message ->
                if(Random().nextInt(100) < reactionPercent) {
                    message.addReaction(
                        possibleReactions[Random().nextInt(possibleReactions.size)]
                    ).queue()
                }
            }
        } else {
            event.reply("Mention a user!")
        }
    }

    class Hug : LoveCommand() {
        init {
            name = "Hug"
            help = "Hug someone!"
            reactionPercent = 40
            possibleReactions = arrayOf("U+2764", "U+1F49B")
            category = Reference.funCategory
        }

        override var actionIdentifier: String = "hugg"
        override var embedTitleText: String = "hugs"
        override var embedFooterText: String = embedTitleText
        override var gifs: Array<String> = arrayOf(
            "https://media.tenor.com/images/fb22e549f1f1c9729aeb986783c81371/tenor.gif",
            "https://media.tenor.com/images/6bc667c45027dedb2bda98fda6d3dfdd/tenor.gif",
            "https://media.tenor.com/images/572f9d54fc45c89eb67ac970d640a0ec/tenor.gif",
            "https://media1.tenor.com/images/fd47e55dfb49ae1d39675d6eff34a729/tenor.gif?itemid=12687187",
            "https://media1.tenor.com/images/2d4138c7c24d21b9d17f66a54ee7ea03/tenor.gif?itemid=12535134",
            "https://media1.tenor.com/images/8ac5ada8524d767b77d3d54239773e48/tenor.gif?itemid=16334628",
            "https://media1.tenor.com/images/d7529f6003b20f3b21f1c992dffb8617/tenor.gif?itemid=4782499",
            "https://media1.tenor.com/images/11b756289eec236b3cd8522986bc23dd/tenor.gif?itemid=10592083",
            "https://media1.tenor.com/images/24ac13447f9409d41c1aecb923aedf81/tenor.gif?itemid=5026057",
            "https://media1.tenor.com/images/dd1b8fe694d7bfba2ae87e1ede030244/tenor.gif?itemid=15999080",
            "https://media1.tenor.com/images/452bf03f209ca23c668826ffa07ea6a7/tenor.gif?itemid=15965620",
            "https://media1.tenor.com/images/ce9dc4b7e715cea12604f8dbdb49141b/tenor.gif?itemid=4451998",
            "https://media1.tenor.com/images/d71b341af9242b69b3331aae8f968750/tenor.gif?itemid=18302861",
            "https://media1.tenor.com/images/b7492c8996b25e613a2ab58a5d801924/tenor.gif?itemid=14227401",
            "https://media1.tenor.com/images/f20151a1f7e003426ca7f406b6f76c82/tenor.gif?itemid=13985247",
            "https://media1.tenor.com/images/0ccd912ac62159482be3fa6c1024c9a8/tenor.gif?itemid=13354472",
            "https://media1.tenor.com/images/3fee00811a33590e4ee490942f233c78/tenor.gif?itemid=14712845",
            "https://media1.tenor.com/images/11bb43404d06d1adabd683953fd8e294/tenor.gif?itemid=13113601",
            "https://media1.tenor.com/images/f7c08f19ec947f8a0f0b9b2eebced471/tenor.gif?itemid=5304752",
            "https://media1.tenor.com/images/474ce31f4df1e3592f5ec787f915eb37/tenor.gif?itemid=15579685",
            "https://media1.tenor.com/images/b445cd212986c5d53d2bdf7cd31f6c34/tenor.gif?itemid=9894421",
            "https://media1.tenor.com/images/ea5f73389b6e03d14369e89ca31e26f0/tenor.gif?itemid=16459675",
            "https://media1.tenor.com/images/216e23dbe3daddc40e5e17778fe29293/tenor.gif?itemid=15782411",
            "https://media1.tenor.com/images/0579398ee09fee39f5e2532823fe9d5b/tenor.gif?itemid=14091892",
            "https://media1.tenor.com/images/7106845393e9a9d584f82a6d9484c2b0/tenor.gif?itemid=13611267",
            "https://media1.tenor.com/images/e47f73d232620feb7e1889261c0c1046/tenor.gif?itemid=11070433",
            "https://media1.tenor.com/images/69a1b8dade3765d769263a587a93d863/tenor.gif?itemid=11667705",
            "https://media1.tenor.com/images/607b01352712d545f5abbc7d6ca02fdd/tenor.gif?itemid=14184904",
            "https://media1.tenor.com/images/4f9e31e2a854c2435d128defd1a1988d/tenor.gif?itemid=9836912",
            "https://media1.tenor.com/images/def3e28433435d91b1f2be072f3da59d/tenor.gif?itemid=17635753",
            "https://media1.tenor.com/images/2d97fed04f29e3594289126a15d53f33/tenor.gif?itemid=14539123",
            "https://media1.tenor.com/images/6145bba0655e427da9c46005e8ae9164/tenor.gif?itemid=13093760",
            "https://c.tenor.com/pLLBo8ET910AAAAj/love-you.gif",
            "https://c.tenor.com/yghabkhR3dIAAAAj/goodnight-couple.gif",
            "https://c.tenor.com/ETMhbi-oLz0AAAAj/miya-lili.gif",
            "https://media1.tenor.com/images/dd1b8fe694d7bfba2ae87e1ede030244/tenor.gif?itemid=15999080",
            "https://media1.tenor.com/images/b1189e353db0bed3521885bec284264b/tenor.gif?itemid=11453877",
            "https://media1.tenor.com/images/3264bcc47ee47ebbdd441f9f1d203542/tenor.gif?itemid=12498539",
            "https://media1.tenor.com/images/7f8260e2d66b4af116c866a0c63ece03/tenor.gif?itemid=16992613",
            "https://media1.tenor.com/images/9f85f4f47502af453227047339198ab0/tenor.gif?itemid=14541099",
            "https://media1.tenor.com/images/d349db7108547e020d54c40fc560091e/tenor.gif?itemid=11735639",
            "https://media1.tenor.com/images/c9e2e21f4eedd767a72004e4ab521c9d/tenor.gif?itemid=13576064",
            "https://media1.tenor.com/images/949d3eb3f689fea42258a88fa171d4fc/tenor.gif",
            "https://media1.tenor.com/images/6b87bf15500fe0b8608d9ef9e00d639c/tenor.gif",
            "https://media1.tenor.com/images/b545981037e7a3031eb54eb2dca9182e/tenor.gif"
        )
    }

    class Kiss : LoveCommand() {
        init {
            name = "Kiss"
            help = "Kiss someone!"
            reactionPercent = 60
            category = Reference.funCategory
        }

        override var actionIdentifier: String = "kiss"
        override var embedTitleText: String = "kisses"
        override var embedFooterText: String = embedTitleText
        override var gifs: Array<String> = arrayOf(
            "https://media1.tenor.com/images/5c712c9fc3f17b1735a36b8ec65996ba/tenor.gif?itemid=12535181",
            "https://media1.tenor.com/images/015c71df440861e567364cf44e5d00fe/tenor.gif?itemid=16851922",
            "https://media1.tenor.com/images/ef9687b36e36605b375b4e9b0cde51db/tenor.gif?itemid=12498627",
            "https://media1.tenor.com/images/293d18ad6ab994d9b9d18aed8a010f73/tenor.gif?itemid=13001030",
            "https://media1.tenor.com/images/31362a548dc7574f80d01a42a637bc93/tenor.gif?itemid=13985240",
            "https://media1.tenor.com/images/8658f2f1224bc500a9f3bcb045d002ec/tenor.gif?itemid=15951391",
            "https://media1.tenor.com/images/896519dafbd82b9b924b575e3076708d/tenor.gif?itemid=8811697",
            "https://media1.tenor.com/images/9fac3eab2f619789b88fdf9aa5ca7b8f/tenor.gif?itemid=12925177",
            "https://media1.tenor.com/images/4c66d14c58838d05376b5d2712655d91/tenor.gif?itemid=15009390",
            "https://media1.tenor.com/images/1306732d3351afe642c9a7f6d46f548e/tenor.gif?itemid=6155670",
            "https://media1.tenor.com/images/558f63303a303abfdddaa71dc7b3d6ae/tenor.gif?itemid=12879850",
            "https://media1.tenor.com/images/7fd98defeb5fd901afe6ace0dffce96e/tenor.gif?itemid=9670722",
            "https://media1.tenor.com/images/78095c007974aceb72b91aeb7ee54a71/tenor.gif?itemid=5095865",
            "https://media1.tenor.com/images/c760a05b0e4d45b7ec79827330ea0e6d/tenor.gif?itemid=11453879",
            "https://media1.tenor.com/images/9f85f4f47502af453227047339198ab0/tenor.gif?itemid=14541099",
            "https://media1.tenor.com/images/11c21cfa8b064836c7f0709386e5b403/tenor.gif?itemid=18239318",
            "https://media1.tenor.com/images/66f920fdd54c519f98af3a8a24fd14a7/tenor.gif?itemid=13418531",
            "https://media1.tenor.com/images/98dbf1fc71b662a76d5832b0ff4bf084/tenor.gif?itemid=12816968",
            "https://media1.tenor.com/images/04c894c51f70dd24acd59ec5392a1584/tenor.gif?itemid=12720024",
            "https://media1.tenor.com/images/4700f51c48d41104e541459743db42ae/tenor.gif?itemid=17947049",
            "https://media1.tenor.com/images/d905f920710681dd4d8b53435defcab4/tenor.gif?itemid=14646715",
            "https://media1.tenor.com/images/874cc7b9b51b7bf4e2087c398fcd4187/tenor.gif?itemid=17307705",
            "https://media1.tenor.com/images/20afd6fa304cd271ba789c45132f6755/tenor.gif?itemid=13989786",
            "https://media1.tenor.com/images/4e7dd78052008a5bc7adefe250deb5b2/tenor.gif",
            "https://media1.tenor.com/images/cd4b5a5c8db4473b6e321947d7dd27a1/tenor.gif",
            "https://media1.tenor.com/images/d7b83e67c540166976d31c8fb5f9340c/tenor.gif"
        )
    }

    class Cuddle : LoveCommand() {
        init {
            name = "Cuddle"
            help = "Cuddle someone!"
            reactionPercent = 85
            category = Reference.funCategory
        }

        override var actionIdentifier: String = "cudd"
        override var embedTitleText: String = "cuddles"
        override var embedFooterText: String = embedTitleText
        override var gifs: Array<String> = arrayOf(
            "https://c.tenor.com/yghabkhR3dIAAAAj/goodnight-couple.gif",
            "https://media1.tenor.com/images/3264bcc47ee47ebbdd441f9f1d203542/tenor.gif?itemid=12498539",
            "https://media1.tenor.com/images/8658f2f1224bc500a9f3bcb045d002ec/tenor.gif?itemid=15951391",
            "https://media1.tenor.com/images/f20151a1f7e003426ca7f406b6f76c82/tenor.gif?itemid=13985247",
            "https://media1.tenor.com/images/d905f920710681dd4d8b53435defcab4/tenor.gif?itemid=14646715",
            "https://media1.tenor.com/images/11f864ac2ac3c7ae094308f0d16495e9/tenor.gif?itemid=12554159",
            "https://media1.tenor.com/images/52471e5ee4d2c953127091efac41de23/tenor.gif?itemid=14541113",
            "https://media1.tenor.com/images/b2010973cddee5baf2d3ee369897709c/tenor.gif?itemid=16619904",
            "https://media1.tenor.com/images/5ccc34d0e6f1dccba5b1c13f8539db77/tenor.gif?itemid=17694740",
            "https://media1.tenor.com/images/d6f300f2086c6547ce44c0bff80517e6/tenor.gif?itemid=19571525",
            "https://media1.tenor.com/images/6db54c4d6dad5f1f2863d878cfb2d8df/tenor.gif?itemid=7324587",
            "https://media1.tenor.com/images/0cc4ed655f373d5ae0c55f4ca279fa0f/tenor.gif?itemid=13240992",
            "https://media1.tenor.com/images/7edded2757934756fdc240019d956cb3/tenor.gif?itemid=16403937",
            "https://media1.tenor.com/images/6f7eebef17bf270fd7e1cb9117d190be/tenor.gif?itemid=16542536",
            "https://media1.tenor.com/images/9af57b60dca6860724a0ff6c1689c246/tenor.gif?itemid=8467962",
            "https://media1.tenor.com/images/8cbe0edadc12ca1056d5eb685a4c27f6/tenor.gif?itemid=14518537",
            "https://media1.tenor.com/images/08f02bc06b2057f00b59ae56b4ffd6c4/tenor.gif?itemid=16335213",
            "https://media1.tenor.com/images/cfcf13eac39bd6271411117fa9e075c7/tenor.gif?itemid=14182306",
            "https://media1.tenor.com/images/91d70bcb828698cf5e678b1fd8abe94e/tenor.gif?itemid=16861394",
            "https://media1.tenor.com/images/b7492c8996b25e613a2ab58a5d801924/tenor.gif?itemid=14227401",
            "https://media1.tenor.com/images/bafc9da32b6f96d400d2b122a318e6dc/tenor.gif?itemid=13979929",
            "https://media1.tenor.com/images/982a14a4601015e6fba75d10e181aa10/tenor.gif?itemid=14087315",
            "https://media1.tenor.com/images/bcd8b3ec70bed429aba73284f13671f7/tenor.gif?itemid=14541101",
            "https://media1.tenor.com/images/2b72cb0a463b34e9f943dd5a1b86aa17/tenor.gif?itemid=4931555",
            "https://media1.tenor.com/images/ece6a8ca799c992af834eafb055ce554/tenor.gif?itemid=13595694",
            "https://media1.tenor.com/images/0ccd912ac62159482be3fa6c1024c9a8/tenor.gif?itemid=13354472",
            "https://media1.tenor.com/images/38560c81435e6ec0251f78e38b6dfcc2/tenor.gif?itemid=14232718",
            "https://media1.tenor.com/images/11f864ac2ac3c7ae094308f0d16495e9/tenor.gif?itemid=12554159"
        )
    }
}