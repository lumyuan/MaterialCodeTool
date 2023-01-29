package ly.android.material.code.tool.ui.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
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
                    val offset = drawerView.width * slideOffset
                    translationX = offset
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