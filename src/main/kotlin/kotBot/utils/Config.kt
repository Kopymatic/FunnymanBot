package kotBot.utils

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

data class Config(
    val mainToken: String = getFile()?.get(0)!!,
    val devToken: String = getFile()?.get(1)!!,
    val url: String = getFile()?.get(2)!!,
    val userName: String = getFile()?.get(3)!!,
    val password: String = getFile()?.get(4)!!,
)

//TODO make this not a text file
fun getFile(): List<String>? {
    val loadedFile: List<String>
    return try {
        loadedFile = Files.readAllLines(Paths.get("Config.txt"))
        loadedFile
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}