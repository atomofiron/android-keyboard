package demo.atomofiron.keyboard

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.animation.DecelerateInterpolator
import androidx.core.graphics.Insets
import androidx.core.view.WindowInsetsAnimationControlListenerCompat
import androidx.core.view.WindowInsetsAnimationControllerCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class InsetsController(
    private val controllerCompat: WindowInsetsControllerCompat,
    private val callback: InsetsCallback,
) :
    WindowInsetsAnimationControlListenerCompat,
    ValueAnimator.AnimatorUpdateListener,
    Animator.AnimatorListener {

    private var controller: WindowInsetsAnimationControllerCompat? = null
    private var animator: ValueAnimator? = null

    override fun onReady(controller: WindowInsetsAnimationControllerCompat, types: Int) {
        callback.setInProgress(true)
        poop("onReady")
        this.controller = controller
    }

    override fun onFinished(controller: WindowInsetsAnimationControllerCompat) {
        poop("onFinished")
        callback.setInProgress(false)
        this.controller = null
    }

    override fun onCancelled(controller: WindowInsetsAnimationControllerCompat?) {
        poop("onCancelled")
        callback.setInProgress(false)
        this.controller = null
    }

    fun start(): Boolean {
        if (!callback.toShown) {
            return false
        }
        animator?.cancel()
        animator = null
        callback.setInProgress(true)
        controllerCompat.controlWindowInsetsAnimation(WindowInsetsCompat.Type.ime(), -1, null, null, this)
        return true
    }

    fun move(dy: Int) {
        val controller = controller ?: return
        val bottom = controller.currentInsets.bottom - dy
        val new = Insets.of(0, 0, 0, bottom)
        val fraction = bottom.toFloat() / controller.shownStateInsets.bottom
        controller.setInsetsAndAlpha(new, 1f, fraction)
    }

    fun stop(shown: Boolean?) {
        val controller = controller ?: return
        when {
            controller.shownStateInsets.bottom - controller.currentInsets.bottom <= 2 -> return finish(true)
            controller.currentInsets.bottom - controller.hiddenStateInsets.bottom <= 2 -> return finish(false)
        }
        val toShown = shown ?: ((controller.currentInsets.bottom.toFloat() / controller.shownStateInsets.bottom) > 0.5f)
        val target = when {
            toShown -> controller.shownStateInsets.bottom
            else -> controller.hiddenStateInsets.bottom
        }
        callback.toShown = toShown
        animator = ValueAnimator.ofInt(controller.currentInsets.bottom, target).apply {
            addUpdateListener(this@InsetsController)
            addListener(this@InsetsController)
            duration = 500
            interpolator = DecelerateInterpolator(4f)
            start()
        }
    }

    override fun onAnimationUpdate(animation: ValueAnimator) {
        val controller = controller ?: return
        val bottom = animation.animatedValue as Int
        val new = Insets.of(0, 0, 0, bottom)
        val fraction = bottom.toFloat() / controller.shownStateInsets.bottom
        controller.setInsetsAndAlpha(new, 1f, fraction)
    }

    override fun onAnimationEnd(animation: Animator) {
        val animator = animator ?: return
        animator.removeAllUpdateListeners()
        animator.removeAllListeners()
        finish(show = animator.animatedValue != 0)
    }

    override fun onAnimationStart(animation: Animator) = Unit

    override fun onAnimationCancel(animation: Animator) = Unit

    override fun onAnimationRepeat(animation: Animator) = Unit

    private fun finish(show: Boolean) {
        callback.toShown = show
        controller?.finish(show)
    }
}