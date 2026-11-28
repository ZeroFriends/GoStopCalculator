package zero.friends.gostopcalculator.ui.common

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId

@OptIn(ExperimentalComposeUiApi::class)
actual fun Modifier.enableTestTagsAsResourceId(): Modifier =
    this.semantics { testTagsAsResourceId = true }
