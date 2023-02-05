package ly.android.material.code.tool.ui.dialogs

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lxj.xpopup.core.BottomPopupView
import ly.android.material.code.tool.R
import ly.android.material.code.tool.data.AliIconSetColorViewModel
import ly.android.material.code.tool.databinding.DialogAliIconSetColorsBinding
import ly.android.material.code.tool.ui.theme.MaterialCodeToolTheme
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.ByteArrayInputStream
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Source
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

@SuppressLint("ViewConstructor")
class AliIconSetColorDialog(
    context: Context, private val viewModel: AliIconSetColorViewModel
) : BottomPopupView(context){

    override fun getImplLayoutId(): Int {
        return R.layout.dialog_ali_icon_set_colors
    }

    private val colorKey = "fill"
    private lateinit var binding: DialogAliIconSetColorsBinding
    override fun onCreate() {
        super.onCreate()
        binding = DialogAliIconSetColorsBinding.bind(rootView.findViewById(R.id.composeView))

        binding.composeView.setContent {
            MaterialCodeToolTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = Color.Transparent
                ) {
                    ContentView()
                }
            }
        }

    }

    private lateinit var pathList: NodeList
    private lateinit var documentElement: Element
    @Composable
    fun ContentView(){
//        try {
//
//        }catch (e: Exception){
//            e.printStackTrace()
//            Text(text = stringResource(id = R.string.analysis_fail))
//        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    shape = RoundedCornerShape(30.dp)
                )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(text = stringResource(id = R.string.edit_color), style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.size(16.dp))
                val byteArrayInputStream = ByteArrayInputStream(viewModel.svg.value.toString().toByteArray())
                val documentBuilderFactory = DocumentBuilderFactory.newInstance()
                val builder = documentBuilderFactory.newDocumentBuilder()
                val parse = builder.parse(byteArrayInputStream)
                documentElement = parse.documentElement
                pathList = documentElement.getElementsByTagName("path")
                for (i in 0 until pathList.length){
                    val element = pathList.item(i) as Element
                    //颜色编辑视图
                    EditView(index = i, element)
                }
                Spacer(modifier = Modifier.size(8.dp))
                Button(onClick = {
                    viewModel.svg.value = transform(documentElement)
                    dismiss()
                },
                    modifier = Modifier.fillMaxWidth()) {
                    Text(text = stringResource(id = R.string.save))
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun EditView(
        index: Int,
        element: Element
    ){
        var text by remember {
            mutableStateOf(element.getAttribute(colorKey))
        }
        var color by remember {
            mutableStateOf(Color.Transparent)
        }

        color = try {
            Color(android.graphics.Color.parseColor(text))
        }catch (e: Exception){
            e.printStackTrace()
            Color.Transparent
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Path ${index + 1}: ", style = MaterialTheme.typography.bodyLarge)
            Spacer(
                modifier = Modifier
                    .size(16.dp)
                    .background(color = color)
                    .clip(
                        RoundedCornerShape(8.dp)
                    )
            )
            Spacer(modifier = Modifier.size(8.dp))
            OutlinedTextField(
                value = text,
                onValueChange = {
                                text = it.uppercase()
                    (pathList.item(index) as Element).setAttribute(colorKey, text)
                },
                label = {
                    Text(text = stringResource(id = R.string.color))
                },
                singleLine = true
            )
        }
    }

    private fun transform(node: Node): String{
        val transFactory = TransformerFactory.newInstance()
        val transformer = transFactory.newTransformer()
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8")
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no")
        val writer = StringWriter()
        val source: Source = DOMSource(node)
        val result = StreamResult(writer)
        transformer.transform(source, result)
        return writer.toString()
    }
}