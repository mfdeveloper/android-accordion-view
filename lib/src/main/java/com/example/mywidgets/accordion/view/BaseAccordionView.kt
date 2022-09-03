package com.example.mywidgets.accordion.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import com.example.mywidgets.accordion.R
import com.example.mywidgets.accordion.animations.AccordionTransitionAnimation
import com.example.mywidgets.accordion.data.ContentAttributes
import com.example.mywidgets.accordion.data.HeaderAttributes
import com.example.mywidgets.accordion.extensions.fullHeight
import com.example.mywidgets.accordion.extensions.strId
import com.example.mywidgets.accordion.listeners.OnExpandableViewListenerWrapper
import com.example.mywidgets.accordion.animations.ViewRotateAnimationSet

/**
 * Vertical accordion layout
 * Allows adding dynamic views to the content and handles the state of the views on click
 */
// TODO: Migrate this to use Android "DataBinding" instead of "ViewBinding"
abstract class BaseAccordionView @JvmOverloads constructor(
    context: Context, var attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    abstract var binding: ViewBinding
    abstract var header: TextView
    abstract var headerDivider: View
    abstract var dropdownIcon: ImageView
    abstract var content: ViewGroup

    var enableCollapseOthers = false
        protected set

    var isAnimated = true
        protected set

    var isExpanded = false
        protected set

    var isDisplayDivider = false
        protected set

    protected open var headerAttributes: HeaderAttributes? = null
    protected open var contentAttributes: ContentAttributes = ContentAttributes()

    /**
     * List of views added to the Accordion
     */
    protected open val relatedAccordions = arrayListOf<BaseAccordionView>()

    private var contentChildren: ArrayList<View?> = arrayListOf()
    private var initialChildrenIds: ArrayList<Int> = arrayListOf()
    private var relatedAccordionsIds: Array<CharSequence> = arrayOf()

    private val toggleContentVisibility = OnClickListener {
        if (content.isVisible) {
            collapse()
        } else expand()
    }

    protected open var listener = OnExpandableViewListenerWrapper()

    companion object {
        const val HEADER_ARROW_ROTATE_DEGREES = 180.0f
        const val HEADER_ARROW_ROTATE_DURATION = 300
    }

    /**
     * Initializes the layout. Defines its orientation
     */
    init {
        if (attrs != null) {
            this.handleAttributeSet(context, attrs)
        }

        this.prepareInitialViews()
        this.updateViews()
    }

    override fun onFinishInflate() {
        if (attrs != null) {
            prepareLayout()
        } else prepareLayoutWithoutChildren()

        super.onFinishInflate()
    }

    override fun onAttachedToWindow() {

        seekRelatedAccordions()

        if (isExpanded) {
            expand()
        } else collapse()

        setOnClickListenerOnHeader()

        super.onAttachedToWindow()
    }

    protected open fun prepareInitialViews() {

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            initialChildrenIds.add(child.id)
        }
    }

    /***
     * This function is used to prepare the layout after the initialize function but is called when the developer PROGRAMATICALLY adds
     * the accordion from the class. Hence, the accordion does not have the UI elements (children) yet
     *
     */
    protected open fun prepareLayoutWithoutChildren() {
        initializeViewsWithoutChildren()

        headerDivider.isVisible = isDisplayDivider
        header.text = headerAttributes?.text
        content.isVisible = true
    }

    /***
     * This function; after initializing the accordion, performs necessary UI operations like setting the partition or adding animation or
     * expanding or collapsing the accordion
     */
    protected open fun prepareLayout() {
        initializeViews()

        headerDivider.isVisible = isDisplayDivider
        header.text = headerAttributes?.text

        headerAttributes?.textSize.let {
            header.textSize = it!!.toFloat()
        }

        content.isVisible = true
    }

    /***
     * This creates an accordion layout. This is called when the user programatically creates an accordion. 'Without Children' signifies that no UI elements
     * have been added to the content of the accordion yet.
     */
    private fun initializeViewsWithoutChildren() {

        if (childCount > initialChildrenIds.size) {
            content.removeAllViews()
        }

        val contentLayoutParams = content.layoutParams as LayoutParams

        contentAttributes.apply {
            bottomMargin = contentLayoutParams.bottomMargin
            topMargin = contentLayoutParams.topMargin
        }
    }

    /***
     * This function is called when the accordion is added in the XML itself and is used to initialize the various components
     * of the accordion
     */
    protected open fun initializeViews() {

        if (childCount > initialChildrenIds.size) {

            content.removeAllViews()

            addDynamicView(content, contentChildren, childCount)
        }

        val contentLayoutParams = content.layoutParams as LayoutParams

        contentAttributes.apply {
            bottomMargin = contentLayoutParams.bottomMargin
            topMargin = contentLayoutParams.topMargin
        }
    }

    protected open fun handleAttributeSet(context: Context, attrs: AttributeSet?) {

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.AccordionView,
            0, 0).apply {
            try {
                isAnimated = getBoolean(R.styleable.AccordionView_animate, false)
                isExpanded = getBoolean(R.styleable.AccordionView_expanded, false)
                isDisplayDivider = getBoolean(R.styleable.AccordionView_displayDivider, false)
                enableCollapseOthers = getBoolean(R.styleable.AccordionView_collapseOthers, false)

                val relatedIds = getTextArray(R.styleable.AccordionView_relatedAccordions)
                if (relatedIds != null) {
                    relatedAccordionsIds = relatedIds
                }

                // Header
                // TODO: Apply header attributes to a new [ViewGroup] "headerLayout" element
                headerAttributes = HeaderAttributes(
                    text = getString(R.styleable.AccordionView_headerText),
                    textSize = getDimensionPixelSize(R.styleable.AccordionView_android_textSize, 20).toFloat(),
                    textStyle = getInt(R.styleable.AccordionView_android_textStyle, 0),
                    textColor = getColor(R.styleable.AccordionView_android_textColor, Color.BLACK),
                    textColorHint = getColor(R.styleable.AccordionView_android_textColorHint, Color.TRANSPARENT),
                    background = getDrawable(R.styleable.AccordionView_android_background),
                    backgroundColor = getColor(R.styleable.AccordionView_android_colorBackground, Color.TRANSPARENT),
                    animateArrow = getBoolean(R.styleable.AccordionView_animateArrow, false),
                    dropdownIcon = getResourceId(
                        R.styleable.AccordionView_iconDrawable,
                        R.drawable.ic_accordion_arrow_selector
                    ),
                )

                contentAttributes = ContentAttributes(
                    background = getDrawable(R.styleable.AccordionView_contentBackground),
                    backgroundColor = getColor(R.styleable.AccordionView_contentBackgroundColor, Color.TRANSPARENT)
                )

            } finally {
                recycle()
            }
        }
    }

    protected open fun updateViews() {
        headerAttributes?.apply {

            if (text?.isNotEmpty() == true && header.text != text)
                header.text = text

            header.textSize = textSize
            header.setTypeface(header.typeface, textStyle)
            header.setTextColor(textColor)
            header.setHintTextColor(textColorHint)

            if (background != null)
                header.background = background

            header.setBackgroundColor(backgroundColor)
            setDropdownIcon(dropdownIcon)
        }

        contentAttributes.apply {
            content.background = background
            content.setBackgroundColor(backgroundColor)
        }
    }

    protected open fun seekRelatedAccordions() {
        if (parent == null) {
            return
        }

        val parentGroup = parent as ViewGroup
        for (i in 0 until parentGroup.childCount) {
            val sibling = parentGroup.getChildAt(i)

            if (sibling is BaseAccordionView) {
                val strId = sibling.strId(true)
                if (relatedAccordionsIds.isNotEmpty() && relatedAccordionsIds.contains(strId)) {
                    relatedAccordions.add(sibling)
                } else if (sibling != this) {
                    relatedAccordions.add(sibling)
                }
            }
        }
    }

    protected open fun setOnClickListenerOnHeader() {
        header.setOnClickListener(toggleContentVisibility)
        dropdownIcon.setOnClickListener(toggleContentVisibility)
    }

    // TODO: [New Feature] Reuse this method to add dynamic elements to "headerLayout" too
    protected open fun addDynamicView(groupView: ViewGroup, childrenList: ArrayList<View?>, childCount: Int) {
        // Check the count values before => after inflate()
        // to loop over only new views to add dynamically to the "@+id/content" ViewGroup container
        var i = childCount - 1
        val limitViewsCount = i - (childCount - initialChildrenIds.size)

        // TODO: Migrate this while to "for(i in [number] until/downTo [count])"
        while (i > limitViewsCount) {

            val child = getChildAt(i)

            childrenList.add(child)
            removeView(child)
            i--
        }

        // Add views from the last to first child View
        // this way avoid alignment constraint problems
        // TODO: [Refactor] Move this "addView()" to the loop above
        //       and remove this loop here
        if (childrenList.isNotEmpty()) {
            i = childrenList.size - 1
            while (i >= 0) {
                groupView.addView(childrenList[i])
                i--
            }
        }
    }

    /**
     *
     *
     * @param views - list of ExpandableViews to add in the accordion
     */
    fun addRelatedAccordions(views : List<BaseAccordionView>) = views.forEach { addRelatedAccordion(it) }

    /**
     * Add any other single [BaseAccordionView] to related with this one
     *
     * @param view - [BaseAccordionView] to make a relationship with this accordion view
     */
    fun addRelatedAccordion(view : BaseAccordionView) = relatedAccordions.add(view)

    /**
     * Handles click on [BaseAccordionView]. It expands or collapses the clicked view according to its state
     * and collapses all other views
     *
     */
    open fun expand() {

        if (enableCollapseOthers && relatedAccordions.isNotEmpty()) {
            collapseAll()
        }

        if (isAnimated) {
            // TODO: Try fix the "animation glitches", or look for other animation
            val expandAnimation = AccordionTransitionAnimation(content, 300, AccordionTransitionAnimation.EXPAND)
                .apply {
                    height = content.fullHeight()
                    endBottomMargin = contentAttributes.bottomMargin
                    endTopMargin = contentAttributes.topMargin
                }

            content.startAnimation(expandAnimation)
        } else {
            content.isVisible = true
        }

        headerDivider.isVisible = isDisplayDivider

        if (headerAttributes?.animateArrow == true) {
            dropdownIcon.startAnimation(
                ViewRotateAnimationSet(degrees = -HEADER_ARROW_ROTATE_DEGREES, duration = HEADER_ARROW_ROTATE_DURATION, shareInterpolator = true)
            )
        } else dropdownIcon.isSelected = true

        isExpanded = true

        listener.onExpand(this)
    }

    open fun collapse() {

        if (isAnimated) {
            // TODO: Try fix the "animation glitches", or look for other animation
            val collapseAnimation = AccordionTransitionAnimation(content, 300, AccordionTransitionAnimation.COLLAPSE)
            content.startAnimation(collapseAnimation)
        } else {
            content.isGone = true
        }

        headerDivider.isVisible = isDisplayDivider

        if (headerAttributes?.animateArrow == true) {

            dropdownIcon.startAnimation(
                ViewRotateAnimationSet(-HEADER_ARROW_ROTATE_DEGREES, degrees = 0.0f, duration = HEADER_ARROW_ROTATE_DURATION, shareInterpolator = true)
            )
        } else dropdownIcon.isSelected = false

        isExpanded = false

        listener.onCollapse(this)
    }

    open fun collapseAll() {
        relatedAccordions.forEach { otherAccordion ->
            if (otherAccordion != this && otherAccordion.isExpanded)
                otherAccordion.collapse()
        }
    }

    /**
     * Set listeners defined in [com.example.mywidgets.accordion.listeners.OnExpandableViewListener]
     * as Kotlin DSL. With this, you can register a listener for all callback methods once, or just one
     * like defined by SOLID [Interface segregation principle](https://en.wikipedia.org/wiki/Interface_segregation_principle)
     *
     * ## Reference
     *
     * - [Listeners with several functions in Kotlin](https://antonioleiva.com/listeners-several-functions-kotlin)
     */
    @Suppress("unused")
    fun setListeners(init: OnExpandableViewListenerWrapper.() -> Unit): OnExpandableViewListenerWrapper {
        listener.init()
        return listener
    }

    /***
     * This function adds the view to the content [ViewGroup] element
     * @param child
     */
    fun addViewToBody(child: View?) {
        content.addView(child)
    }

    /***
     * This function adds the view to the content [ViewGroup] element
     * An alias to [addViewToBody]
     * @param child
     */
    @Suppress("unused")
    fun addViewsToContent(child: View?) = addViewToBody(child)

    fun addViewsToContent(childCollection: List<View>) {
        for (i in 0 until childCollection.count()) {
            val child = childCollection[i]
            addViewToBody(child)
        }
    }

    @Suppress("unused")
    fun getHeaderText(): String {
        return StringBuilder(header.text).toString()
    }

    @Suppress("unused")
    fun setHeaderText(text: String?) {
        headerAttributes?.text = text
        header.text = text
        invalidate()
        requestLayout()
    }

    @Suppress("unused")
    fun getHeaderTextColor(): Int {
        return header.currentTextColor
    }

    @Suppress("unused")
    fun setHeaderTextColor(color: Int) {
        headerAttributes?.textColor = color
        header.setTextColor(color)
        invalidate()
        requestLayout()
    }

    fun getDropdownIcon(): Int {
        return headerAttributes?.dropdownIcon ?: 0
    }

    @Suppress("unused")
    fun setDropdownIcon(resId: Int, invalidateRoot: Boolean = false) {
        headerAttributes?.dropdownIcon = resId
        dropdownIcon.setImageResource(resId)

        if (invalidateRoot)
            invalidate()
            requestLayout()
    }

    /**
     * ## Reference
     * - [Get background color of a Layout](https://stackoverflow.com/questions/14779259/get-background-color-of-a-layout)
     */
    @Suppress("unused")
    fun getBackgroundColor(): Int {
        var color = Color.TRANSPARENT

        if (background is ColorDrawable)
            color = (background as ColorDrawable).color

        return color
    }

    override fun setBackgroundColor(color: Int) {
        headerAttributes?.backgroundColor = color
        super.setBackgroundColor(color)
    }
}
