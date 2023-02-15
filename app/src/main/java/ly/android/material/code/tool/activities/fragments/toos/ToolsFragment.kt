package ly.android.material.code.tool.activities.fragments.toos

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ly.android.material.code.tool.R
import ly.android.material.code.tool.activities.tools.*
import ly.android.material.code.tool.activities.tools.postdev.PostDevActivity
import ly.android.material.code.tool.databinding.FragmentToolsBinding
import ly.android.material.code.tool.ui.base.BaseFragment
import ly.android.material.code.tool.ui.common.bind
import ly.android.material.code.tool.ui.theme.MaterialCodeToolTheme

class ToolsFragment : BaseFragment() {

    private val binding by bind(FragmentToolsBinding::inflate)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = ToolsFragment()
    }

    override fun initView(root: View) {
        super.initView(root)

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
    fun ScrollView(){
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.size(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.size(16.dp))
                FunctionView(modifier = Modifier.weight(1f), iconId = R.drawable.ic_photo, titleId = R.string.ali_icon) {
                    val intent = Intent(requireContext(), AliIconActivity::class.java)
                    requireActivity().startActivity(intent)
                }
                Spacer(modifier = Modifier.size(8.dp))
                FunctionView(modifier = Modifier
                    .weight(1f), iconId = R.drawable.ic_color_scheme, titleId = R.string.md_color_scheme) {
                    val intent = Intent(requireContext(), ColorSchemeActivity::class.java)
                    requireActivity().startActivity(intent)
                }
                Spacer(modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.size(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.size(16.dp))
                FunctionView(modifier = Modifier.weight(1f), iconId = R.drawable.ic_color_picker, titleId = R.string.color_picker) {
                    val intent = Intent(requireContext(), ColorPickerActivity::class.java)
                    requireActivity().startActivity(intent)
                }
                Spacer(modifier = Modifier.size(8.dp))
                FunctionView(modifier = Modifier
                    .weight(1f), iconId = R.drawable.ic__post_dev, titleId = R.string.post_dev) {
                    val intent = Intent(requireContext(), PostDevActivity::class.java)
                    requireActivity().startActivity(intent)
                }
                Spacer(modifier = Modifier.size(16.dp))
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview(){
        MaterialCodeToolTheme {
            ScrollView()
        }
    }

}

@Composable
fun FunctionView(
    modifier: Modifier = Modifier,
    @DrawableRes iconId: Int,
    @StringRes titleId: Int,
    onClick: () -> Unit
) = Card(
    modifier = modifier
        .background(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(12.dp)
        )
        .clip(
            RoundedCornerShape(12.dp)
        )
        .clickable {
            onClick()
        }
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconId),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = stringResource(id = titleId),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ItemPreview(){
    MaterialCodeToolTheme {
        FunctionView(iconId = R.drawable.ic_logo, titleId = R.string.app_name) {
            
        }
    }
}