import dev.approvers.jubilant.commands.CommandResult
import dev.approvers.jubilant.commands.abc.EventListener
import dev.approvers.jubilant.commands.annotations.EventReceiver
import dev.approvers.jubilant.commands.event.EventType
import net.dv8tion.jda.api.events.ReadyEvent

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
