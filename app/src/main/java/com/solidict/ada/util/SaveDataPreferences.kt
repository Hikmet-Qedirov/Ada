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

class SaveDataPreferences
@Inject
constructor(
    private val context: Context,
) {
    private val Context.token: DataStore<Preferences> by preferencesDataStore(name = "token")
    private val Context.videoPart: DataStore<Preferences> by preferencesDataStore(name = "videoUri")
    private val Context.videoId: DataStore<Preferences> by preferencesDataStore(name = "videoId")

    private val tokenCodeTag = stringPreferencesKey(TOKEN_CODE_TAG)
    private val videoPartTag = stringPreferencesKey(VIDEO_URI_TAG)
    private val videoIdTag = stringPreferencesKey(VIDEO_ID_TAG)

    suspend fun saveVideoId(value: String) {
        context.videoId.edit { videoId ->
            videoId[videoIdTag] = value
        }
        Log.d(TAG, "SaveDataPreferences saveVideoId:: ${context.videoId.data.first()}")
    }

    suspend fun readVideoId(): String? {
        val preferences = context.videoId.data.first()
        Log.d(TAG, "SaveDataPreferences readVideoId :: ${preferences[videoIdTag]}")
        return preferences[videoIdTag]
    }

    suspend fun clearVideoId() {
        context.videoId.edit { videoId ->
            videoId[videoIdTag] = ""
        }
    }

    suspend fun saveVideoUri(value: String) {
        clearVideoUri()
        context.videoPart.edit { videoId ->
            videoId[videoPartTag] = value
        }
        Log.d(TAG, "SaveDataPreferences saveVideoUri:: ${context.videoPart.data.first()}")
    }

    suspend fun readVideoUri(): String? {
        val preferences = context.videoPart.data.first()
        Log.d(TAG, "SaveDataPreferences saveVideoUri :: ${preferences[videoPartTag]}")
        return preferences[videoPartTag]
    }

    suspend fun clearVideoUri() {
        context.videoPart.edit { videoPart ->
            videoPart[videoPartTag] = ""
        }
    }

    suspend fun saveToken(value: String) {
        context.token.edit { token ->
            token[tokenCodeTag] = value
        }
        Log.d(TAG, "SaveDataPreferences saveToken:: ${context.token.data.first()}")
    }

    suspend fun readToken(): String? {
        val preferences = context.token.data.first()
        Log.d(TAG, "SaveDataPreferences readToken :: ${preferences[tokenCodeTag]}")
        return preferences[tokenCodeTag]
    }

    companion object {
        private const val TOKEN_CODE_TAG = "com.solidict.ada.TOKEN_CODE_TAG"
        private const val VIDEO_ID_TAG = "com.solidict.ada.VIDEO_ID_TAG"
        private const val VIDEO_URI_TAG = "com.solidict.ada.VIDEO_URI_TAG"
    }

}