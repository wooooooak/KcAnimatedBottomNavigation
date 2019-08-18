package wooooooak.com.animatednavigation

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.TransitionDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import kotlinx.android.synthetic.main.item_tab_view.view.*
import wooooooak.com.library.R

class ToggleTabView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val inflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private var isActive = false
    private lateinit var toggleTabAttrModel: ToggleTabAttrModel

    private val iconTitleContainerView =
        inflater.inflate(R.layout.item_tab_view, null, false) as ConstraintLayout
    private val activeLine = iconTitleContainerView.active_line
    private val iconView = iconTitleContainerView.imageView
    private val titleView = iconTitleContainerView.textView

    private var measuredTitleWidth: Int = 0

    init {
        attrs?.let {
            val ta = context.obtainStyledAttributes(
                attrs,
                R.styleable.ToggleTab, 0, 0
            )
            mapAttrToModel(ta)
            ta.recycle()

            bindAttr()

            titleView.measure(0, 0)
            measuredTitleWidth = titleView.measuredWidth
            addView(iconTitleContainerView)

            setInitialState()
        }
    }

    private fun mapAttrToModel(ta: TypedArray) {
        with(ta) {
            isActive = getBoolean(R.styleable.ToggleTab_tt_active, false)
            val icon = getDrawable(R.styleable.ToggleTab_tt_icon) ?: ContextCompat.getDrawable(
                context, R.drawable.ic_launcher_foreground
            )
            val iconSize = getDimension(
                R.styleable.ToggleTab_tt_icon_size,
                resources.getDimension(R.dimen.default_icon_size)
            )
            val title = getString(R.styleable.ToggleTab_tt_title) ?: ""
            val titleSize = getDimension(
                R.styleable.ToggleTab_tt_title_size,
                resources.getDimension(R.dimen.default_text_size)
            )
            val wrapper =
                getDrawable(R.styleable.ToggleTab_tt_wrapper) ?: ContextCompat.getDrawable(
                    context,
                    R.drawable.transition_background
                )
            val topLineHeight = getDimension(
                R.styleable.ToggleTab_tt_top_line_height,
                resources.getDimension(R.dimen.default_top_line_height)
            )
            val tintColor = getColor(
                R.styleable.ToggleTab_tt_tint_color,
                ContextCompat.getColor(context, R.color.colorBlue)
            )
            toggleTabAttrModel = ToggleTabAttrModel(
                isActive, icon, iconSize, title, titleSize, wrapper,
                topLineHeight, tintColor
            )
        }
    }

    private fun bindAttr() {
        activeLine.apply {
            //            layoutParams =
//                ConstraintLayout.LayoutParams(
//                    ConstraintLayout.LayoutParams.MATCH_CONSTRAINT, toggleTabAttrModel.topLineHeight.toInt()
//                )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                background.setTint(ContextCompat.getColor(context, R.color.colorAccent))
            }
        }
        iconView.apply {
            val sizePixel = toggleTabAttrModel.iconSize.toInt()
            layoutParams = LayoutParams(sizePixel, sizePixel)
            id = ViewCompat.generateViewId()
            setImageDrawable(toggleTabAttrModel.icon)
        }
        titleView.apply {
            text = toggleTabAttrModel.title
            setTextSize(TypedValue.COMPLEX_UNIT_PX, toggleTabAttrModel.titleSize)
            setSingleLine(true)
        }
    }

    private fun setInitialState() {
        val customBackground = toggleTabAttrModel.wrapper
        activeLine.background = customBackground
        if (isActive) {
            this.isActive = true
            titleView.visibility = View.VISIBLE
            titleView.setTextColor(
                ContextCompat.getColor(context, R.color.colorBlue)
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                iconView.drawable.setTint(
                    ContextCompat.getColor(context, R.color.colorBlue)
                )
            }
            if (customBackground is TransitionDrawable) {
                customBackground.startTransition(0)
            }
        } else {
            this.isActive = false
            titleView.visibility = View.GONE
            titleView.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.color_default_inactive
                )
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                iconView.drawable.setTint(
                    ContextCompat.getColor(
                        context,
                        R.color.color_default_inactive
                    )
                )
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            iconView.drawable.setTint(
                ContextCompat.getColor(context, R.color.color_default_inactive)
            )
        }

        titleView.setTextColor(
            ContextCompat.getColor(context, R.color.color_default_inactive)
        )

        val animator = ValueAnimator.ofFloat(1f, 0f).apply {
            addUpdateListener {
                val value = it.animatedValue as Float
                titleView.width = (measuredTitleWidth * value).toInt()
                if (value <= 0.0f) titleView.visibility = View.GONE
            }
        }
        animator.start()

        val customBackground = toggleTabAttrModel.wrapper
        activeLine.background = customBackground
        if (customBackground is TransitionDrawable) {
            customBackground.reverseTransition(0)
        } else {
            background = null
        }
    }

    private fun active() {
        isActive = true
        titleView.visibility = View.VISIBLE
        titleView.setTextColor(toggleTabAttrModel.tintColor)
        val customBackground = toggleTabAttrModel.wrapper
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            iconView.drawable.setTint(toggleTabAttrModel.tintColor)
            customBackground?.setTint(toggleTabAttrModel.tintColor)
        }

        val animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = DEFAULT_ANIMATION_DURATION.toLong()
            addUpdateListener {
                val value = it.animatedValue as Float
                titleView.width = (measuredTitleWidth * value).toInt()
            }
        }
        animator.start()

        activeLine.background = customBackground
        if (customBackground is TransitionDrawable) {
            customBackground.startTransition(0)
        }
    }

    companion object {
        const val DEFAULT_ANIMATION_DURATION = 300
    }
}