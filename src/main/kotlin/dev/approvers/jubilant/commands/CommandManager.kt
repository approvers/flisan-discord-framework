package dev.approvers.jubilant.commands

import dev.approvers.jubilant.commands.abc.EventListener
import dev.approvers.jubilant.commands.abc.AbstractCommand
import dev.approvers.jubilant.commands.event.EventInfo
import dev.approvers.jubilant.type.sendable.Sendable
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.lang.reflect.InvocationTargetException

/**
 * コマンドを司る。
 */
class CommandManager(
   private val prefix: String,
   private val formatter: SystemMessageFormatter
) {

   init {
      prefix.ifEmpty { throw IllegalArgumentException("Prefix cannot be empty!") }
   }

   /**
    * コマンドを実行するオブジェクトたち。
    */
   private val commands: MutableList<AbstractCommand> = mutableListOf()

   /**
    * イベントで発火されるコマンドを実行するオブジェクトたち。
    */
   private val eventListeners: MutableList<EventListener> = mutableListOf()

   /**
    * メッセージに反応するコマンドを追加する。
    * @param command コマンド。
    */
   fun addMessageCommand(command: AbstractCommand) {
      commands.add(command)
   }
   /**
    * イベントリスナーを追加する。
    */
   fun addEventCommand(command: EventListener) {
      eventListeners.add(command)
   }

   /**
    * [event] を基にコマンドを実行する。
    */
   fun executeCommand(event: MessageReceivedEvent) {

      // コマンドに関する情報をかき集める
      val doesHavePrefix = event.message.contentDisplay.startsWith(prefix)
      val rawText =
         event.message.contentDisplay.let { if (doesHavePrefix) it.substring(prefix.length) else it }
      val content = rawText.split(" ")

      val command = content[0]
      val subCommand = content.getOrElse(1) {"(サブコマンドなし)"}

      if (doesHavePrefix && command == "help") {
         sendHelp(event.channel)
         return
      }

      // 実行する
      val result = try {
         commands.find { it.isApplicable(if (doesHavePrefix) command else event.message.contentDisplay) }
            ?.executeCommand(content, event) ?: CommandResult.UNKNOWN_MAIN_COMMAND
      } catch (e: InvocationTargetException) {
         formatter.onExceptionThrown(command, subCommand, event, e.targetException)?.send(event.channel)
         return
      }

      // 結果に応じて処理をする
      val sendable : Sendable? = when (result) {
         CommandResult.SUCCESS -> formatter.onCommandSucceed(command, subCommand, event)
         CommandResult.FAILED -> formatter.onCommandFailed(command, subCommand, event)
         CommandResult.INVALID_ARGUMENTS -> formatter.onInvalidArgumentsPassed(command, subCommand, event)
         CommandResult.UNKNOWN_MAIN_COMMAND -> if(doesHavePrefix) formatter.onUnknownCommandPassed(command, event) else null
         CommandResult.UNKNOWN_SUB_COMMAND -> formatter.onUnknownSubCommandPassed(command, subCommand, event)
      }
      sendable?.send(event.channel)
   }

   fun dispatchEvent(eventInfo: EventInfo) {

      eventListeners
         .filter { it.isApplicable(eventInfo.eventType) }
         .forEach { it.executeCommand(eventInfo) }

   }

   private fun sendHelp(channel: MessageChannel) {
      channel.sendMessage(buildString {
         append("```")
         commands.forEach {
            val info = it.commandInfo ?: return@forEach
            append("${info.name} (${prefix}${info.identify})\n")
            append("  ${info.description}\n``````")
         }
         delete(length - 3, length)
         append("各コマンドの詳細は`//<command.name>`を叩くと表示されます")
      }).queue()
   }

}