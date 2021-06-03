package kotBot.commands.`fun`

import com.jagrosh.jdautilities.command.CommandEvent
import dev.minn.jda.ktx.Embed
import kotBot.utils.*
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
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
    private lateinit var guildSettings: GuildSettings

    /**
     * Default behavior - can be overridden
     */
    override suspend fun onCommandRun(event: CommandEvent, guildSettings: GuildSettings) {
        this.event = event
        this.guildSettings = guildSettings
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
    private fun import() {
        val rawText = event.args
        var textTag: String //sql null
        var linkTag = "NULL" //sql null

        val matcher = Message.JUMP_URL_PATTERN.matcher(event.message.contentRaw)
        if (matcher.find()) {
            linkTag = matcher.group(0) //This might not work
            textTag = rawText.replace(linkTag, "")
            if (textTag.isBlank()) textTag = "NULL"
        } else {
            textTag = rawText
        }
        val attachments = event.message.attachments
        var successes = 0
        for (i in 0 until attachments.size) {
            val ps = connection.prepareStatement(
                """
            INSERT INTO ${this.dbTableName}
            VALUES(DEFAULT, ?, ?, ?, ?, ?, ?);
            """.trimIndent()
            )
            ps.setString(1, event.guild.id) //Set the first ? in the prepared statement to guildID
            ps.setString(2, attachments[i].url) //second ? is imageURL
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
            successes++
        }
        if (successes == 0) {
            event.reactError()
        } else {
            event.reactSuccess()
        }
        if (successes > 1) {
            event.replyWithReference("Successfully imported $successes images.")
        }
    }

    private fun edit() {
        val toEdit = try {
            event.args.split(" ")[1].toInt()
        } catch (e: NumberFormatException) {
            event.replyWithReference("Invalid number!")
            return
        }

        val ps = connection.prepareStatement("SELECT * FROM ${this.dbTableName} WHERE id = $toEdit;")
        val rs = ps.executeQuery()
        if (rs.next()) {
            if (!isValidGuild(rs.getString("GuildID"))) {
                event.reply("This ID isn't available in this guild!")
                return
            }

            if (!event.member.permissions.contains(Permission.ADMINISTRATOR) && event.member.id != rs.getString("ImporterID")) {
                event.replyWithReference("You must either be a server administrator or the original importer to remove this!")
                return
            }

            val rawText = event.args.replace("edit $toEdit", "", true)
            var textTag: String //sql null
            var linkTag = "NULL" //sql null

            val matcher = Message.JUMP_URL_PATTERN.matcher(event.message.contentRaw)
            if (matcher.find()) {
                linkTag = matcher.group(0) //This might not work
                textTag = rawText.replace(linkTag, "")
                if (textTag.isBlank()) textTag = "NULL"
            } else {
                textTag = rawText
            }

            val ps = connection.prepareStatement(
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

    private fun delete() {
        val toDelete = try {
            event.args.split(" ")[1].toInt()
        } catch (e: NumberFormatException) {
            event.replyWithReference("Invalid number!")
            return
        }

        val ps = connection.prepareStatement("SELECT * FROM ${this.dbTableName} WHERE id = $toDelete;")
        val rs = ps.executeQuery()
        if (rs.next()) {
            if (!isValidGuild(rs.getString("GuildID"))) {
                event.reply("This ID isn't available in this guild!")
                return
            }

            if (!event.member.permissions.contains(Permission.ADMINISTRATOR) && event.member.id != rs.getString("ImporterID")) {
                event.replyWithReference("You must either be a server administrator or the original importer to remove this!")
                return
            }

            val ps = connection.prepareStatement("DELETE FROM ${this.dbTableName} WHERE id=$toDelete;")
            if (ps.executeUpdate() == 1) {
                event.reactSuccess()
            }
        } else {
            event.replyWithReference("Invalid number! Either not a valid ID or out of database range!")
        }
    }

    private fun send() {
        val ps1 = connection.prepareStatement("SELECT MAX(id) FROM ${this.dbTableName}")
        val rs1 = ps1.executeQuery()
        rs1.next()
        val dbSize = rs1.getInt(1)

        if (event.args.isBlank()) {
            var rs: ResultSet? = null
            var found = false
            var reallySmallNumber: Double = 1.0 / dbSize

            var whereStatement =
                "WHERE GuildID='${event.guild.id}'" //Slightly inefficient, because a guild should always be partnered with itself
            for (partner in guildSettings.partneredGuilds!!) {
                whereStatement += " OR GuildID='$partner'"
            }

            while (reallySmallNumber <= 100) {
                val ps =
                    connection.prepareStatement("SELECT * FROM ${this.dbTableName} TABLESAMPLE BERNOULLI($reallySmallNumber) $whereStatement LIMIT 1;")
                rs = ps.executeQuery()

                if (!rs.next()) {
                    reallySmallNumber *= 10
                    continue
                } else {
                    found = true
                    break
                }
            }
            if (!found) {
                val ps =
                    connection.prepareStatement("SELECT * FROM ${this.dbTableName} TABLESAMPLE BERNOULLI(100) WHERE GuildID='${event.guild.id}' LIMIT 1;")
                rs = ps.executeQuery()
                if (!rs.next()) {
                    event.replyWithReference("Error: Likely you have nothing imported in this server, or a database error has occurred.")
                    return
                }
            }
            val embed = makeEmbed(rs!!)
            if (embed == null) {
                event.reply(makeText(rs))
            } else event.reply(embed)

        } else {
            val args = event.args
            try {
                val request = args.toInt()

                val ps = connection.prepareStatement("SELECT * FROM ${this.dbTableName} WHERE id = $request;")
                val rs = ps.executeQuery()
                if (!rs.next()) {
                    event.replyWithReference(
                        Embed(
                            description = "This entry is unavailable! Its likely it was deleted or is out of the database range",
                            footerText = "The highest ID is $dbSize, but some may be unavailable due to deleting or being in different guilds",
                            color = Color.red.rgb
                        )
                    )
                    return
                }

                val guildID = rs.getString("guildID")
                //Check if the guild is valid
                if (!guildSettings.partneredGuilds!!.contains(guildID)) { //if guild is invalid
                    event.replyWithReference("That ${this.name} entry is not available in this guild!")
                    return
                }
                val embed = makeEmbed(rs!!)
                if (embed == null) {
                    event.reply(makeText(rs))
                } else event.reply(embed)

            } catch (e: NumberFormatException) {
                var whereStatement = "AND (GuildID='${event.guild.id}'"
                for (partner in guildSettings.partneredGuilds!!) {
                    whereStatement += " OR GuildID='$partner'"
                }
                whereStatement += ")"

                var ps =
                    connection.prepareStatement("SELECT * FROM ${this.dbTableName} WHERE TextTag ILIKE ? $whereStatement;") //ILIKE means case insensitive
                ps.setString(1, "%$args%") //Why do we put percent signs here??

                if (args.equals("all", true)) {
                    ps = connection.prepareStatement(
                        "SELECT * FROM ${this.dbTableName} WHERE ${
                            whereStatement.removePrefix("AND ")
                        };"
                    )
                }
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
                        .waitOnSinglePage(false)
                        .setLeftRightText("l", "r")

                    var pageNum = 1

                    do {
                        val embed = makeEmbed(rs, "Page $pageNum")
                        if (embed != null) {
                            ep.addItems(embed)
                            pageNum++
                        }
                    } while (rs.next())
                    if (pageNum >= 2) {
                        ep.build().paginate(event.channel, 0)
                    } else {
                        event.replyWithReference("No results found! (Cannot search for videos)")
                        return
                    }
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

    private fun makeEmbed(rs: ResultSet): MessageEmbed? {
        return makeEmbed(rs, footers[Random().nextInt(footers.size)])
    }

    private fun makeEmbed(rs: ResultSet, footer: String): MessageEmbed? {
        val id = rs.getInt("id")
        val textTag = rs.getString("textTag")
        val linkTag = rs.getString("linkTag")
        val image = rs.getString("imageLink")

        val invalidEndings = arrayOf(".mp4")
        var isInvalid = false
        for (ending in invalidEndings) {
            if (image.endsWith(ending, true)) {
                isInvalid = true
                break
            }
        }
        if (isInvalid) {
            return null
        }
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
            color = guildSettings.rgb
        ))
    }

    private fun makeText(rs: ResultSet): String {
        val id = rs.getInt("id")
        val textTag = rs.getString("textTag")
        val image = rs.getString("imageLink")

        return "${this.name} #$id${if (textTag.isEmpty()) "" else "\nText: $textTag"}\n$image"
            .trimIndent()
    }

    override fun getAdvancedHelp(): EmbedBuilder? {
        return EmbedBuilder()
            .setTitle("How to use ${this.name}:")
            .setDescription("${this.name} is a simple command with many complex operations you can do. Here's an explanation.")
            .addField(
                "Getting a random ${this.name} entry:",
                "This is as simple as running the command with no arguments",
                false
            )
            .addField(
                "Getting a specific ${this.name} entry:",
                "Run the command with a search or an entry id to get a specific entry" +
                        "\n**Examples:** `${Reference.mainPrefix}${this.name} (search term)` or `${Reference.mainPrefix}${this.name} (entry id)`",
                false
            )
            .addField(
                "Importing:",
                "To import an image to the database, send the command with an attachment. Optionally, you can supply a description and/or a link." +
                        "\n**Note:** Currently, only **Discord message jump links** are supported. Videos are **not** currently supported.",
                false
            )
            .addField(
                "Editing:",
                "If you ever wish to edit the text or link of something you previously imported, " +
                        "send the command the same as you would with importing, but with edit and an id at the beginning and no attachment" +
                        "\n**Example:** `${Reference.mainPrefix}${this.name} edit (entry ID) (new text here) (new link here)`" +
                        "\n**Note:** Editing an import that has a link with no link will delete that link.", false
            )
            .addField(
                "Deleting:",
                "If you wish to delete something you imported, just run the command with delete and an id" +
                        "\n**Example**: `${Reference.mainPrefix}${this.name} delete (entry ID)`",
                false
            )
    }

    init {
        botPermissions = arrayOf(
            Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_EMBED_LINKS,
            Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.VIEW_CHANNEL
        )
    }
}

class NoContextCmd : RandomImageCommand() {
    init {
        name = "NoContext"
        aliases = arrayOf("nc")
        help = "Shows a random no context image from the group chat"
        arguments = "Do ${Reference.mainPrefix}help ${this.name} for advanced help."
        dbTableName = "NoContext"
        footers = arrayOf("Laugh. Now.", "laugh! >:(", "nice meme, very poggers")
        category = Reference.funCategory
    }
}

class PeopleCmd : RandomImageCommand() {
    init {
        name = "People"
        aliases = arrayOf("me", "ppl")
        help = "Shows a random person"
        arguments = "Do ${Reference.mainPrefix}help ${this.name} for advanced help."
        dbTableName = "People"
        footers =
            arrayOf("Oh this- this is beautiful", "Looking fabulous!", "that's a cute ass person ya got there")
        category = Reference.funCategory
    }
}

class PetCmd : RandomImageCommand() {
    init {
        name = "Pet"
        aliases = arrayOf("dog", "cat")
        help = "Shows a random pet"
        arguments = "Do ${Reference.mainPrefix}help ${this.name} for advanced help."
        dbTableName = "Pets"
        footers = arrayOf(
            "Oh this- this is beautiful",
            "Looking fabulous!",
            "aww cute pet",
            "that's a cute ass pet ya got there"
        )
        category = Reference.funCategory
    }
}

class MemeCmd : RandomImageCommand() {
    init {
        name = "Meme"
        help = "Shows a random meme"
        arguments = "Do ${Reference.mainPrefix}help ${this.name} for advanced help."
        dbTableName = "Memes"
        footers = arrayOf("haha funny", "nice meme, very poggers", "laugh! >:(")
        category = Reference.funCategory
    }
}