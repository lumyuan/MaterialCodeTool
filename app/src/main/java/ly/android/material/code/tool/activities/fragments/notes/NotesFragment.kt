package ly.android.material.code.tool.activities.fragments.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ly.android.material.code.tool.R
import ly.android.material.code.tool.databinding.FragmentNotesBinding
import ly.android.material.code.tool.ui.base.BaseFragment
import ly.android.material.code.tool.ui.common.bind

class NotesFragment : BaseFragment() {

    private val binding by bind(FragmentNotesBinding::inflate)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            NotesFragment()
    }
}