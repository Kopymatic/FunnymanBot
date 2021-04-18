package kotBot.commands.`fun`.randomImageCommands

import com.jagrosh.jdautilities.command.CommandEvent
import dev.minn.jda.ktx.Embed
import kotBot.utils.*
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.exceptions.PermissionException
import java.awt.Color
import java.sql.ResultSet
import java.util.*
import java.util.concurrent.TimeUnit


abstract class RandomImageCommand : KopyCommand() {
    /**
     * The name of the database table to store this command's info inside of - this MUST BE SET
     */
    protected var dbTableName: String = "Default"

    /**
     * The footers this command can show - this MUST BE SET
     */
    protected var footers: Array<String> = arrayOf("default")

    /**
     * The commandevent to use
     */
    protected lateinit var event: CommandEvent

    /**
     * Default behavior - can be overridden
     */
    override fun onCommandRun(event: CommandEvent) {
        this.event = event
        when {
            event.message.hasAttachments() -> import()
            event.args.toLowerCase().trim().startsWith("edit") -> edit()
            event.args.toLowerCase().trim().startsWith("delete") -> delete()
            else -> send()
        }
    }

    /**
     * Function to import a new entry
     */
    protected fun import() {
        val rawText = event.args
        var textTag = "NULL" //sql null
        var linkTag = "NULL" //sql null

        val matcher = Message.JUMP_URL_PATTERN.matcher(event.message.contentRaw)
        if (matcher.find()) {
            linkTag = matcher.group(0) //This might not work
            textTag = rawText.replace(linkTag, "")
            if (textTag.isBlank()) textTag = "NULL"
        } else {
            textTag = rawText
        }

        //DEBUG STATEMENT
        println(
            "New ${this.name} entry:\nid: DEFAULT\nguildID: ${event.guild.id}\nImageLink: ${event.message.attachments[0].url}\n" +
                    "LinkTag: $linkTag\nTextTag: $textTag\nImporterID: ${event.member.id}\nImportMessageID: ${event.message.id}"
        )

        val ps = kdb.connection.prepareStatement(
            """
            INSERT INTO ${this.dbTableName}
            VALUES(DEFAULT, ?, ?, ?, ?, ?, ?);
            """.trimIndent()
        )
        ps.setString(1, event.guild.id) //Set the first ? in the prepared statement to guildID
        ps.setString(2, event.message.attachments[0].url) //second ? is imageURL
        ps.setString(3, linkTag) //third is linkTag
        ps.setString(4, textTag) //fourth is textTag
        ps.setString(5, event.member.id) //fifth is memberID
        ps.setString(6, event.message.id) //Last is messageID
        try {
            ps.executeUpdate()
        } catch (e: Exception) {
            e.printStackTrace()
            event.reactError()
            return
        }
        event.reactSuccess()
    }

    fun edit() {
        val toEdit = try {
            event.args.split(" ")[1].toInt()
        } catch (e: NumberFormatException) {
            event.replyWithReference("Invalid number!")
            return
        }

        val rs = kdb.querySQL("SELECT * FROM ${this.dbTableName} WHERE id = $toEdit;")
        if (rs.next()) {
            if (!isValidGuild(rs.getString("GuildID")) && !(event.guild.id == "654578321543266305" || event.guild.id == "793293945437814797")) {
                event.reply("This ID isn't available in this guild!")
                return
            }

            val rawText = event.args.replace("edit $toEdit", "", true)
            var textTag = "NULL" //sql null
            var linkTag = "NULL" //sql null

            val matcher = Message.JUMP_URL_PATTERN.matcher(event.message.contentRaw)
            if (matcher.find()) {
                linkTag = matcher.group(0) //This might not work
                textTag = rawText.replace(linkTag, "")
                if (textTag.isBlank()) textTag = "NULL"
            } else {
                textTag = rawText
            }

            val ps = kdb.connection.prepareStatement(
                """
                UPDATE ${this.dbTableName}
                SET textTag = ?, linkTag = ?
                WHERE id = ?;
            """.trimIndent()
            )
            ps.setString(1, textTag)
            ps.setString(2, linkTag)
            ps.setInt(3, toEdit)

            if (ps.executeUpdate() == 1) {
                event.reactSuccess()
            } else {
                event.reactError()
            }
        } else {
            event.replyWithReference("Invalid number! Either not a valid ID or out of database range!")
        }
    }

    fun delete() {
        val toDelete = try {
            event.args.split(" ")[1].toInt()
        } catch (e: NumberFormatException) {
            event.replyWithReference("Invalid number!")
            return
        }

        val rs = kdb.querySQL("SELECT * FROM ${this.dbTableName} WHERE id = $toDelete;")
        if (rs.next()) {
            if (!isValidGuild(rs.getString("GuildID")) && !(event.guild.id == "654578321543266305" || event.guild.id == "793293945437814797")) {
                event.reply("This ID isn't available in this guild!")
                return
            }

            val ps = kdb.connection.prepareStatement("DELETE FROM ${this.dbTableName} WHERE id=$toDelete;")
            if (ps.executeUpdate() == 1) {
                event.reactSuccess()
            }
        } else {
            event.replyWithReference("Invalid number! Either not a valid ID or out of database range!")
        }
    }

    fun send() {
        val ps1 = kdb.connection.prepareStatement("SELECT MAX(id) FROM ${this.dbTableName}")
        val rs1 = ps1.executeQuery()
        rs1.next()
        val dbSize = rs1.getInt(1)

        if (event.args.isBlank()) {
            var validEntry = false
            var rs: ResultSet
            do {
                val rndNum = Random().nextInt(dbSize) + 1
                val ps = kdb.connection.prepareStatement("SELECT * FROM ${this.dbTableName} WHERE id = $rndNum")
                rs = ps.executeQuery()

                val nextSuccess = rs.next() //true if next was successful
                if (!nextSuccess) continue

                val guildID = rs.getString("guildID")
                if (isValidGuild(guildID) || (event.guild.id == "654578321543266305" || event.guild.id == "793293945437814797")) {
                    validEntry = true
                }
            } while (!validEntry && !nextSuccess)

            event.reply(makeEmbed(rs))

        } else {
            val args = event.args
            try {
                val request = args.toInt()

                val ps = kdb.connection.prepareStatement("SELECT * FROM ${this.dbTableName} WHERE id = $request;")
                val rs = ps.executeQuery()
                if (!rs.next()) {
                    event.replyWithReference(
                        Embed(
                            description = "This entry is unavailable! Its likely it was deleted or is out of the database range",
                            footerText = "The highest ID is $dbSize, but some may be missing due to deleting",
                            color = Color.red.rgb
                        )
                    )
                    return
                }

                val guildID = rs.getString("guildID")
                //Check if the guild is valid, with a couple hardcoded testing guilds
                if (!isValidGuild(guildID) && !(event.guild.id == "654578321543266305" || event.guild.id == "793293945437814797")) {
                    event.replyWithReference("That ${this.name} entry is not available in this guild!")
                    return
                }
                event.reply(makeEmbed(rs))

            } catch (e: NumberFormatException) {
                val ps =
                    kdb.connection.prepareStatement("SELECT * FROM ${this.dbTableName} WHERE TextTag ILIKE ?;") //ILIKE means case insensitive
                ps.setString(1, "%$args%")
                val rs = ps.executeQuery()

                if (rs.next()) {
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
                        .waitOnSinglePage(true)
                        .setLeftRightText("l", "r")

                    var pageNum = 1

                    ep.addItems(makeEmbed(rs, "Page $pageNum"))
                    while (rs.next()) {
                        pageNum++
                        ep.addItems(makeEmbed(rs, "Page $pageNum"))
                    }
                    ep.build().paginate(event.channel, 0)


                } else {
                    event.replyWithReference("No results found!")
                    return
                }
            }
        }
    }

    private fun isValidGuild(guildID: String): Boolean {
        return this.event.guild.id == guildID
    }

    private fun makeEmbed(rs: ResultSet): MessageEmbed {
        return makeEmbed(rs, footers[Random().nextInt(footers.size)])
    }

    private fun makeEmbed(rs: ResultSet, footer: String): MessageEmbed {
        val id = rs.getInt("id")
        val textTag = rs.getString("textTag")
        val linkTag = rs.getString("linkTag")
        val image = rs.getString("imageLink")

        var descText: String? = "[$textTag]($linkTag)"
        var url: String? = null

        if (textTag == "NULL" && linkTag != "NULL") {
            url = linkTag
            descText = null
        }

        if (linkTag == "NULL") {
            descText = textTag
        }

        return (Embed(
            title = "${this.name} #$id",
            url = url,
            description = descText,
            image = image,
            footerText = footer,
            color = Reference.rgb
        ))
    }
}

class NoContextCmd : RandomImageCommand() {
    init {
        this.name = "NoContext"
        this.aliases = arrayOf("nc")
        this.help = "Shows a random no context image from the group chat"
        this.arguments = "Do ${Reference.mainPrefix}help ${this.name} for advanced help."
        this.dbTableName = "NoContext"
        this.footers = arrayOf("Laugh. Now.", "laugh! >:(", "nice meme, very poggers")
    }
}

class PeopleCmd : RandomImageCommand() {
    init {
        this.name = "People"
        this.aliases = arrayOf("me", "ppl")
        this.help = "Shows a random person"
        this.arguments = "Do ${Reference.mainPrefix}help ${this.name} for advanced help."
        this.dbTableName = "People"
        this.footers =
            arrayOf("Oh this- this is beautiful", "Looking fabulous!", "thats a cute ass person ya got there")
    }
}

class PetCmd : RandomImageCommand() {
    init {
        this.name = "Pet"
        this.aliases = arrayOf("dog", "cat")
        this.help = "Shows a random pet"
        this.arguments = "Do ${Reference.mainPrefix}help ${this.name} for advanced help."
        this.dbTableName = "Pets"
        this.footers = arrayOf(
            "Oh this- this is beautiful",
            "Looking fabulous!",
            "awwww cute pet",
            "thats a cute ass pet ya got there"
        )
    }
}

class MemeCmd : RandomImageCommand() {
    init {
        this.name = "Meme"
        this.help = "Shows a random meme"
        this.arguments = "Do ${Reference.mainPrefix}help ${this.name} for advanced help."
        this.dbTableName = "Memes"
        this.footers = arrayOf("haha funny", "nice meme, very poggers", "laugh! >:(")
    }
}