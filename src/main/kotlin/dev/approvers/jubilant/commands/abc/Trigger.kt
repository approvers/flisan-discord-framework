package dev.approvers.jubilant.commands.abc

import dev.approvers.jubilant.commands.CommandInfo
import dev.approvers.jubilant.commands.CommandResult
import dev.approvers.jubilant.commands.annotations.TriggerHandler
import dev.approvers.jubilant.contentEquals
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import kotlin.reflect.KCallable
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.full.findAnnotation

/**
 * 接頭辞なしコマンドを実際に実行する機能を提供する†抽象クラス†。
 * あらゆる接頭辞なしコマンドはこれを継承してください
 */
abstract class Trigger : AbstCommand() {

   override val commandInfo: CommandInfo? = null

   /**
    * Triggerを実装するメソッドとそれについてるアノテーションのキャッシュ
    */
   private val triggerMethodCache: List<Pair<KCallable<CommandResult>, TriggerHandler>>

   init {
      // メソッドは変わらないし毎回リフレクションゴリゴリするの嫌なので(個人の感想)
      // ここでメソッドとアノテーションをキャッシュしてしまいます
      val triggerMethods: MutableList<Pair<KCallable<CommandResult>, TriggerHandler>> = mutableListOf()

      val expectedParamTypes = listOf(
         this::class.createType(),
         List::class.createType(listOf(KTypeProjection.invariant(String::class.createType()))),
         MessageReceivedEvent::class.createType()
      )

      for (callable in this::class.members) {
         if (callable.returnType != CommandResult::class.createType()) continue
         val commandAnt = callable.findAnnotation<TriggerHandler>() ?: continue
         if (!(callable.parameters.map { it.type } contentEquals expectedParamTypes)) continue

         @Suppress("UNCHECKED_CAST") // ゆるしてください
         triggerMethods.add(Pair(callable as KCallable<CommandResult>, commandAnt))
      }

      triggerMethodCache = triggerMethods.toList()
   }

   override fun isApplicable(query: String): Boolean {
      return this.triggerMethodCache.find { Regex(it.second.triggerRegex).containsMatchIn(query) } != null
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
      for (method in this.triggerMethodCache) {
         if (!Regex(method.second.triggerRegex).containsMatchIn(content)) continue
         return method.first
      }
      return null
   }

}