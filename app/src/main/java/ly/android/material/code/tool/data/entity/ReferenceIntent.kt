package ly.android.material.code.tool.data.entity

import ly.android.material.code.tool.data.enums.ReferenceLanguage
import ly.android.material.code.tool.data.enums.UrlType

data class ReferenceIntent(
    val title: String,
    val lang: ReferenceLanguage,
    val referenceFileType: ReferenceFileType = ReferenceFileType.CODE,
    val urlType: UrlType
)
