package com.example.spaceexplorer.binding

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter

/**
 * Data Binding adapters specific to the app.
 */
object BindingAdapters {
    @JvmStatic
    @BindingAdapter("visibleGone")
    fun showHide(view: View, show: Boolean) {
        view.visibility = if (show) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("text")
    fun setText(view: TextView, integer: Int) {
        view.setText(integer.toString())
    }

    @JvmStatic
    @BindingAdapter("text")
    fun setText(view: TextView, double: Double) {
        view.setText(double.toString())
    }
}
