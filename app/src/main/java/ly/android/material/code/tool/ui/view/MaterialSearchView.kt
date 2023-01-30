package ly.android.material.code.tool.ui.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.setMargins
import com.google.android.material.card.MaterialCardView
import ly.android.material.code.tool.R
import ly.android.material.code.tool.common.dip2px

class MaterialSearchView : MaterialCardView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr)

    private var textView: EditText
    private var closeView: ImageView

    init {
        //设置动画
        val layoutTransition = LayoutTransition()
        val appearing = ObjectAnimator.ofFloat(null, "alpha", 1F).apply {
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    super.onAnimationStart(animation)
                    showKeyboard()
                }
            })
        }
        val disappearing = ObjectAnimator.ofFloat(null, "alpha", 0F).apply {
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    super.onAnimationStart(animation)
                    //closeKeyboard()
                }
            })
        }
        layoutTransition.setAnimator(LayoutTransition.APPEARING, appearing)
        layoutTransition.setAnimator(LayoutTransition.DISAPPEARING, disappearing)

        val linearLayout = LinearLayout(context).apply {
            setPadding(dip2px(5F), dip2px(5F), dip2px(5F), dip2px(5F))
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            this.layoutTransition = layoutTransition
            orientation = LinearLayout.HORIZONTAL
        }
        val imageView = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(dip2px(20F), dip2px(20F)).apply {
                setMargins(dip2px(10F))
            }
            setImageResource(R.mipmap.ic_search)
            setColorFilter(Color.BLACK)
        }
        textView = AppCompatEditText(context).apply {
            layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                weight = 1F
                marginStart = dip2px(10F)
            }
            setBackgroundResource(R.drawable.shape_search_editor)
            hint = context.getString(R.string.search_app)
            isSingleLine = true
            textSize = 13F
            visibility = GONE
        }
        closeView = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(dip2px(20F), dip2px(20F)).apply {
                marginStart = dip2px(10F)
                marginEnd = dip2px(10F)
            }
            setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            setColorFilter(Color.BLACK)
            visibility = GONE
            this.setOnClickListener {
                textView.setText("")
            }
        }

        linearLayout.addView(imageView)
        linearLayout.addView(textView)
        linearLayout.addView(closeView)
        addView(linearLayout)

//        post{
//            radius = height / 2F
//        }

        this.setOnFeedbackListener {
            setState(VISIBLE)
        }

        textView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                closeView.visibility = if (s.isNotEmpty()) VISIBLE else INVISIBLE
                onTextChangedListener(s)
            }

        })
    }

    fun getText(): Editable{
        return textView.text
    }

    private fun dip2px(dp: Float): Int{
        return context.dip2px(dp)
    }

    private var onTextChangedListener: (CharSequence) -> Unit = {}

    fun setOnTextChangedListener(onTextChangedListener: (CharSequence) -> Unit){
        this.onTextChangedListener = onTextChangedListener
    }

    @FunctionalInterface
    interface OnTextChangedListener{
        fun afterTextChanged(s: Editable)
    }

    private fun closeKeyboard(){
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(textView.windowToken, 0)
    }

    private fun showKeyboard(){
        textView.apply {
            isFocusable = true
        }
    }

    fun setState(state: Int){
        if (textView.visibility == state){
            textView.visibility = GONE
            closeView.visibility = GONE
            layoutParams.width = LayoutParams.WRAP_CONTENT
            requestLayout()
            try {
                closeKeyboard()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }else {
            textView.visibility = VISIBLE
            closeView.visibility = if (textView.text!!.isNotEmpty()) VISIBLE else INVISIBLE
            layoutParams.width = LayoutParams.MATCH_PARENT
            requestLayout()
        }
    }
}