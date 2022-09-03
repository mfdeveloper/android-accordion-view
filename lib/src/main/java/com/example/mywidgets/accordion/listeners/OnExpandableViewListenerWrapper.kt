package com.example.mywidgets.accordion.listeners

import com.example.mywidgets.accordion.view.BaseAccordionView

/**
 * A wrapper/helper of [OnExpandableViewListener] to register listeners
 * like Kotlin DSL's.
 *
 * ## Reference
 *
 * - [Listeners with several functions in Kotlin. How to make them shine?](https://antonioleiva.com/listeners-several-functions-kotlin)
 */
open class OnExpandableViewListenerWrapper : OnExpandableViewListener {

    private var expandEvent: ((expandedView: BaseAccordionView) -> Unit)? = null
    private var collapseEvent: ((collapsedView: BaseAccordionView) -> Unit)? = null

    override fun onExpand(expandedView: BaseAccordionView) {
        expandEvent?.invoke(expandedView)
    }

    override fun onCollapse(collapsedView: BaseAccordionView) {
        collapseEvent?.invoke(collapsedView)
    }

    open fun onExpand(callback: (expandedView: BaseAccordionView) -> Unit) {
        expandEvent = callback
    }

    open fun onCollapse(callback: (collapsedView: BaseAccordionView) -> Unit) {
        collapseEvent = callback
    }
}
