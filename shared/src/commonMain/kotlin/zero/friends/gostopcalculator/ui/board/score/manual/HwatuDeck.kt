package zero.friends.gostopcalculator.ui.board.score.manual

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_01_1
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_01_2
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_01_3
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_01_4
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_02_1
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_02_2
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_02_3
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_02_4
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_03_1
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_03_2
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_03_3
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_03_4
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_04_1
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_04_2
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_04_3
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_04_4
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_05_1
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_05_2
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_05_3
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_05_4
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_06_1
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_06_2
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_06_3
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_06_4
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_07_1
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_07_2
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_07_3
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_07_4
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_08_1
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_08_2
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_08_3
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_08_4
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_09_1
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_09_2
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_09_3
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_09_4
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_10_1
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_10_2
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_10_3
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_10_4
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_11_1
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_11_2
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_11_3
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_11_4
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_12_1
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_12_2
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_12_3
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_12_4
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_month_1
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_month_11
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_month_12
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_month_3
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_month_8
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_month_9
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_month_bonus
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_name_11_gwang
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_name_11_ssangpi
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_name_12_gwang
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_name_12_ssangpi
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_name_1_gwang
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_name_3_gwang
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_name_8_gwang
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_name_9_ssangpi
import zero.friends.gostopcalculator.shared.generated.resources.hwatu_name_bonus
import zero.friends.gostopcalculator.shared.generated.resources.ic_hwatu_bonus_pi
import zero.friends.gostopcalculator.shared.generated.resources.manual_month_1
import zero.friends.gostopcalculator.shared.generated.resources.manual_month_10
import zero.friends.gostopcalculator.shared.generated.resources.manual_month_11
import zero.friends.gostopcalculator.shared.generated.resources.manual_month_12
import zero.friends.gostopcalculator.shared.generated.resources.manual_month_2
import zero.friends.gostopcalculator.shared.generated.resources.manual_month_3
import zero.friends.gostopcalculator.shared.generated.resources.manual_month_4
import zero.friends.gostopcalculator.shared.generated.resources.manual_month_5
import zero.friends.gostopcalculator.shared.generated.resources.manual_month_6
import zero.friends.gostopcalculator.shared.generated.resources.manual_month_7
import zero.friends.gostopcalculator.shared.generated.resources.manual_month_8
import zero.friends.gostopcalculator.shared.generated.resources.manual_month_9
import zero.friends.gostopcalculator.shared.generated.resources.manual_role_bigwang
import zero.friends.gostopcalculator.shared.generated.resources.manual_role_cheongdan
import zero.friends.gostopcalculator.shared.generated.resources.manual_role_chodan
import zero.friends.gostopcalculator.shared.generated.resources.manual_role_godori
import zero.friends.gostopcalculator.shared.generated.resources.manual_role_gwang
import zero.friends.gostopcalculator.shared.generated.resources.manual_role_hongdan
import zero.friends.gostopcalculator.shared.generated.resources.manual_role_pi
import zero.friends.gostopcalculator.shared.generated.resources.manual_role_ssangpi
import zero.friends.gostopcalculator.shared.generated.resources.manual_role_tti
import zero.friends.gostopcalculator.shared.generated.resources.manual_role_yeol
import zero.friends.gostopcalculator.shared.generated.resources.manual_role_yeol_ssangpi
import zero.friends.gostopcalculator.shared.generated.resources.manual_score_3gwang_badge
import zero.friends.gostopcalculator.shared.generated.resources.manual_score_3gwang_desc
import zero.friends.gostopcalculator.shared.generated.resources.manual_score_3gwang_title
import zero.friends.gostopcalculator.shared.generated.resources.manual_score_4gwang_badge
import zero.friends.gostopcalculator.shared.generated.resources.manual_score_4gwang_desc
import zero.friends.gostopcalculator.shared.generated.resources.manual_score_4gwang_title
import zero.friends.gostopcalculator.shared.generated.resources.manual_score_5gwang_badge
import zero.friends.gostopcalculator.shared.generated.resources.manual_score_5gwang_desc
import zero.friends.gostopcalculator.shared.generated.resources.manual_score_5gwang_title
import zero.friends.gostopcalculator.shared.generated.resources.manual_score_bigwang_badge
import zero.friends.gostopcalculator.shared.generated.resources.manual_score_bigwang_desc
import zero.friends.gostopcalculator.shared.generated.resources.manual_score_bigwang_title
import zero.friends.gostopcalculator.shared.generated.resources.manual_score_dan_badge
import zero.friends.gostopcalculator.shared.generated.resources.manual_score_dan_desc
import zero.friends.gostopcalculator.shared.generated.resources.manual_score_dan_title
import zero.friends.gostopcalculator.shared.generated.resources.manual_score_godori_badge
import zero.friends.gostopcalculator.shared.generated.resources.manual_score_godori_desc
import zero.friends.gostopcalculator.shared.generated.resources.manual_score_godori_title
import zero.friends.gostopcalculator.shared.generated.resources.manual_score_pi10_badge
import zero.friends.gostopcalculator.shared.generated.resources.manual_score_pi10_desc
import zero.friends.gostopcalculator.shared.generated.resources.manual_score_pi10_title
import zero.friends.gostopcalculator.shared.generated.resources.manual_score_tti5_badge
import zero.friends.gostopcalculator.shared.generated.resources.manual_score_tti5_desc
import zero.friends.gostopcalculator.shared.generated.resources.manual_score_tti5_title
import zero.friends.gostopcalculator.shared.generated.resources.manual_score_yeol5_badge
import zero.friends.gostopcalculator.shared.generated.resources.manual_score_yeol5_desc
import zero.friends.gostopcalculator.shared.generated.resources.manual_score_yeol5_title
import zero.friends.gostopcalculator.shared.generated.resources.manual_tab_bak
import zero.friends.gostopcalculator.shared.generated.resources.manual_tab_cards
import zero.friends.gostopcalculator.shared.generated.resources.manual_tab_flow
import zero.friends.gostopcalculator.shared.generated.resources.manual_tab_go
import zero.friends.gostopcalculator.shared.generated.resources.manual_tab_scores

enum class ManualTab(val index: Int, val labelRes: StringResource) {
    GameFlow(0, Res.string.manual_tab_flow),
    CardScores(1, Res.string.manual_tab_scores),
    GoAndBonus(2, Res.string.manual_tab_go),
    Penalties(3, Res.string.manual_tab_bak),
    MonthlyCards(4, Res.string.manual_tab_cards);

    companion object {
        fun fromIndex(index: Int): ManualTab =
            entries.firstOrNull { it.index == index } ?: GameFlow
    }
}

enum class HwatuCardRole(val labelRes: StringResource) {
    GWANG(Res.string.manual_role_gwang),
    BI_GWANG(Res.string.manual_role_bigwang),
    GODORI(Res.string.manual_role_godori),
    YEOL(Res.string.manual_role_yeol),
    YEOL_SSANGPI(Res.string.manual_role_yeol_ssangpi),
    HONGDAN(Res.string.manual_role_hongdan),
    CHEONGDAN(Res.string.manual_role_cheongdan),
    CHODAN(Res.string.manual_role_chodan),
    TTI(Res.string.manual_role_tti),
    SSANGPI(Res.string.manual_role_ssangpi),
    PI(Res.string.manual_role_pi),
}

data class HwatuCardEntry(
    val id: String,
    val drawable: DrawableResource,
    val role: HwatuCardRole,
)

data class HwatuMonthSection(
    val month: Int,
    val nameRes: StringResource,
    val cards: List<HwatuCardEntry>,
)

data class SellingCardEntry(
    val drawable: DrawableResource,
    val monthRes: StringResource,
    val nameRes: StringResource,
    val isBiGwang: Boolean = false,
)

data class ScoreComboCardSpec(
    val cards: List<DrawableResource>,
    val titleRes: StringResource,
    val badgeRes: StringResource,
    val descRes: StringResource,
    val overlapDp: Int = 10,
)

object HwatuDeck {
    val monthlySections: List<HwatuMonthSection> = listOf(
        HwatuMonthSection(
            month = 1,
            nameRes = Res.string.manual_month_1,
            cards = listOf(
                HwatuCardEntry("01_1", Res.drawable.hwatu_01_1, HwatuCardRole.GWANG),
                HwatuCardEntry("01_2", Res.drawable.hwatu_01_2, HwatuCardRole.HONGDAN),
                HwatuCardEntry("01_3", Res.drawable.hwatu_01_3, HwatuCardRole.PI),
                HwatuCardEntry("01_4", Res.drawable.hwatu_01_4, HwatuCardRole.PI),
            )
        ),
        HwatuMonthSection(
            month = 2,
            nameRes = Res.string.manual_month_2,
            cards = listOf(
                HwatuCardEntry("02_1", Res.drawable.hwatu_02_1, HwatuCardRole.GODORI),
                HwatuCardEntry("02_2", Res.drawable.hwatu_02_2, HwatuCardRole.HONGDAN),
                HwatuCardEntry("02_3", Res.drawable.hwatu_02_3, HwatuCardRole.PI),
                HwatuCardEntry("02_4", Res.drawable.hwatu_02_4, HwatuCardRole.PI),
            )
        ),
        HwatuMonthSection(
            month = 3,
            nameRes = Res.string.manual_month_3,
            cards = listOf(
                HwatuCardEntry("03_1", Res.drawable.hwatu_03_1, HwatuCardRole.GWANG),
                HwatuCardEntry("03_2", Res.drawable.hwatu_03_2, HwatuCardRole.HONGDAN),
                HwatuCardEntry("03_3", Res.drawable.hwatu_03_3, HwatuCardRole.PI),
                HwatuCardEntry("03_4", Res.drawable.hwatu_03_4, HwatuCardRole.PI),
            )
        ),
        HwatuMonthSection(
            month = 4,
            nameRes = Res.string.manual_month_4,
            cards = listOf(
                HwatuCardEntry("04_1", Res.drawable.hwatu_04_1, HwatuCardRole.GODORI),
                HwatuCardEntry("04_2", Res.drawable.hwatu_04_2, HwatuCardRole.CHODAN),
                HwatuCardEntry("04_3", Res.drawable.hwatu_04_3, HwatuCardRole.PI),
                HwatuCardEntry("04_4", Res.drawable.hwatu_04_4, HwatuCardRole.PI),
            )
        ),
        HwatuMonthSection(
            month = 5,
            nameRes = Res.string.manual_month_5,
            cards = listOf(
                HwatuCardEntry("05_1", Res.drawable.hwatu_05_1, HwatuCardRole.YEOL),
                HwatuCardEntry("05_2", Res.drawable.hwatu_05_2, HwatuCardRole.CHODAN),
                HwatuCardEntry("05_3", Res.drawable.hwatu_05_3, HwatuCardRole.PI),
                HwatuCardEntry("05_4", Res.drawable.hwatu_05_4, HwatuCardRole.PI),
            )
        ),
        HwatuMonthSection(
            month = 6,
            nameRes = Res.string.manual_month_6,
            cards = listOf(
                HwatuCardEntry("06_1", Res.drawable.hwatu_06_1, HwatuCardRole.YEOL),
                HwatuCardEntry("06_2", Res.drawable.hwatu_06_2, HwatuCardRole.CHEONGDAN),
                HwatuCardEntry("06_3", Res.drawable.hwatu_06_3, HwatuCardRole.PI),
                HwatuCardEntry("06_4", Res.drawable.hwatu_06_4, HwatuCardRole.PI),
            )
        ),
        HwatuMonthSection(
            month = 7,
            nameRes = Res.string.manual_month_7,
            cards = listOf(
                HwatuCardEntry("07_1", Res.drawable.hwatu_07_1, HwatuCardRole.YEOL),
                HwatuCardEntry("07_2", Res.drawable.hwatu_07_2, HwatuCardRole.CHODAN),
                HwatuCardEntry("07_3", Res.drawable.hwatu_07_3, HwatuCardRole.PI),
                HwatuCardEntry("07_4", Res.drawable.hwatu_07_4, HwatuCardRole.PI),
            )
        ),
        HwatuMonthSection(
            month = 8,
            nameRes = Res.string.manual_month_8,
            cards = listOf(
                HwatuCardEntry("08_1", Res.drawable.hwatu_08_1, HwatuCardRole.GWANG),
                HwatuCardEntry("08_2", Res.drawable.hwatu_08_2, HwatuCardRole.GODORI),
                HwatuCardEntry("08_3", Res.drawable.hwatu_08_3, HwatuCardRole.PI),
                HwatuCardEntry("08_4", Res.drawable.hwatu_08_4, HwatuCardRole.PI),
            )
        ),
        HwatuMonthSection(
            month = 9,
            nameRes = Res.string.manual_month_9,
            cards = listOf(
                HwatuCardEntry("09_1", Res.drawable.hwatu_09_1, HwatuCardRole.YEOL_SSANGPI),
                HwatuCardEntry("09_2", Res.drawable.hwatu_09_2, HwatuCardRole.CHEONGDAN),
                HwatuCardEntry("09_3", Res.drawable.hwatu_09_3, HwatuCardRole.PI),
                HwatuCardEntry("09_4", Res.drawable.hwatu_09_4, HwatuCardRole.PI),
            )
        ),
        HwatuMonthSection(
            month = 10,
            nameRes = Res.string.manual_month_10,
            cards = listOf(
                HwatuCardEntry("10_1", Res.drawable.hwatu_10_1, HwatuCardRole.YEOL),
                HwatuCardEntry("10_2", Res.drawable.hwatu_10_2, HwatuCardRole.CHEONGDAN),
                HwatuCardEntry("10_3", Res.drawable.hwatu_10_3, HwatuCardRole.PI),
                HwatuCardEntry("10_4", Res.drawable.hwatu_10_4, HwatuCardRole.PI),
            )
        ),
        HwatuMonthSection(
            month = 11,
            nameRes = Res.string.manual_month_11,
            cards = listOf(
                HwatuCardEntry("11_1", Res.drawable.hwatu_11_1, HwatuCardRole.GWANG),
                HwatuCardEntry("11_2", Res.drawable.hwatu_11_2, HwatuCardRole.SSANGPI),
                HwatuCardEntry("11_3", Res.drawable.hwatu_11_3, HwatuCardRole.PI),
                HwatuCardEntry("11_4", Res.drawable.hwatu_11_4, HwatuCardRole.PI),
            )
        ),
        HwatuMonthSection(
            month = 12,
            nameRes = Res.string.manual_month_12,
            cards = listOf(
                HwatuCardEntry("12_1", Res.drawable.hwatu_12_1, HwatuCardRole.BI_GWANG),
                HwatuCardEntry("12_2", Res.drawable.hwatu_12_2, HwatuCardRole.YEOL),
                HwatuCardEntry("12_3", Res.drawable.hwatu_12_3, HwatuCardRole.TTI),
                HwatuCardEntry("12_4", Res.drawable.hwatu_12_4, HwatuCardRole.SSANGPI),
            )
        ),
    )

    val sellingGwangEntries: List<SellingCardEntry> = listOf(
        SellingCardEntry(Res.drawable.hwatu_01_1, Res.string.hwatu_month_1, Res.string.hwatu_name_1_gwang),
        SellingCardEntry(Res.drawable.hwatu_03_1, Res.string.hwatu_month_3, Res.string.hwatu_name_3_gwang),
        SellingCardEntry(Res.drawable.hwatu_08_1, Res.string.hwatu_month_8, Res.string.hwatu_name_8_gwang),
        SellingCardEntry(Res.drawable.hwatu_11_1, Res.string.hwatu_month_11, Res.string.hwatu_name_11_gwang),
        SellingCardEntry(Res.drawable.hwatu_12_1, Res.string.hwatu_month_12, Res.string.hwatu_name_12_gwang, isBiGwang = true),
    )

    val sellingSsangpiEntries: List<SellingCardEntry> = listOf(
        SellingCardEntry(Res.drawable.hwatu_09_1, Res.string.hwatu_month_9, Res.string.hwatu_name_9_ssangpi),
        SellingCardEntry(Res.drawable.hwatu_11_2, Res.string.hwatu_month_11, Res.string.hwatu_name_11_ssangpi),
        SellingCardEntry(Res.drawable.hwatu_12_4, Res.string.hwatu_month_12, Res.string.hwatu_name_12_ssangpi),
        SellingCardEntry(Res.drawable.ic_hwatu_bonus_pi, Res.string.hwatu_month_bonus, Res.string.hwatu_name_bonus),
    )

    val gwangScoreCombos: List<ScoreComboCardSpec> = listOf(
        ScoreComboCardSpec(
            cards = listOf(
                Res.drawable.hwatu_01_1,
                Res.drawable.hwatu_03_1,
                Res.drawable.hwatu_08_1,
                Res.drawable.hwatu_11_1,
                Res.drawable.hwatu_12_1,
            ),
            titleRes = Res.string.manual_score_5gwang_title,
            badgeRes = Res.string.manual_score_5gwang_badge,
            descRes = Res.string.manual_score_5gwang_desc,
            overlapDp = 12,
        ),
        ScoreComboCardSpec(
            cards = listOf(
                Res.drawable.hwatu_01_1,
                Res.drawable.hwatu_03_1,
                Res.drawable.hwatu_08_1,
                Res.drawable.hwatu_11_1,
            ),
            titleRes = Res.string.manual_score_4gwang_title,
            badgeRes = Res.string.manual_score_4gwang_badge,
            descRes = Res.string.manual_score_4gwang_desc,
            overlapDp = 10,
        ),
        ScoreComboCardSpec(
            cards = listOf(
                Res.drawable.hwatu_01_1,
                Res.drawable.hwatu_03_1,
                Res.drawable.hwatu_08_1,
            ),
            titleRes = Res.string.manual_score_3gwang_title,
            badgeRes = Res.string.manual_score_3gwang_badge,
            descRes = Res.string.manual_score_3gwang_desc,
            overlapDp = 8,
        ),
        ScoreComboCardSpec(
            cards = listOf(
                Res.drawable.hwatu_01_1,
                Res.drawable.hwatu_03_1,
                Res.drawable.hwatu_12_1,
            ),
            titleRes = Res.string.manual_score_bigwang_title,
            badgeRes = Res.string.manual_score_bigwang_badge,
            descRes = Res.string.manual_score_bigwang_desc,
            overlapDp = 8,
        ),
    )

    val otherScoreCombos: List<ScoreComboCardSpec> = listOf(
        ScoreComboCardSpec(
            cards = listOf(
                Res.drawable.hwatu_02_1,
                Res.drawable.hwatu_04_1,
                Res.drawable.hwatu_08_2,
            ),
            titleRes = Res.string.manual_score_godori_title,
            badgeRes = Res.string.manual_score_godori_badge,
            descRes = Res.string.manual_score_godori_desc,
            overlapDp = 8,
        ),
        ScoreComboCardSpec(
            cards = listOf(
                Res.drawable.hwatu_05_1,
                Res.drawable.hwatu_06_1,
                Res.drawable.hwatu_07_1,
                Res.drawable.hwatu_10_1,
                Res.drawable.hwatu_12_2,
            ),
            titleRes = Res.string.manual_score_yeol5_title,
            badgeRes = Res.string.manual_score_yeol5_badge,
            descRes = Res.string.manual_score_yeol5_desc,
            overlapDp = 12,
        ),
        ScoreComboCardSpec(
            cards = listOf(
                Res.drawable.hwatu_01_2,
                Res.drawable.hwatu_06_2,
                Res.drawable.hwatu_04_2,
            ),
            titleRes = Res.string.manual_score_dan_title,
            badgeRes = Res.string.manual_score_dan_badge,
            descRes = Res.string.manual_score_dan_desc,
            overlapDp = 8,
        ),
        ScoreComboCardSpec(
            cards = listOf(
                Res.drawable.hwatu_02_2,
                Res.drawable.hwatu_05_2,
                Res.drawable.hwatu_09_2,
                Res.drawable.hwatu_10_2,
                Res.drawable.hwatu_12_3,
            ),
            titleRes = Res.string.manual_score_tti5_title,
            badgeRes = Res.string.manual_score_tti5_badge,
            descRes = Res.string.manual_score_tti5_desc,
            overlapDp = 12,
        ),
    )

    val piScoreCombo: ScoreComboCardSpec = ScoreComboCardSpec(
        cards = listOf(
            Res.drawable.hwatu_01_3,
            Res.drawable.hwatu_02_3,
            Res.drawable.hwatu_03_3,
            Res.drawable.hwatu_09_1,
            Res.drawable.hwatu_11_2,
            Res.drawable.hwatu_12_4,
        ),
        titleRes = Res.string.manual_score_pi10_title,
        badgeRes = Res.string.manual_score_pi10_badge,
        descRes = Res.string.manual_score_pi10_desc,
        overlapDp = 8,
    )

    val gwangScoreComboPairs: List<List<ScoreComboCardSpec>> = gwangScoreCombos.chunked(2)

    val otherScoreComboPairs: List<List<ScoreComboCardSpec>> = otherScoreCombos.chunked(2)
}
