package dev.approvers.jubilant.type.sendable

import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.MessageEmbed

class EmbedSendable(
    val embed: MessageEmbed
) : Sendable {
    override fun send(channel: MessageChannel) {
        channel.sendMessage(embed).queue()
    }
}
