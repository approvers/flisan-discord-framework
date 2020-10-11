# Jubilant

JDAの簡単なラッパーです。<br>
簡単かつ柔軟にコマンドを作成し、簡単にDiscord Botを作成することができるようになります。

**実験段階にあるリポジトリです！**
**破壊的変更が頻繁に起こる可能性があります。**

## For non-japanese users
Unfortunately, this repository and package are for Japanese users.<br />
I'm currently working for internationalization of this repo and package.

## 依存関係に追加する
JCenterで利用可能です。

[ ![Download](https://api.bintray.com/packages/approvers/Jubilant/dev.approvers.jubilant/images/download.svg) ](https://bintray.com/approvers/Jubilant/dev.approvers.jubilant/_latestVersion)

### Maven
```xml
<dependency>
  <groupId>dev.approvers</groupId>
  <artifactId>jubilant</artifactId>
  <version>0.0.0</version>
  <type>pom</type>
</dependency>
```

### Gradle
```groovy
implementation 'dev.approvers:jubilant:0.0.0'
```

### Ivy
```xml
<dependency org='dev.approvers' name='jubilant' rev='0.0.0'>
  <artifact name='jubilant' ext='pom' ></artifact>
</dependency>
```

## 使用方法
### コマンドを作成する
Jubilantでは、`PrefixnessCommand`または`PrefixlessCommand`を継承してコマンドを作成します。

#### `PrefixnessCommand`
`PrefixnessCommand`を継承したクラスを作成すると、
プレフィックス付きのメッセージに反応するコマンドを作成できます。

[サンプル](https://github.com/approvers/jubilant/tree/dev/examples/Calculator.kt)

#### `PrefixlessCommand`
`PrefixlessComannd`を継承したクラスを作成すると、
指定した正規表現に反応するコマンドを作成できます。

[サンプル](https://github.com/approvers/jubilant/tree/dev/examples/Caller.kt)

### (実験的) メッセージ送信時以外のイベントを取得する
**この機能は実験的です。**<br>
`EventListener`を継承したクラスを作成すると、
指定したイベントに反応するイベントリスナーを作成できます。

[サンプル](https://github.com/approvers/jubilant/tree/dev/examples/EnterEventListener.kt)

### Botを実行する
[サンプル](https://github.com/approvers/jubilant/tree/dev/examples/Main.kt)
#### Botの設定を作成する
`ClientSettingInfo`を生成し、クライアントに情報を与える必要があります。
```kotlin
val settingInfo = ClientSettingInfo(
  // Botの名前。
  botName = "Random bot",

  // プレフィックス。詳細はREADMEの「コマンドの書式」を確認してください。
  prefix = "//",

  // トークン。
  // トークンにnullが渡された場合は、DISCORD_TOKEN環境変数を利用しようとします。
  // DISCORD_TOKEN環境変数も存在しない場合は、NullPointerExceptionをスローします。
  token = null,

  // ログ用チャンネルのID。
  loggingChannelId = 1234567890123456,

  // Botフィルターから除外するIDのリスト。詳細はREADMEの「動作仕様」を確認してください。
  botIdsWhiteList = listOf(),

  // ログ用チャンネルのみで動作するか。
  // falseの場合は、所属するサーバー上の全てのチャンネルで反応します。
  // trueの場合は、loggingChannelId で指定されたチャンネル上のみで反応します。
  reactOnlyLoggingChanel = true
)
```
Botの設定を作成した後は、`settingInfo`を`Client`のコンストラクタの引数に渡し、クライアントを生成します。

#### クライアントにコマンドを登録する
クライアントを作成した後は、`addMessageCommand(command)`を使用してクライアントにコマンドを登録します。

### クライアントを実行する
`launch()`を実行し、クライアントを実行します。これで、Discord上でBotが動作し、コマンドが叩けるようになります。

## 実行仕様について

### 動作

#### コマンドの書式
```
<settingInfo.prefix><command> <subcommand> <args[0]> <args[1]> ... <args[n]>
```

#### 「コマンド」「サブコマンド」
Jubilantでは、2階層に分けてコマンドを指定します。<br>
より大きな階層が「コマンド」、小さな階層が「サブコマンド」です。

#### 反応するユーザ
全てのユーザに反応しますが、**他のBotが送信したメッセージには反応しません**。<br>
ただし、特別な事由によって、特定のBotのメッセージには反応させたい場合は、`ClientSettingInfo#botIdsWhiteList`に
該当するBotのユーザIDを追加することで反応させることができます。

#### Jubilantが送信するメッセージについて
Jubilantのクライアントは、以下の条件に合致した時、以下のメッセージを送信します。

|条件|メッセージ|場所|
|---|---------|---|
|Botが起動した時|***†`<settingInfo.botName> READY`†***<br>プレフィックスは`<settingInfo.prefix`です|`settingInfo.loggingChannelId`|
|不明なコマンドが実行されようとした時|それ is 何|コマンドを実行しようとしたチャンネル|
|コマンドは存在するが、サブコマンドが不明な場合|そのサブコマンド is 何|コマンドを実行しようとしたチャンネル|
|コマンドもサブコマンドも存在するが、引数が不正な場合|引数がおかしいみたいです|コマンドを実行しようとしたチャンネル|
|コマンドの実行中に例外が発生した時|ﾐ゜(`<例外クラス名>`)<br>`<例外の説明>`だそうです|コマンドを実行したチャンネル|
