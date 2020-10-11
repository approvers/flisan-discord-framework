package example

import dev.approvers.jubilant.commands.CommandResult
import dev.approvers.jubilant.commands.abc.PrefixnessCommand
import dev.approvers.jubilant.commands.annotations.Argument
import dev.approvers.jubilant.commands.annotations.SubCommand
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

// コマンドは `PrefixnessCommand` を継承したクラスを作成することで作成できます。
class Calculator : PrefixnessCommand(
    // PrefixnessCommandのコンストラクタに、コマンドの情報を入力します。
    // ここで入力した内容が、`help`サブコマンド実行時に表示されます。
    name = "PingPong Bot",
    description = "Pingコマンドが実行されたときにPongと返す。",
    identify = "calc"
) {

    // サブコマンドは@SubCommandアノテーションを付与したメソッドを作成することで作成できます。
    @SubCommand(identify = "add", name = "足し算", description = "数字を足し算します")
    //
    // 引数を取りたい場合は、Argumentアノテーションも同時に付与します。
    @Argument(count = 2, denyMore = false)
    //
    // メソッドの引数の型は、必ずmethod(List<String>, MessageReceivedEvent)としてください。
    // また、CommandResultを返す必要があります。
    // これらの条件を満たさない場合、SubCommandアノテーションを付与していても
    // サブコマンドとして認識されません。
    //
    // IDEなどを使用している場合、使用されていないメソッドとして警告が表示されますが
    // 無視してください。
    fun addCommand(args: List<String>, event: MessageReceivedEvent): CommandResult {

        // 与えられた引数を数値に変換します。変換できない文字列が含まれていた場合は、
        // 不正な引数が渡されたことを示す CommandResult.INVALID_ARGUMENTS を返します。
        val numbers = args.map { it.toIntOrNull() ?: return CommandResult.INVALID_ARGUMENTS }

        // 数字を足し算します。
        val sum = numbers.sum()

        // メッセージを送信します。
        event.channel.sendMessage("= $sum").queue()

        // コマンドの実行に成功したので、成功したことを示す CommandResult.SUCCESS を渡します。
        return CommandResult.SUCCESS
    }

    // 複数のサブコマンドを作成することも可能です。
    @SubCommand(identify = "mul", name = "掛け算", description = "数字を掛け算します")
    @Argument(count = 2, denyMore = false)
    fun subtractCommand(args: List<String>, event: MessageReceivedEvent): CommandResult {

        val num = args
            .map { it.toIntOrNull() ?: return CommandResult.INVALID_ARGUMENTS }
            .reduce {left, right -> left * right}
        event.channel.sendMessage("= $num").queue()
        return CommandResult.SUCCESS

    }

}