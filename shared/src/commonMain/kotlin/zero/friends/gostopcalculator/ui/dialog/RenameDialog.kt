package zero.friends.gostopcalculator.ui.dialog

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.cancel
import zero.friends.gostopcalculator.shared.generated.resources.clear_input_cd
import zero.friends.gostopcalculator.shared.generated.resources.ic_topbar_close
import zero.friends.gostopcalculator.ui.board.score.common.ScoreCardBorder
import zero.friends.gostopcalculator.ui.board.score.common.ScoreDisabledBadgeBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreMutedText
import zero.friends.gostopcalculator.ui.board.score.common.ScoreNeutralBtnBg
import zero.friends.gostopcalculator.ui.board.score.common.ScorePrimaryRed
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSoftPinkBg
import zero.friends.gostopcalculator.ui.board.score.common.ScoreSoftPinkBorder
import zero.friends.gostopcalculator.ui.board.score.common.ScoreTextDark
import zero.friends.gostopcalculator.ui.board.score.common.StatusPillBadge
import zero.friends.gostopcalculator.ui.common.enableTestTagsAsResourceId

/**
 * Shared centered modal dialog for renaming a Game or Round.
 */
@Composable
fun RenameDialog(
    title: String,
    badgeText: String? = null,
    initialValue: String,
    placeholder: String,
    helperText: String,
    maxLength: Int,
    allowBlank: Boolean,
    confirmText: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var fieldValue by rememberSaveable(initialValue, stateSaver = TextFieldValue.Saver) {
        val clipped = initialValue.take(maxLength)
        mutableStateOf(TextFieldValue(text = clipped, selection = TextRange(clipped.length)))
    }
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val isOverLength = fieldValue.text.length > maxLength
    val canSubmit = !isOverLength && (allowBlank || fieldValue.text.isNotBlank())

    LaunchedEffect(Unit) {
        withFrameMillis { }
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    val submit = {
        if (canSubmit) {
            keyboardController?.hide()
            onConfirm(fieldValue.text.trim().take(maxLength).trimEnd())
        }
    }

    val dismissWithKeyboardHide = {
        keyboardController?.hide()
        onDismiss()
    }

    Dialog(
        onDismissRequest = dismissWithKeyboardHide,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .enableTestTagsAsResourceId(),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .widthIn(max = 360.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                elevation = 8.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 22.dp, vertical = 22.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ScoreTextDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false),
                        )
                        if (!badgeText.isNullOrBlank()) {
                            StatusPillBadge(
                                text = badgeText,
                                backgroundColor = ScoreSoftPinkBg,
                                textColor = ScorePrimaryRed,
                                borderColor = ScoreSoftPinkBorder,
                                fontSize = 11.sp,
                                horizontalPadding = 8.dp,
                                verticalPadding = 3.dp,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 52.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFF8F9FA),
                        border = BorderStroke(
                            width = if (isFocused) 1.5.dp else 1.dp,
                            color = when {
                                isOverLength -> ScorePrimaryRed
                                isFocused -> ScoreTextDark
                                else -> ScoreCardBorder
                            },
                        ),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 14.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            BasicTextField(
                                value = fieldValue,
                                onValueChange = { incoming ->
                                    val sanitized = incoming.text.replace("\r", "").replace("\n", " ")
                                    fieldValue = if (incoming.composition != null || sanitized.length <= maxLength) {
                                        incoming.copy(text = sanitized)
                                    } else {
                                        val clipped = sanitized.take(maxLength)
                                        incoming.copy(
                                            text = clipped,
                                            selection = TextRange(
                                                incoming.selection.start.coerceIn(0, clipped.length),
                                                incoming.selection.end.coerceIn(0, clipped.length),
                                            ),
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 44.dp)
                                    .focusRequester(focusRequester)
                                    .onFocusChanged { isFocused = it.isFocused }
                                    .testTag("rename_text_input"),
                                singleLine = true,
                                textStyle = TextStyle(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ScoreTextDark,
                                ),
                                cursorBrush = SolidColor(ScorePrimaryRed),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = { submit() }),
                                decorationBox = { innerTextField ->
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.CenterStart,
                                    ) {
                                        if (fieldValue.text.isEmpty()) {
                                            Text(
                                                text = placeholder,
                                                fontSize = 15.sp,
                                                color = ScoreMutedText,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                            )
                                        }
                                        innerTextField()
                                    }
                                },
                            )

                            if (fieldValue.text.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .testTag("rename_clear_button")
                                        .clickable(role = Role.Button) {
                                            fieldValue = TextFieldValue(text = "", selection = TextRange.Zero)
                                        },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFE5E7EB)),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Icon(
                                            painter = painterResource(Res.drawable.ic_topbar_close),
                                            contentDescription = stringResource(Res.string.clear_input_cd),
                                            tint = ScoreMutedText,
                                            modifier = Modifier.size(14.dp),
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top,
                    ) {
                        Text(
                            text = helperText,
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp,
                            color = ScoreMutedText,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
                        )
                        Text(
                            text = "${fieldValue.text.length} / $maxLength",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isOverLength) ScorePrimaryRed else ScoreMutedText,
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .testTag("rename_cancel_button")
                                .clickable(role = Role.Button, onClick = dismissWithKeyboardHide),
                            shape = RoundedCornerShape(14.dp),
                            color = ScoreNeutralBtnBg,
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = stringResource(Res.string.cancel),
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ScoreTextDark,
                                )
                            }
                        }
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .testTag("rename_confirm_button")
                                .clickable(enabled = canSubmit, role = Role.Button, onClick = submit),
                            shape = RoundedCornerShape(14.dp),
                            color = if (canSubmit) ScorePrimaryRed else ScoreDisabledBadgeBg,
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = confirmText,
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (canSubmit) Color.White else ScoreMutedText,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
