package zero.friends.gostopcalculator.ui.board.result

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import zero.friends.domain.model.RoundTrace
import zero.friends.gostopcalculator.util.getMoneyColor
import java.util.Locale
import javax.inject.Inject
import kotlin.math.abs

class RoundTraceFormatter @Inject constructor(
    @ApplicationContext private val context: Context
) {

    data class TraceLine(
        val title: String,
        val formula: CharSequence,
        val amount: CharSequence
    )

    fun toLines(trace: RoundTrace): List<TraceLine> {
        return trace.terms
            .filter { it.amount != 0 }
            .map { term ->
            TraceLine(
                title = term.label,
                formula = term.detail.orEmpty(),
                amount = buildAmount(term.amount)
            )
        }
    }

    fun formatAmount(value: Int): CharSequence = buildAmount(value)

    private fun buildAmount(
        value: Int
    ): CharSequence {
        val builder = SpannableStringBuilder()
        val sign = if (value >= 0) "+" else "-"
        val amount = String.format(Locale.KOREA, "%,d", abs(value))
        val start = builder.length
        builder.append(sign).append(amount)
        val end = builder.length
        val color = ContextCompat.getColor(context, value.getMoneyColor())
        builder.setSpan(
            ForegroundColorSpan(color),
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        builder.setSpan(
            StyleSpan(Typeface.BOLD),
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return builder
    }
}
