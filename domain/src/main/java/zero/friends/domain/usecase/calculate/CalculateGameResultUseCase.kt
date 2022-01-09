package zero.friends.domain.usecase.calculate

import zero.friends.domain.model.Gamer
import zero.friends.domain.model.LoserOption
import zero.friends.domain.model.Rule
import zero.friends.domain.model.ScoreOption
import zero.friends.domain.repository.GameRepository
import zero.friends.domain.repository.RuleRepository
import zero.friends.domain.usecase.gamer.GetRoundGamerUseCase
import zero.friends.domain.util.Const
import javax.inject.Inject

class CalculateGameResultUseCase @Inject constructor(
    private val gameRepository: GameRepository,
    private val ruleRepository: RuleRepository,
    private val roundGamerUseCase: GetRoundGamerUseCase,
    private val updateAccountUseCase: UpdateAccountUseCase
) {
    private lateinit var rules: List<Rule>
    private lateinit var roundGamers: List<Gamer>

    suspend operator fun invoke(seller: Gamer?, winner: Gamer?) {
        rules = ruleRepository.getRules(requireNotNull(gameRepository.getCurrentGameId()))
        roundGamers = roundGamerUseCase()

        //1 광팜 계산
        if (seller != null) calculateSellAccount(seller)

        //2 패자 계산
        if (winner != null) calculateLoserAccount(seller, winner)

        //3 점수옵션 계산
        calculateScoreAccount(seller)
    }

    private suspend fun calculateSellAccount(seller: Gamer) {
        //광팔기
        val sellScore = rules.firstOrNull { it.name == Const.Rule.Sell }?.score ?: 0
        val gamers = roundGamers - seller
        val account = sellScore * seller.score
        gamers.forEach {
            updateAccountUseCase(seller, it, account)
            updateAccountUseCase(it, seller, -account)
        }
    }

    private suspend fun calculateLoserAccount(seller: Gamer?, winner: Gamer) {
        //점당
        val gameScore = rules.first { it.name == Const.Rule.Score }.score
        val winnerScore = winner.score

        val losers = if (seller != null) roundGamers - winner - seller else roundGamers - winner
        val goBakGamer = losers.firstOrNull { it.loserOption.contains(LoserOption.GoBak) }

        if (goBakGamer != null) {
            //고박 플레이어는 나머지 플레이어들의 돈을 다 내준다....
            val remainLoser = losers - goBakGamer
            remainLoser.forEach { loser ->
                val account = getLoserAccount(loser, winnerScore, gameScore)
                updateAccountUseCase(goBakGamer, winner, -1 * account)
                updateAccountUseCase(winner, goBakGamer, account)
            }
            val account = getLoserAccount(goBakGamer, winnerScore, gameScore)
            updateAccountUseCase(goBakGamer, winner, -1 * account)
            updateAccountUseCase(winner, goBakGamer, account)
        } else {
            losers.forEach { loser ->
                val account = getLoserAccount(loser, winnerScore, gameScore)
                updateAccountUseCase(loser, winner, account * -1)
                updateAccountUseCase(winner, loser, account)
            }
        }
    }

    private fun getLoserAccount(loser: Gamer, winnerScore: Int, gameScore: Int): Int {
        return if (loser.loserOption.isNotEmpty()) {
            loser.loserOption.map { winnerScore * 2 * gameScore }.sum() //패자 점수 2배
        } else {
            winnerScore * gameScore
        }
    }

    private suspend fun calculateScoreAccount(seller: Gamer?) {
        //뻑
        val fuckScore = rules.first { it.name == Const.Rule.Fuck }.score
        val gamers = if (seller != null) roundGamers - seller else roundGamers
        gamers.forEach { gamer ->
            gamer.scoreOption.forEach { option ->
                val account = when (option) {
                    ScoreOption.FirstFuck -> {
                        fuckScore
                    }
                    ScoreOption.SecondFuck -> {
                        fuckScore * 2
                    }
                    ScoreOption.ThreeFuck -> {
                        fuckScore * 4
                    }
                    ScoreOption.FirstDdadak -> {
                        fuckScore
                    }
                }
                (gamers - gamer).forEach {
                    updateAccountUseCase(gamer, it, account)
                    updateAccountUseCase(it, gamer, -account)
                }
            }
        }
    }

}