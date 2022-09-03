package com.example.mywidgets.accordion.data

import android.graphics.drawable.Drawable

// TODO: Migrate this to use inside of a ViewModel + DataBinding
data class HeaderAttributes(
    var text: String?,
    var textSize: Float,
    var textColor: Int,
    var textColorHint: Int,
    var textStyle: Int,
    var backgroundColor: Int,
    var background: Drawable?,
    var dropdownIcon: Int,
    var animateArrow: Boolean = false
)
