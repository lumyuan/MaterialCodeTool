package ly.android.material.code.tool.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner

abstract class BaseFragment : Fragment{

    private var firstResumed = false

    constructor()
    constructor(layoutId: Int) : super(layoutId)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        var rootView = super.onCreateView(inflater, container, savedInstanceState)
        if (rootView == null) {
            rootView = onCreateViewByReturn(inflater, container)
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        initViewModel(viewLifecycleOwner)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        firstResumed = false
    }

    override fun onResume() {
        super.onResume()
        if (!firstResumed) {
            firstResumed = true
            loadSingleData()
        }
        loadData()
    }

    /**
     * 不使用Fragment(int)构造方法需要提供View
     **/
    protected open fun onCreateViewByReturn(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): View? {
        return null
    }

    protected open fun initView(root: View) {}

    protected open fun initViewModel(viewLifecycleOwner: LifecycleOwner) {}

    protected open fun loadData() {}

    protected open fun loadSingleData() {}
}