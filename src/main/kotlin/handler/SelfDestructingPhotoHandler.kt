package handler

import it.tdlight.client.SimpleTelegramClient
import it.tdlight.jni.TdApi
import it.tdlight.jni.TdApi.DownloadFile
import it.tdlight.jni.TdApi.MessagePhoto
import it.tdlight.jni.TdApi.MessageVideo
import kotlinx.coroutines.future.await
import utils.*
import java.io.File

class SelfDestructingPhotoHandler(
    private val client: SimpleTelegramClient,
    private val destinationChatId: Long,
) {

    fun onUpdateNewMessage(update: TdApi.UpdateNewMessage) {
        val message = update.message
        val sender = message.senderId
        val media = message.content

        if (sender !is TdApi.MessageSenderUser) return
        if ((media is MessagePhoto && !media.isSecret) || (media is MessageVideo && !media.isSecret)) return

        val fieldId = when (media) {
            is MessagePhoto -> media.photo.sizes.last().photo.id
            is MessageVideo -> media.video.video.id
            else -> return
        }

        val downloadFile = DownloadFile()

        downloadFile.fileId = fieldId
        downloadFile.priority = 1
        downloadFile.synchronous = true

        coroutine {
            val downloadedFile = client.send(downloadFile).await()
            val path = downloadedFile.local.path
            val caption = """
                new secret media -
                
                sender_id=tg://user?id=${sender.userId}
                message_id=${message.id}
            """.trimIndent()

            val response = when (media) {
                is MessagePhoto -> client.sendPhotoMessage(destinationChatId, path, caption, true)
                is MessageVideo -> client.sendVideoMessage(destinationChatId, path, caption, true)
                else -> return@coroutine
            }.await()

            println(response)

            File(path).delete()
        }
    }
}