package hook.tool

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.children
import androidx.core.widget.doOnTextChanged

private const val UI_TAG = "inject_ui"

fun injectUI(activity: Activity) {
    val context = activity
    val parent = (activity.window.decorView as? ViewGroup) ?: return

    if (parent.children.any { it.tag == UI_TAG }) {
        return
    }

    val container = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
    }

    val titleView = TextView(context).apply {
        text = "Fake WIFI"
        textSize = 20f
        setTypeface(null, Typeface.BOLD)
        setPadding(0, 0, 0, 16)
    }
    container.addView(titleView)

    fun createItem(title: String, key: String): View {
        val row = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 24
            }
            gravity = Gravity.CENTER_VERTICAL
        }

        var savedValue = getValue(key, context)
        var editingText = savedValue

        val saveButton = Button(context).apply {
            isAllCaps = false
            text = "Save"
            setBtnEnable(savedValue != editingText)
        }

        val editText = EditText(context).apply {
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            setText(getValue(key, context))
            doOnTextChanged { charSequence, _, _, _ ->
                editingText = text.toString().trim()
                saveButton.setBtnEnable(savedValue != editingText)
            }
            hint = title
        }

        saveButton.setOnClickListener {
            val current = editText.text.toString().trim()
            if (current != savedValue.trim()) {
                savedValue = current
                saveValue(key, current, context)
                saveButton.setBtnEnable(savedValue != editingText)
                Toast.makeText(context, "$title Saved", Toast.LENGTH_SHORT).show()
            }
        }

        row.addView(editText)
        row.addView(saveButton)

        return row
    }

    container.addView(createItem("SSID", "ssid"))
    container.addView(createItem("BSSID", "bssid"))
    container.addView(createItem("MAC", "mac"))

    parent.addView(DialogView(activity, container).apply {
        tag = UI_TAG
    })
}

private fun Button.setBtnEnable(enable: Boolean) {
    isEnabled = enable
    setTextColor(if (enable) Color.parseColor("#333333") else Color.parseColor("#999999"))
    setBackground(ShapeDrawable(RoundRectShape(FloatArray(8) { 1080.toPx() }, null, null)).apply {
        setTint(if (enable) Color.parseColor("#53C4EF") else Color.parseColor("#0F333333"))
    })
}