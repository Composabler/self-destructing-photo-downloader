package handler

import it.tdlight.client.SimpleTelegramClient
import it.tdlight.jni.TdApi
import it.tdlight.jni.TdApi.DownloadFile
import it.tdlight.jni.TdApi.MessagePhoto
import kotlinx.coroutines.future.await
import utils.coroutine
import utils.sendPhotoMessage
import java.io.File

class SelfDestructingPhotoHandler(
    private val client: SimpleTelegramClient,
    private val destinationChatId: Long,
) {

    fun onUpdateNewMessage(update: TdApi.UpdateNewMessage) {
        val message = update.message
        val sender = message.senderId
        val photo = message.content

        if (sender !is TdApi.MessageSenderUser || photo !is MessagePhoto || !photo.isSecret) return

        val size = photo.photo.sizes.last()
        val downloadFile = DownloadFile()

        downloadFile.fileId = size.photo.id
        downloadFile.priority = 1
        downloadFile.synchronous = true

        coroutine {
            val downloadedFile = client.send(downloadFile).await()
            val path = downloadedFile.local.path

            client.sendPhotoMessage(destinationChatId, path, "save from ${sender.userId}", false)

            File(path).delete()
        }
    }
}