package io.github.loxygen.discord_framework.commands.abc

import io.github.loxygen.discord_framework.commands.CommandInfo
import io.github.loxygen.discord_framework.commands.CommandResult
import io.github.loxygen.discord_framework.commands.annotations.PrefixlessSubCommand
import io.github.loxygen.discord_framework.contentEquals
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import kotlin.reflect.KCallable
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.full.findAnnotation

/**
 * 接頭辞なしコマンドを実際に実行する機能を提供する†抽象クラス†。
 * あらゆる接頭辞なしコマンドはこれを継承してください
 */
abstract class PrefixlessCommand : AbstractCommand() {

   override val commandInfo: CommandInfo? = null

   /**
    * Prefixlessコマンドを実装するメソッドとそれについてるアノテーションのキャッシュ
    */
   private val prefixlessMethodCache: List<Pair<KCallable<CommandResult>, PrefixlessSubCommand>>

   init {
      // メソッドは変わらないし毎回リフレクションゴリゴリするの嫌なので(個人の感想)
      // ここでメソッドとアノテーションをキャッシュしてしまいます
      val prefixlessMethods: MutableList<Pair<KCallable<CommandResult>, PrefixlessSubCommand>> = mutableListOf()

      val expectedParamTypes = listOf(
         this::class.createType(),
         List::class.createType(listOf(KTypeProjection.invariant(String::class.createType()))),
         MessageReceivedEvent::class.createType()
      )

      for (callable in this::class.members) {
         if (callable.returnType != CommandResult::class.createType()) continue
         val commandAnt = callable.findAnnotation<PrefixlessSubCommand>() ?: continue
         if (!(callable.parameters.map { it.type } contentEquals expectedParamTypes)) continue

         @Suppress("UNCHECKED_CAST") // ゆるしてください
         prefixlessMethods.add(Pair(callable as KCallable<CommandResult>, commandAnt))
      }

      prefixlessMethodCache = prefixlessMethods.toList()
   }

   override fun isApplicable(query: String): Boolean {
      return this.prefixlessMethodCache.find { Regex(it.second.triggerRegex).containsMatchIn(query) } != null
   }

   /**
    * コマンドを解析して処理を実行する。
    * @param content 分割されたコマンドのメッセージ。
    * @param event メッセージイベント。
    */
   override fun executeCommand(content: List<String>, event: MessageReceivedEvent): CommandResult {

      // 実行対象のメソッドを取得する
      val method = fetchSubCommandMethodToRun(event.message.contentDisplay)
         ?: return CommandResult.UNKNOWN_SUB_COMMAND

      // メソッドを叩いて実行結果を返す
      return method.call(
         this,
         event.message.contentDisplay,
         event
      )
   }

   /**
    * 実行対象の接頭辞付きコマンドを実装しているメソッドを返す
    * @param content メッセージの中身
    */
   private fun fetchSubCommandMethodToRun(content: String): KCallable<CommandResult>? {
      for (method in this.prefixlessMethodCache) {
         if (!Regex(method.second.triggerRegex).containsMatchIn(content)) continue
         return method.first
      }
      return null
   }

}