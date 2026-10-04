package com.example

import android.app.Application
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppRepository(application)
    
    val installedApps: List<ApplicationInfo> = application.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        .filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 && it.packageName != application.packageName }

    private val _selectedPackages = MutableStateFlow(repository.getSelectedPackages())
    val selectedPackages: StateFlow<Set<String>> = _selectedPackages

    fun triggerFreeze() {
        val intent = Intent(FreezerService.ACTION_MANUAL_FREEZE)
        getApplication<android.app.Application>().sendBroadcast(intent)
    }

    fun toggleAppSelection(packageName: String) {
        val current = _selectedPackages.value.toMutableSet()
        if (current.contains(packageName)) {
            current.remove(packageName)
        } else {
            current.add(packageName)
            // Trigger freeze immediately for the newly selected app
            val am = getApplication<android.app.Application>().getSystemService(android.content.Context.ACTIVITY_SERVICE) as android.app.ActivityManager
            try {
                am.killBackgroundProcesses(packageName)
            } catch (e: Exception) {
                // Skip if error occurs
            }
        }
        _selectedPackages.value = current
        repository.setSelectedPackages(current)
    }
}
