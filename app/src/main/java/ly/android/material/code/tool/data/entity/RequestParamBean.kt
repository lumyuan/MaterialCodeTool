package ly.android.material.code.tool.data.entity

data class RequestParamBean(
    var isChecked: Boolean = false,
    var key: String? = null,
    var value: String? = null,
    var type: ParamType
)

enum class ParamType{
    ADD, PARAM
}
