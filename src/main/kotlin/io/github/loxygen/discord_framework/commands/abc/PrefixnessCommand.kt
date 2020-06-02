package io.github.loxygen.discord_framework.commands.abc

import io.github.loxygen.discord_framework.commands.CommandInfo
import io.github.loxygen.discord_framework.commands.CommandResult
import io.github.loxygen.discord_framework.commands.annotations.Argument
import io.github.loxygen.discord_framework.commands.annotations.SubCommand
import io.github.loxygen.discord_framework.contentEquals
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import kotlin.math.min
import kotlin.reflect.KCallable
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.full.findAnnotation

/**
 * 接頭辞つきコマンドを実際に実行する機能を提供する†抽象クラス†。
 * あらゆる接頭辞つきコマンドはこれを継承してください
 */
abstract class PrefixnessCommand(
   identify: String,
   name: String,
   description: String
) : AbstractCommand() {

   override val commandInfo: CommandInfo? = CommandInfo(
      identify, name, description
   )

   /**
    * Prefixfulコマンドを実装するメソッドとそれについてるアノテーションのキャッシュ
    */
   private val prefixfulMethodCache: List<Pair<KCallable<CommandResult>, SubCommand>>

   init {
      // メソッドは変わらないし毎回リフレクションゴリゴリするの嫌なので(個人の感想)
      // ここでメソッドとアノテーションをキャッシュしてしまいます
      val prefixfulMethods: MutableList<Pair<KCallable<CommandResult>, SubCommand>> = mutableListOf()

      val expectedParamTypes = listOf(
         this::class.createType(),
         List::class.createType(listOf(KTypeProjection.invariant(String::class.createType()))),
         MessageReceivedEvent::class.createType()
      )

      for (callable in this::class.members) {
         if (callable.returnType != CommandResult::class.createType()) continue
         val commandAnt = callable.findAnnotation<SubCommand>() ?: continue
         if (!(callable.parameters.map { it.type } contentEquals expectedParamTypes)) continue

         @Suppress("UNCHECKED_CAST") // ゆるしてください
         prefixfulMethods.add(Pair(callable as KCallable<CommandResult>, commandAnt))
      }

      prefixfulMethodCache = prefixfulMethods.toList()
   }

   override fun isApplicable(query: String): Boolean {
      return query == this.commandInfo?.identify
   }

   /**
    * コマンドを解析して処理を実行する。
    * @param content 分割されたコマンドのメッセージ。
    * @param event メッセージイベント。
    */
   override fun executeCommand(content: List<String>, event: MessageReceivedEvent): CommandResult {

      val subCommandContent = content.subList(1, content.size)
      val searchQuery = if (subCommandContent.isNotEmpty()) subCommandContent[0] else ""

      // 実行対象のメソッドを取得する
      val method = try {
         fetchSubCommandMethodToRun(
            searchQuery,
            subCommandContent.size - 1
         )
      } catch (e: IllegalArgumentException) {
         return CommandResult.INVALID_ARGUMENTS
      } ?: return CommandResult.UNKNOWN_SUB_COMMAND

      // メソッドを叩いて実行結果を返す
      return method.call(
         this,
         content.subList(min(content.size, 2), content.size),
         event
      )
   }

   /**
    * 実行対象の接頭辞付きコマンドを実装しているメソッドを返す
    * @param identify サブコマンドの識別文字
    * @param argCount 与えられた引数の数
    */
   private fun fetchSubCommandMethodToRun(identify: String, argCount: Int): KCallable<CommandResult>? {

      // サブコマンドが与えられていなければexecNoSubCommand()を返す
      if (identify == "") {
         @Suppress("UNCHECKED_CAST") // ゆるしてください
         return this::class.members.find { it.name == "sendHelpText" } as KCallable<CommandResult>
      }

      for (method in this.prefixfulMethodCache) {

         if (method.second.identify != identify) continue

         // --- 引数の数が受け入れ可能か確認する
         val argumentAnt = method.first.findAnnotation<Argument>()

         // 引数を取らない(@Argumentアノテーションがない)のに引数が提供されている場合は拒否
         if (argumentAnt == null && argCount > 0) throw IllegalArgumentException()
         if (argumentAnt != null) {
            // 引数を取る(@Argumentアノテーションがある)場合はアノテーションの引数によって判断する
            if (argumentAnt.denyMore && argCount > argumentAnt.count) throw IllegalArgumentException()
            if (argumentAnt.denyLess && argCount < argumentAnt.count) throw IllegalArgumentException()
         }

         return method.first
      }
      return null
   }

   /**
    * サブコマンドが与えられなかった際に呼ばれるメソッド
    * デフォルトではヘルプが送信される
    * @param args 引数
    * @param event メッセージを受信したイベント
    */
   open fun execSingleCommand(args: List<String>, event: MessageReceivedEvent): CommandResult {
      return this.sendHelpText(args, event)
   }

   /**
    * ヘルプコマンド。
    */
   @SubCommand(identify = "help", name = "ヘルプ", description = "ヘルプを表示します。")
   @Argument(count = 0, denyLess = false, denyMore = false)
   fun sendHelpText(args: List<String>, event: MessageReceivedEvent): CommandResult {
      event.channel.sendMessage(buildString {
         append("** --- ${commandInfo!!.name} (`${commandInfo!!.identify}`) --- **\n")
         append("${commandInfo!!.description}\n```")
         prefixfulMethodCache.forEach {
            append("${it.second.name} (${it.second.identify})\n")
            append("  ${it.second.description}\n``````")
         }
         delete(length - 3, length)
      }).queue()
      return CommandResult.SUCCESS
   }

}