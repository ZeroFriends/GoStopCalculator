package zero.friends.gostopcalculator.util

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import zero.friends.domain.model.LoserOption
import zero.friends.domain.model.Option
import zero.friends.domain.model.Rule
import zero.friends.domain.model.ScoreOption
import zero.friends.domain.model.SellerOption
import zero.friends.domain.model.WinnerOption
import zero.friends.domain.util.Const
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.rule_first_ddadak
import zero.friends.gostopcalculator.shared.generated.resources.rule_first_fuck
import zero.friends.gostopcalculator.shared.generated.resources.rule_fuck
import zero.friends.gostopcalculator.shared.generated.resources.rule_go_bak
import zero.friends.gostopcalculator.shared.generated.resources.rule_light_bak
import zero.friends.gostopcalculator.shared.generated.resources.rule_mong_bak
import zero.friends.gostopcalculator.shared.generated.resources.rule_pea_bak
import zero.friends.gostopcalculator.shared.generated.resources.rule_score
import zero.friends.gostopcalculator.shared.generated.resources.rule_script_essential
import zero.friends.gostopcalculator.shared.generated.resources.rule_script_sell_four_players
import zero.friends.gostopcalculator.shared.generated.resources.rule_second_fuck
import zero.friends.gostopcalculator.shared.generated.resources.rule_sell
import zero.friends.gostopcalculator.shared.generated.resources.rule_seller
import zero.friends.gostopcalculator.shared.generated.resources.rule_three_fuck
import zero.friends.gostopcalculator.shared.generated.resources.rule_winner

fun Const.Rule.stringRes(): StringResource = when (this) {
    Const.Rule.Winner -> Res.string.rule_winner
    Const.Rule.Seller -> Res.string.rule_seller
    Const.Rule.Fuck -> Res.string.rule_fuck
    Const.Rule.FirstFuck -> Res.string.rule_first_fuck
    Const.Rule.SecondFuck -> Res.string.rule_second_fuck
    Const.Rule.ThreeFuck -> Res.string.rule_three_fuck
    Const.Rule.FirstDdadak -> Res.string.rule_first_ddadak
    Const.Rule.PeaBak -> Res.string.rule_pea_bak
    Const.Rule.LightBak -> Res.string.rule_light_bak
    Const.Rule.MongBak -> Res.string.rule_mong_bak
    Const.Rule.GoBak -> Res.string.rule_go_bak
    Const.Rule.Score -> Res.string.rule_score
    Const.Rule.Sell -> Res.string.rule_sell
}

@Composable
fun Const.Rule.localizedName(): String = stringResource(stringRes())

fun Option.toRule(): Const.Rule = when (this) {
    WinnerOption.Winner -> Const.Rule.Winner
    SellerOption.Seller -> Const.Rule.Seller
    ScoreOption.FirstFuck -> Const.Rule.FirstFuck
    ScoreOption.SecondFuck -> Const.Rule.SecondFuck
    ScoreOption.ThreeFuck -> Const.Rule.ThreeFuck
    ScoreOption.FirstDdadak -> Const.Rule.FirstDdadak
    LoserOption.PeaBak -> Const.Rule.PeaBak
    LoserOption.LightBak -> Const.Rule.LightBak
    LoserOption.MongBak -> Const.Rule.MongBak
    LoserOption.GoBak -> Const.Rule.GoBak
}

@Composable
fun Option.localizedName(): String = toRule().localizedName()

@Composable
fun Rule.localizedScript(): String = when (name) {
    Const.Rule.Score -> if (script.isNotEmpty()) stringResource(Res.string.rule_script_essential) else ""
    Const.Rule.Sell -> if (script.isNotEmpty()) stringResource(Res.string.rule_script_sell_four_players) else ""
    else -> script
}
