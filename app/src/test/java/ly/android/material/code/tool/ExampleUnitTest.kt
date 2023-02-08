package ly.android.material.code.tool

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import org.junit.Test

import org.junit.Assert.*
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        println(System.currentTimeMillis())
    }

    private val json = "{\n" +
            "    \"reason\":\"成功的返回\",\n" +
            "    \"result\":{\n" +
            "        \"stat\":\"1\",\n" +
            "        \"data\":[\n" +
            "            {\n" +
            "                \"uniquekey\":\"4aa8ba3a720a12f1ac9b0ea2b7c1bb8b\",\n" +
            "                \"title\":\"中国贸促会：加强投资 推动“一带一路”建设\",\n" +
            "                \"date\":\"2018-04-12 18:09\",\n" +
            "                \"category\":\"头条\",\n" +
            "                \"author_name\":\"央视网\"\n" +
            "\n" +
            "            },      \n" +
            "            {\n" +
            "                \"uniquekey\":\"d53883da51e856c2c96c8b219c6d06e7\",\n" +
            "                \"title\":\"冯俊：人类命运共同体 全球治理的中国方案\",\n" +
            "                \"date\":\"2018-04-12 16:06\",\n" +
            "                \"category\":\"头条\"\n" +
            "\n" +
            "            }\n" +
            "        ]\n" +
            "    }\n" +
            "\n" +
            "}"

    @Test
    fun jsonTest(){
        analyticJson(json)
    }

    private val gson = Gson()
    private val objType = object : TypeToken<Map<String, Any?>>(){}.type
    private val arrayType = object : TypeToken<List<String?>>(){}.type
    fun analyticJson(data: String?){
        println(data)
        if (data == null)
            return
        if (data.startsWith("[")){ //array
            val array = gson.fromJson<List<String?>>(data, arrayType)
            array.forEach {
                analyticJson(it)
            }
        }else if (data.startsWith("{")){ //object
            val map = gson.fromJson<Map<String, Any?>>(data, objType)
            map.forEach { (k, v) ->
                analyticJson(v.toString())
            }
        }else { //value

        }
    }
}