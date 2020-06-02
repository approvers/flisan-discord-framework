package io.github.loxygen.discord_framework.commands.annotations

import io.github.loxygen.discord_framework.commands.CommandResult
import net.dv8tion.jda.api.events.message.MessageReceivedEvent


/**
 * Prefixを付けて実行するコマンドのメソッドにつけるアノテーション。
 * メソッドは以下の形式にそっている必要がある:
 * method(args: List<String>, event: [MessageReceivedEvent]) : [CommandResult]
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class SubCommand(
    /**
     * サブコマンドの識別文字列。
     */
    val identify: String,
    /**
     * サブコマンドの名前。
     */
    val name: String,
    /**
     * サブコメントの説明。
     */
    val description: String
)
