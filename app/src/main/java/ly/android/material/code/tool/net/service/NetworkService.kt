package ly.android.material.code.tool.net.service

import ly.android.material.code.natives.MaterialCodeToolNative
import ly.android.material.code.tool.net.factory.ApiFactory

/**
 * 网络服务类
 */
object NetworkService {
    // 接口API服务(挂起)
    val api by lazy {
        ApiFactory.createService(MaterialCodeToolNative.getIp(), ApiService::class.java)
    }
}