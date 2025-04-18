package hook.tool

import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hook.fakewifi.R
import com.hook.fakewifi.ui.theme.primaryColor

const val INJECT_UI_TAG = "fake_wifi_config"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun injectUI(parent: ViewGroup) {
    Dialog(
        onDismissRequest = { (parent.parent as? ViewGroup)?.removeView(parent) }
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
        ) {
            TopAppBar(title = { Text("Fake WIFI") })
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Item("SSID", "ssid")
                    Item("BSSID", "bssid")
                    Item("MAC", "mac")
                }
            }
        }
    }
}


@Composable
private fun Item(title: String, saveKey: String) {
    val context = LocalContext.current

    var editingValue by remember { mutableStateOf(getValue(saveKey, context)) }
    var savedValue by remember { mutableStateOf(getValue(saveKey, context)) }

    val changed by remember {
        derivedStateOf {
            savedValue.trim() != editingValue.trim()
        }
    }

    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        TextField(
            editingValue.trim(),
            label = { Text(title) },
            onValueChange = { editingValue = it.trim() },
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(16.dp))
        Image(
            painterResource(R.drawable.save), null,
            contentScale = ContentScale.Inside,
            colorFilter = ColorFilter.tint(if (changed) primaryColor else primaryColor.copy(0.5f)),
            modifier = Modifier
                .size(48.dp)
                .shadow(12.dp, RoundedCornerShape(8.dp), clip = false)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .clickable {
                    if (changed) {
                        savedValue = editingValue.trim()
                        saveValue(saveKey, editingValue.trim(), context)
                        Toast.makeText(context, "${title} Saved", Toast.LENGTH_SHORT).show()
                    }
                }
        )
    }
}