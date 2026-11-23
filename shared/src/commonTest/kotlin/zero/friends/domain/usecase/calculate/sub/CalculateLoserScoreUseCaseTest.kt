package zero.friends.domain.usecase.calculate.sub

import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import zero.friends.domain.mock.MockGamerRepository
import zero.friends.domain.mock.MockRuleRepository
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.LoserOption
import zero.friends.domain.model.Rule
import zero.friends.domain.usecase.calculate.CalculateLoserScoreUseCase
import zero.friends.domain.util.Const

class CalculateLoserScoreUseCaseTest {

    private lateinit var useCase: CalculateLoserScoreUseCase
    private lateinit var ruleRepository: MockRuleRepository
    private lateinit var gamerRepository: MockGamerRepository

    @BeforeTest
    fun setup() {
        ruleRepository = MockRuleRepository()
        gamerRepository = MockGamerRepository()
        useCase = CalculateLoserScoreUseCase(ruleRepository, gamerRepository)
    }

    @Test
    fun `기본 패자 피박 2배 계산`() = runTest {
        val gameId = 1L
        val roundId = 1L
        val winner = Gamer(id = 1, roundId = roundId, score = 5)
        val loser = Gamer(id = 2, roundId = roundId, loserOption = listOf(LoserOption.PeaBak))

        gamerRepository.setRoundGamers(roundId, listOf(winner, loser))
        ruleRepository.setRules(gameId, listOf(Rule(name = Const.Rule.Score, score = 100)))

        val result = useCase(gameId, roundId, winner)

        // 5점 * 100원 * 2(피박) = 1,000원 지불/수령
        assertEquals(1000, result.accounts[1])
        assertEquals(-1000, result.accounts[2])
    }

    @Test
    fun `3인 이상 고박 발생 시 고박자는 본인 2배 중복 가산 없이 전체 패자 금액만 독박 부담`() = runTest {
        val gameId = 1L
        val roundId = 1L
        val winner = Gamer(id = 1, roundId = roundId, score = 3)
        val normalLoser = Gamer(id = 2, roundId = roundId, loserOption = listOf(LoserOption.PeaBak))
        val goBakLoser = Gamer(id = 3, roundId = roundId, loserOption = listOf(LoserOption.GoBak))

        gamerRepository.setRoundGamers(roundId, listOf(winner, normalLoser, goBakLoser))
        ruleRepository.setRules(gameId, listOf(Rule(name = Const.Rule.Score, score = 100)))

        val result = useCase(gameId, roundId, winner)

        // 나무위키 3인 고스톱 룰: normalLoser(피박=600원) + goBakLoser(본인 무박=300원) = 900원 독박 대납
        assertEquals(null, result.accounts[2])
        assertEquals(600, result.individualLosses[2])
        assertEquals(300, result.individualLosses[3])
        assertEquals(-900, result.accounts[3])
        assertEquals(900, result.accounts[1])
    }

    @Test
    fun `2인 맞고 고박은 2배 배수로 적용되고 3인 이상 고스톱 고박은 타 패자 대납으로만 적용된다`() = runTest {
        val gameId = 1L
        ruleRepository.setRules(gameId, listOf(Rule(name = Const.Rule.Score, score = 100)))
        val baks = listOf(LoserOption.MongBak, LoserOption.LightBak, LoserOption.GoBak)

        // 1) 2인 맞고: 승자 2점 3고 = (2+3)*2 = 10점(1,000원) * 2^3(멍박·광박·고박) = 8,000원
        val round2P = 10L
        val winner2P = Gamer(id = 1, roundId = round2P, score = 2, go = 3)
        val loser2P = Gamer(id = 2, roundId = round2P, loserOption = baks)
        gamerRepository.setRoundGamers(round2P, listOf(winner2P, loser2P))

        val res2P = useCase(gameId, round2P, winner2P)
        assertEquals(8000, res2P.accounts[1])
        assertEquals(-8000, res2P.accounts[2])

        // 2) 3인 고스톱: 승자 2점 3고(1,000원), 패자A(멍박·광박·고박 -> 본인 4,000원), 패자B(무박 -> 1,000원) => 총 5,000원 독박
        val round3P = 11L
        val winner3P = Gamer(id = 1, roundId = round3P, score = 2, go = 3)
        val goBakLoser3P = Gamer(id = 2, roundId = round3P, loserOption = baks)
        val otherLoser3P = Gamer(id = 3, roundId = round3P)
        gamerRepository.setRoundGamers(round3P, listOf(winner3P, goBakLoser3P, otherLoser3P))

        val res3P = useCase(gameId, round3P, winner3P)
        assertEquals(5000, res3P.accounts[1])
        assertEquals(-5000, res3P.accounts[2])
        assertEquals(null, res3P.accounts[3])
        assertEquals(4000, res3P.individualLosses[2])
        assertEquals(1000, res3P.individualLosses[3])
    }

    @Test
    fun `극한 고점수 및 고판돈에서도 음수 오버플로우 없이 Zero-Sum이 보장된다`() = runTest {
        val gameId = 1L
        val roundId = 1L
        val winner = Gamer(id = 1, roundId = roundId, score = 8_519_680, go = 20)
        val loser1 = Gamer(id = 2, roundId = roundId, loserOption = listOf(LoserOption.PeaBak, LoserOption.LightBak))
        val loser2 = Gamer(id = 3, roundId = roundId, loserOption = listOf(LoserOption.MongBak))

        gamerRepository.setRoundGamers(roundId, listOf(winner, loser1, loser2))
        ruleRepository.setRules(gameId, listOf(Rule(name = Const.Rule.Score, score = 1_000_000)))

        val result = useCase(gameId, roundId, winner)

        assertTrue((result.accounts[1] ?: 0) > 0)
        assertTrue((result.accounts[2] ?: 0) < 0)
        assertTrue((result.accounts[3] ?: 0) < 0)
        assertEquals(0, result.accounts.values.sum())
    }
}
