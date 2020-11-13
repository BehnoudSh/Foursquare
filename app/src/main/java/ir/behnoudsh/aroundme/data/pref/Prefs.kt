package ir.behnoudsh.aroundme.data.pref

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class Prefs(context: Context) {

    companion object {
        private const val PREFS_FILENAME = "aroundme_prefs"
        private const val KEY_MY_LOCATION = "myLocation"
        private const val KEY_OFFSET = "previousOffset"

    }

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

    var myLocation: String
        get() = sharedPrefs.getString(KEY_MY_LOCATION, "") ?: ""
        set(value) = sharedPrefs.edit { putString(KEY_MY_LOCATION, value) }

    var previousOffset: Int
        get() = sharedPrefs.getInt(KEY_OFFSET, 0) ?: 0
        set(value) = sharedPrefs.edit { putInt(KEY_OFFSET, value) }


}
