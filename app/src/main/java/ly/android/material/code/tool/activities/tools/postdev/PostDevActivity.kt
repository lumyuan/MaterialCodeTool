package ly.android.material.code.tool.activities.tools.postdev

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.documentfile.provider.DocumentFile
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.impl.LoadingPopupView
import ly.android.io.common.IOUtils
import ly.android.material.code.tool.R
import ly.android.material.code.tool.activities.tools.postdev.request.PostRequestFragment
import ly.android.material.code.tool.activities.tools.postdev.response.PostResponseFragment
import ly.android.material.code.tool.data.PostDevViewModel
import ly.android.material.code.tool.data.entity.BodyParamType
import ly.android.material.code.tool.data.entity.ResponseStateBean
import ly.android.material.code.tool.data.enums.RequestFunction
import ly.android.material.code.tool.databinding.ActivityPostDevBinding
import ly.android.material.code.tool.ui.adapter.PagerAdapterForFragment
import ly.android.material.code.tool.ui.common.bind
import ly.android.material.code.tool.ui.theme.MyTheme
import ly.android.material.code.tool.ui.view.setOnFeedbackListener
import ly.android.material.code.tool.util.HttpCreator
import ly.android.material.code.tool.util.ToastUtils
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit

class PostDevActivity : AppCompatActivity() {

    private val binding by bind(ActivityPostDevBinding::inflate)
    private val viewModel by viewModels<PostDevViewModel>()

    private val titles by lazy {
        arrayOf(
            getString(R.string.request),
            getString(R.string.response)
        )
    }

    private val requestFunctionString by lazy {
        val values = RequestFunction.values()
        val arrayOf = arrayOfNulls<String>(values.size)
        for (index in values.indices) {
            arrayOf[index] = values[index].toString()
        }
        arrayOf
    }

    private val requestFunctions = RequestFunction.values()

    private val popupView by lazy {
        XPopup.Builder(this)
            .hasShadowBg(false)
            .isCoverSoftInput(true)
            .atView(binding.functionName)
            .isDarkTheme(MyTheme.isDarkMode())
            .asAttachList(
                requestFunctionString, null
            ) { position, _ ->
                viewModel.requestFunctionState.value = requestFunctions[position]
            }
    }

    private val loadPopup by lazy {
        XPopup.Builder(this)
            .dismissOnBackPressed(false)
            .dismissOnTouchOutside(false)
            .isDarkTheme(!MyTheme.isDarkMode())
            .animationDuration(500)
            .asLoading(getString(R.string.posting), LoadingPopupView.Style.ProgressBar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            this.setTitle(R.string.post_dev)
            setDisplayHomeAsUpEnabled(true)
        }

        val pages = ArrayList<PagerAdapterForFragment.Page>().apply {
            add(
                PagerAdapterForFragment.Page(
                    PostRequestFragment.newInstance(), titles[0]
                )
            )
            add(
                PagerAdapterForFragment.Page(
                    PostResponseFragment.newInstance(), titles[1]
                )
            )
        }

        binding.viewpager.apply {
            adapter = PagerAdapterForFragment(pages.toArray(arrayOf()), supportFragmentManager)
        }

        binding.pagerTabLayout.setupWithViewPager(binding.viewpager)


        viewModel.requestFunctionState.observe(this) {
            binding.functionName.text = it.toString()
        }

        binding.functionName.setOnFeedbackListener {
            popupView.show()
        }

        binding.urlText.addTextChangedListener {
            if (binding.urlText.hasFocus()){
                viewModel.url.value = it.toString()
            }
        }

        binding.urlText.setText(viewModel.url.value)

        //发送请求
        binding.send.setOnFeedbackListener {
            viewModel.sendState.value = System.currentTimeMillis()
            viewModel.responseState.value = null
            viewModel.bodyData.value = null
            loadPopup.show()
            try {
                HttpCreator(viewModel).enqueue {
                    binding.viewpager.currentItem = 1
                    loadPopup.delayDismiss(1000)
                }
            }catch (e: Exception){
                e.printStackTrace()
                viewModel.bodyData.value = e.toString()
                loadPopup.delayDismiss(1000)
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.url.value = binding.urlText.text.toString()
    }
}