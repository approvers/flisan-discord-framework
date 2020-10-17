package dev.approvers.jubilant.type.sendable

import net.dv8tion.jda.api.entities.MessageChannel

interface Sendable {
    fun send(channel: MessageChannel)
}