package io.github.loxygen.discord_framework.commands

import io.github.loxygen.discord_framework.commands.abc.EventCommand
import io.github.loxygen.discord_framework.commands.abc.MessageCommand
import io.github.loxygen.discord_framework.commands.event.EventInfo
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.awt.Event

/**
 * コマンドを司る。
 */
class CommandManager(
   private val prefix: String
) {

   init {
      if(prefix.isEmpty()) throw IllegalArgumentException("Prefix cannot be empty!")
   }

   /**
    * コマンドを実行するオブジェクトたち。
    */
   private val commands: MutableList<MessageCommand> = mutableListOf()

   /**
    * イベントで発火されるコマンドを実行するオブジェクトたち。
    */
   private val eventCommands:MutableList<EventCommand> = mutableListOf()

   /**
    * 実行対象のコマンドを追加する。
    */
   fun addCommand(command: MessageCommand) {
      commands.add(command)
   }
   /**
    * 実行対象のコマンドを追加する。
    */
   fun addEventCommand(command: EventCommand) {
      eventCommands.add(command)
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

      if (doesHavePrefix && content[0] == "help") {
         sendHelp(event.channel)
         return
      }

      // 実行する
      val result: CommandResult = commands.find {
         it.isApplicable(if (doesHavePrefix) content[0] else event.message.contentDisplay)
      }?.runCommand(content, event)
         ?: CommandResult.UNKNOWN_MAIN_COMMAND

      // 結果に応じて処理をする
      when (result) {
         CommandResult.SUCCESS -> {
            println("command succeeded:\n${event.author.name}\n  ${event.message.contentDisplay}")
         }
         CommandResult.FAILED -> {
            event.channel.sendMessage("ズサーッ！(コマンドがコケた音)").queue()
         }
         CommandResult.INVALID_ARGUMENTS -> {
            event.channel.sendMessage("引数がおかしいみたいです").queue()
         }
         CommandResult.UNKNOWN_MAIN_COMMAND -> {
            if (doesHavePrefix) event.channel.sendMessage("それ is 何").queue()
         }
         CommandResult.UNKNOWN_SUB_COMMAND -> {
            event.channel.sendMessage("そのサブコマンド is 何").queue()
         }
      }
   }

   fun dispatchEvent(eventInfo: EventInfo) {

      eventCommands
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