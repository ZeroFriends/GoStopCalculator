package zero.friends.gostopcalculator.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import zero.friends.gostopcalculator.R
import zero.friends.gostopcalculator.ui.common.GoStopButton
import zero.friends.gostopcalculator.ui.common.GoStopOutLinedTextField
import zero.friends.gostopcalculator.ui.precondition.PlayerViewModel

@Composable
fun NameEditDialog(
    viewModel: PlayerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.getUiState().collectAsState()
    val editPlayerName = remember {
        val playerName = uiState.dialogState.editPlayer?.name ?: ""
        mutableStateOf(TextFieldValue(playerName, selection = TextRange(playerName.length)))
    }
    if (!uiState.dialogState.openDialog) {
        viewModel.closeDialog()
    }

    val editPlayer = {
        val player = uiState.dialogState.editPlayer
        requireNotNull(player)
        viewModel.editPlayer(player, player.copy(name = editPlayerName.value.text))
    }

    AlertDialog(
        onDismissRequest = { viewModel.closeDialog() },
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
                    text = editPlayerName.value,
                    hint = "",
                    color = colorResource(id = R.color.black),
                    onValueChange = {
                        editPlayerName.value = it
                    },
                    error = uiState.dialogState.error?.message,
                    showKeyboard = true,
                    onDone = editPlayer
                )
            }

        },
        confirmButton = {
            GoStopButton(
                text = stringResource(R.string.edit),
                modifier = Modifier.padding(horizontal = 12.dp),
                onClick = editPlayer
            )
        },
        shape = RoundedCornerShape(16.dp),
        contentColor = colorResource(id = R.color.black)
    )
}