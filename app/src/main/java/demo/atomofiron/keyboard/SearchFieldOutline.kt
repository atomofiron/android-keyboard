package demo.atomofiron.keyboard

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider

class SearchFieldOutline(
    var width: Int,
    private val radius: Float,
) : ViewOutlineProvider() {
    override fun getOutline(view: View, outline: Outline) = outline.setRoundRect(0, 0, width, view.height, radius)
}