package zero.friends.gostopcalculator.ui.common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.close
import zero.friends.gostopcalculator.shared.generated.resources.ic_check_small
import zero.friends.gostopcalculator.shared.generated.resources.ic_topbar_close
import zero.friends.gostopcalculator.shared.generated.resources.network_gate_badge
import zero.friends.gostopcalculator.shared.generated.resources.network_gate_check_airplane
import zero.friends.gostopcalculator.shared.generated.resources.network_gate_check_wifi
import zero.friends.gostopcalculator.shared.generated.resources.network_gate_continue_button
import zero.friends.gostopcalculator.shared.generated.resources.network_gate_continue_countdown
import zero.friends.gostopcalculator.shared.generated.resources.network_gate_retry_button
import zero.friends.gostopcalculator.shared.generated.resources.network_gate_retry_failed
import zero.friends.gostopcalculator.shared.generated.resources.network_gate_subtitle
import zero.friends.gostopcalculator.shared.generated.resources.network_gate_title
import zero.friends.gostopcalculator.ui.board.score.common.ScoreCanvasBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreCardBorder
import zero.friends.gostopcalculator.ui.board.score.common.ScoreDisabledBadgeBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreMutedText
import zero.friends.gostopcalculator.ui.board.score.common.ScoreNeutralBtnBg
import zero.friends.gostopcalculator.ui.board.score.common.ScorePrimaryRed
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSoftPinkBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSubtleGray
import zero.friends.gostopcalculator.ui.board.score.common.ScoreTextDark
import zero.friends.gostopcalculator.ui.board.score.common.StatusPillBadge

@Composable
fun NetworkRequiredDialog(
    showRetryError: Boolean = false,
    retryAttemptCount: Int = 0,
    countdownSeconds: Int = 3,
    onRetry: () -> Unit,
    onContinueOffline: () -> Unit,
    onDismiss: () -> Unit,
) {
    val closeText = stringResource(Res.string.close)
    val errorShakeOffset = remember { Animatable(0f) }

    LaunchedEffect(showRetryError, retryAttemptCount) {
        if (showRetryError && retryAttemptCount > 0) {
            val offsets = listOf(-6f, 6f, -4f, 4f, 0f)
            for (target in offsets) {
                errorShakeOffset.animateTo(target, animationSpec = tween(durationMillis = 36))
            }
        } else {
            errorShakeOffset.snapTo(0f)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .widthIn(max = 360.dp)
                .fillMaxWidth()
                .enableTestTagsAsResourceId()
                .testTag("network_required_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            elevation = 8.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable(role = Role.Button, onClick = onDismiss)
                            .semantics {
                                contentDescription = closeText
                            }
                            .testTag("network_close_button"),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(ScoreNeutralBtnBg),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_topbar_close),
                                contentDescription = null,
                                tint = ScoreMutedText,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 10.dp)
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(ScoreSoftPinkBg),
                        contentAlignment = Alignment.Center,
                    ) {
                        WifiOffIcon(
                            color = ScorePrimaryRed,
                            modifier = Modifier.size(28.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                StatusPillBadge(
                    text = stringResource(Res.string.network_gate_badge),
                    backgroundColor = ScoreSoftPinkBg,
                    textColor = ScorePrimaryRed,
                    leadingDotColor = ScorePrimaryRed,
                    horizontalPadding = 10.dp,
                    verticalPadding = 4.dp,
                    fontSize = 11.5.sp,
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(Res.string.network_gate_title),
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = ScoreTextDark,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(Res.string.network_gate_subtitle),
                    fontSize = 13.5.sp,
                    color = ScoreMutedText,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                )

                Spacer(modifier = Modifier.height(18.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = ScoreCanvasBg,
                    border = BorderStroke(1.dp, ScoreCardBorder),
                    elevation = 0.dp,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        ChecklistRow(
                            iconBg = ScoreSoftPinkBg,
                            iconContent = {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_check_small),
                                    contentDescription = null,
                                    tint = ScorePrimaryRed,
                                    modifier = Modifier.size(13.dp),
                                )
                            },
                            text = stringResource(Res.string.network_gate_check_wifi),
                        )
                        ChecklistRow(
                            iconBg = ScoreDisabledBadgeBg,
                            iconContent = {
                                AirplaneIcon(
                                    color = ScoreMutedText,
                                    modifier = Modifier.size(12.dp),
                                )
                            },
                            text = stringResource(Res.string.network_gate_check_airplane),
                        )
                    }
                }

                if (showRetryError) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(Res.string.network_gate_retry_failed),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = ScorePrimaryRed,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .graphicsLayer { translationX = errorShakeOffset.value }
                            .semantics { liveRegion = LiveRegionMode.Polite },
                    )
                }

                Spacer(modifier = Modifier.height(if (showRetryError) 12.dp else 20.dp))

                Button(
                    onClick = onRetry,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = ScorePrimaryRed,
                        contentColor = Color.White,
                    ),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 50.dp)
                        .testTag("network_retry_button"),
                ) {
                    RefreshIcon(
                        color = Color.White,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(Res.string.network_gate_retry_button),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                ContinueOfflineButton(
                    countdownSeconds = countdownSeconds,
                    onContinueOffline = onContinueOffline,
                )
            }
        }
    }
}

@Composable
private fun ContinueOfflineButton(
    countdownSeconds: Int,
    onContinueOffline: () -> Unit,
) {
    val initialSeconds = countdownSeconds.coerceAtLeast(0)
    var remainingSeconds by remember(initialSeconds) {
        mutableIntStateOf(initialSeconds)
    }

    LaunchedEffect(initialSeconds) {
        remainingSeconds = initialSeconds
        while (remainingSeconds > 0) {
            delay(1_000L)
            remainingSeconds -= 1
        }
    }

    val canContinueOffline = remainingSeconds == 0
    val continueLabel = if (canContinueOffline) {
        stringResource(Res.string.network_gate_continue_button)
    } else {
        stringResource(Res.string.network_gate_continue_countdown, remainingSeconds)
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .heightIn(min = 48.dp)
            .clickable(enabled = canContinueOffline, role = Role.Button, onClick = onContinueOffline)
            .padding(horizontal = 14.dp)
            .testTag("network_continue_offline_button"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = continueLabel,
            fontSize = 13.sp,
            fontWeight = if (canContinueOffline) FontWeight.Medium else FontWeight.Normal,
            color = if (canContinueOffline) ScoreMutedText else ScoreSubtleGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f, fill = false),
        )
        if (canContinueOffline) {
            Text(
                text = " ›",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = ScoreMutedText,
                modifier = Modifier.clearAndSetSemantics {},
            )
        }
    }
}

@Composable
private fun ChecklistRow(
    iconBg: Color,
    iconContent: @Composable () -> Unit,
    text: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            iconContent()
        }
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = ScoreTextDark,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun WifiOffIcon(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.drawWithCache {
            val strokeWidth = 2.dp.toPx()
            val stroke = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
            val cx = size.width / 2f
            val cy = size.height * 0.68f
            val outerTopLeft = Offset(cx - size.width * 0.44f, cy - size.width * 0.44f)
            val outerSize = Size(size.width * 0.88f, size.width * 0.88f)
            val innerTopLeft = Offset(cx - size.width * 0.28f, cy - size.width * 0.28f)
            val innerSize = Size(size.width * 0.56f, size.width * 0.56f)
            val dotRadius = 1.8.dp.toPx()
            val dotCenter = Offset(cx, size.height * 0.78f)
            val slashStart = Offset(size.width * 0.18f, size.height * 0.18f)
            val slashEnd = Offset(size.width * 0.82f, size.height * 0.82f)

            onDrawBehind {
                drawArc(
                    color = color,
                    startAngle = -140f,
                    sweepAngle = 100f,
                    useCenter = false,
                    topLeft = outerTopLeft,
                    size = outerSize,
                    style = stroke,
                )
                drawArc(
                    color = color,
                    startAngle = -140f,
                    sweepAngle = 100f,
                    useCenter = false,
                    topLeft = innerTopLeft,
                    size = innerSize,
                    style = stroke,
                )
                drawCircle(
                    color = color,
                    radius = dotRadius,
                    center = dotCenter,
                )
                drawLine(
                    color = color,
                    start = slashStart,
                    end = slashEnd,
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round,
                )
            }
        }
    )
}

@Composable
private fun AirplaneIcon(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.drawWithCache {
            val strokeWidth = 1.6.dp.toPx()
            val stroke = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
            val wingPath = Path().apply {
                moveTo(size.width * 0.14f, size.height * 0.56f)
                lineTo(size.width * 0.5f, size.height * 0.34f)
                lineTo(size.width * 0.86f, size.height * 0.56f)
            }
            val bodyStart = Offset(size.width * 0.5f, size.height * 0.12f)
            val bodyEnd = Offset(size.width * 0.5f, size.height * 0.86f)
            val tailStart = Offset(size.width * 0.34f, size.height * 0.82f)
            val tailEnd = Offset(size.width * 0.66f, size.height * 0.82f)

            onDrawBehind {
                drawLine(
                    color = color,
                    start = bodyStart,
                    end = bodyEnd,
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round,
                )
                drawPath(path = wingPath, color = color, style = stroke)
                drawLine(
                    color = color,
                    start = tailStart,
                    end = tailEnd,
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round,
                )
            }
        }
    )
}

@Composable
private fun RefreshIcon(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.drawWithCache {
            val strokeWidth = 2.dp.toPx()
            val arcStroke = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            val arrowStroke = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
            val inset = size.width * 0.14f
            val arcTopLeft = Offset(inset, inset)
            val arcSize = Size(size.width - inset * 2f, size.height - inset * 2f)
            val tipX = size.width * 0.82f
            val tipY = size.height * 0.34f
            val arrowPath = Path().apply {
                moveTo(tipX - size.width * 0.20f, tipY - size.height * 0.04f)
                lineTo(tipX, tipY)
                lineTo(tipX - size.width * 0.02f, tipY - size.height * 0.20f)
            }

            onDrawBehind {
                drawArc(
                    color = color,
                    startAngle = -45f,
                    sweepAngle = 285f,
                    useCenter = false,
                    topLeft = arcTopLeft,
                    size = arcSize,
                    style = arcStroke,
                )
                drawPath(
                    path = arrowPath,
                    color = color,
                    style = arrowStroke,
                )
            }
        }
    )
}
