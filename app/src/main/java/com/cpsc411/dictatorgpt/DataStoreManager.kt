package com.cpsc411.dictatorgpt

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEmpty

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "dictator_data")

class DataStoreManager(private val context: Context) {
    suspend fun getEvents(id: Preferences.Key<String>): String? {
        return context.dataStore.data.map { prefs ->
            prefs[id]
        }.firstOrNull()
    }

    suspend fun saveEvents(id: Preferences.Key<String>, data: String) {
        context.dataStore.edit { prefs -> prefs[id] = data }
    }

    suspend fun getReminders(id: Preferences.Key<String>): String? {
        return context.dataStore.data.map { prefs ->
            prefs[id]
        }.firstOrNull()
    }

    suspend fun saveReminders(id: Preferences.Key<String>, data: String) {
        context.dataStore.edit { prefs -> prefs[id] = data }
    }
}