package config

import com.fasterxml.jackson.annotation.JsonProperty

data class AppConfig(
    @JsonProperty("auth") val auth: AuthConfig,
    @JsonProperty("session") val session: SessionConfig,
    @JsonProperty("destination_chat_id") val destinationChatId: Long,
)
