package ly.android.material.code.tool.activities.fragments.reference

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.just.agentweb.AgentWeb
import ly.android.material.code.tool.R
import ly.android.material.code.tool.databinding.FragmentAndroidDeveloperBinding
import ly.android.material.code.tool.ui.base.BaseFragment
import ly.android.material.code.tool.ui.common.bind
import ly.android.material.code.tool.ui.view.setOnFeedbackListener
import ly.android.material.code.tool.util.ToastUtils


class AndroidDeveloperFragment : BaseFragment() {

    private val binding by bind(FragmentAndroidDeveloperBinding::inflate)
    private var isFailed = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    private var targetUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            targetUrl = it.getString("url")
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(url: String) = AndroidDeveloperFragment().apply {
            arguments = Bundle().apply {
                putString("url", url)
            }
        }
    }

    private lateinit var agentWeb: AgentWeb

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    override fun initView(root: View) {
        super.initView(root)

        agentWeb = AgentWeb.with(this)
            .setAgentWebParent(binding.webLayout, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
            .useDefaultIndicator()
            .createAgentWeb()
            .ready()
            .get()

        agentWeb.webCreator.webView.apply {
            this.webViewClient = mWebViewClient
            this.webChromeClient = mWebChromeClient
        }

        binding.floatActionButton.setOnFeedbackListener(
            callOnLongClick = true,
            click = {
                    if (!agentWeb.back()){
                        ToastUtils.toast(R.string.cannot_go_back)
                    }
            },
            onLongClick = {
                ToastUtils.toast(R.string.go_back, Toast.LENGTH_SHORT)
            }
        )
    }

    private val mWebViewClient: WebViewClient = object : WebViewClient() {
        @SuppressLint("SetTextI18n")
        @RequiresApi(Build.VERSION_CODES.M)
        override fun onReceivedError(
            view: WebView,
            request: WebResourceRequest,
            error: WebResourceError
        ) {
            super.onReceivedError(view, request, error)
//            isFailed = true
//            binding.loadFail.visibility = View.VISIBLE
//            binding.webLayout.visibility = View.GONE
//            binding.message.text = "COde: ${error.errorCode}\nDescription: ${error.description}"
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            binding.loadFail.visibility = View.GONE
            binding.webLayout.visibility = View.VISIBLE
        }
    }
    private val mWebChromeClient: WebChromeClient = object : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            //do you work
            if (newProgress == 100) {
                isFailed = false
            }
        }
    }

    override fun loadSingleData() {
        agentWeb.urlLoader.loadUrl(targetUrl)
    }

    override fun onDestroyView() {
        agentWeb.webLifeCycle.onDestroy()
        super.onDestroyView()
    }
}