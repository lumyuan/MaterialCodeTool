package ly.android.material.code.tool.data.entity

import ly.android.material.code.tool.data.enums.ReferenceLanguage
import ly.android.material.code.tool.data.enums.UrlType

data class HomePageDataBean(
    var fragmentBean: FragmentBean?,
    var title: String?,
    var isShow: Boolean = true
)

data class FragmentBean (
    var url: String?,
    var title: String?,
    var lang: ReferenceLanguage?,
    var urlType: UrlType?,
    var referenceFileType: ReferenceFileType?
)