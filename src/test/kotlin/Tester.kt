import io.github.loxygen.discord_framework.client.Client
import io.github.loxygen.discord_framework.client.ClientSettingInfo
import io.github.loxygen.discord_framework.commands.CommandResult
import io.github.loxygen.discord_framework.commands.abc.PrefixnessCommand
import io.github.loxygen.discord_framework.commands.annotations.SubCommand
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

object EventHandler

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
   client.commandManager.addCommand(Tester)
   client.launch()

}