package zero.friends.gostopcalculator.ui.board.result

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import zero.friends.domain.model.Gamer
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.databinding.ItemCalculatedGamerBinding
import zero.friends.gostopcalculator.util.getMoneyColor

class CalculatedGamerAdapter : ListAdapter<Gamer, CalculatedGamerAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCalculatedGamerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    class ViewHolder(
        private val binding: ItemCalculatedGamerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(gamer: Gamer, position: Int) {
            binding.apply {
                // Gamer 정보
                tvGamerNumber.text = (position + 1).toString()
                tvGamerName.text = gamer.name
                
                // 금액 표시
                val account = gamer.account
                tvGamerScore.text = String.format("%,d원", account)
                tvGamerScore.setTextColor(
                    ContextCompat.getColor(root.context, account.getMoneyColor())
                )

                // 옵션 표시 (따닥, 박, 쌍피 등)
                val options = buildList {
                    val winnerOption = gamer.winnerOption?.korean
                    if (winnerOption != null) {
                        add(winnerOption)
                    }
                    
                    gamer.loserOption.forEach { option ->
                        add(option.korean)
                    }
                    
                    gamer.scoreOption.forEach { option ->
                        add(option.korean)
                    }
                }

                if (options.isNotEmpty()) {
                    tvGamerOptions.isVisible = true
                    tvGamerOptions.text = options.joinToString(" / ")
                } else {
                    tvGamerOptions.isVisible = false
                }

                // 정산 상세 내역
                layoutCalculateDetails.removeAllViews()
                
                if (gamer.calculate.isEmpty()) {
                    divider.isVisible = false
                } else {
                    divider.isVisible = true
                    gamer.calculate.forEach { target ->
                        val textView = TextView(root.context).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            ).apply {
                                bottomMargin = root.context.resources.getDimensionPixelSize(R.dimen.spacing_small)
                            }
                            
                            text = buildCalculateText(target.name, target.account)
                            textSize = 14f
                        }
                        layoutCalculateDetails.addView(textView)
                    }
                }
            }
        }

        private fun buildCalculateText(targetName: String, account: Int): SpannableStringBuilder {
            val action = if (account > 0) "받아야합니다." else "줘야합니다."
            val amountText = String.format("%,d원", account)
            
            return SpannableStringBuilder().apply {
                append("${targetName}에게 ")
                
                val start = length
                append(amountText)
                val end = length
                
                append(" 을 $action")
                
                // 금액에 색상 적용
                val color = ContextCompat.getColor(
                    binding.root.context,
                    account.getMoneyColor()
                )
                setSpan(
                    ForegroundColorSpan(color),
                    start,
                    end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                setSpan(
                    StyleSpan(Typeface.BOLD),
                    start,
                    end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Gamer>() {
        override fun areItemsTheSame(oldItem: Gamer, newItem: Gamer): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Gamer, newItem: Gamer): Boolean {
            return oldItem == newItem
        }
    }
}

