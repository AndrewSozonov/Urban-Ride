package com.andrewsozonov.urbanride.adapter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class HistoryItemDecoration(val context: Context, val resId: Int, val spaceHeight: Int) : RecyclerView.ItemDecoration() {
    var divider: Drawable? = null

    init {
        divider = ContextCompat.getDrawable(context, resId)
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = spaceHeight
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left: Int = parent.getPaddingLeft()
        val right: Int = parent.getWidth() - parent.getPaddingRight();

        val childCount = parent.getChildCount()
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)

            val params: RecyclerView.LayoutParams =
                child.getLayoutParams() as RecyclerView.LayoutParams

           /* val top = child.getBottom() + params.bottomMargin
            val bottom = top + divider!!.getIntrinsicHeight()

            divider!!.setBounds(left, top, right, bottom)
            divider!!.draw(c);*/
            val upTop = child.top + params.topMargin - divider!!.intrinsicHeight
            val upBottom = child.top + params.bottomMargin
            /*val topLeft = child.top + params.topMargin
            val topRight = topLeft + divider!!.intrinsicHeight*/
            val downTop = child.bottom + params.bottomMargin
            val downBottom = child.bottom + params.bottomMargin + divider!!.intrinsicHeight

            divider!!.setBounds(left, upTop, right, upBottom)
            divider!!.draw(c)

            divider!!.setBounds(left, downTop, right, downBottom)
            divider!!.draw(c)
        }
    }
}