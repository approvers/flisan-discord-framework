import io.github.loxygen.discord_framework.client.Client
import io.github.loxygen.discord_framework.client.ClientSettingInfo
import io.github.loxygen.discord_framework.commands.CommandResult
import io.github.loxygen.discord_framework.commands.abc.EventListener
import io.github.loxygen.discord_framework.commands.abc.PrefixnessCommand
import io.github.loxygen.discord_framework.commands.annotations.EventReceiver
import io.github.loxygen.discord_framework.commands.annotations.SubCommand
import io.github.loxygen.discord_framework.commands.event.EventType
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

object Tester : PrefixnessCommand(
   identify = "test",
   name = "テスト",
   description = "さっきのより実用性を高くしました"
) {

   @SubCommand(identify = "hi", name = "ごあいさつ", description = "ちゃんと挨拶してほら！")
   fun greeting(args: List<String>, event: MessageReceivedEvent) : CommandResult {
      event.channel.sendMessage("Hennlo world!").queue()
      return CommandResult.SUCCESS
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

   val settingInfo = ClientSettingInfo(
      "Library Test Bot",
      "[t]:",
      null,
      695976154779222047,
      listOf(),
      true
   )

   val client = Client(settingInfo)
   client.addMessageCommand(Tester)
   client.addEventListener(EventListener)
   client.launch()

}