package ly.android.material.code.tool.activities.tools

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import ly.android.material.code.tool.R
import ly.android.material.code.tool.data.AliIconViewModel
import ly.android.material.code.tool.data.entity.LoadState
import ly.android.material.code.tool.databinding.ActivityAliIconBinding
import ly.android.material.code.tool.net.pojo.response.AliIconBean
import ly.android.material.code.tool.ui.adapter.AliIconListAdapter
import ly.android.material.code.tool.ui.common.bind
import ly.android.material.code.tool.util.AliIconHttpUtils
import ly.android.material.code.tool.util.SharedPreferencesUtil
import ly.android.material.code.tool.util.ToastUtils
import java.util.LinkedHashMap

class AliIconActivity : AppCompatActivity() {

    private val binding by bind(ActivityAliIconBinding::inflate)

    private val viewModel by viewModels<AliIconViewModel>()

    private lateinit var adapter: AliIconListAdapter

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        viewModel.cookieState.observe(this){
            if (it != null){
                viewModel.cookie.value = SharedPreferencesUtil.load("cookie")
                binding.searchView.visibility = View.VISIBLE
            }
        }

        //打开网站
        viewModel.requestCookie()

        viewModel.loadState.observe(this){
            when (it) {
                is LoadState.Fail -> {
                    when (it.message) {
                        "searchIcon" -> {
                            binding.tips.visibility = View.VISIBLE
                            binding.tips.setText(R.string.load_fail)
                            viewModel.page.value = 1
                        }
                    }
                }
                is LoadState.Loading -> {

                }
                is LoadState.Success -> {
                    when(it.message){
                        "searchIcon" -> {
                            binding.tips.visibility = View.GONE
                        }
                    }
                }
            }
        }

        adapter = AliIconListAdapter()

        viewModel.searchIcon.observe(this){
            if (it!!.data != null){
                adapter.add(it.data?.icons!!)
                viewModel.page.value = viewModel.page.value?.plus(1)
            }else {
                ToastUtils.toast(R.string.to_bottom)
            }
        }

        binding.list.apply {
            layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            this.adapter = this@AliIconActivity.adapter
        }

        binding.searchView.apply {
            imeOptions = EditorInfo.IME_ACTION_SEARCH
            setIconifiedByDefault(false)
            isSubmitButtonEnabled = true
            setOnQueryTextListener(object : OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (TextUtils.isEmpty(query)){
                        ToastUtils.toast(R.string.please_input)
                    }else {
                        viewModel.page.value = 1
                        adapter.clear()
                        searchIcon(query?.trim()!!)
                        val inputMethodManager =
                            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(this@apply.windowToken, 0)
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }

            })
        }

        binding.refreshLayout.setDisableContentWhenLoading(false)
        binding.refreshLayout.setOnRefreshListener {
            viewModel.page.value = 1
            adapter.clear()
            val query = binding.searchView.query
            if (!TextUtils.isEmpty(query)){
                searchIcon(query.toString())
            }else {
                binding.refreshLayout.finishRefresh(true)
            }
        }

        binding.refreshLayout.setOnLoadMoreListener {
            val query = binding.searchView.query
            if (!TextUtils.isEmpty(query)){
                searchIcon(query.toString())
            }else {
                binding.refreshLayout.finishLoadMore(false)
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
//        SharedPreferencesUtil.save("cookie", null)
    }

    private val handler = Handler(Looper.getMainLooper())
    private val gson = Gson()
    private val iconTypeToken = object : TypeToken<AliIconBean>(){}.type

    @SuppressLint("CheckResult")
    private fun searchIcon(target: String) {
        viewModel.loadState.value = LoadState.Loading("searchIcon")
        Observable.create {
            try {
                val post = AliIconHttpUtils.post(
                    "https://www.iconfont.cn/api/icon/search.json",
                    LinkedHashMap<String, String>().apply {
                        put("q", target)
                        put("sortType", "updated_at")
                        put("page", "${viewModel.page.value}")
//                        put("pageSize", "54")
                        put("fills", "")
                        put("t", "${System.currentTimeMillis()}")
                        put("ctoken", "${SharedPreferencesUtil.load("cookie")}")
                    }
                )
                val aliIconBean = gson.fromJson<AliIconBean>(post, iconTypeToken)
                it.onNext(aliIconBean)
            }catch (e: Exception){
                e.printStackTrace()
                handler.post {
                    viewModel.loadState.value = LoadState.Fail("searchIcon")
                    binding.refreshLayout.finishRefresh(true)
                    binding.refreshLayout.finishLoadMore(false)
                }
            }
            it.onComplete()
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                viewModel.searchIcon.value = it
                viewModel.loadState.value = LoadState.Success("searchIcon")
                binding.refreshLayout.finishRefresh(true)
                binding.refreshLayout.finishLoadMore(true)
            }
    }
}