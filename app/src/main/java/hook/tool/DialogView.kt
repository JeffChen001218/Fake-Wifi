package hook.tool

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

class DialogView(
    context: Context,
    child: View
) : FrameLayout(context) {

    init {
        setBackgroundColor(Color.parseColor("#66000000"))

        setOnClickListener {
            (parent as? ViewGroup)?.removeView(this)
        }

        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT,
        )

        setPadding(32.toPx().toInt(), 0, 32.toPx().toInt(), 0)
        addView(child.apply {
            background = ShapeDrawable(RoundRectShape(FloatArray(8) { 8.toPx() }, null, null)).apply {
                setTint(Color.WHITE)
            }
            val hPadding = 24.toPx().toInt()
            val vPadding = 16.toPx().toInt()
            setPadding(hPadding, vPadding, hPadding, vPadding)
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
            }
        })
    }
}

fun Number.toPx() = Resources.getSystem().displayMetrics.density * this.toFloat() + 0.5f