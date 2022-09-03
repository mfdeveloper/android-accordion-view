package com.example.mywidgets.accordion.animations

import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation

class ViewRotateAnimationSet(
    private val fromDegrees: Float = 0.0f,
    private val degrees: Float = -180.0f,
    private val pivotValue: Float = 0.5f,
    private val duration: Int = 1500,
    shareInterpolator: Boolean = true
) : AnimationSet(shareInterpolator) {

    init {
        interpolator = DecelerateInterpolator()
        fillAfter = true
        isFillEnabled = true

        addRotateAnimation()
    }

    private fun addRotateAnimation() {
        val animRotate = RotateAnimation(
            fromDegrees, degrees,
            RELATIVE_TO_SELF, pivotValue,
            RELATIVE_TO_SELF, pivotValue
        )

        animRotate.duration = duration.toLong()
        animRotate.fillAfter = true

        addAnimation(animRotate)
    }
}
