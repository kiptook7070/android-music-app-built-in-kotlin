
package code.sn.monkey.retromusic.extensions

import android.content.SharedPreferences

fun SharedPreferences.getStringOrDefault(key: String, default: String): String {
    return getString(key, default) ?: default
}
