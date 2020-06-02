package io.github.loxygen.discord_framework.commands

/**
 * コマンドの実行結果を示す列挙体。
 */
enum class CommandResult {
   /**
    * メインコマンドが違う
    */
   UNKNOWN_MAIN_COMMAND,

   /**
    * そんなサブコマンドはない
    */
   UNKNOWN_SUB_COMMAND,

   /**
    * サブコマンドはあったけど引数の形式が受け付けられない
    */
   INVALID_ARGUMENTS,

   /**
    * 特に何もなくコマンドが成功
    */
   SUCCESS,

   /**
    * 処理に失敗した
    */
   FAILED
}
