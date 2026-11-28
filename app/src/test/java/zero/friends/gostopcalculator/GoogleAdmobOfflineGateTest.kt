package zero.friends.gostopcalculator

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GoogleAdmobOfflineGateTest {

    @Test
    fun `DEBUG 빌드이고 디버그 광고 토글이 꺼져 있으면 오프라인이어도 게이트를 띄우지 않는다`() {
        assertFalse(
            shouldShowOfflineGate(
                isDebugBuild = true,
                isDebugAdEnabled = false,
                isInCooldown = false,
                hasReadyAd = false,
                isNetworkConnected = false,
            )
        )
    }

    @Test
    fun `DEBUG 빌드라도 디버그 광고 토글이 켜져 있고 오프라인이며 캐시 광고가 없으면 게이트를 띄운다`() {
        assertTrue(
            shouldShowOfflineGate(
                isDebugBuild = true,
                isDebugAdEnabled = true,
                isInCooldown = false,
                hasReadyAd = false,
                isNetworkConnected = false,
            )
        )
    }

    @Test
    fun `DEBUG 빌드이고 디버그 광고 토글이 켜져 있어도 쿨다운 중이면 게이트를 띄우지 않는다`() {
        assertFalse(
            shouldShowOfflineGate(
                isDebugBuild = true,
                isDebugAdEnabled = true,
                isInCooldown = true,
                hasReadyAd = false,
                isNetworkConnected = false,
            )
        )
    }

    @Test
    fun `DEBUG 빌드이고 디버그 광고 토글이 켜져 있어도 캐시 광고가 있으면 게이트를 띄우지 않는다`() {
        assertFalse(
            shouldShowOfflineGate(
                isDebugBuild = true,
                isDebugAdEnabled = true,
                isInCooldown = false,
                hasReadyAd = true,
                isNetworkConnected = false,
            )
        )
    }

    @Test
    fun `DEBUG 빌드이고 디버그 광고 토글이 켜져 있어도 온라인이면 게이트를 띄우지 않는다`() {
        assertFalse(
            shouldShowOfflineGate(
                isDebugBuild = true,
                isDebugAdEnabled = true,
                isInCooldown = false,
                hasReadyAd = false,
                isNetworkConnected = true,
            )
        )
    }

    @Test
    fun `RELEASE 빌드에서 오프라인이고 캐시 광고와 쿨다운이 모두 없으면 게이트를 띄운다`() {
        assertTrue(
            shouldShowOfflineGate(
                isDebugBuild = false,
                isDebugAdEnabled = false,
                isInCooldown = false,
                hasReadyAd = false,
                isNetworkConnected = false,
            )
        )
    }

    @Test
    fun `RELEASE 빌드에서 쿨다운 중이면 오프라인이어도 게이트를 띄우지 않는다`() {
        assertFalse(
            shouldShowOfflineGate(
                isDebugBuild = false,
                isDebugAdEnabled = false,
                isInCooldown = true,
                hasReadyAd = false,
                isNetworkConnected = false,
            )
        )
    }

    @Test
    fun `RELEASE 빌드에서 이미 로드된 광고가 있으면 오프라인이어도 게이트를 띄우지 않는다`() {
        assertFalse(
            shouldShowOfflineGate(
                isDebugBuild = false,
                isDebugAdEnabled = false,
                isInCooldown = false,
                hasReadyAd = true,
                isNetworkConnected = false,
            )
        )
    }

    @Test
    fun `RELEASE 빌드에서 네트워크가 연결되어 있으면 캐시 광고 유무와 무관하게 게이트를 띄우지 않는다`() {
        assertFalse(
            shouldShowOfflineGate(
                isDebugBuild = false,
                isDebugAdEnabled = false,
                isInCooldown = false,
                hasReadyAd = false,
                isNetworkConnected = true,
            )
        )
    }

    @Test
    fun `단조 증가 쿨다운 윈도우는 미기록 시 false이고 10초 미만에서만 true이며 10초 이상이나 음수 경과에서는 false다`() {
        val cooldown = 10_000L
        assertFalse(isCooldownWindowActive(lastMarkedElapsedMs = 0L, nowElapsedMs = 5_000L, cooldownMillis = cooldown))
        assertTrue(isCooldownWindowActive(lastMarkedElapsedMs = 1_000L, nowElapsedMs = 1_000L, cooldownMillis = cooldown))
        assertTrue(isCooldownWindowActive(lastMarkedElapsedMs = 1_000L, nowElapsedMs = 10_999L, cooldownMillis = cooldown))
        assertFalse(isCooldownWindowActive(lastMarkedElapsedMs = 1_000L, nowElapsedMs = 11_000L, cooldownMillis = cooldown))
        assertFalse(isCooldownWindowActive(lastMarkedElapsedMs = 5_000L, nowElapsedMs = 4_000L, cooldownMillis = cooldown))
    }
}
