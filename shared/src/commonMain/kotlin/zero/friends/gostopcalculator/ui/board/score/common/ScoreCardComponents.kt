package zero.friends.gostopcalculator.ui.board.score.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.ic_stepper_add
import zero.friends.gostopcalculator.shared.generated.resources.ic_stepper_remove

val ScoreCanvasBg = Color(0xFFF7F8FA)
val ScoreCardBorder = Color(0xFFF0F2F5)
val ScoreBorderNeutral = Color(0xFFE5E8EB)
val ScoreSoftPinkBg = Color(0xFFFFF0F0)
val ScoreSoftPinkButtonBg = Color(0xFFFFF5F5)
val ScoreSoftPinkBorder = Color(0xFFFFD6D6)
val ScoreTextDark = Color(0xFF191F28)
val ScoreMutedText = Color(0xFF6B7684)
val ScoreSubtleGray = Color(0xFF8B95A1)
val ScoreDisabledBg = Color(0xFFF9FAFB)
val ScoreDisabledBorder = Color(0xFFD1D6DB)
val ScoreDisabledBadgeBg = Color(0xFFEDF0F3)
val ScoreNeutralBtnBg = Color(0xFFF3F4F6)
val ScoreMintBg = Color(0xFFE0F5F4)
val ScoreMintText = Color(0xFF006765)
val ScoreDarkBadge = Color(0xFF1E2022)
val ScorePrimaryRed = Color(0xFFFF5252)
val ScoreSummaryCardBg = Color(0xFFF8F9FA)
val ScoreRuleCardBg = Color(0xFFF9F9FF)
val ScoreRuleCardBorder = Color(0xFFE1E8FD)

@Composable
fun ScoreHeroCard(
    icon: DrawableResource,
    mainText: String,
    subText: String,
    modifier: Modifier = Modifier,
    highlightText: String? = null,
) {
    val annotatedSubText = buildAnnotatedString {
        append(subText)
        if (!highlightText.isNullOrEmpty()) {
            val start = subText.indexOf(highlightText)
            if (start >= 0) {
                addStyle(
                    style = SpanStyle(
                        color = ScorePrimaryRed,
                        fontWeight = FontWeight.Bold
                    ),
                    start = start,
                    end = start + highlightText.length
                )
            }
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, ScoreCardBorder),
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(ScoreSoftPinkBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = ScorePrimaryRed,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = mainText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ScoreTextDark,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = annotatedSubText,
                fontSize = 13.5.sp,
                lineHeight = 20.sp,
                color = ScoreMutedText,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ScorePlayerCard(
    modifier: Modifier = Modifier,
    isHighlighted: Boolean = false,
    borderColor: Color = if (isHighlighted) ScorePrimaryRed else ScoreCardBorder,
    borderWidth: Dp = if (isHighlighted) 1.5.dp else 1.dp,
    elevation: Dp = if (isHighlighted) 4.dp else 2.dp,
    onClick: (() -> Unit)? = null,
    clickRole: Role = Role.Button,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(16.dp)
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        color = Color.White,
        border = BorderStroke(borderWidth, borderColor),
        elevation = elevation,
    ) {
        if (onClick != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .clickable(role = clickRole, onClick = onClick)
            ) {
                content()
            }
        } else {
            content()
        }
    }
}

@Composable
fun DashedGamerCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(ScoreDisabledBg)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val strokePx = 1.dp.toPx()
            val dashPx = 5.dp.toPx()
            val gapPx = 4.dp.toPx()
            val radiusPx = cornerRadius.toPx()
            drawRoundRect(
                color = ScoreDisabledBorder,
                cornerRadius = CornerRadius(radiusPx, radiusPx),
                style = Stroke(
                    width = strokePx,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashPx, gapPx), 0f)
                )
            )
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            content()
        }
    }
}

@Composable
fun PlayerIndexBadge(
    index: Int,
    containerColor: Color,
    textColor: Color,
    size: Dp = 26.dp,
    shape: Shape = CircleShape,
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(shape)
            .background(containerColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = (index + 1).toString(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
fun StatusPillBadge(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    borderColor: Color? = null,
    leadingDotColor: Color? = null,
    horizontalPadding: Dp = 8.dp,
    verticalPadding: Dp = 3.dp,
    fontSize: TextUnit = 11.sp,
    maxLines: Int = 1,
) {
    val shape = RoundedCornerShape(100.dp)
    Surface(
        modifier = modifier,
        shape = shape,
        color = backgroundColor,
        border = borderColor?.let { BorderStroke(1.dp, it) }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = horizontalPadding, vertical = verticalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            if (leadingDotColor != null) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(leadingDotColor)
                )
            }
            Text(
                text = text,
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                color = textColor,
                maxLines = maxLines,
                softWrap = maxLines > 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun StepperNumberField(
    value: Long,
    endText: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isHighlighted: Boolean = false,
    filledPlusWhenHighlighted: Boolean = true,
    centerWidth: Dp = 72.dp,
    centerHeight: Dp = 36.dp,
    buttonSize: Dp = 30.dp,
    spacing: Dp = 6.dp,
    outlinedNeutralButtons: Boolean = false,
    inputTestTag: String? = null,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    onValueChange: (Long) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val displayText = if (value == 0L) "" else value.toString()

    val canDec = enabled && value > 0L
    val minusBg = when {
        outlinedNeutralButtons -> if (enabled) Color.White else ScoreDisabledBg
        !enabled -> ScoreDisabledBg
        else -> ScoreNeutralBtnBg
    }
    val minusBorder = if (outlinedNeutralButtons) ScoreRuleCardBorder else null
    val minusTint = when {
        !canDec -> ScoreSubtleGray.copy(alpha = 0.45f)
        outlinedNeutralButtons -> Color(0xFF5B403E)
        else -> ScoreMutedText
    }

    val useFilledPlus = isHighlighted && filledPlusWhenHighlighted
    val plusBg = when {
        !enabled -> ScoreDisabledBg
        useFilledPlus -> ScorePrimaryRed
        outlinedNeutralButtons -> Color.White
        else -> ScoreNeutralBtnBg
    }
    val plusBorder = if (outlinedNeutralButtons && !useFilledPlus) ScoreRuleCardBorder else null
    val plusTint = when {
        !enabled -> ScoreSubtleGray.copy(alpha = 0.45f)
        useFilledPlus -> Color.White
        outlinedNeutralButtons -> ScorePrimaryRed
        else -> ScoreMutedText
    }

    val boxBorderColor = when {
        !enabled -> ScoreDisabledBorder
        isHighlighted -> ScorePrimaryRed
        else -> ScoreBorderNeutral
    }
    val boxBorderWidth = if (enabled && isHighlighted) 1.5.dp else 1.dp
    val boxBg = if (enabled) Color.White else ScoreDisabledBg
    val numberColor = when {
        !enabled -> ScoreSubtleGray
        value == 0L -> ScoreMutedText
        else -> ScoreTextDark
    }
    val unitColor = if (enabled) ScoreMutedText else ScoreSubtleGray

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
        StepperIconButton(
            icon = Res.drawable.ic_stepper_remove,
            contentDescription = "-",
            size = buttonSize,
            backgroundColor = minusBg,
            borderColor = minusBorder,
            iconTint = minusTint,
            enabled = canDec,
            onClick = {
                focusManager.clearFocus()
                onDecrement()
            }
        )

        Row(
            modifier = Modifier
                .width(centerWidth)
                .defaultMinSize(minHeight = centerHeight)
                .height(centerHeight)
                .clip(RoundedCornerShape(10.dp))
                .background(boxBg)
                .border(boxBorderWidth, boxBorderColor, RoundedCornerShape(10.dp))
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (displayText.isEmpty()) {
                    Text(
                        text = "0",
                        color = numberColor.copy(alpha = 0.55f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        softWrap = false,
                        textAlign = TextAlign.End
                    )
                }
                BasicTextField(
                    value = displayText,
                    onValueChange = { input ->
                        val parsed = input.filter { it.isDigit() }.toLongOrNull() ?: 0L
                        onValueChange(parsed)
                    },
                    enabled = enabled,
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.End,
                        color = numberColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .let { if (inputTestTag != null) it.testTag(inputTestTag) else it }
                )
            }
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = endText,
                color = unitColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                softWrap = false
            )
        }

        StepperIconButton(
            icon = Res.drawable.ic_stepper_add,
            contentDescription = "+",
            size = buttonSize,
            backgroundColor = plusBg,
            borderColor = plusBorder,
            iconTint = plusTint,
            enabled = enabled,
            onClick = {
                focusManager.clearFocus()
                onIncrement()
            }
        )
    }
}

@Composable
private fun StepperIconButton(
    icon: DrawableResource,
    contentDescription: String,
    size: Dp,
    backgroundColor: Color,
    borderColor: Color?,
    iconTint: Color,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(9.dp)
    Box(
        modifier = Modifier
            .size(size)
            .clip(shape)
            .background(backgroundColor)
            .let { if (borderColor != null) it.border(1.dp, borderColor, shape) else it }
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun CompactNumericInputBox(
    value: Long,
    endText: String,
    modifier: Modifier = Modifier,
    width: Dp = 56.dp,
    height: Dp = 34.dp,
    isHighlighted: Boolean = false,
    inputTestTag: String? = null,
    onValueChange: (Long) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val displayText = if (value == 0L) "" else value.toString()
    val borderColor = if (isHighlighted) ScorePrimaryRed else ScoreBorderNeutral

    Row(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterEnd
        ) {
            if (displayText.isEmpty()) {
                Text(
                    text = "0",
                    color = ScoreMutedText.copy(alpha = 0.55f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    softWrap = false,
                    textAlign = TextAlign.End
                )
            }
            BasicTextField(
                value = displayText,
                onValueChange = { input ->
                    val parsed = input.filter { it.isDigit() }.toLongOrNull() ?: 0L
                    onValueChange(parsed)
                },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.End,
                    color = if (isHighlighted) ScoreTextDark else ScoreMutedText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }),
                modifier = Modifier
                    .fillMaxWidth()
                    .let { if (inputTestTag != null) it.testTag(inputTestTag) else it }
            )
        }
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = endText,
            color = ScoreMutedText,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            softWrap = false
        )
    }
}
