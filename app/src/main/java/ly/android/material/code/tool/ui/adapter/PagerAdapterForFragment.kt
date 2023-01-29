package ly.android.material.code.tool.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class PagerAdapterForFragment(private val list: Array<Page>, fragmentManager: FragmentManager)
    : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    open class Page(val fragment: Fragment, val title: CharSequence? = null)

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Fragment {
        return list[position].fragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return list[position].title
    }
}