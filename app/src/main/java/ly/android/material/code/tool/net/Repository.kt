package ly.android.material.code.tool.net

import ly.android.material.code.tool.net.pojo.BaseBean
import ly.android.material.code.tool.net.pojo.SearchAliIconBean
import ly.android.material.code.tool.net.service.NetworkService


/**
 * 接口资源
 * @author ssq
 */
object Repository {

    /**
     * 预处理数据(错误)
     */
    private fun <T : BaseBean> preprocessData(baseBean: T): T =
        if (baseBean.status == 200) {// 成功
            // 返回数据
            baseBean
        } else {// 失败
            // 抛出接口异常
            throw ApiException(status = baseBean.status, message = baseBean.msg)
        }


    suspend fun getHtml(): String = NetworkService.aliIconApi.getCookie()

    suspend fun searchIcon(searchAliIconBean: SearchAliIconBean) = NetworkService
        .aliIconApi.searchIcon(searchAliIconBean)
}