package dev.approvers.jubilant.client

import dev.approvers.jubilant.commands.CommandManager
import dev.approvers.jubilant.commands.SystemMessageFormatter
import dev.approvers.jubilant.commands.abc.EventListener
import dev.approvers.jubilant.commands.abc.AbstractCommand
import dev.approvers.jubilant.commands.event.EventInfo
import dev.approvers.jubilant.commands.event.EventType
import dev.approvers.jubilant.exception.TokenMissingException
import dev.approvers.jubilant.type.sendable.EmbedSendable
import dev.approvers.jubilant.type.sendable.Sendable
import dev.approvers.jubilant.type.sendable.StringSendable
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import kotlin.system.exitProcess

/**
 * Discordのクライアント。
 */
class Client(
   val clientSettingInfo: ClientSettingInfo,
   formatter: SystemMessageFormatter? = null
) : ListenerAdapter() {

   private val defaultFormatter : SystemMessageFormatter = object : SystemMessageFormatter {
      override fun onClientReady(event: ReadyEvent): Sendable?
              = StringSendable("*\uD83D\uDE80 ${clientSettingInfo.botName}が起動しました。*")

      override fun onCommandSucceed(command: String, subCommand: String, event: MessageReceivedEvent): Sendable?
              = null

      override fun onCommandFailed(command: String, subCommand: String, event: MessageReceivedEvent): Sendable?
              = null

      override fun onExceptionThrown(
         command: String,
         subCommand: String,
         event: MessageReceivedEvent,
         exception: Throwable
      ): Sendable? = EmbedSendable(
         EmbedBuilder().apply {
            setTitle("コマンドの実行中に例外が発生しました！")
            setFooter("このBotのバグである可能性があります。開発者に連絡してください。")
            setDescription("開発者向け情報:")
            addField("例外クラス名", "`${exception::class.simpleName}`", false)
            addField("説明", exception.message, false)
            setColor(0xed5353)
         }.build()
      )

      override fun onInvalidArgumentsPassed(
         command: String,
         subCommand: String,
         event: MessageReceivedEvent
      ): Sendable? = StringSendable("引数が不正です。ヘルプ(`${clientSettingInfo.prefix}$command help`)を確認してください")

      override fun onUnknownCommandPassed(
         command: String,
         event: MessageReceivedEvent
      ): Sendable? = StringSendable("不明なコマンドです。ヘルプ(`${clientSettingInfo.prefix}help`)を参照してください")

      override fun onUnknownSubCommandPassed(
         command: String,
         subCommand: String,
         event: MessageReceivedEvent
      ): Sendable? = StringSendable("不正なサブコマンドです。ヘルプ(`${clientSettingInfo.prefix}$command help`)を確認してください")
   }

   /**
    * システムメッセージを整形する。
    */
   private val systemMessageFormatter = formatter ?: defaultFormatter

   /**
    * JDA実体
    */
   private lateinit var discordClient: JDA

   /**
    * コマンドを管理する。
    */
   private val commandManager: CommandManager = CommandManager(clientSettingInfo.prefix, systemMessageFormatter)

   /**
    * コマンドを実行する
    */
   fun launch() {

      val token = clientSettingInfo.token ?: System.getenv("DISCORD_TOKEN") ?: throw TokenMissingException()

      discordClient = JDABuilder.createDefault(token)
         .addEventListeners(this)
         .build()

   }

   /**
    * メッセージに反応するコマンドを登録する。
    * @param command コマンド。
    */
   fun addMessageCommand(command: AbstractCommand) {
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

      systemMessageFormatter.onClientReady(event)?.send(channel)
      commandManager.dispatchEvent(EventInfo(event, EventType.BOT_READY))

   }

   override fun onMessageReceived(event: MessageReceivedEvent) {

      if (event.author.isBot && !clientSettingInfo.botIdsWhiteList.contains(event.author.idLong)) return
      if (clientSettingInfo.reactOnlyLoggingChanel && event.channel.idLong != clientSettingInfo.loggingChannelId) return

      event.message.contentDisplay.ifEmpty { return }

      commandManager.executeCommand(event)

   }

}
