# Android Widget: Accordion View

**[Android Studio](https://developer.android.com/studio#downloads):** Chipmunk | `2021.2.1`

A custom **accordion/expandable** view using the oficial guidelines defiend in: [Creating a View Class](https://developer.android.com/develop/ui/views/layout/custom-views/create-view#kotlin)

Also, we are using the **_Kotlin DSL_** approach to register `expand/collapse` listeners. 

> For more details, see `BaseAccordionView.setListeners` class implementation that use Kotlin [Function literals with receiver](https://kotlinlang.org/docs/lambdas.html#function-literals-with-receiver)

## Getting started

You can use the accordion view in a `.xml` layout file:

```xml
<com.example.mywidgets.accordion.view.AccordionView
      android:id="@+id/accordionView"
      style="@style/body"
      android:layout_width="match_parent"
      android:layout_height="wrap_content"
      android:textStyle="bold"
      android:visibility="visible"
      app:animate="false"
      app:animateArrow="false"
      app:expanded="true"
      app:displayDivider="true"
      app:collapseOthers="true"
      app:headerText="Expandable">

              <!-- Add any view here to put inside of "content" ViewGroup -->
              <TextView
                  android:layout_width="match_parent"
                  android:layout_height="wrap_content"
                  android:text="Content text"
                  android:gravity="center"/>

              <Button
                  android:id="@+id/btnAccordion"
                  android:layout_width="match_parent"
                  android:layout_height="wrap_content"
                  android:text="Accordion Button"
                  android:gravity="center"
                    />
</com.example.mywidgets.accordion.AccordionView>
```

Or instantiate this view programmatically:

```kotlin
var accordionItem = AccordionView(context)

// Define the title of the accordion
accordionItem.setHeaderText("Expandable title")

// Add views programatically to this accordion
accordionItem.addViewsToContent([view1, view2])
```

## TODO

- [ ] Add unit tests
- [ ] Add integration/UI tests to this custom view. Maybe create a example fragment and test inside of it ?
- [ ] Add an `app` module with an example Android app to test this view.

## References

- [Creating a View Class](https://developer.android.com/develop/ui/views/layout/custom-views/create-view#kotlin)

- [Create CustomView with ViewBinding](https://medium.com/@ducvinh.bui88/create-customview-with-viewbinding-10d57aa1ba5d)

- [Listeners with several functions in Kotlin](https://antonioleiva.com/listeners-several-functions-kotlin)

- [ViewBindingUtil](https://github.com/matsudamper/ViewBindingUtil): Similar to android native [DataBindingUtil](https://developer.android.com/reference/android/databinding/DataBindingUtil)
