package com.example.mywidgets.accordion.animations

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout

// TODO: Check if is possible use <CoordinatorLayout> view with "CoordinatorLayout.AttachedBehavior"
//       implementation to perform a better and responsive expand/collapse animation
/**
 * ## References
 *
 * - [Android Animation Libraries You Should Know](https://www.nomtek.com/blog/android-animation-libraries)
 * - [CoordinatorLayoutFragment example](https://github.com/nomtek/android-animations/blob/master/app/src/main/java/com/nomtek/animations/demo/CoordinatorLayoutFragment.kt)
 * - [ShrinkBehaviour example](https://github.com/nomtek/android-animations/blob/master/app/src/main/java/com/nomtek/animations/demo/behaviour/ShrinkBehaviour.kt)
 */
class AccordionTransitionAnimation(view: View, duration: Int, type: Int) : Animation() {

    private val view: View
    private var endHeight: Int
    var endBottomMargin: Int
    var endTopMargin: Int
    private val type: Int
    private val layoutParams: ConstraintLayout.LayoutParams
    var height: Int
        get() = view.height
        set(height) {
            endHeight = height
        }

    companion object {
        const val COLLAPSE = 1
        const val EXPAND = 0
    }

    init {
        setDuration(duration.toLong())
        this.view = view
        this.type = type
        endHeight = this.view.measuredHeight
        layoutParams = view.layoutParams as ConstraintLayout.LayoutParams
        endBottomMargin = layoutParams.bottomMargin
        endTopMargin = layoutParams.topMargin

        if (this.type == EXPAND) {
            layoutParams.height = 0
            layoutParams.topMargin = 0
            layoutParams.bottomMargin = 0
        } else {
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
        }
        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
        view.visibility = View.VISIBLE
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        super.applyTransformation(interpolatedTime, t)
        if (interpolatedTime < 1.0f) {
            if (type == EXPAND) {
                layoutParams.height = (endHeight * interpolatedTime).toInt()
                layoutParams.topMargin = (endTopMargin * interpolatedTime).toInt()
                layoutParams.bottomMargin = (endBottomMargin * interpolatedTime).toInt()
                view.invalidate()
            } else {
                layoutParams.height = (endHeight * (1 - interpolatedTime)).toInt()
                layoutParams.topMargin = (endTopMargin * (1 - interpolatedTime)).toInt()
                layoutParams.bottomMargin = (endBottomMargin * (1 - interpolatedTime)).toInt()
            }
            view.requestLayout()
        } else {
            if (type == EXPAND) {
                layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
                view.requestLayout()
            } else {
                view.visibility = View.GONE
            }
        }
    }
}
