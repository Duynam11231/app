package com.example

import android.content.Context
import android.content.SharedPreferences

class AppRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_freezer_prefs", Context.MODE_PRIVATE)

    fun getSelectedPackages(): Set<String> {
        return prefs.getStringSet("selected_packages", emptySet()) ?: emptySet()
    }

    fun setSelectedPackages(packages: Set<String>) {
        prefs.edit().putStringSet("selected_packages", packages).apply()
    }
}
