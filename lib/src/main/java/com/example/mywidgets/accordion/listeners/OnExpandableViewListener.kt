package com.example.mywidgets.accordion.listeners

import com.example.mywidgets.accordion.view.BaseAccordionView

interface OnExpandableViewListener {
    fun onExpand(expandedView: BaseAccordionView)
    fun onCollapse(collapsedView: BaseAccordionView)
}
