package wooooooak.com.animatednavigation

import android.graphics.drawable.Drawable

class ToggleTabAttrModel(
    val active: Boolean,
    val icon: Drawable?,
    val iconSize: Float,
    val title: String,
    val titleSize: Float,
    val wrapper: Drawable?,
    val topLineHeight: Float,
    val tintColor: Int
)