package zero.friends.gostopcalculator.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import zero.friends.domain.model.Player
import zero.friends.gostopcalculator.shared.generated.resources.Res
import zero.friends.gostopcalculator.shared.generated.resources.duplicate_player_name_alert
import zero.friends.gostopcalculator.shared.generated.resources.edit
import zero.friends.gostopcalculator.shared.generated.resources.edit_name
import zero.friends.gostopcalculator.shared.generated.resources.over_player_name_alert
import zero.friends.gostopcalculator.ui.common.GoStopButton
import zero.friends.gostopcalculator.ui.common.GoStopOutLinedTextField
import zero.friends.gostopcalculator.ui.precondition.PlayerViewModel

private const val MAX_PLAYER_NAME_LENGTH = 12

@Composable
fun NameEditDialog(
    viewModel: PlayerViewModel,
    player: Player,
    onClose: () -> Unit = {}
) {
    var editPlayerName by remember {
        mutableStateOf(TextFieldValue(player.name, selection = TextRange(player.name.length)))
    }

    var error by remember {
        mutableStateOf("")
    }

    val duplicateNameAlert = stringResource(Res.string.duplicate_player_name_alert)
    val overPlayerNameAlert = stringResource(Res.string.over_player_name_alert)

    val editPlayer: () -> Unit = {
        runCatching {
            viewModel.editPlayer(player, editPlayerName.text)
        }.onFailure {
            error = duplicateNameAlert
        }.onSuccess {
            onClose()
        }
    }

    AlertDialog(
        onDismissRequest = {
            error = ""
            onClose()
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(Res.string.edit_name),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.padding(bottom = 40.dp))
                GoStopOutLinedTextField(
                    text = editPlayerName,
                    hint = player.name,
                    color = Color.Black,
                    onValueChange = {
                        if (it.text.length <= MAX_PLAYER_NAME_LENGTH) {
                            editPlayerName = it
                            error = ""
                        } else {
                            error = overPlayerNameAlert
                        }
                    },
                    error = error.ifEmpty { null },
                    showKeyboard = true,
                    onDone = editPlayer
                )
            }

        },
        confirmButton = {
            GoStopButton(
                text = stringResource(Res.string.edit),
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
                onClick = editPlayer
            )
        },
        shape = RoundedCornerShape(16.dp),
        contentColor = Color.Black
    )
}