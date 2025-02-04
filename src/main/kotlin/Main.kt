import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import config.AppConfig
import handler.AuthorizationHandler
import handler.SelfDestructingPhotoHandler
import it.tdlight.jni.TdApi
import it.tdlight.jni.TdApi.ChatList
import it.tdlight.jni.TdApi.ChatListMain
import it.tdlight.jni.TdApi.TopChatCategory
import it.tdlight.jni.TdApi.TopChatCategoryUsers
import kotlinx.coroutines.future.await
import utils.coroutine
import utils.sendPhotoMessage
import utils.sendVideoMessage
import java.io.File

fun main(args: Array<String>) {
    if (args.size != 1) {
        println("Specify path to config")
        return
    }

    val configFilePath = args[0]
    val config = loadConfig(configFilePath)

    val client = TelegramClientInitializer.initClient(config.auth, config.session)
    val authorizationHandler = AuthorizationHandler()
    val selfDestructingPhotoHandler = SelfDestructingPhotoHandler(client, config.destinationChatId)

    TelegramClientInitializer.addUpdateHandlers(client, authorizationHandler, selfDestructingPhotoHandler)
}

private fun loadConfig(filePath: String): AppConfig {
    val objectMapper = ObjectMapper()
    return objectMapper.readValue(File(filePath))
}