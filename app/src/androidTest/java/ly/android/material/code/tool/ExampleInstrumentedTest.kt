package ly.android.material.code.tool

import android.net.Uri
import android.text.Html
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("ly.android.material.code.tool", appContext.packageName)
    }

    @Test
    fun myTest(){

    }

    @Test
    fun androidEncodeTest(){
        println(Html.escapeHtml("<a>"))
    }
}