package dev.approvers.jubilant.client

import dev.approvers.jubilant.commands.CommandManager
import dev.approvers.jubilant.commands.abc.EventListener
import dev.approvers.jubilant.commands.abc.MessageCommand
import dev.approvers.jubilant.commands.event.EventInfo
import dev.approvers.jubilant.commands.event.EventType
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.lang.NullPointerException
import kotlin.system.exitProcess

/**
 * Discordのクライアント。
 */
class Client(
   val clientSettingInfo: ClientSettingInfo
) : ListenerAdapter() {

   /**
    * JDA実体
    */
   private lateinit var discordClient: JDA

   /**
    * コマンドを管理する。
    */
   private val commandManager: CommandManager = CommandManager(clientSettingInfo.prefix)

   /**
    * コマンドを実行する
    */
   fun launch() {

      val token = clientSettingInfo.token ?: System.getenv("DISCORD_TOKEN")
         ?: throw NullPointerException(
               "The token is missing! \nProvide a token via ClientSettingInfo or Environment Variable \"DISCORD_TOKEN\"."
            )

      discordClient = JDABuilder.createDefault(token)
         .addEventListeners(this)
         .build()

   }

   /**
    * メッセージに反応するコマンドを登録する。
    * @param command コマンド。
    */
   fun addMessageCommand(command: MessageCommand) {
      this.commandManager.addMessageCommand(command)
   }

   /***
    * イベントリスナーを登録する。
    * @param listener リスナー。
    */
   fun addEventListener(listener: EventListener) {
      this.commandManager.addEventCommand(listener)
   }

   override fun onReady(event: ReadyEvent) {
      val channel = discordClient.getTextChannelById(
         clientSettingInfo.loggingChannelId
      )
      if (channel == null) {
         println("--- Setting Error: Logging Channel ID ---")
         println("A channel which has Id ${clientSettingInfo.loggingChannelId} doesn't exist!")
         println("Bot will exit with exit code 1.")
         exitProcess(1)
      }
      channel.sendMessage(
         "***†${clientSettingInfo.botName} Ready†***\n" +
              "プレフィックスは`${clientSettingInfo.prefix}`です").queue()

      commandManager.dispatchEvent(EventInfo(event, EventType.BOT_READY))

   }

   override fun onMessageReceived(event: MessageReceivedEvent) {

      if (event.author.isBot && !clientSettingInfo.botIdsWhiteList.contains(event.author.idLong)) return
      if (clientSettingInfo.reactOnlyLoggingChanel && event.channel.idLong != clientSettingInfo.loggingChannelId) return

      event.message.contentDisplay.ifEmpty { return }

      commandManager.executeCommand(event)

   }

}
