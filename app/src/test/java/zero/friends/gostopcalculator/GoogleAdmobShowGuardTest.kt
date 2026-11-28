package zero.friends.gostopcalculator

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GoogleAdmobShowGuardTest {

    @Test
    fun `광고 표시 중이 아니면 세션 복구가 필요하지 않다`() {
        assertFalse(
            shouldRecoverStaleAdSession(
                isShowingAd = false,
                trackedHostAlive = false,
                sameHostAsCaller = false,
            )
        )
    }

    @Test
    fun `광고 표시 중이고 동일 호스트 Activity가 살아 있으면 중복 호출을 차단한다`() {
        assertFalse(
            shouldRecoverStaleAdSession(
                isShowingAd = true,
                trackedHostAlive = true,
                sameHostAsCaller = true,
            )
        )
    }

    @Test
    fun `광고 표시 중 호스트 Activity가 파괴되었으면 스턱 세션을 복구한다`() {
        assertTrue(
            shouldRecoverStaleAdSession(
                isShowingAd = true,
                trackedHostAlive = false,
                sameHostAsCaller = true,
            )
        )
    }

    @Test
    fun `광고 표시 중 호출한 Activity 인스턴스가 다르면 스턱 세션을 복구한다`() {
        assertTrue(
            shouldRecoverStaleAdSession(
                isShowingAd = true,
                trackedHostAlive = true,
                sameHostAsCaller = false,
            )
        )
    }

    @Test
    fun `Activity가 종료 중이거나 파괴되었거나 백그라운드이면 광고를 띄울 수 없다`() {
        assertTrue(
            isActivityEligibleForAd(
                isFinishing = false,
                isDestroyed = false,
                isAtLeastResumed = true,
            )
        )
        assertFalse(
            isActivityEligibleForAd(
                isFinishing = true,
                isDestroyed = false,
                isAtLeastResumed = true,
            )
        )
        assertFalse(
            isActivityEligibleForAd(
                isFinishing = false,
                isDestroyed = true,
                isAtLeastResumed = true,
            )
        )
        assertFalse(
            isActivityEligibleForAd(
                isFinishing = false,
                isDestroyed = false,
                isAtLeastResumed = false,
            )
        )
    }
}
