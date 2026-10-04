package com.example

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startService(Intent(this, FreezerService::class.java))
        
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppList(viewModel, Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun AppList(viewModel: AppViewModel, modifier: Modifier = Modifier) {
    val selectedPackages by viewModel.selectedPackages.collectAsState()
    val apps = viewModel.installedApps

    val frozenApps = apps.filter { selectedPackages.contains(it.packageName) }
    val activeApps = apps.filter { !selectedPackages.contains(it.packageName) }

    LazyColumn(modifier = modifier.fillMaxSize().padding(16.dp)) {
        item { Text("Ứng dụng hoạt động", fontWeight = FontWeight.Bold) }
        items(activeApps) { app ->
            AppItem(appInfo = app, isFrozen = false, onToggle = { viewModel.toggleAppSelection(app.packageName) })
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item { Text("Ứng dụng đóng băng", fontWeight = FontWeight.Bold) }
        items(frozenApps) { app ->
            AppItem(appInfo = app, isFrozen = true, onToggle = { viewModel.toggleAppSelection(app.packageName) })
        }
    }
}

@Composable
fun AppItem(appInfo: ApplicationInfo, isFrozen: Boolean, onToggle: () -> Unit) {
    val context = LocalContext.current
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = appInfo.loadLabel(context.packageManager).toString())
        }
        Switch(checked = isFrozen, onCheckedChange = { onToggle() })
    }
}
