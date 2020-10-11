package test

import dev.approvers.jubilant.commands.CommandResult
import dev.approvers.jubilant.commands.abc.EventListener
import dev.approvers.jubilant.commands.abc.PrefixnessCommand
import dev.approvers.jubilant.commands.annotations.Argument
import dev.approvers.jubilant.commands.annotations.EventReceiver
import dev.approvers.jubilant.commands.annotations.SubCommand
import dev.approvers.jubilant.commands.event.EventType
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.lang.RuntimeException

// 各種機能のテストを行うためのコマンドです。
class Tester : PrefixnessCommand(
    identify = "test",
    name = "テスト",
    description = "さっきのより実用性を高くしました"
) {

    @SubCommand(identify = "hi", name = "ごあいさつ", description = "ちゃんと挨拶してほら！")
    fun greeting(args: List<String>, event: MessageReceivedEvent): CommandResult {
        event.channel.sendMessage("Hennlo world!").queue()
        return CommandResult.SUCCESS
    }

    @SubCommand(identify = "args", name = "引数テスト", description = "引数周り、ちゃんと動いてほしいな")
    @Argument(count = 3, help = ["引数1", "引数2", "引数3"])
    fun argument(args: List<String>, event: MessageReceivedEvent): CommandResult {
        sendArgsInfo(args, event)
        return CommandResult.SUCCESS
    }

    @SubCommand(identify = "args", name = "省略可能な引数がある場合のテスト", description = "引数周り、ちゃんと動いてほしいな")
    @Argument(count = 3, denyLess = false, help = ["引数1", "引数2", "引数3"])
    fun lessArgs(args: List<String>, event: MessageReceivedEvent): CommandResult {
        sendArgsInfo(args, event)
        return CommandResult.SUCCESS
    }

    @SubCommand(identify = "args", name = "いっぱい列挙できる引数が有る場合のテスト", description = "引数周り、ちゃんと動いてほしいな")
    @Argument(count = 3, denyMore = false, help = ["引数1", "引数2", "引数3"])
    fun moreArgs(args: List<String>, event: MessageReceivedEvent): CommandResult {
        sendArgsInfo(args, event)
        return CommandResult.SUCCESS
    }

    @SubCommand(identify = "fail", name = "必ず失敗するコマンド", description = "")
    fun fail(args: List<String>, event: MessageReceivedEvent): CommandResult {
        throw RuntimeException()
    }

    private fun sendArgsInfo(args: List<String>, event: MessageReceivedEvent) {
        event.channel.sendMessage(
            "以下の引数が渡されました:${
                args.mapIndexed { index: Int, s: String ->
                    "${index + 1} $s"
                }.joinToString("\n")
            }"
        )
    }

}

// メッセージが送信されたとき以外にイベントを取得するには、EventListenerを継承したクラスを使用します。
// 注意: この機能は実験的であり、破壊的変更が見込まれます。
class EnterEventListener : EventListener() {

    // イベントを処理するメソッドには、EventListenerアノテーションを追加します。
    @EventReceiver(EventType.BOT_READY)
    //
    // 引数の型はEventType.Hoge.classTypeと合致するようにしてください。
    // また、CommandResultを返す必要があります。
    fun eventReady(event: ReadyEvent): CommandResult {

        // Botの起動完了時に、標準出力に「Bot started!!」と出力します。
        print("Bot started!!")

        // コマンドの実行に成功したので、CommandResult.SUCCESS を返します。
        return CommandResult.SUCCESS
    }

}
