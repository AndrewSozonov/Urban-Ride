package com.andrewsozonov.urbanride.presentation.history.adapter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

/**
 * ItemDecoration для списка истории поездок
 *
 * @param resId ресурс разделителя
 * @param spaceHeight пространство между элементами
 *
 * @author Андрей Созонов
 */
class HistoryItemDecoration(val context: Context, private val resId: Int, private val spaceHeight: Int) : RecyclerView.ItemDecoration() {
    private var divider: Drawable? = null

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
        val left: Int = parent.paddingLeft
        val right: Int = parent.width - parent.paddingRight;

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)

            val params: RecyclerView.LayoutParams =
                child.layoutParams as RecyclerView.LayoutParams


            val upTop = child.top + params.topMargin - divider!!.intrinsicHeight
            val upBottom = child.top + params.bottomMargin

            val downTop = child.bottom + params.bottomMargin
            val downBottom = child.bottom + params.bottomMargin + divider!!.intrinsicHeight

            divider!!.setBounds(left, upTop, right, upBottom)
            divider!!.draw(c)

            divider!!.setBounds(left, downTop, right, downBottom)
            divider!!.draw(c)
        }
    }
}