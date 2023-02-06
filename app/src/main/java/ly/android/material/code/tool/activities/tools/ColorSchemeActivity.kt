package ly.android.material.code.tool.activities.tools

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ly.android.io.common.IOUtils
import ly.android.material.code.tool.R
import ly.android.material.code.tool.common.clip
import ly.android.material.code.tool.ui.theme.MaterialCodeToolTheme
import ly.android.material.code.tool.util.ToastUtils
import kotlin.math.abs
import kotlin.math.pow

class ColorSchemeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialCodeToolTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ColorFrame()
                }
            }
        }
    }

    @Composable
    private fun ColorFrame() {
        val colors by remember {
            mutableStateOf(
                arrayOf(
                    "colors/red",
                    "colors/pink",
                    "colors/purple",
                    "colors/deeppurple",
                    "colors/indigo",
                    "colors/blue",
                    "colors/lightblue",
                    "colors/cyan",
                    "colors/teal",
                    "colors/green",
                    "colors/lightgreen",
                    "colors/lime",
                    "colors/yellow",
                    "colors/amber",
                    "colors/orange",
                    "colors/deeporange",
                    "colors/brown",
                    "colors/grey",
                    "colors/bluegrey"
                )
            )
        }

        val position = remember {
            mutableStateOf(0)
        }

        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.size(16.dp))
            //TabView
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
            ) {
                for (index in colors.indices) {
                    val bytes = readAssets(fileName = colors[index])
                    if (bytes != null) {
                        Spacer(
                            modifier = Modifier.size(
                                if (index == 0) {
                                    51.dp
                                } else {
                                    8.dp
                                }
                            )
                        )
                        ColorTab(
                            position = position,
                            index = index,
                            colorFile = String(bytes)
                        )
                    } else {
                        finish()
                        ToastUtils.toast(R.string.data_error)
                        return
                    }
                }
                Spacer(modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.size(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Spacer(modifier = Modifier.size(51.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    ColorToolbar(position = position, colors = colors)
                }
                Spacer(modifier = Modifier.size(16.dp))
                ColorListView(
                    position = position,
                    colors = colors
                )
            }
            Spacer(modifier = Modifier.size(16.dp))
        }
    }

    @Composable
    private fun readAssets(fileName: String): ByteArray? {
        val open = LocalContext.current.assets.open(fileName)
        return try {
            IOUtils.readBytes(open)
        } catch (e: Exception) {
            null
        }
    }

    @Composable
    private fun ColorTab(
        position: MutableState<Int>,
        index: Int,
        onClick: () -> Unit = {},
        colorFile: String
    ) {
        val colorTab = colorFile.substring(
            colorFile.indexOf("  ") + 2, colorFile.indexOf(";")
        )
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(
                    color = Color(
                        android.graphics.Color.parseColor(
                            colorTab
                        )
                    ),
                    shape = RoundedCornerShape(25.dp)
                )
                .clip(
                    shape = RoundedCornerShape(25.dp)
                )
                .clickable {
                    position.value = index
                    onClick()
                }
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(visible = index == position.value) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_paint_pen),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    @Composable
    private fun ColorToolbar(
        position: MutableState<Int>,
        colors: Array<String>
    ) {
        val readAssets = readAssets(fileName = colors[position.value])
        if (readAssets != null) {
            val colorFile = String(readAssets)
            val colorTab = colorFile.substring(
                colorFile.indexOf("  ") + 2, colorFile.indexOf(";")
            )
            val tabText = colorFile.substring(colorFile.indexOf(";") + 1, colorFile.indexOf("("))
            Text(
                text = tabText.uppercase(), color = Color(
                    android.graphics.Color.parseColor(
                        colorTab
                    )
                ), style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.size(16.dp))
        }
    }

    @Composable
    private fun ColorListView(
        position: MutableState<Int>,
        colors: Array<String>
    ) {
        val readAssets = readAssets(fileName = colors[position.value])
        if (readAssets != null) {
            val colorFile = String(readAssets)
            val data = colorFile.substring(colorFile.indexOf("(") + 1, colorFile.indexOf(")"))
            val colorList = data.split("  ")
            LazyColumn {
                items(colorList.size) { index ->
//                    val element = colorList[index]
//                    val color = element.substring(element.indexOf("#"))
                    ColorItem(colorList[index], index)
                    Spacer(modifier = Modifier.size(8.dp))
                }
            }
        }
    }

    @Composable
    private fun ColorItem(
        data: String, index: Int
    ) {
        val number = data.substring(0, data.indexOf("#")).toInt()
        val colorString = data.substring(data.indexOf("#"))
        val textColor = if (computeContrastBetweenColors(
                bg = android.graphics.Color.parseColor(
                    colorString
                ), fg = android.graphics.Color.WHITE
            ) > 3f
        ) {
            Color.White
        }else {
            Color.Black
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(
                        android.graphics.Color.parseColor(
                            colorString
                        )
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                .clip(
                    shape = RoundedCornerShape(12.dp)
                )
                .height(120.dp)
                .clickable {
                    clip(colorString)
                    ToastUtils.toast(R.string.copied_color)
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "$number",
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = colorString,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )
                AnimatedVisibility(visible = number == 500 || number == 700) {
                    Column(
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.fillMaxWidth().fillMaxHeight()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_bucket),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            colorFilter = ColorFilter.tint(textColor)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun computeContrastBetweenColors(bg: Int, fg: Int): Float {
        var bgR = android.graphics.Color.red(bg) / 255f
        var bgG = android.graphics.Color.green(bg) / 255f
        var bgB = android.graphics.Color.blue(bg) / 255f
        bgR = if (bgR < 0.03928f) bgR / 12.92f else ((bgR + 0.055f) / 1.055f).toDouble().pow(2.4)
            .toFloat()
        bgG = if (bgG < 0.03928f) bgG / 12.92f else ((bgG + 0.055f) / 1.055f).toDouble().pow(2.4)
            .toFloat()
        bgB = if (bgB < 0.03928f) bgB / 12.92f else ((bgB + 0.055f) / 1.055f).toDouble().pow(2.4)
            .toFloat()
        val bgL = 0.2126f * bgR + 0.7152f * bgG + 0.0722f * bgB
        var fgR = android.graphics.Color.red(fg) / 255f
        var fgG = android.graphics.Color.green(fg) / 255f
        var fgB = android.graphics.Color.blue(fg) / 255f
        fgR = if (fgR < 0.03928f) fgR / 12.92f else ((fgR + 0.055f) / 1.055f).toDouble().pow(2.4)
            .toFloat()
        fgG = if (fgG < 0.03928f) fgG / 12.92f else ((fgG + 0.055f) / 1.055f).toDouble().pow(2.4)
            .toFloat()
        fgB = if (fgB < 0.03928f) fgB / 12.92f else ((fgB + 0.055f) / 1.055f).toDouble().pow(2.4)
            .toFloat()
        val fgL = 0.2126f * fgR + 0.7152f * fgG + 0.0722f * fgB
        return abs((fgL + 0.05f) / (bgL + 0.05f))
    }
}