package zero.friends.domain.usecase.calculate

import zero.friends.domain.model.RoundTrace
import javax.inject.Inject

/**
 * 저장된 스냅샷 없이 현재 데이터/규칙으로 라운드 트레이스를 생성한다.
 */
class BuildRoundTraceUseCase @Inject constructor(
    private val roundTraceBuilder: RoundTraceBuilder
) {
    suspend operator fun invoke(gameId: Long, roundId: Long): RoundTrace? {
        return roundTraceBuilder.build(gameId = gameId, roundId = roundId)
    }
}
