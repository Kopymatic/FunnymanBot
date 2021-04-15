package kotBot.commands.`fun`.randomImageCommands

import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import kotBot.Bot
import kotBot.utils.KopyCommand
import net.dv8tion.jda.api.entities.Message
import java.util.*
import java.util.function.Consumer
import java.util.regex.Matcher

abstract class RandomImageCommand : KopyCommand() {
    /**
     * The name of the database table to store this command's info inside of
     */
    protected abstract var dbTableName: String

    /**
     * The footers this command can show
     */
    protected abstract var footers: Array<String>

    /**
     * The commandevent to use
     */
    protected lateinit var event: CommandEvent

    private val waiter: EventWaiter = Bot().waiter


    /**
     * Function to import a new entry
     */
    protected fun import(): Boolean {
        val rawSanitizedText = event.args
            .replace("DROP TABLE", "", true)
            .replace("DELETE FROM`", "", true)
            .replace(Regex("SELECT * FROM", RegexOption.IGNORE_CASE), "")
            .replace("--", "", true)
            .replace(";", "", true)
        var textTag = "NULL" //sql null
        var linkTag = "NULL" //sql null

        val matcher = Message.JUMP_URL_PATTERN.matcher(event.message.contentRaw)
        if(matcher.find()) {
            linkTag = matcher.group(0) //This might not work
            textTag = rawSanitizedText.replace(linkTag, "")
            if(textTag.isBlank()) textTag = "NULL"
        }

        event.reply(
            """
                id: DEFAULT
                guildID: ${event.guild.id}
                ImageLink: ${event.message.attachments[0].url}
                LinkTag: $linkTag
                TextTag: $textTag
                ImporterID: ${event.member.id}
                ImportMessageID: ${event.message.id}
            """.trimIndent()
        )
        return true
    }

//    /**
//     * method to import with tags
//     *
//     * @param event the CommandEvent given by JDA-Utils
//     * @return boolean representing success or failure
//     */
//    protected fun importWithTags(event: CommandEvent): Boolean {
//        val kf = KopyFiles(imagesFile)
//        val url: String = event.getMessage().getAttachments().get(0).getUrl()
//        val imgImport: Boolean = kf.writeNewLine(url)
//
//        //If we have text for a description, import that
//        kf.setFile(tagsFile)
//        val tagsImport: Boolean
//        tagsImport = kf.writeNewLine(event.getArgs().strip())
//        return imgImport && tagsImport
//    }
//
//    /**
//     * Method to import with links
//     *
//     * @param event the CommandEvent given by JDA-Utils
//     * @return boolean representing success or failure
//     */
//    protected fun importWithLink(event: CommandEvent): Boolean {
//        val kf = KopyFiles(imagesFile)
//        val url: String = event.getMessage().getAttachments().get(0).getUrl()
//        val args: Array<String> = event.getArgs().split(" ").toTypedArray()
//        val imgImport: Boolean = kf.writeNewLine(url)
//
//        //If we have a link, import that to the tags
//        kf.setFile(linksFile)
//        val tagsImport: Boolean = if (args[0].strip().startsWith("https://discord.com/channels/")) {
//            kf.writeNewLine(event.getArgs().strip())
//        } else if (args[0].strip().startsWith("https://canary.discord.com/channels/")) {
//            kf.writeNewLine(event.getArgs().strip().replace("canary.", ""))
//        } else {
//            kf.writeNewLine("null")
//        }
//
//        //if both imports were successful, react success
//        return imgImport && tagsImport
//    }
//
//    /**
//     * method to edit existing tags
//     *
//     * @return boolean representing success or failure
//     */
//    protected fun editTags(): Boolean {
//        //if the user wants to edit
//        val kf = KopyFiles(tagsFile)
//        val args: Array<String> = event.getArgs().split(" ").toTypedArray()
//        var newTag: String = event.getArgs().replace(args[0], "")
//        newTag = newTag.replace("edit", "").strip()
//        return kf.replaceLineInFile(newTag, args[0].toInt())
//    }
//
//    /**
//     * method to edit existing links
//     *
//     * @return boolean representing success or failure
//     */
//    protected fun editLink(): Boolean {
//        //if user wants to edit, edit
//        val kf = KopyFiles(linksFile)
//        val args: Array<String> = event.getArgs().split(" ").toTypedArray()
//        val argsString: String = event.getArgs().strip()
//        return if (args[2].strip().startsWith("https://discord.com/channels/") || argsString.contains("null")) {
//            kf.replaceLineInFile(args[2], args[0].toInt())
//        } else if (args[2].strip().startsWith("https://canary.discord.com/channels/")) {
//            kf.replaceLineInFile(args[2].strip().replace("canary.", ""), args[0].toInt())
//        } else {
//            event.reply("That isn't a valid jump url!")
//            event.reactWarning()
//            false
//        }
//    }
//
//    /**
//     * method to delete an image and associated tags/links
//     */
//    protected fun delete() {
//        val args: Array<String> = event.getArgs().split(" ").toTypedArray()
//        //delete the image
//        val kf = KopyFiles(imagesFile)
//        val images: List<String> = kf.loadFile()
//        val selection = args[0].toInt()
//        images.removeAt(selection)
//        kf.replaceFileContent(images)
//
//        //do the same with the tags and/or links
//        if (Objects.nonNull(linksFile)) {
//            kf.setFile(linksFile)
//            val links: List<String> = kf.loadFile()
//            val selection1 = args[0].toInt()
//            links.removeAt(selection1)
//            kf.replaceFileContent(links)
//        }
//        if (Objects.nonNull(tagsFile)) {
//            kf.setFile(tagsFile)
//            val tags: List<String> = kf.loadFile()
//            val selection2 = args[0].toInt()
//            tags.removeAt(selection2)
//            kf.replaceFileContent(tags)
//        }
//        event.reactSuccess()
//    }
//
//    /**
//     * Method to send the final message with a link
//     */
//    protected fun sendWithLink() {
//        //if it has no attachments, send a random image or edit
//        val eb = EmbedBuilder()
//        eb.setColor(Reference.DEFAULT_COLOR)
//        eb.setFooter(footers[Random().nextInt(footers.size)])
//        val kf = KopyFiles()
//        kf.setFile(imagesFile)
//        val images: List<String> = kf.loadFile()
//        kf.setFile(linksFile)
//        val tags: List<String> = kf.loadFile()
//        val imageChoice: Int
//        if (event.getArgs().equals("", ignoreCase = true)) {
//            //if no args, send random image
//            imageChoice = 1 + Random().nextInt(images.size - 1)
//        } else {
//            //if there are args, get them
//            val args: Array<String> = event.getArgs().split(" ").toTypedArray()
//            imageChoice = args[0].toInt()
//            if (imageChoice > images.size - 1 || imageChoice <= 0) {
//                //if request is invalid, send error
//                eb.setTitle("The dataset has " + (images.size - 1) + " images, your number is invalid!")
//                eb.setFooter(null)
//                eb.setColor(Color.red)
//                event.reply(eb.build())
//                return
//            }
//        }
//        //Once we get imageChoice, we send the image
//        val finalImage = images[imageChoice]
//        if (finalImage.contains(".mov") || finalImage.contains(".mp4")) {
//            if (tags[imageChoice] == "null") {
//                event.reply(this.name.toString() + " #" + imageChoice + "\n" + finalImage)
//            } else {
//                event.reply(this.name.toString() + " #" + imageChoice + "\n" + finalImage + "\n" + tags[imageChoice])
//            }
//            return
//        } else {
//            eb.setImage(images[imageChoice])
//        }
//        if (tags[imageChoice] == "null") {
//            eb.setTitle(this.name.toString() + " #" + imageChoice, null)
//        } else {
//            eb.setTitle(this.name.toString() + " #" + imageChoice, tags[imageChoice])
//        }
//        // Reply with the embed
//        event.reply(eb.build())
//    }
//
//    /**
//     * Method to send the final embed with tags
//     */
//    protected fun sendWithTags() {
//
//        //if it isn't importing
//        val eb = EmbedBuilder()
//        eb.setColor(Reference.DEFAULT_COLOR)
//        eb.setFooter(footers[Random().nextInt(footers.size)])
//        val kf = KopyFiles()
//        kf.setFile(imagesFile)
//        val images: List<String> = kf.loadFile()
//        kf.setFile(tagsFile)
//        val tags: List<String> = kf.loadFile()
//        val imageChoice: Int
//        if (event.getArgs().equals("", ignoreCase = true)) {
//            //if no args, send rand image
//            imageChoice = 1 + Random().nextInt(images.size - 1)
//        } else {
//            //if no args, get them
//            val args: Array<String> = event.getArgs().split(" ").toTypedArray()
//            imageChoice = args[0].toInt()
//            if (imageChoice > images.size - 1 || imageChoice <= 0) {
//                //if invalid request, send error
//                eb.setTitle("The dataset has " + (images.size - 1) + " images, your number is invalid!")
//                eb.setFooter(null)
//                eb.setColor(Color.red)
//                event.reply(eb.build())
//                return
//            }
//        }
//        //once we get imageChoice, send
//        eb.setImage(images[imageChoice])
//        eb.setTitle(this.name.toString() + " #" + imageChoice, null)
//        eb.setDescription(tags[imageChoice])
//        // Reply with the embed
//        event.reply(eb.build())
//    }
//
//    protected fun sendWithPagination(filterTags: String) {
//        val kf = KopyFiles(imagesFile)
//        if (Objects.isNull(filterTags)) {
//            val images: List<String> = kf.loadFile()
//            images.removeAt(0)
//            val items: Array<Any> = images.toTypedArray()
//            val stringArray = Arrays.copyOf(items, items.size, Array<String>::class.java)
//            val ss: Slideshow.Builder = Slideshow.Builder()
//                .setUrls(*stringArray)
//                .setColor(Reference.DEFAULT_COLOR)
//                .showPageNumbers(true)
//                .setText(EmbedBuilder.ZERO_WIDTH_SPACE)
//                .setEventWaiter(waiter)
//                .setTimeout(2, TimeUnit.MINUTES)
//                .setBulkSkipNumber(11)
//                .allowTextInput(true)
//                .wrapPageEnds(true)
//                .setFinalAction(Consumer { m: Message ->
//                    try {
//                        m.clearReactions().queue()
//                    } catch (ex: PermissionException) {
//                        logError(
//                            """Bot doesn't have the proper permissions in guild ${event.getGuild().getName()}
// Exception message${ex.message}"""
//                        )
//                        m.delete().queue()
//                    }
//                })
//                .setDescription(this.name)
//            ss.build().paginate(event.getChannel(), 1)
//        } else {
//            val images: List<String> = kf.loadFile()
//            images.removeAt(0)
//            kf.setFile(tagsFile)
//            val tags: List<String> = kf.loadFile()
//            tags.removeAt(0)
//            val finalImages: MutableList<String> = ArrayList()
//            for (i in images.indices) {
//                if (tags[i].toLowerCase().contains(filterTags.toLowerCase())) {
//                    finalImages.add(images[i])
//                }
//            }
//            if (finalImages.size <= 0) {
//                event.reply("No images found for $filterTags")
//                return
//            }
//            val items: Array<Any> = finalImages.toTypedArray()
//            val stringArray = Arrays.copyOf(items, items.size, Array<String>::class.java)
//            val ss: Slideshow.Builder = Slideshow.Builder()
//                .setUrls(*stringArray)
//                .setColor(Reference.DEFAULT_COLOR)
//                .showPageNumbers(true)
//                .setText(EmbedBuilder.ZERO_WIDTH_SPACE)
//                .setEventWaiter(waiter)
//                .setTimeout(2, TimeUnit.MINUTES)
//                .setBulkSkipNumber(11)
//                .allowTextInput(true)
//                .wrapPageEnds(true)
//                .setFinalAction(Consumer { m: Message ->
//                    try {
//                        m.clearReactions().queue()
//                    } catch (ex: PermissionException) {
//                        logError(
//                            """Bot doesn't have the proper permissions in guild ${event.getGuild().getName()}
// Exception message${ex.message}"""
//                        )
//                        m.delete().queue()
//                    }
//                })
//                .setDescription(this.name)
//            ss.build().paginate(event.getChannel(), 1)
//        }
//    }
}