package handler

import it.tdlight.client.SimpleTelegramClient
import it.tdlight.jni.TdApi
import it.tdlight.jni.TdApi.DownloadFile
import it.tdlight.jni.TdApi.InputFile
import it.tdlight.jni.TdApi.InputFileGenerated
import it.tdlight.jni.TdApi.InputFileId
import it.tdlight.jni.TdApi.InputFileLocal
import it.tdlight.jni.TdApi.InputFileRemote
import it.tdlight.jni.TdApi.InputMessageDocument
import it.tdlight.jni.TdApi.InputMessagePhoto
import it.tdlight.jni.TdApi.InputThumbnail
import it.tdlight.jni.TdApi.Message
import it.tdlight.jni.TdApi.MessagePhoto
import it.tdlight.jni.TdApi.MessageSelfDestructType
import it.tdlight.jni.TdApi.MessageSelfDestructTypeTimer
import it.tdlight.jni.TdApi.SendMessage
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.future.await
import utils.coroutine
import utils.sendTextMessage
import java.io.DataInput
import java.io.DataInputStream

@OptIn(ObsoleteCoroutinesApi::class)
class MessageHandler(
    private val client: SimpleTelegramClient,
    private val leoMatchBotId: Long,
    private val matchWords: Set<String>,
) {

    fun onUpdateNewMessage(update: TdApi.UpdateNewMessage) {
        val message = update.message
        val sender = message.senderId

        if (sender !is TdApi.MessageSenderUser || message.content !is MessagePhoto) return

        val photo = message.content as MessagePhoto
        val size = photo.photo.sizes.last()
        val downloadFile = DownloadFile()

        downloadFile.fileId = size.photo.id
        downloadFile.priority = 1
        downloadFile.synchronous = true


    }

    private fun processMessage(update: TdApi.UpdateNewMessage) {
        val message = update.message
        val sender = message.senderId

        println(message.chatId)
        if (sender !is TdApi.MessageSenderUser) return

        if (message.content is MessagePhoto && message.chatId == 565543331L) {
            val photo = message.content as MessagePhoto
            println("receive message -> $photo")
            val size = photo.photo.sizes.last()

            val downloadFile = DownloadFile()
            downloadFile.fileId = size.photo.id
            downloadFile.priority = 1
            downloadFile.synchronous = true

            coroutine {
                val response = client.send(downloadFile).await()
                val path = response.local.path

                val sendMessage = SendMessage()

                sendMessage.chatId = 1010083874L

                val inputFile = InputFileLocal()

                inputFile.path = path

                val inputPhoto = InputMessagePhoto()
                inputPhoto.photo = inputFile

                sendMessage.inputMessageContent = inputPhoto

                client.sendMessage(sendMessage, false)
            }

/*            sendMessage.inputMessageContent = inputDocument

            coroutine {
                val response = client.sendMessage(sendMessage, true).await()
                println("sent message -> $response")
            }*/
        }

        if (sender.userId != leoMatchBotId) return

        val text = when (val content = message.content) {
            is TdApi.MessagePhoto -> content.caption.text
            is TdApi.MessageVideo -> content.caption.text
            else -> return
        }

        val words = text
            .split(" ", ",", ".", "-", "(", ")", "!", "?", ":", ";", "\"", "«", "»", "–", "\n", "\r", "\t")
            .map(String::trim)
            .filter(String::isNotEmpty)

        if (words.intersect(matchWords).isEmpty()) {
            client.sendTextMessage(leoMatchBotId, "\uD83D\uDC4E", wait = false)
            println("skip")
            return
        }

        println("match")
        client.sendTextMessage(leoMatchBotId, "match", wait = false)
    }
}