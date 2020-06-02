package io.github.loxygen.discord_framework.client

data class ClientSettingInfo(
   val botName: String,
   val prefix: String,
   val token: String?,
   val loggingChannelId: Long,
   val botIdsWhiteList: List<Long>,
   val reactOnlyLoggingChanel: Boolean
)