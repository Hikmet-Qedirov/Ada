package com.solidict.ada.util

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private const val TAG = "TestTokenPreferences"

class TokenPreferences
@Inject
constructor(
    private val context: Context,
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

    private val tokenCodeTag = stringPreferencesKey(TOKEN_CODE_TAG)

    suspend fun readToken(): String? {
        val preferences = context.dataStore.data.first()
        Log.d(TAG, "${preferences[tokenCodeTag]}")
        return preferences[tokenCodeTag]
    }

    suspend fun saveToken(value: String) {
        context.dataStore.edit { token ->
            token[tokenCodeTag] = value
        }
    }

    companion object {
        private const val TOKEN_CODE_TAG = "com.solidict.ada.TOKEN_CODE_TAG"
    }

}