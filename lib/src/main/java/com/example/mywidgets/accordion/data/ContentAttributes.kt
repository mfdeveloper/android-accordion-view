package com.example.mywidgets.accordion.data

import android.graphics.drawable.Drawable

// TODO: Migrate this to use inside of a ViewModel + DataBinding
data class ContentAttributes(
    var backgroundColor: Int = 0,
    var background: Drawable? = null,
    var bottomMargin: Int = 0,
    var topMargin: Int = 0
)
