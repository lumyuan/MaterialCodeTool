package ly.android.material.code.tool.net.pojo.response

data class AliIconBean(
    val code: Int,
    val data: Maps?
)

data class Maps(
    val icons: List<Icon>,
    val count: Int
)

data class Icon(
    val id: Int,
    val name: String,
    val status: Int,
    val is_private: Int,
    val category_id: String,
    val slug: String,
    val unicode: String,
    val width: Int,
    val height: Int,
    val defs: String?,
    val path_attributes: String?,
    val fills: Int,
    val font_class: String,
    val user_id: Int,
    val repositorie_id: String,
    val created_at: String,
    val updated_at: String,
    val svg_hash: String,
    val svg_fill_hash: String,
    val fork_from: String?,
    val deleted_at: String?,
    val show_svg: String?
)