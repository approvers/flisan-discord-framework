package io.github.loxygen.discord_framework.commands

/**
 * コマンドの情報を格納するデータクラス。
 */
data class CommandInfo(
   /**
    * コマンドの識別子。
    */
   val identify: String,
   /**
    * コマンドの名前。
    */
   val name: String,
   /**
    * コマンドの説明。
    */
   val description: String
) {
   init {
      // コマンドの識別子が空文字だったらキレる
      if(identify == "") throw IllegalArgumentException("†キレた†")
   }
}