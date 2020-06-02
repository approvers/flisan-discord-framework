package io.github.loxygen.discord_framework.commands.abc

import io.github.loxygen.discord_framework.commands.event.EventType
import io.github.loxygen.discord_framework.commands.CommandResult
import io.github.loxygen.discord_framework.commands.annotations.EventReceiver
import io.github.loxygen.discord_framework.commands.event.EventInfo
import io.github.loxygen.discord_framework.contentEquals
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import kotlin.reflect.KCallable
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.full.findAnnotation

abstract class EventCommand {


   /**
    * Prefixfulコマンドを実装するメソッドとそれについてるアノテーションのキャッシュ
    */
   private val eventMethodCache: List<Pair<KCallable<CommandResult>, EventType>>

   init {
      // メソッドは変わらないし毎回リフレクションゴリゴリするの嫌なので(個人の感想)
      // ここでメソッドとアノテーションをキャッシュしてしまいます
      val eventMethod: MutableList<Pair<KCallable<CommandResult>, EventType>> = mutableListOf()

      for (callable in this::class.members) {
         if (callable.returnType != CommandResult::class.createType()) continue
         val commandAnt = callable.findAnnotation<EventReceiver>() ?: continue
         if (!(callable.parameters.map { it.type } contentEquals listOf(this::class.createType(), commandAnt.eventType.classType))) continue

         @Suppress("UNCHECKED_CAST") // ゆるしてください
         eventMethod.add(Pair(callable as KCallable<CommandResult>, commandAnt.eventType))
      }

      eventMethodCache = eventMethod.toList()
   }

   fun isApplicable(eventType: EventType): Boolean {
      return eventMethodCache.find {
         it.second == eventType
      } != null
   }

   fun executeCommand(eventInfo: EventInfo) : List<CommandResult> {

      return eventMethodCache
         .filter { it.second == eventInfo.eventType }
         .map { it.first.call(this, eventInfo.event) }

   }

}