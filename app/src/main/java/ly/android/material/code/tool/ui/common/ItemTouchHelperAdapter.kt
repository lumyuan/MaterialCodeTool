package ly.android.material.code.tool.ui.common

import androidx.recyclerview.widget.RecyclerView

interface ItemTouchHelperAdapter {
    //数据交换
    fun onItemMove(source: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?)

    //数据删除
    fun onItemDismiss(source: RecyclerView.ViewHolder?)

    //drag或者swipe选中
    fun onItemSelect(source: RecyclerView.ViewHolder?)

    //状态清除
    fun onItemClear(source: RecyclerView.ViewHolder?)
}