package com.qinoteapp.qinoteapp.notification

enum class NotificationStrategy {
    SUPER_ISLAND_DIRECT,
    SUPER_ISLAND_SHIZUKU,
    LIVE_UPDATES,
    ONGOING_NOTIFICATION
}

class NotificationStrategyResolver(
    private val capabilityDetector: DeviceCapabilityDetector,
    private val superIslandEnabled: Boolean = true
) {
    private var cachedStrategy: NotificationStrategy? = null

    fun resolve(): NotificationStrategy {
        cachedStrategy?.let { return it }

        val strategy = resolveInternal()
        cachedStrategy = strategy
        return strategy
    }

    fun invalidateCache() {
        cachedStrategy = null
    }

    private fun resolveInternal(): NotificationStrategy {
        if (capabilityDetector.isHyperOS3Plus() && capabilityDetector.isIslandSupported()) {
            if (!superIslandEnabled) {
                return if (capabilityDetector.isAndroid16Plus()) {
                    NotificationStrategy.LIVE_UPDATES
                } else {
                    NotificationStrategy.ONGOING_NOTIFICATION
                }
            }

            if (capabilityDetector.hasFocusPermission()) {
                return NotificationStrategy.SUPER_ISLAND_DIRECT
            }

            if (capabilityDetector.isShizukuReady()) {
                return NotificationStrategy.SUPER_ISLAND_SHIZUKU
            }

            return if (capabilityDetector.isAndroid16Plus()) {
                NotificationStrategy.LIVE_UPDATES
            } else {
                NotificationStrategy.ONGOING_NOTIFICATION
            }
        }

        return if (capabilityDetector.isAndroid16Plus()) {
            NotificationStrategy.LIVE_UPDATES
        } else {
            NotificationStrategy.ONGOING_NOTIFICATION
        }
    }
}
