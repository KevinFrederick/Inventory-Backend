package api.socket

import io.ktor.websocket.DefaultWebSocketSession
import java.util.concurrent.ConcurrentHashMap

object SyncSocketManager {
    val sessions = ConcurrentHashMap<String, MutableSet<DefaultWebSocketSession>>()
}