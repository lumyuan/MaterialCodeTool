package ly.android.material.code.tool.net.pojo

open class BaseBean(val status: Int? = null, val msg: String? = null){
    override fun toString(): String {
        return "[status: $status, msg: $msg]"
    }
}