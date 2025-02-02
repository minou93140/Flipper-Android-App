package com.flipperdevices.infrared.editor.compose.screen

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.flipperdevices.infrared.editor.model.InfraredEditorState
import com.flipperdevices.infrared.editor.viewmodel.InfraredEditorViewModel
import com.flipperdevices.keyscreen.shared.screen.ComposableKeyScreenError
import com.flipperdevices.keyscreen.shared.screen.ComposableKeyScreenLoading
import tangle.viewmodel.compose.tangleViewModel

@Composable
internal fun ComposableInfraredEditorScreen(
    onBack: () -> Unit,
    viewModel: InfraredEditorViewModel = tangleViewModel()
) {
    val keyState by viewModel.getKeyState().collectAsState()
    val dialogState by viewModel.getDialogState().collectAsState()

    BackHandler {
        viewModel.processCancel(keyState, onBack)
    }

    when (val localState = keyState) {
        InfraredEditorState.InProgress -> ComposableKeyScreenLoading()
        is InfraredEditorState.Error -> ComposableKeyScreenError(
            text = stringResource(id = localState.reason)
        )
        is InfraredEditorState.Ready ->
            ComposableInfraredEditorScreenReady(
                keyState = localState,
                dialogState = dialogState,
                onDoNotSave = onBack,
                onDismissDialog = viewModel::onDismissDialog,
                onCancel = {
                    viewModel.processCancel(keyState, onBack)
                },
                onSave = {
                    viewModel.processSave(currentState = localState, onBack)
                },
                onChangeName = { index, value ->
                    viewModel.editRemoteName(
                        currentState = localState,
                        index = index,
                        source = value
                    )
                },
                onDelete = {
                    viewModel.processDeleteRemote(
                        currentState = localState,
                        index = it
                    )
                },
                onEditOrder = { from, to ->
                    viewModel.processEditOrder(
                        currentState = localState,
                        from = from,
                        to = to
                    )
                },
                onChangeIndexEditor = {
                    viewModel.processChangeIndexEditor(localState, it)
                }
            )
    }
}
