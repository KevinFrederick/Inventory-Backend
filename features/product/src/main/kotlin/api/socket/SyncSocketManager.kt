package api.socket

import io.ktor.websocket.DefaultWebSocketSession
import java.util.Collections

object SyncSocketManager {
    val collections = Collections.synchronizedSet(LinkedHashSet<DefaultWebSocketSession>())
}