package com.example.mywidgets.accordion.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewbinding.ViewBinding
import com.example.mywidgets.accordion.databinding.AccordionItemBinding

/**
 * A basic accordion implementation, to define the layout binding
 * using [ViewBinding] inflate.
 *
 * Also, assign the default base views required to an accordion
 * (e.g header, dropdown image, content [ViewGroup]...)
 *
 * If you wish a completely new accordion layout, but keep the main features,
 * inherit from [BaseAccordionView] abstract class and customize it!
 */
class AccordionView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseAccordionView(context, attrs, defStyleAttr) {

    /**
     * Layout Binding
     */
    override var binding: ViewBinding = AccordionItemBinding.inflate(LayoutInflater.from(context), this)
    private val layoutBinding = binding as AccordionItemBinding

    /**
     * Base views overriding.
     *
     * Can be different for custom accordions that inherit from [BaseAccordionView]
     */
    override var header: TextView =  layoutBinding.header
    override var headerDivider: View = layoutBinding.headerDivider
    override var dropdownIcon: ImageView = layoutBinding.dropdownIcon
    override var content: ViewGroup = layoutBinding.content
}
