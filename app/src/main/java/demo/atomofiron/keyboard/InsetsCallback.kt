package demo.atomofiron.keyboard

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.inputmethod.InputMethodManager
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsAnimationCompat.BoundsCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type
import androidx.core.view.isVisible
import demo.atomofiron.keyboard.databinding.ActivityMainBinding
import kotlin.math.roundToInt

class InsetsCallback(
    private val binding: ActivityMainBinding,
) : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_STOP) {

    private val resources = binding.root.resources
    private val inputManager = binding.root.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    private val buttonWidth = resources.getDimensionPixelSize(R.dimen.button_width)
    private val cornerRadius = resources.getDimension(R.dimen.corner_radius)
    private val buttonMargin = resources.getDimensionPixelSize(R.dimen.button_margin)
    private val marginBetween = resources.getDimensionPixelSize(R.dimen.half)
    private val common = resources.getDimensionPixelSize(R.dimen.button_margin)

    private val outline = SearchFieldOutline(buttonWidth, cornerRadius)

    private var inProgress = false
    private var imeHeight = 0
    var toShown = true

    init {
        binding.searchField.run {
            outlineProvider = outline
            clipToOutline = true
            val colors = ColorStateList(
                arrayOf(intArrayOf(-android.R.attr.state_enabled), intArrayOf()),
                intArrayOf(Color.TRANSPARENT, textColors.defaultColor),
            )
            setTextColor(colors)
        }
    }

    override fun onStart(animation: WindowInsetsAnimationCompat, bounds: BoundsCompat): BoundsCompat {
        poop("onStart")
        inProgress = true
        imeHeight = bounds.upperBound.bottom
        return super.onStart(animation, bounds)
    }

    override fun onProgress(
        insets: WindowInsetsCompat,
        runningAnimations: List<WindowInsetsAnimationCompat>,
    ): WindowInsetsCompat = binding.run {
        val anim = runningAnimations.find { it.typeMask == Type.ime() }
        anim ?: return insets
        val ime = insets.getInsets(Type.ime())
        render(ime.bottom)
        return insets
    }

    override fun onEnd(animation: WindowInsetsAnimationCompat) = binding.run {
        poop("onEnd")
        super.onEnd(animation)
        inProgress = false
        searchBtn.isVisible = !toShown
        searchField.isEnabled = toShown
        // when app was minimized
        render(if (toShown) imeHeight else 0)
    }

    fun show() = binding.run {
        searchBtn.isVisible = false
        searchField.isEnabled = true
        searchField.requestFocus()
        inputManager.showSoftInput(searchField, 0)
    }

    fun setInProgress(value: Boolean) {
        poop("setInProgress $value")
        inProgress = value
    }

    fun setTarget(imeBottom: Int) {
        poop("setTarget $imeBottom")
        toShown = imeBottom > 0
    }

    private fun ActivityMainBinding.render(imeBottom: Int) {
        val progress = imeBottom.toFloat() / imeHeight
        val buttonsStartOffset = buttonMargin + buttonWidth + marginBetween
        val buttonsEndOffset = root.width - buttonsStartOffset
        // don't change layout params to don't trigger measuring and layouting, only redrawing (translation and outline)!
        outline.width = buttonWidth + ((root.width - buttonWidth - buttonMargin * 2) * progress).roundToInt()
        searchField.invalidateOutline()
        buttons.translationX = buttonsEndOffset * progress
        bottomUi.translationY = -(imeBottom - navigation.height).coerceAtLeast(0).toFloat()
        val headerTranslation = header.run { height - paddingBottom - common } * progress
        header.translationY = -headerTranslation
        headerContent.translationY = headerTranslation - headerContent.height + common
    }
}