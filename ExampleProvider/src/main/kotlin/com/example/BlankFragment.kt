package com.example

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lagradost.cloudstream3.R
import com.lagradost.cloudstream3.utils.UIHelper.colorFromAttribute

/**
 * A simple [Fragment] subclass.
 */
class BlankFragment(private val plugin: ExamplePlugin) : BottomSheetDialogFragment() {

    // Resolve against the plugin resources, not the host application's package.
    @SuppressLint("DiscouragedApi")
    private fun Resources.resourceId(name: String, type: String): Int? =
        resolveResourceId(name, type) { resourceName, resourceType ->
            getIdentifier(
                resourceName, resourceType, BuildConfig.LIBRARY_PACKAGE_NAME
            )
        }

    private fun getDrawable(name: String): Drawable? {
        val resources = plugin.resources ?: return null
        val id = resources.resourceId(name, "drawable") ?: return null
        return ResourcesCompat.getDrawable(resources, id, null)
    }

    private fun getString(name: String): String? {
        val resources = plugin.resources ?: return null
        val id = resources.resourceId(name, "string") ?: return null
        return resources.getString(id)
    }

    private fun <T : View> View.findViewByName(name: String): T? {
        val id = plugin.resources?.resourceId(name, "id") ?: return null
        return findViewById(id)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val resources = plugin.resources ?: return null
        val layoutId = resources.resourceId("fragment_blank", "layout") ?: return null
        return inflater.inflate(resources.getLayout(layoutId), container, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        val imageView: ImageView? = view.findViewByName("imageView")
        val imageView2: ImageView? = view.findViewByName("imageView2")
        val textView: TextView? = view.findViewByName("textView")
        val textView2: TextView? = view.findViewByName("textView2")

        // Set text and styling if the views are found
        textView?.apply {
            text = getString("hello_fragment")
            TextViewCompat.setTextAppearance(this, R.style.ResultInfoText)
        }

        textView2?.text = view.context.resources.getText(R.string.legal_notice_text)

        // Set image resources and tint if the views are found
        imageView?.apply {
            setImageDrawable(getDrawable("ic_android_24dp"))
            imageTintList = ColorStateList.valueOf(view.context.getColor(R.color.white))
        }

        imageView2?.apply {
            setImageDrawable(getDrawable("ic_android_24dp"))
            imageTintList = ColorStateList.valueOf(view.context.colorFromAttribute(R.attr.white))
        }
    }
}