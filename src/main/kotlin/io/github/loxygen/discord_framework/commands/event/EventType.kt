package io.github.loxygen.discord_framework.commands.event

import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import kotlin.reflect.KType
import kotlin.reflect.full.createType

enum class EventType(val classType: KType) {

   BOT_READY(ReadyEvent::class.createType()),

}