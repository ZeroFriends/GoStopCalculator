package zero.friends.domain.usecase.result

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import zero.friends.domain.model.Gamer
import zero.friends.domain.model.Target
import zero.friends.domain.mock.MockGamerRepository

/**
 * 결과화면과 수익현황의 내역이 동일한지 검증하는 테스트
 */
class ResultViewModelTest {

    private lateinit var gamerRepository: MockGamerRepository

    @Before
    fun setup() {
        gamerRepository = MockGamerRepository()
    }

    @Test
    fun `결과화면과 수익현황의 내역이 동일한지 검증 - 단일 라운드`() = runTest {
        // Given: 게임 1개, 라운드 1개, 3명 플레이어
        val gameId = 1L
        val roundId = 1L
        
        // 플레이어1: 승자, 7점, 패자2에게 700원 받음
        val gamer1 = Gamer(
            id = 1L,
            name = "플레이어1",
            roundId = roundId,
            gameId = gameId,
            score = 7,
            account = 700,
            winnerOption = zero.friends.domain.model.WinnerOption.Winner,
            calculate = listOf(
                Target(playerId = 2L, name = "플레이어2", account = 700)
            )
        )
        
        // 플레이어2: 패자, 700원 냄
        val gamer2 = Gamer(
            id = 2L,
            name = "플레이어2",
            roundId = roundId,
            gameId = gameId,
            score = 0,
            account = -700,
            calculate = listOf(
                Target(playerId = 1L, name = "플레이어1", account = -700)
            )
        )
        
        // 플레이어3: 패자, 0원
        val gamer3 = Gamer(
            id = 3L,
            name = "플레이어3",
            roundId = roundId,
            gameId = gameId,
            score = 0,
            account = 0,
            calculate = emptyList()
        )
        
        gamerRepository.setRoundGamers(roundId, listOf(gamer1, gamer2, gamer3))
        gamerRepository.setAllGamers(listOf(gamer1, gamer2, gamer3))
        
        // When: 수익현황 데이터 (전체 라운드 누적)
        val calculateGamers = gamerRepository.observeGamers(gameId).first()
        val calculateResult = calculateGamers
            .groupBy { it.name }
            .mapValues { (_, sameGamers) ->
                val calculate = sameGamers
                    .map { it.calculate }
                    .scan<List<Target>, MutableMap<String, Target>>(mutableMapOf()) { map, targets ->
                        targets.forEach {
                            val target = map[it.name]
                            if (target != null) target.account += it.account
                            else map[it.name] = it
                        }
                        map
                    }
                    .map { it.values.toList() }
                    .distinct()
                    .last()
                
                Gamer(
                    name = sameGamers.first().name,
                    account = sameGamers.sumOf { it.account },
                    calculate = calculate
                )
            }
            .values
            .toList()
        
        // When: 결과화면 데이터 (해당 라운드까지 누적)
        val detailGamers = gamerRepository.observeGamers(gameId).first()
            .filter { it.roundId <= roundId }
        val detailResult = detailGamers
            .groupBy { it.name }
            .mapValues { (_, sameGamers) ->
                val calculate = sameGamers
                    .map { it.calculate }
                    .scan<List<Target>, MutableMap<String, Target>>(mutableMapOf()) { map, targets ->
                        targets.forEach {
                            val target = map[it.name]
                            if (target != null) target.account += it.account
                            else map[it.name] = it
                        }
                        map
                    }
                    .map { it.values.toList() }
                    .distinct()
                    .last()
                
                Gamer(
                    name = sameGamers.first().name,
                    account = sameGamers.sumOf { it.account },
                    calculate = calculate
                )
            }
            .values
            .toList()
        
        // Then: 수익현황과 결과화면의 내역이 동일해야 함
        assertEquals(calculateResult.size, detailResult.size)
        calculateResult.forEach { calculateGamer ->
            val detailGamer = detailResult.find { it.name == calculateGamer.name }
            assertEquals("${calculateGamer.name}의 account가 다름", calculateGamer.account, detailGamer?.account)
            assertEquals("${calculateGamer.name}의 calculate 개수가 다름", calculateGamer.calculate.size, detailGamer?.calculate?.size)
            calculateGamer.calculate.forEach { calculateTarget ->
                val detailTarget = detailGamer?.calculate?.find { it.name == calculateTarget.name }
                assertEquals("${calculateGamer.name}의 ${calculateTarget.name}에 대한 account가 다름", 
                    calculateTarget.account, detailTarget?.account)
            }
        }
    }

    @Test
    fun `결과화면과 수익현황의 내역이 동일한지 검증 - 여러 라운드`() = runTest {
        // Given: 게임 1개, 라운드 2개, 3명 플레이어
        val gameId = 1L
        val roundId1 = 1L
        val roundId2 = 2L
        
        // 라운드1: 플레이어1 승자, 플레이어2에게 700원 받음
        val gamer1Round1 = Gamer(
            id = 1L,
            name = "플레이어1",
            roundId = roundId1,
            gameId = gameId,
            score = 7,
            account = 700,
            winnerOption = zero.friends.domain.model.WinnerOption.Winner,
            calculate = listOf(Target(playerId = 2L, name = "플레이어2", account = 700))
        )
        val gamer2Round1 = Gamer(
            id = 2L,
            name = "플레이어2",
            roundId = roundId1,
            gameId = gameId,
            account = -700,
            calculate = listOf(Target(playerId = 1L, name = "플레이어1", account = -700))
        )
        val gamer3Round1 = Gamer(
            id = 3L,
            name = "플레이어3",
            roundId = roundId1,
            gameId = gameId,
            account = 0,
            calculate = emptyList()
        )
        
        // 라운드2: 플레이어2 승자, 플레이어1에게 800원 받음
        val gamer1Round2 = Gamer(
            id = 4L,
            name = "플레이어1",
            roundId = roundId2,
            gameId = gameId,
            account = -800,
            calculate = listOf(Target(playerId = 5L, name = "플레이어2", account = -800))
        )
        val gamer2Round2 = Gamer(
            id = 5L,
            name = "플레이어2",
            roundId = roundId2,
            gameId = gameId,
            score = 8,
            account = 800,
            winnerOption = zero.friends.domain.model.WinnerOption.Winner,
            calculate = listOf(Target(playerId = 4L, name = "플레이어1", account = 800))
        )
        val gamer3Round2 = Gamer(
            id = 6L,
            name = "플레이어3",
            roundId = roundId2,
            gameId = gameId,
            account = 0,
            calculate = emptyList()
        )
        
        gamerRepository.setRoundGamers(roundId1, listOf(gamer1Round1, gamer2Round1, gamer3Round1))
        gamerRepository.setRoundGamers(roundId2, listOf(gamer1Round2, gamer2Round2, gamer3Round2))
        gamerRepository.setAllGamers(listOf(gamer1Round1, gamer2Round1, gamer3Round1, gamer1Round2, gamer2Round2, gamer3Round2))
        
        // When: 수익현황 데이터 (전체 라운드 누적)
        val calculateGamers = gamerRepository.observeGamers(gameId).first()
        val calculateResult = calculateGamers
            .groupBy { it.name }
            .mapValues { (_, sameGamers) ->
                val calculate = sameGamers
                    .map { it.calculate }
                    .scan<List<Target>, MutableMap<String, Target>>(mutableMapOf()) { map, targets ->
                        targets.forEach {
                            val target = map[it.name]
                            if (target != null) target.account += it.account
                            else map[it.name] = it
                        }
                        map
                    }
                    .map { it.values.toList() }
                    .distinct()
                    .last()
                
                Gamer(
                    name = sameGamers.first().name,
                    account = sameGamers.sumOf { it.account },
                    calculate = calculate
                )
            }
            .values
            .toList()
        
        // When: 결과화면 데이터 (라운드2까지 누적)
        val detailGamers = gamerRepository.observeGamers(gameId).first()
            .filter { it.roundId <= roundId2 }
        val detailResult = detailGamers
            .groupBy { it.name }
            .mapValues { (_, sameGamers) ->
                val calculate = sameGamers
                    .map { it.calculate }
                    .scan<List<Target>, MutableMap<String, Target>>(mutableMapOf()) { map, targets ->
                        targets.forEach {
                            val target = map[it.name]
                            if (target != null) target.account += it.account
                            else map[it.name] = it
                        }
                        map
                    }
                    .map { it.values.toList() }
                    .distinct()
                    .last()
                
                Gamer(
                    name = sameGamers.first().name,
                    account = sameGamers.sumOf { it.account },
                    calculate = calculate
                )
            }
            .values
            .toList()
        
        // Then: 수익현황과 결과화면의 내역이 동일해야 함
        assertEquals(calculateResult.size, detailResult.size)
        calculateResult.forEach { calculateGamer ->
            val detailGamer = detailResult.find { it.name == calculateGamer.name }
            assertEquals("${calculateGamer.name}의 account가 다름", calculateGamer.account, detailGamer?.account)
            assertEquals("${calculateGamer.name}의 calculate 개수가 다름", calculateGamer.calculate.size, detailGamer?.calculate?.size)
            calculateGamer.calculate.forEach { calculateTarget ->
                val detailTarget = detailGamer?.calculate?.find { it.name == calculateTarget.name }
                assertEquals("${calculateGamer.name}의 ${calculateTarget.name}에 대한 account가 다름", 
                    calculateTarget.account, detailTarget?.account)
            }
        }
    }
}

