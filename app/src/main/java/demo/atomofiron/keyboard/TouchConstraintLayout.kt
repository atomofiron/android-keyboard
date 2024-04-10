package demo.atomofiron.keyboard

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.math.abs
import kotlin.math.roundToInt

class TouchConstraintLayout : ConstraintLayout {

    private var tracker = VelocityTracker.obtain()
    private var tracking = false
    private var ignoring = false
    private var prevX = 0f
    private var prevY = 0f
    private var prevT = 0L

    lateinit var controller: InsetsController

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        tracker.recycle()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                tracker.addMovement(event)
                ignoring = false
                tracking = false
                prevX = event.x
                prevY = event.y
                prevT = System.currentTimeMillis()
            }
            MotionEvent.ACTION_MOVE -> {
                tracker.addMovement(event)
                when {
                    ignoring -> Unit
                    tracking -> move(event)
                    System.currentTimeMillis() - prevT < 100 -> Unit
                    abs(event.x - prevX) >= abs(event.y - prevY) -> ignoring = true
                    start(event) -> {
                        event.action = MotionEvent.ACTION_CANCEL
                        super.dispatchTouchEvent(event)
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                tracker.addMovement(event)
                tracker.computeCurrentVelocity(100)
                val shown = when {
                    tracker.yVelocity < -10 -> true
                    tracker.yVelocity > 10 -> false
                    else -> null
                }
                controller.stop(shown)
                tracker.clear()
            }
        }
        if (!tracking) super.dispatchTouchEvent(event)
        return true
    }

    private fun start(event: MotionEvent): Boolean {
        prevY = event.y
        tracking = controller.start()
        ignoring = !tracking
        return tracking
    }

    private fun move(event: MotionEvent) {
        val dy = event.y - prevY
        prevY = event.y
        controller.move(dy.roundToInt())
    }
}