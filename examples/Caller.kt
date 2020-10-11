package example

import dev.approvers.jubilant.commands.CommandResult
import dev.approvers.jubilant.commands.abc.PrefixlessCommand
import dev.approvers.jubilant.commands.annotations.PrefixlessSubCommand
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

// プレフィックスを持たない、任意のメッセージに反応させたい場合は、
// PrefixlessCommandを継承したクラスを作成することで
// 任意のメッセージに反応させることができます。
class Caller : PrefixlessCommand() {

    // メッセージを処理するコマンドはPrefixlessSubCommandアノテーションを
    // 付与したメソッドを作成することで作成することができます。
    // 反応させる文字列の正規表現は triggerRegex 引数で指定します。
    @PrefixlessSubCommand(triggerRegex = "おー+い[！!]")
    //
    // メソッドの引数の型は、必ずmethod(List<String>, MessageReceivedEvent)としてください。
    // また、CommandResultを返す必要があります。
    // これらの条件を満たさない場合、SubCommandアノテーションを付与していても
    // サブコマンドとして認識されません。
    //
    // IDEなどを使用している場合、使用されていないメソッドとして警告が表示されますが
    // 無視してください。
    fun reply(message: String, event: MessageReceivedEvent): CommandResult {

        // メンション用文字列を取得します。
        val mention = event.author.asMention

        // 呼びかけたユーザーにメンションするメッセージを送信します。
        event.channel.sendMessage("どうしたの、$mention？").queue()

        // コマンドが成功したので、CommandResult.SUCCESS を返します。
        return CommandResult.SUCCESS
    }

    // 複数コマンドを作成することもできます。
    // Higuchi(またはhiguchi)と送信されたら、Bold+Italicで「ichiyo」と返すコマンドです。
    // (何故樋口一葉なのかは、 https://twitter.com/UFIApprovers または
    //  https://twitter.com/search?q=%E9%99%90%E7%95%8C%E9%96%8B%E7%99%BA%E9%AF%96 を参照すると分かるかもしれません。)
    @PrefixlessSubCommand(triggerRegex = "[hH]iguchi")
    fun ichiyo(message: String, event: MessageReceivedEvent): CommandResult {

        event.channel.sendMessage("***Ichiyo***")
        return CommandResult.SUCCESS

    }


}
