package zero.friends.gostopcalculator.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import zero.friends.gostopcalculator.theme.Gray
import zero.friends.gostopcalculator.theme.Nero
import zero.friends.gostopcalculator.theme.OrangeyRed

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GoStopOutLinedTextField(
    modifier: Modifier = Modifier,
    text: TextFieldValue,
    hint: String,
    color: Color = Gray,
    onValueChange: (TextFieldValue) -> Unit,
    error: String? = null,
    showKeyboard: Boolean = false,
    onDone: () -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    Column {
        OutlinedTextField(
            value = text,
            onValueChange = onValueChange,
            modifier = modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            singleLine = true,
            maxLines = 1,
            shape = RoundedCornerShape(18.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = color,
                focusedBorderColor = color,
                cursorColor = Nero
            ),
            placeholder = { Text(text = hint, color = Gray.copy(alpha = 0.9f)) },
            textStyle = TextStyle(fontSize = 16.sp, color = Nero),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
                keyboardController?.hide()
                onDone()
            })
        )
        if (error != null) {
            Text(
                modifier = Modifier.padding(top = 6.dp),
                text = error,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
            )
        }
    }

    LaunchedEffect(Unit) {
        if (showKeyboard) {
            focusRequester.requestFocus()
            delay(100)
            keyboardController?.show()
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NumberTextField(
    modifier: Modifier = Modifier,
    text: String = "",
    endText: String,
    interactionSource: MutableInteractionSource? = null,
    isEnable: Boolean = true,
    hintColor: Color = Nero,
    onValueChange: (Long) -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val textColor = if (isEnable) Nero else hintColor
    val resolvedInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val displayText = text.takeUnless { it == "0" } ?: ""

    Row(
        modifier = modifier
            .background(
                color = Color.LightGray,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 10.dp),
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
                    color = hintColor.copy(alpha = 0.4f),
                    fontSize = 16.sp,
                    textAlign = TextAlign.End
                )
            }
            BasicTextField(
                value = displayText,
                onValueChange = { input ->
                    onValueChange(input.toLongOrNull() ?: 0L)
                },
                enabled = isEnable,
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.End,
                    color = textColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }),
                modifier = Modifier.fillMaxWidth(),
                interactionSource = resolvedInteractionSource
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = endText,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TitleOutlinedTextField(
    title: String,
    text: TextFieldValue,
    hint: String,
    modifier: Modifier = Modifier,
    onValueChange: (TextFieldValue) -> Unit,
) {
    Column(modifier = modifier) {
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Nero, modifier = Modifier.padding(bottom = 4.dp))
        GoStopOutLinedTextField(text = text, hint = hint, onValueChange = onValueChange)
    }
}
