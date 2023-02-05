package ly.android.material.code.tool.net.pojo
//"q="+keyword,"sortType=updated_at","page="+page,"pageSize=54","fills=","t="+times,"ctoken="+ctoken
class SearchAliIconBean(
    val q: String?,
    val sortType: String? = "updated_at",
    val page: Int,
    val pageSize: Int = 54,
    val fills: String? = null,
    val t: Long = System.currentTimeMillis(),
    val ctoken: String
)
