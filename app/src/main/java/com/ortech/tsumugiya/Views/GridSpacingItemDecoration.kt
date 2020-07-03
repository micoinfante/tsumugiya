package com.ortech.tsumugiya.Views

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridSpacingItemDecoration(private val spanCount: Int = 2,
                                private val spacing: Int = 20,
                                private val includeEdge: Boolean = false
    ): RecyclerView.ItemDecoration() {


  override fun getItemOffsets(
    outRect: Rect,
    view: View,
    parent: RecyclerView,
    state: RecyclerView.State
  ) {
    super.getItemOffsets(outRect, view, parent, state)

    val position = parent.getChildAdapterPosition(view)
    val column = position % spanCount

    if (includeEdge) {
      outRect.left = spacing - column * spacing / spanCount
      outRect.right = (column + 1) * spacing / spanCount

      if (position < spanCount) {
        outRect.top = spacing
      }
      outRect.bottom = spacing
    } else {
      outRect.left = column * spacing / spanCount
      outRect.right = spacing - (column + 1) * spacing / spanCount
      if (position >= spanCount) {
        outRect.top = spacing
      }
    }


  }
}