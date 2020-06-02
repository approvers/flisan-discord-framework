package io.github.loxygen.discord_framework.commands.annotations

import io.github.loxygen.discord_framework.commands.event.EventType

annotation class EventReceiver(
   val eventType: EventType
)