package dev.approvers.jubilant.commands.annotations

import dev.approvers.jubilant.commands.event.EventType

annotation class EventReceiver(
   val eventType: EventType
)