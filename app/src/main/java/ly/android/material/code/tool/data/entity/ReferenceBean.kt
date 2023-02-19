package ly.android.material.code.tool.data.entity

data class ReferenceBean(
    val title: String?,
    val content: String?,
    val author: String? = null,
    val referenceFileType: ReferenceFileType = ReferenceFileType.CODE
)

enum class ReferenceFileType{
    CODE, MD
}