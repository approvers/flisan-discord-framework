import dev.approvers.jubilant.client.Client
import dev.approvers.jubilant.client.ClientSettingInfo
import java.lang.RuntimeException


fun main() {

    // Botがログを残すチャンネルを取得します。
    // このプログラムでは環境変数から読み込んでいますが、ハードコーディングでも構いません。
    val testChannelId = System.getenv("TEST_CHANNEL_ID")?.toLongOrNull()
        ?: throw RuntimeException("TEST_CHANNEL_ID is not set, or invalid.")

    // ClientSettingInfoを作成します。
    val settingInfo = ClientSettingInfo(
        // Botの名前。
        botName = "Jubliant Tester",

        // プレフィックス。詳細はREADMEの「コマンドの書式」を確認してください。
        prefix = "[t]:",

        // トークン。
        // トークンにnullが渡された場合は、DISCORD_TOKEN環境変数を利用しようとします。
        // DISCORD_TOKEN環境変数も存在しない場合は、MissingTokenExceptionをスローします。
        token = null,

        // ログ用チャンネルのID。
        loggingChannelId = testChannelId,

        // Botフィルターから除外するIDのリスト。詳細はREADMEの「動作仕様」を確認してください。
        botIdsWhiteList = listOf(),

        // ログ用チャンネルのみで動作するか。
        // falseの場合は、所属するサーバー上の全てのチャンネルで反応します。
        // trueの場合は、loggingChannelId で指定されたチャンネル上のみで反応します。
        reactOnlyLoggingChanel = true
    )

    // 作成したClientSettingInfoからクライアントを生成します。
    val client = Client(settingInfo)

    // クライアントにコマンドを登録します。
    client.addMessageCommand(Calculator())        // https://github.com/approvers/jubilant/tree/dev/src/test/kotlin/example/Calculator.kt
    client.addMessageCommand(Caller())            // https://github.com/approvers/jubilant/tree/dev/src/test/kotlin/example/Caller.kt

    // クライアントにイベントリスナーを登録します。
    client.addEventListener(EnterEventListener()) // https://github.com/approvers/jubilant/tree/dev/src/test/kotlin/test/Tester.kt

    // クライアントを実行します。
    // Botの実行が開始されると、別プロセスにフォークされます。
    client.launch()

    // Botが動作中は、main関数を離脱してもプログラムの実行が停止しません。
}
