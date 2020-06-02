package io.github.loxygen.discord_framework.commands.event

import net.dv8tion.jda.api.events.Event
import kotlin.reflect.full.createType

data class EventInfo (
   val event: Event,
   val eventType: EventType
) {

   init {
      if(event::class.createType() != eventType.classType)
         throw IllegalArgumentException("Type of event does not match to required Type!")
   }

}
