package ly.android.material.code.tool.ui.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.drawerlayout.widget.DrawerLayout

class FullDrawerLayout: DrawerLayout {
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initView()
    }

    private fun initView() {
        setScrimColor(Color.TRANSPARENT)
        addDrawerListener(object : DrawerListener{
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                findViewWithTag<View>("content")?.apply {
                    if (this.visibility == VISIBLE){
                        val layoutParams = drawerView.layoutParams as LayoutParams
                        val offset = drawerView.width * slideOffset * if (layoutParams.gravity == Gravity.START) 1 else -1
                        translationX = offset
                    }
                }
            }

            override fun onDrawerOpened(drawerView: View) {

            }

            override fun onDrawerClosed(drawerView: View) {

            }

            override fun onDrawerStateChanged(newState: Int) {

            }
        })
    }
}