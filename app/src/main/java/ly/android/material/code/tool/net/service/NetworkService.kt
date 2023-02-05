package ly.android.material.code.tool.net.service

import ly.android.material.code.natives.MaterialCodeToolNative
import ly.android.material.code.tool.net.factory.ApiFactory

/**
 * 网络服务类
 */
object NetworkService {
    // 接口API服务(挂起)
    val encodeApi by lazy {
        ApiFactory.createEncodeService(MaterialCodeToolNative.getIp(), EncodeApiService::class.java)
    }

    val aliIconApi by lazy {
        ApiFactory.createAliIconService("https://www.iconfont.cn", AliIconApiService::class.java)
    }
}