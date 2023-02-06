package ly.android.material.code.tool.activities.tools

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import ly.android.material.code.tool.R
import ly.android.material.code.tool.common.clip
import ly.android.material.code.tool.data.ColorPickerViewModel
import ly.android.material.code.tool.databinding.ActivityColorPickerBinding
import ly.android.material.code.tool.ui.common.bind
import ly.android.material.code.tool.ui.view.setOnFeedbackListener
import ly.android.material.code.tool.util.ToastUtils

class ColorPickerActivity : AppCompatActivity() {

    private val binding by bind(ActivityColorPickerBinding::inflate)

    private val viewModel by viewModels<ColorPickerViewModel>()
    private var bitmap: Bitmap? = null
    private var imageColor = Color.TRANSPARENT
    private var colorString = "#00000000"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        binding.coordinatorView.setOnCoordinateChangeListener { x, y ->
            bitmap?.apply {
                getColor(x.toInt(), y.toInt(), this)
                try {
                    binding.coordinatorView.setPointColor(colorString)
                }catch (e: Exception){
                    binding.coordinatorView.setPointColor("#00000000")
                }
            }
        }

        binding.coordinatorView.setOnFocusChangeListener {
            if (it){
                binding.imageView.buildDrawingCache(true)
                binding.imageView.buildDrawingCache()
                bitmap = binding.imageView.drawingCache
            }else {
                binding.imageView.destroyDrawingCache()
            }
        }

        binding.choosePic.setOnFeedbackListener {
            selectPicture()
        }

        binding.changeAction.setOnFeedbackListener {
            if (viewModel.picUri.value == null){
                ToastUtils.toast(R.string.please_choose_pic)
            }else {
                viewModel.actionState.value = if (viewModel.actionState.value == 0) {
                    1
                }else {
                    0
                }
            }
        }

        binding.copyColor.setOnFeedbackListener {
            if (viewModel.picUri.value == null){
                ToastUtils.toast(R.string.please_choose_pic)
            }else {
                clip(colorString)
                ToastUtils.toast(R.string.copied_color)
            }
        }

        viewModel.actionState.observe(this){
            when (it) {
                0 -> {
                    binding.changeAction.setText(R.string.zoom)
                    binding.coordinatorView.apply {
                        setCanDrag(true)
                        visibility = View.GONE
                    }
                }
                else -> {
                    binding.changeAction.setText(R.string.get_color)
                    binding.coordinatorView.apply {
                        setCanDrag(false)
                        visibility = View.VISIBLE
                    }
                }
            }
        }

        viewModel.picUri.observe(this) {
            it?.apply {
                Glide.with(this@ColorPickerActivity).load(this).into(binding.imageView)
            }
        }
    }

    private fun selectPicture() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*" //设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, 1)
    }

    private fun getColor(x: Int, y: Int, bitmap: Bitmap) {
        try {
            val pixel = bitmap.getPixel(x, y)
            val alpha = Color.alpha(pixel)
            val red = Color.red(pixel)
            val green = Color.green(pixel)
            val blue = Color.blue(pixel)
            imageColor = Color.argb(alpha, red, green, blue)
            colorString =
                ("#" + toHexString(alpha) + toHexString(red) + toHexString(green) + toHexString(blue)).uppercase()
        } catch (e: Exception) {
            imageColor = Color.argb(0, 0, 0, 0)
            colorString =
                ("#" + toHexString(0) + toHexString(0) + toHexString(0) + toHexString(0)).uppercase()
        }
    }

    private fun toHexString(i: Int): String {
        val string = Integer.toHexString(i)
        return if (string.length < 2){
            "0$string"
        }else {
            string
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //本活动只有一种回调事件，所以不用判断reqCode
        if (resultCode == RESULT_OK) {
            data?.apply {
                viewModel.picUri.value = this.data
                viewModel.actionState.value = 1
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

}