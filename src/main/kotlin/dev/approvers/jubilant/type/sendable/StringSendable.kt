package dev.approvers.jubilant.type.sendable

import net.dv8tion.jda.api.entities.MessageChannel

class StringSendable(
    val message: String
) : Sendable {
    override fun send(channel: MessageChannel) {
        channel.sendMessage(message).queue()
    }
}