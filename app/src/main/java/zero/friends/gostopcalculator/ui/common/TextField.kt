package zero.friends.gostopcalculator.ui.common

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import zero.friends.gostopcalculator.R

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GoStopOutLinedTextField(
    text: TextFieldValue,
    hint: String,
    color: Color = colorResource(id = R.color.gray),
    modifier: Modifier = Modifier,
    onValueChange: (TextFieldValue) -> Unit,
    error: String? = null,
    showKeyboard: Boolean = false,
    onDone: () -> Unit = {},
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val (focusRequester) = remember {
        FocusRequester.createRefs()
    }

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
                cursorColor = colorResource(id = R.color.nero)
            ),
            placeholder = { Text(text = hint, color = colorResource(id = R.color.gray).copy(alpha = 0.9f)) },
            textStyle = TextStyle(fontSize = 16.sp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide()
                onDone()
            })
        )
        if (error != null) {
            Text(
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
    text: String = "",
    modifier: Modifier = Modifier,
    endText: String,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    unFocusDeleteMode: Boolean = false,
    isEnable: Boolean = true,
    hintColor: Color = colorResource(id = R.color.nero),
    onValueChane: (Int) -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focus by interactionSource.collectIsFocusedAsState()
    var inputText by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(focus) {
        if (unFocusDeleteMode && !focus) {
            inputText = TextFieldValue(text)
            onValueChane(0)
        }
    }

    Box(
        modifier = modifier
    ) {
        TextField(
            value = inputText,
            onValueChange = {
                if (Regex("[0-9]+").matches(it.text)) {
                    inputText = it
                    onValueChane(it.text.toInt())
                } else if (it.text.isBlank()) {
                    inputText = it
                    onValueChane(0)
                }
            },
            enabled = isEnable,
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = colorResource(id = android.R.color.transparent),
                cursorColor = colorResource(id = R.color.black),
                focusedIndicatorColor = colorResource(id = R.color.orangey_red)
            ),
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide()
            }),
            modifier = Modifier.padding(end = 3.dp),
            interactionSource = interactionSource,
            placeholder = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    Text(text = "0", textAlign = TextAlign.End, color = hintColor)
                }
            }
        )
        Text(
            text = endText,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterEnd),
            color = hintColor
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
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))
        GoStopOutLinedTextField(text, hint, onValueChange = onValueChange)
    }
}

@Preview
@Composable
private fun TitleOutlinedTextField() {
    TitleOutlinedTextField(title = "title", text = TextFieldValue("text"), hint = "hint", onValueChange = {})
}

@Preview(showBackground = true)
@Composable
private fun GoStopOutLinedTextFieldPreView() {
    GoStopOutLinedTextField(TextFieldValue("ZeroWorld!!"), "hint", onValueChange = {})
}

@Preview
@Composable
private fun NumberTextFieldPreView() {
    NumberTextField(endText = "원")
}
