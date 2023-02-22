package ly.android.material.code.tool.activities.tools

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ly.android.material.code.tool.R
import ly.android.material.code.tool.databinding.ActivityHexadecimalConversionBinding
import ly.android.material.code.tool.ui.common.bind
import ly.android.material.code.tool.ui.theme.MaterialCodeToolTheme
import java.lang.Double
import java.lang.Long

class HexadecimalConversionActivity : AppCompatActivity() {

    private val binding by bind(ActivityHexadecimalConversionBinding::inflate)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)

        supportActionBar?.apply {
            this.setTitle(R.string.hexadecimal_conversion)
            this.setSubtitle(R.string.hexadecimal_conversion_tip)
            this.setDisplayHomeAsUpEnabled(true)
        }

        binding.composeView.setContent {
            MaterialCodeToolTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ScrollView()
                }
            }
        }

    }

    @Composable
    private fun ScrollView() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            val binaryValue = remember {
                mutableStateOf("0")
            }
            val octalValue = remember {
                mutableStateOf("0")
            }
            val decimalValue = remember {
                mutableStateOf("0")
            }
            val hexadecimalValue = remember {
                mutableStateOf("0")
            }
            Spacer(modifier = Modifier.size(16.dp))
            BinaryInputView(binaryValue, octalValue, decimalValue, hexadecimalValue)
            Spacer(modifier = Modifier.size(16.dp))
            OctalInputView(binaryValue, octalValue, decimalValue, hexadecimalValue)
            Spacer(modifier = Modifier.size(16.dp))
            DecimalInputView(binaryValue, octalValue, decimalValue, hexadecimalValue)
            Spacer(modifier = Modifier.size(16.dp))
            HexInputView(binaryValue, octalValue, decimalValue, hexadecimalValue)
            Spacer(modifier = Modifier.size(16.dp))
        }
    }

    @Composable
    private fun BinaryInputView(
        binaryValue: MutableState<String>,
        octalValue: MutableState<String>,
        decimalValue: MutableState<String>,
        hexadecimalValue: MutableState<String>
    ) {
        InputView(
            title = stringResource(id = R.string.binary),
            binaryValue,
            "^[01]*$",
            onTextChangedListener = { editable ->
                val binToDec = Long.valueOf(editable, 2)
                octalValue.value = Long.toOctalString(binToDec)
                decimalValue.value = binToDec.toString()
                hexadecimalValue.value = Long.toHexString(binToDec)
            }
        )
    }

    @Composable
    private fun OctalInputView(binaryValue: MutableState<String>,
                               octalValue: MutableState<String>,
                               decimalValue: MutableState<String>,
                               hexadecimalValue: MutableState<String>) {
        InputView(
            title = stringResource(id = R.string.octal),
            octalValue,
            "^[01234567]*$",
            onTextChangedListener = { editable ->
                val otcToDec = Long.valueOf(editable, 8)
                binaryValue.value = Long.toBinaryString(otcToDec)
                decimalValue.value = otcToDec.toString()
                hexadecimalValue.value = Long.toHexString(otcToDec)
            }
        )
    }

    @Composable
    private fun DecimalInputView(binaryValue: MutableState<String>,
                                 octalValue: MutableState<String>,
                                 decimalValue: MutableState<String>,
                                 hexadecimalValue: MutableState<String>) {
        InputView(
            title = stringResource(id = R.string.decimal),
            decimalValue,
            "^[0123456789]*$",
            onTextChangedListener = { editable ->
                val dec = java.lang.Long.valueOf(editable, 10)
                binaryValue.value = Long.toBinaryString(dec)
                octalValue.value = Long.toOctalString(dec)
                hexadecimalValue.value = Long.toHexString(dec)
            }
        )
    }

    @Composable
    private fun HexInputView(binaryValue: MutableState<String>,
                             octalValue: MutableState<String>,
                             decimalValue: MutableState<String>,
                             hexadecimalValue: MutableState<String>) {
        InputView(
            title = stringResource(id = R.string.hexadecimal),
            hexadecimalValue,
            "^[0123456789abcdefABCDEF]*$",
            onTextChangedListener = { editable ->
                val hexToDec = java.lang.Long.valueOf(editable, 16)
                binaryValue.value = Long.toBinaryString(hexToDec)
                octalValue.value = Long.toOctalString(hexToDec)
                decimalValue.value = hexToDec.toString()
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun InputView(
        title: String,
        valueState: MutableState<String>,
        pattern: String,
        onTextChangedListener: (String) -> Unit = {}
    ) {
        Row {
            Spacer(modifier = Modifier.size(16.dp))
            OutlinedTextField(
                label = {
                    Text(text = title)
                },
                value = valueState.value,
                onValueChange = {
                    val s = if (it == "") {
                        "0"
                    } else {
                        it
                    }
                    if (s.matches(Regex(pattern)) && charTimes(s, '.') <= 1 && !s.startsWith(".")){
                        if (s.contains(".")){
                            if (s.length - s.indexOf(".") <= 17){
                                valueState.value = s
                                try {
                                    onTextChangedListener(s)
                                }catch (e: Exception){
                                    e.printStackTrace()
                                }
                            }
                        }else {
                            valueState.value = s
                            try {
                                onTextChangedListener(s)
                            }catch (e: Exception){
                                e.printStackTrace()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.size(16.dp))
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

    private fun charTimes(content: String, target: Char): Int {
        var times = 0
        val length = content.length
        for (i in 0 until length){
            if (content.get(i) == target) {
                ++times
            }
        }
        return times
    }
}