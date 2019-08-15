package wooooooak.com.library

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.children

class ToggleNavigationBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), View.OnClickListener {

    private val toggleTabList = mutableListOf<ToggleTabView>()

    private var navigationChangeListener: ((view: View, position: Int) -> Unit)? = null

    private var currentActiveTabPosition = 0

    init {
        gravity = Gravity.CENTER
        orientation = HORIZONTAL

        post {
            children.forEach {
                if (it is ToggleTabView) {
                    toggleTabList.add(it)
                    it.setOnClickListener(this)
                }
            }
        }

    }

    fun setNavigationChangeListener(navigationChangeListener: (view: View, position: Int) -> Unit) {
        this.navigationChangeListener = navigationChangeListener
    }

    private fun getClickedTabPosition(viewId: Int): Int {
        var position = 0
        toggleTabList.forEachIndexed { index, toggleTabView ->
            if (viewId == toggleTabView.id) {
                position = index
                return@forEachIndexed
            }
        }
        return position
    }


    override fun onClick(v: View) {
        if (v.id == toggleTabList[currentActiveTabPosition].id) {
            return
        } else {
            val newActiveTabPosition = getClickedTabPosition(v.id)
            toggleTabList[currentActiveTabPosition].toggle()
            toggleTabList[newActiveTabPosition].toggle()
            currentActiveTabPosition = newActiveTabPosition
            navigationChangeListener?.let { it(v, newActiveTabPosition) }
        }

    }

}