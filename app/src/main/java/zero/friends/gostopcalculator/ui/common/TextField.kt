package zero.friends.gostopcalculator.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import zero.friends.gostopcalculator.R

@Composable
fun GoStopOutLinedTextField(
    initialText: String,
    hint: String,
    color: Color = colorResource(id = R.color.gray),
    modifier: Modifier = Modifier,
    onValueChane: (TextFieldValue) -> Unit,
    error: String? = null,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    val inputText = remember {
        mutableStateOf(TextFieldValue(initialText))
    }

    Column {
        OutlinedTextField(
            value = inputText.value,
            onValueChange = {
                inputText.value = it
                onValueChane(it)
            },
            modifier = modifier
                .fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = color,
                focusedBorderColor = color,
                cursorColor = colorResource(id = R.color.nero)
            ),
            placeholder = { Text(text = hint, color = color) },
            textStyle = TextStyle(fontSize = 16.sp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = keyboardActions
        )
        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
            )
        }
    }

}

@Composable
fun NumberTextField(modifier: Modifier = Modifier, endText: String, onValueChane: (TextFieldValue) -> Unit = {}) {
    val textFieldValue = remember {
        mutableStateOf(TextFieldValue("0"))
    }
    Box(
        modifier = modifier.background(colorResource(id = R.color.white))
    ) {
        TextField(
            value = textFieldValue.value,
            onValueChange = {
                textFieldValue.value = it
                onValueChane(it)
            },
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = colorResource(id = R.color.white),
                cursorColor = colorResource(id = R.color.black),
                focusedIndicatorColor = colorResource(id = R.color.ford_gray)
            ),
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.padding(end = 3.dp)
        )
        Text(
            text = endText,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GoStopOutLinedTextFieldPreView() {
    GoStopOutLinedTextField("ZeroWorld!!", "hint", onValueChane = {})
}

@Preview
@Composable
fun NumberTextFieldPreView() {
    NumberTextField(endText = "원")
}