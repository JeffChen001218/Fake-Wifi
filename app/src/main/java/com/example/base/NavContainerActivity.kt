package com.hook.fakewifi

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hook.fakewifi.ui.theme.AppTheme
import com.hook.fakewifi.ui.theme.primaryColor
import hook.tool.getValue
import hook.tool.saveValue

class NavContainerActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(title = { Text(stringResource(R.string.app_name)) })
                    }
                ) { paddings ->
                    Column(
                        Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(paddings)
                            .padding(horizontal = 24.dp),
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
    }

    @Composable
    fun Item(title: String, saveKey: String) {
        var editingValue by remember { mutableStateOf(getValue(saveKey)) }
        var savedValue by remember { mutableStateOf(getValue(saveKey)) }

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
                            saveValue(saveKey, editingValue.trim())
                            Toast.makeText(this@NavContainerActivity, "${title} Saved", Toast.LENGTH_SHORT).show()
                        }
                    }
            )
        }
    }
}