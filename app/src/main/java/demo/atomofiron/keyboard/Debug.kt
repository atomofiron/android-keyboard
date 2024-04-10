package demo.atomofiron.keyboard

import android.util.Log

fun Any.poop(s: String) {
    Log.e("yunogasai", "[${this::class.simpleName}] $s")
}
