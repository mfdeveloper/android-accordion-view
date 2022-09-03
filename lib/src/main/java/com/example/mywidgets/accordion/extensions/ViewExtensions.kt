package com.example.mywidgets.accordion.extensions

import android.view.View
import android.view.ViewGroup

/**
 * @return "{package}:id/[xml-id]"
 * where {package} is your package and [xml-id] is id of view
 * or "no-id" if there is no id
 */
fun View.strId(removePackage: Boolean = false): String {
    return if (id == View.NO_ID) {
        "no-id"
    } else {
        val strId = resources.getResourceName(id)
        if (removePackage) {
            val regex = Regex(".*:id/")
            return regex.replace(strId, "")
        } else return strId
    }
}

/***
 * This function returns the actual height the layout.
 *
 * The [fullHeight] function returns the current height which might be zero if
 * the layout's visibility is [View.GONE]
 *
 * @return
 */
fun ViewGroup.fullHeight(): Int {
    val specWidth = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    val specHeight = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)

    measure(specWidth, specHeight)

    var totalHeight = 0
    val initialVisibility = visibility
    visibility = View.VISIBLE
    val numberOfChildren = childCount

    for (i in 0 until numberOfChildren) {
        val child = getChildAt(i)
        totalHeight += if (child is ViewGroup) {
            child.fullHeight()
        } else {
            val desiredWidth =
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.AT_MOST)
            child.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED)
            child.measuredHeight
        }
    }
    visibility = initialVisibility
    return totalHeight
}
