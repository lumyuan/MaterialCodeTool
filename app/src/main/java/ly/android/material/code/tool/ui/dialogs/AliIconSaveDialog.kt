package ly.android.material.code.tool.ui.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BottomPopupView
import com.pixplicity.sharp.Sharp
import ly.android.io.File
import ly.android.io.common.IOUtils
import ly.android.material.code.tool.MaterialCodeToolApplication
import ly.android.material.code.tool.R
import ly.android.material.code.tool.data.AliIconSetColorViewModel
import ly.android.material.code.tool.databinding.DialogAliIconSaveBinding
import ly.android.material.code.tool.net.pojo.response.Icon
import ly.android.material.code.tool.ui.view.setOnFeedbackListener
import ly.android.material.code.tool.util.ImageConverter
import ly.android.material.code.tool.util.ToastUtils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.xml.parsers.DocumentBuilderFactory

@SuppressLint("ViewConstructor")
class AliIconSaveDialog(
    context: Context, private val icon: Icon
) : BottomPopupView(context) {

    override fun getImplLayoutId(): Int {
        return R.layout.dialog_ali_icon_save
    }

    private lateinit var binding: DialogAliIconSaveBinding
    private lateinit var viewModel: AliIconSetColorViewModel
    private var sharp: Sharp? = null
    override fun onCreate() {
        super.onCreate()
        viewModel = AliIconSetColorViewModel()
        binding = DialogAliIconSaveBinding.bind(rootView.findViewById(R.id.rootView))

        viewModel.svg.value = icon.show_svg

        viewModel.svg.observe((context as AppCompatActivity)){
            sharp = Sharp.loadString(it)
            sharp?.into(binding.icon)
        }

        binding.svg.setOnCheckedChangeListener { _, isChecked ->
            binding.sizeLayout.visibility = if (isChecked){
                GONE
            }else {
                VISIBLE
            }
        }

        binding.setWidth.addTextChangedListener {
            if (binding.setWidth.hasFocus()) {
                binding.setHeight.text = it
            }
        }

        binding.setHeight.addTextChangedListener {
            if (binding.setHeight.hasFocus()) {
                binding.setWidth.text = it
            }
        }

        binding.iconName.setText(icon.name)
        binding.setWidth.setText(icon.width.toString())
        binding.setHeight.setText(icon.height.toString())
        binding.savePath.setText(MaterialCodeToolApplication.setting!!.iconSavePath)
        binding.savePath.apply {
            setSelection(text.toString().length - 1)
        }

        binding.colorEdit.setOnFeedbackListener {
            XPopup.Builder(it.context)
                .moveUpToKeyboard(true)
                .hasBlurBg(true)
                .isViewMode(true)
                .isDestroyOnDismiss(true)
                .asCustom(AliIconSetColorDialog(it.context, viewModel))
                .show()
        }

        binding.saveButton.setOnFeedbackListener {
            val path = binding.savePath.text
            if (TextUtils.isEmpty(path)){
                ToastUtils.toast(R.string.please_input_path)
                return@setOnFeedbackListener
            }
            val file = File(path.toString())
            if (!file.exists()){
                file.mkdirs()
            }
            val name = "${binding.iconName.text}"
            if (TextUtils.isEmpty(name)){
                ToastUtils.toast(R.string.please_input_name)
                return@setOnFeedbackListener
            }
            if (binding.svg.isChecked){
                val svgName = "${name}.svg"
                try {
                    IOUtils.writeBytes("$path/$svgName", viewModel.svg.value.toString().toByteArray())
                    ToastUtils.toast(R.string.save_success)
                }catch (e: Exception){
                    ToastUtils.toast("${context.getString(R.string.save_fail)}: $e")
                }
            }else if (binding.png.isChecked){
                val pngName = "${name}.png"
                val drawable = sharp!!.drawable
                val width = binding.setWidth.text
                val height = binding.setHeight.text
                if (TextUtils.isEmpty(width) || TextUtils.isEmpty(height)){
                    ToastUtils.toast(R.string.please_input_wh)
                    return@setOnFeedbackListener
                }
                val bitmap =
                    ImageConverter.drawableToBitmap(drawable, width.toString().toInt(), height.toString().toInt())
                try {
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                    IOUtils.writeBytes("$path/$pngName", byteArrayOutputStream.toByteArray())
                    ToastUtils.toast(R.string.save_success)
                }catch (e: Exception){
                    ToastUtils.toast("${context.getString(R.string.save_fail)}: $e")
                }
            }else {
                val xmlName = "${name}.xml"
                val xmlPath = "$path/$xmlName"

                val byteArrayInputStream = ByteArrayInputStream(viewModel.svg.value.toString().toByteArray())
                val documentBuilderFactory = DocumentBuilderFactory.newInstance()
                val builder = documentBuilderFactory.newDocumentBuilder()
                val parse = builder.parse(byteArrayInputStream)
                val documentElement = parse.documentElement

                val attributes = documentElement.attributes
                for (i in 0 until  attributes.length){
                    val element = attributes.item(i).ownerDocument.documentElement
                    println(element.tagName)
                }
                //TODO SVG to Vector
            }
        }
    }

}
