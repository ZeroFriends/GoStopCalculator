package zero.friends.gostopcalculator.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import zero.friends.domain.model.Player
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.ui.common.GoStopButton
import zero.friends.gostopcalculator.ui.common.GoStopOutLinedTextField

@Composable
fun NameEditDialog(
    dialogState: MutableState<Player?>,
    viewModel: NameEditDialogViewModel = hiltViewModel(),
) {
    val inputText = remember {
        mutableStateOf(TextFieldValue(dialogState.value?.name ?: ""))
    }

    AlertDialog(
        onDismissRequest = { dialogState.value = null },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.edit_name),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.black),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.padding(bottom = 40.dp))
                GoStopOutLinedTextField(
                    inputText = inputText,
                    onValueChange = { inputText.value = it },
                    hint = "",
                    color = colorResource(id = R.color.black)
                )
            }

        },
        confirmButton = {
            GoStopButton(stringResource(R.string.edit), modifier = Modifier) {
                val player = dialogState.value
                requireNotNull(player)
                viewModel.editPlayer(player, player.copy(name = inputText.value.text))
                dialogState.value = null
            }
        },
        shape = RoundedCornerShape(16.dp),
    )
}