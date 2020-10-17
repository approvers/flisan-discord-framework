package dev.approvers.jubilant.commands

import dev.approvers.jubilant.type.sendable.Sendable
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

interface ResultMessageFormatter {

    /**
     * コマンドの実行に成功したときのメッセージを整形する。
     * @param command コマンド名。
     * @param subCommand サブコマンド名。
     * @param event 解析しているイベント。
     */
    fun onCommandSucceed(command: String, subCommand: String, event: MessageReceivedEvent): Sendable

    /**
     * コマンドの実行に失敗した時のメッセージを整形する。
     * @param command コマンド名。
     * @param subCommand サブコマンド名。
     * @param event 解析しているイベント。
     */
    fun onCommandFailed(command: String, subCommand: String, event: MessageReceivedEvent): Sendable

    /**
     * コマンドの実行中に例外が発生した時のメッセージを整形する。
     * @param command コマンド名。
     * @param subCommand サブコマンド名。
     * @param event 解析しているイベント。
     * @param exception 発生した例外。
     */
    fun onExceptionThrown(command: String, subCommand: String, event: MessageReceivedEvent, exception: Exception): Sendable

    /**
     * コマンドに不正な引数が渡された時のメッセージを整形する。
     * @param command コマンド名。
     * @param subCommand サブコマンド名。
     * @param event 解析しているイベント。
     */
    fun onInvalidArgumentsPassed(command: String, subCommand: String, event: MessageReceivedEvent): Sendable

    /**
     * 登録されていないコマンドが実行されようとした時のメッセージを整形する。
     * @param command 実行されようとしたコマンド名。
     * @param event 解析しているイベント。
     */
    fun onUnknownCommandPassed(command: String, event: MessageReceivedEvent): Sendable

    /**
     * コマンドは存在するが、存在しないサブコマンドが実行されようとした時のメッセージを整形する。
     * @param command コマンド名。
     * @param subCommand 実行されようとしたサブコマンド名。
     * @param event 解析しているイベント。
     */
    fun onUnknownSubCommandPassed(command: String, subCommand: String, event: MessageReceivedEvent): Sendable

}