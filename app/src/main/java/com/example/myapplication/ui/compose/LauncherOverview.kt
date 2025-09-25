import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.data.local.util.Utils
import com.example.myapplication.ui.compose.AppDrawer
import com.example.myapplication.ui.compose.ShortcutScreen
import com.example.myapplication.ui.main.LauncherViewModel
import kotlinx.coroutines.flow.map


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LauncherOverview(viewModel: LauncherViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val apps by viewModel.installedApps
        .map { it.sortedBy { app -> app.packageName } } // normalize order
        .collectAsStateWithLifecycle(emptyList())
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    val filteredApps = remember(searchQuery.text, apps) {
        if (searchQuery.text.isBlank()) apps
        else apps.filter { it.label.startsWith(searchQuery.text, ignoreCase = true) }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }

    // App Drawer
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            AppDrawer(filteredApps, viewModel, { it ->
                searchQuery = it
            }) { Utils.launchApp(context, it.packageName) }

        }
    }
        if (!filteredApps.isEmpty()) {
            ShortcutScreen(viewModel, apps) {
                showSheet = true
            }
        }

}




