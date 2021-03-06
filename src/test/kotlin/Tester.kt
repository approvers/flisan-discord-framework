import dev.approvers.jubilant.client.Client
import dev.approvers.jubilant.client.ClientSettingInfo
import dev.approvers.jubilant.commands.CommandResult
import dev.approvers.jubilant.commands.abc.EventListener
import dev.approvers.jubilant.commands.abc.Command
import dev.approvers.jubilant.commands.annotations.Argument
import dev.approvers.jubilant.commands.annotations.EventReceiver
import dev.approvers.jubilant.commands.annotations.SubCommand
import dev.approvers.jubilant.commands.event.EventType
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.lang.RuntimeException

object Tester : Command(
   identify = "test",
   name = "テスト",
   description = "さっきのより実用性を高くしました"
) {

   @SubCommand(identify = "hi", name = "ごあいさつ", description = "ちゃんと挨拶してほら！")
   fun greeting(args: List<String>, event: MessageReceivedEvent) : CommandResult {
      event.channel.sendMessage("Hennlo world!").queue()
      return CommandResult.SUCCESS
   }

   @SubCommand(identify = "args", name = "引数テスト", description = "引数周り、ちゃんと動いてほしいな")
   @Argument(count = 3, help = ["引数1", "引数2", "引数3"])
   fun argument(args: List<String>, event: MessageReceivedEvent) : CommandResult {
      sendArgsInfo(args, event)
      return CommandResult.SUCCESS
   }

   @SubCommand(identify = "args", name = "省略可能な引数がある場合のテスト", description = "引数周り、ちゃんと動いてほしいな")
   @Argument(count = 3, denyLess = false, help = ["引数1", "引数2", "引数3"])
   fun lessArgs(args: List<String>, event: MessageReceivedEvent) : CommandResult {
      sendArgsInfo(args, event)
      return CommandResult.SUCCESS
   }

   @SubCommand(identify = "args", name = "いっぱい列挙できる引数が有る場合のテスト", description = "引数周り、ちゃんと動いてほしいな")
   @Argument(count = 3, denyMore = false, help = ["引数1", "引数2", "引数3"])
   fun moreArgs(args: List<String>, event: MessageReceivedEvent) : CommandResult {
      sendArgsInfo(args, event)
      return CommandResult.SUCCESS
   }

   private fun sendArgsInfo(args: List<String>, event: MessageReceivedEvent) {
      event.channel.sendMessage("以下の引数が渡されました:${
         args.mapIndexed {index: Int, s: String ->
            "${index + 1} $s"
         }.joinToString("\n")
      }")
   }

}

object EventListener : EventListener() {

   @EventReceiver(EventType.BOT_READY)
   fun eventReady(event: ReadyEvent) : CommandResult {
      event.jda.getTextChannelById(695976154779222047)?.sendMessage("Bot ready event dispatched")?.queue()
      return CommandResult.SUCCESS
   }

}

fun main() {

   val testChannelId = System.getenv("TEST_CHANNEL_ID")?.toLongOrNull()
      ?: throw RuntimeException("TEST_CHANNEL_ID is not set, or invalid.")
   val settingInfo = ClientSettingInfo(
      "Library Test Bot",
      "[t]:",
      null,
      testChannelId,
      listOf(),
      true
   )

   val client = Client(settingInfo)
   client.addMessageCommand(Tester)
   client.addEventListener(EventListener)
   client.launch()

}
