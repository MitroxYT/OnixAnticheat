package me.onixdev.event.impl

import dev.onixac.api.events.api.BaseEvent
import me.onixdev.util.position.PlayerPositionMotion

class PlayerPositionUpdateEvent(val position: PlayerPositionMotion) : BaseEvent() {
}