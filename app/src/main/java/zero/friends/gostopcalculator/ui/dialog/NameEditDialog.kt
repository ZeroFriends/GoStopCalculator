package zero.friends.gostopcalculator.ui.dialog

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
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
    viewModel: NameEditDialogViewModel = hiltViewModel(),
    player: Player,
    onClose: () -> Unit = {}
) {
    val context = LocalContext.current
    val dialogState by viewModel.getDialogState().collectAsState()
    val editPlayerName = remember {
        val playerName = dialogState.editPlayer?.name ?: ""
        mutableStateOf(TextFieldValue(playerName, selection = TextRange(playerName.length)))
    }

    val editPlayer = {
        viewModel.editPlayer(player, editPlayerName.value.text, onSuccess = onClose)
        viewModel.clearState()
    }

    AlertDialog(
        onDismissRequest = {
            viewModel.clearState()
            onClose()
        },
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
                    hint = player.name,
                    color = colorResource(id = R.color.black),
                    onValueChange = {
                        if (it.text.length < 8) editPlayerName.value = it
                        else Toast.makeText(
                            context,
                            context.getString(R.string.over_player_name_alert),
                            Toast.LENGTH_SHORT
                        ).show()

                    },
                    error = dialogState.error?.message,
                    showKeyboard = true,
                    onDone = editPlayer
                )
            }

        },
        confirmButton = {
            GoStopButton(
                text = stringResource(R.string.edit),
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
                onClick = editPlayer
            )
        },
        shape = RoundedCornerShape(16.dp),
        contentColor = colorResource(id = R.color.black)
    )
}