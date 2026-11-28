package zero.friends.gostopcalculator.ui.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.cancel
import zero.friends.gostopcalculator.shared.generated.resources.ic_delete_black
import zero.friends.gostopcalculator.shared.generated.resources.ic_mode_edit_black
import zero.friends.gostopcalculator.ui.board.score.common.ScoreCardBorder
import zero.friends.gostopcalculator.ui.board.score.common.ScoreMutedText
import zero.friends.gostopcalculator.ui.board.score.common.ScoreNeutralBtnBg
import zero.friends.gostopcalculator.ui.board.score.common.ScorePrimaryRed
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSoftPinkBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreTextDark
import zero.friends.gostopcalculator.ui.board.score.common.StatusPillBadge
import zero.friends.gostopcalculator.ui.common.BackHandler

/**
 * Shared modal bottom sheet for managing a Game (more button or long-press) or Round (more button).
 */
@Composable
fun ItemActionBottomSheet(
    visible: Boolean,
    eyebrow: String,
    title: String,
    subtitle: String? = null,
    renameLabel: String,
    deleteLabel: String,
    deleteCautionLabel: String,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    val transitionState = remember { MutableTransitionState(false) }
    LaunchedEffect(visible) {
        transitionState.targetState = visible
    }

    if (!transitionState.currentState && !transitionState.targetState) {
        return
    }

    BackHandler(enabled = visible) {
        onDismiss()
    }

    val cancelLabel = stringResource(Res.string.cancel)

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visibleState = transitionState,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickable(
                        enabled = visible,
                        onClickLabel = cancelLabel,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss,
                    )
            )
        }

        AnimatedVisibility(
            visibleState = transitionState,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically { it },
            exit = slideOutVertically { it },
        ) {
            val sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectTapGestures { }
                    },
                shape = sheetShape,
                color = Color.White,
                elevation = 12.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(36.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(100.dp))
                            .background(Color(0xFFD1D5DB))
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(ScorePrimaryRed)
                        )
                        Text(
                            text = eyebrow,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = ScoreMutedText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = title,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = ScoreTextDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                        if (!subtitle.isNullOrBlank()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = subtitle,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = ScoreMutedText,
                                maxLines = 1,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = ScoreCardBorder, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(8.dp))

                    SheetActionRow(
                        icon = Res.drawable.ic_mode_edit_black,
                        iconBg = ScoreNeutralBtnBg,
                        iconTint = ScoreTextDark,
                        label = renameLabel,
                        labelColor = ScoreTextDark,
                        testTag = "sheet_rename_action",
                        enabled = visible,
                        trailing = {
                            Text(
                                text = "›",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                color = ScoreMutedText,
                                modifier = Modifier.clearAndSetSemantics {},
                            )
                        },
                        onClick = onRename,
                    )

                    SheetActionRow(
                        icon = Res.drawable.ic_delete_black,
                        iconBg = ScoreSoftPinkBg,
                        iconTint = ScorePrimaryRed,
                        label = deleteLabel,
                        labelColor = ScorePrimaryRed,
                        testTag = "sheet_delete_action",
                        enabled = visible,
                        trailing = {
                            StatusPillBadge(
                                text = deleteCautionLabel,
                                backgroundColor = ScoreSoftPinkBg,
                                textColor = ScorePrimaryRed,
                                fontSize = 11.sp,
                                horizontalPadding = 8.dp,
                                verticalPadding = 3.dp,
                            )
                        },
                        onClick = onDelete,
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .testTag("sheet_cancel_button")
                            .clickable(enabled = visible, role = Role.Button, onClick = onDismiss),
                        shape = RoundedCornerShape(14.dp),
                        color = ScoreNeutralBtnBg,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = cancelLabel,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ScoreTextDark,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun SheetActionRow(
    icon: DrawableResource,
    iconBg: Color,
    iconTint: Color,
    label: String,
    labelColor: Color,
    testTag: String,
    enabled: Boolean,
    trailing: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .clip(RoundedCornerShape(14.dp))
            .testTag(testTag)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp),
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = labelColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(8.dp))
        trailing()
    }
}
