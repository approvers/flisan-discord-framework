package dev.approvers.jubilant.commands.annotations

import dev.approvers.jubilant.commands.CommandResult
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

/**
 * Prefixを付けすに実行するコマンドのメソッドにつけるアノテーション。
 * メソッドは以下の形式にそっている必要がある:
 * method(args: List<String>, event: [MessageReceivedEvent]) : [CommandResult]
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class TriggerHandler(
   /**
    * この正規表現にメッセージがマッチすると、コマンドが発火される。
    */
   val triggerRegex: String
)