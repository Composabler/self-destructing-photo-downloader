package utils

import it.tdlight.client.SimpleTelegramClient
import it.tdlight.jni.TdApi
import it.tdlight.jni.TdApi.InputFileLocal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.CompletableFuture


val Scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

fun coroutine(block: suspend CoroutineScope.() -> Unit) = Scope.launch { block() }

fun SimpleTelegramClient.sendPhotoMessage(chatId: Long, path: String, text: String, wait: Boolean): CompletableFuture<TdApi.Message> {
    val sendMessage = TdApi.SendMessage()

    sendMessage.chatId = chatId

    val inputFile = InputFileLocal(path)
    val inputPhoto = TdApi.InputMessagePhoto()

    inputPhoto.photo = inputFile
    inputPhoto.caption = TdApi.FormattedText(text, emptyArray())

    sendMessage.inputMessageContent = inputPhoto

    return this.sendMessage(sendMessage, wait)
}