package cz.tom.wayne.prefs

import android.content.Context

@Suppress("TooManyFunctions")
class SharedPrefsImpl(private val context: Context) {

    private val preferences by lazy { context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE) }

    private fun saveBoolean(key: String, value: Boolean) {
        //logger.setBoolean(key, value)
        preferences.edit().putBoolean(key, value).apply()
    }

    private fun getBoolean(key: String, defValue: Boolean = false): Boolean {
        val value = preferences.getBoolean(key, defValue)
        //logger.d("Getting prefs with key: $key and value: $value")
        return value
    }

    private fun saveString(key: String, value: String?) {
        //logger.setString(key, value ?: "null")
        preferences.edit().putString(key, value).apply()
    }

    private fun getInt(key: String, defValue: Int): Int {
        val value = preferences.getInt(key, defValue)
        //logger.d("Getting prefs with key: $key and value: $value")
        return value
    }

    private fun saveInt(key: String, value: Int) {
        //logger.setInt(key, value)
        preferences.edit().putInt(key, value).apply()
    }

    private fun getString(key: String, defaultValue: String? = null): String? {
        val value = preferences.getString(key, defaultValue)
        //logger.d("Getting prefs with key: $key and value: $value")
        return value
    }

    companion object {
        const val PREFERENCES_NAME = "defPrefs"
        const val NOTIFICATION_KEY = "notification_key"
        const val DEVICE_ID = "device_id"
        const val TEXTURES_DOWNLOADED = "textures_downloaded"
        const val WORLD_OBJECTS_ANDROID_DOWNLOADED = "hexa_items_textures_android_downloaded"
        const val SHOULD_SHOW_LEVEL_ONBOARDING = "should_show_level_onboarding"
        const val CAMPAIGN_CREATION_ONBOARDING = "campaign_cretaion_onboarding_done"
    }
}