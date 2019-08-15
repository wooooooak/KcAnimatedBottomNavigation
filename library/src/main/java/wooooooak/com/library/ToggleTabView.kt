package wooooooak.com.library

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.TransitionDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat

class ToggleTabView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    companion object {
        const val DEFAULT_ANIMATION_DURATION = 300
    }

    var isActive = false

    private lateinit var toggleTab: ToggleTabModel
    private lateinit var iconView: ImageView
    private lateinit var titleView: TextView

    private var measuredTitleWidth: Int = 0

    init {
        attrs?.let {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.ToggleTab, 0, 0)
            val icon = ta.getDrawable(R.styleable.ToggleTab_tt_icon) ?: ContextCompat.getDrawable(
                context, R.drawable.ic_launcher_foreground
            )
            val title = ta.getString(R.styleable.ToggleTab_tt_title) ?: ""
            isActive = ta.getBoolean(R.styleable.ToggleTab_tt_active, false)
            val wrapper = ta.getDrawable(R.styleable.ToggleTab_tt_wrapper) ?: ContextCompat
                .getDrawable(context, R.drawable.transition_background)
            ta.recycle()

            toggleTab = ToggleTabModel(icon, title, isActive, wrapper)
            setPadding(0, 10, 0, 10)
//            gravity = Gravity.CENTER

            // create iconView titleView ...
            val lpIcon = LayoutParams(100, 100).apply {
                addRule(CENTER_VERTICAL, TRUE)
            }

            iconView = ImageView(context).apply {
                id = ViewCompat.generateViewId()
                layoutParams = lpIcon
                setImageDrawable(toggleTab.icon)
                setPadding(20,0,0,0)
            }

            titleView = createTextView(context)
            titleView.measure(0, 0)
            measuredTitleWidth = titleView.measuredWidth
            addView(iconView)
            addView(titleView)

            setInitialState()
        }
    }

    private fun createTextView(context: Context): TextView {
        val lpText = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            addRule(CENTER_VERTICAL, TRUE)
            addRule(END_OF, iconView.id)
        }
        return TextView(context).apply {
            layoutParams = lpText
            setSingleLine(true)
            setPadding(10, 0, 0, 0)
            text = toggleTab.title
            visibility = View.GONE
        }
    }

    private fun setInitialState() {
        val customBackground = toggleTab.wrapper
        background = customBackground
        if (isActive) {
            this.isActive = true
            titleView.visibility = View.VISIBLE
            titleView.setTextColor(ContextCompat.getColor(context, R.color.colorBlue))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                iconView.drawable.setTint(ContextCompat.getColor(context, R.color.colorBlue))
            }
            if (customBackground is TransitionDrawable) {
                customBackground.startTransition(0)
            }
        } else {
            this.isActive = false
            titleView.visibility = View.GONE
            titleView.setTextColor(ContextCompat.getColor(context, R.color.color_default_inactive))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                iconView.drawable.setTint(ContextCompat.getColor(context, R.color.color_default_inactive))
            }
            if (customBackground is TransitionDrawable) {
                customBackground.resetTransition()
            } else {
                background = null
            }
        }
    }

    fun toggle() {
        if (isActive) deActive() else active()
    }

    private fun deActive() {
        isActive = false
        val customBackground = toggleTab.wrapper
        background = customBackground
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            iconView.drawable.setTint(ContextCompat.getColor(context, R.color.color_default_inactive))
        }
        titleView.setTextColor(ContextCompat.getColor(context, R.color.color_default_inactive))

        val animator = ValueAnimator.ofFloat(1f, 0f).apply {
            addUpdateListener {
                val value = it.animatedValue as Float
                titleView.width = (measuredTitleWidth * value).toInt()
                if (value <= 0.0f) titleView.visibility = View.GONE
            }
        }
        animator.start()

        if (customBackground is TransitionDrawable) {
            customBackground.reverseTransition(0)
        } else {
            background = null
        }
    }

    private fun active() {
        isActive = true
        titleView.visibility = View.VISIBLE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            iconView.drawable.setTint(ContextCompat.getColor(context, R.color.colorBlue))
        }
        titleView.setTextColor(ContextCompat.getColor(context, R.color.colorBlue))

        val animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = DEFAULT_ANIMATION_DURATION.toLong()
            addUpdateListener {
                val value = it.animatedValue as Float
                titleView.width = (measuredTitleWidth * value).toInt()
            }
        }
        animator.start()

        val customBackground = toggleTab.wrapper
        background = customBackground
        if (customBackground is TransitionDrawable) {
            customBackground.startTransition(0)
        }
    }

}