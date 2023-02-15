package ly.android.material.code.tool.data.entity

data class FormDataBean(
    var isChecked: Boolean = false,
    var key: String? = null,
    var value: String? = null,
    var file: String? = null,
    var type: ParamType,
    var bodyType: BodyParamType = BodyParamType.TEXT
)

enum class BodyParamType {
    TEXT, FILE
}