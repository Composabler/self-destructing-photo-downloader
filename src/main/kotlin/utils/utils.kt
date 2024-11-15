package utils

import it.tdlight.client.SimpleTelegramClient
import it.tdlight.jni.TdApi
import it.tdlight.jni.TdApi.InputFileLocal
import it.tdlight.jni.TdApi.InputMessageContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.CompletableFuture


val Scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

fun coroutine(block: suspend CoroutineScope.() -> Unit) = Scope.launch { block() }

fun SimpleTelegramClient.sendPhotoMessage(chatId: Long, path: String, text: String, wait: Boolean): CompletableFuture<TdApi.Message> {
    val inputFile = InputFileLocal(path)
    val inputPhoto = TdApi.InputMessagePhoto()

    inputPhoto.photo = inputFile
    inputPhoto.caption = TdApi.FormattedText(text, emptyArray())

    return this.sendMessage(chatId, inputPhoto, wait)
}

fun SimpleTelegramClient.sendVideoMessage(chatId: Long, path: String, text: String, wait: Boolean): CompletableFuture<TdApi.Message> {
    val inputFile = InputFileLocal(path)
    val inputVideo = TdApi.InputMessageVideo()

    inputVideo.video = inputFile
    inputVideo.caption = TdApi.FormattedText(text, emptyArray())

    return this.sendMessage(chatId, inputVideo, wait)
}

fun SimpleTelegramClient.sendVideoNoteMessage(chatId: Long, path: String, text: String, wait: Boolean): CompletableFuture<TdApi.Message> {
    val inputFile = InputFileLocal(path)
    val inputVideo = TdApi.InputMessageVideoNote()

    inputVideo.videoNote = inputFile

    return this.sendMessage(chatId, inputVideo, wait)
}

fun SimpleTelegramClient.sendMessage(chatId: Long, inputMessageContent: InputMessageContent, wait: Boolean): CompletableFuture<TdApi.Message> {
    val sendMessage = TdApi.SendMessage()

    sendMessage.chatId = chatId
    sendMessage.inputMessageContent = inputMessageContent

    return this.sendMessage(sendMessage, wait)
}