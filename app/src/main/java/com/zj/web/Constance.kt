package com.zj.web

import android.content.Context
import com.google.gson.Gson
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

object Constance {

    private const val GAME_JSON_FILENAME = "game_inject.js"
    private const val SCENE_MAIN = "main"
    private const val APP_TYPE_GAME = "game"
    private const val APP_TYPE_SIMPLE = "simple"

//    /**
//     * 拼接水族馆类型js，这里game类给默认值
//     */
//    fun getRealJsInjectStr(context: Context, bean: LuckTimeListBean, scene: String, userCapsule: String = "", ticketCount: Int = 0, ticketPrice: Int = 0): String {
//        // 拼接完成后的数据
//        val jsContent: String = if (ticketCount == 0) {
//            "(function(ns){'use strict';ns.token='${LoginUtils.mCCGameToken}';ns.user=${getUserBeanInfoStr()};ns.apptype='$APP_TYPE_GAME';ns.game=${getUserGameInfo(bean, false)};ns.asset=${getUserAssetStr()};ns.capsule=${getUserCapsule(userCapsule)};ns.appver='${RequestHeaderInfoUtils.getVersionName()}';ns.apihost='${bean.apiHost}';ns.scene='$scene';})(window.cg||(window.cg={}));"
//        } else {
//            "(function(ns){'use strict';ns.token='${LoginUtils.mCCGameToken}';ns.user=${getUserBeanInfoStr()};ns.apptype='$APP_TYPE_GAME';ns.game=${getUserGameInfo(bean, false)};ns.asset=${getUserAssetStr()};ns.capsule=${getUserCapsule(userCapsule)};ns.appver='${RequestHeaderInfoUtils.getVersionName()}';ns.apihost='${bean.apiHost}';ns.scene='$scene';ns.options=${getGameOptions(ticketCount, ticketPrice)};})(window.cg||(window.cg={}));"
//        }
//        return "javascript:$jsContent${getJsReadStr(context)}"
//    }
//
//
//    /**
//     * 拼接小游戏类的数据
//     */
//    fun getRealJsSmallGameStr(context: Context, bean: LuckTimeListBean, scene: String, userCapsule: String = ""): String {
//        // 拼接完成后的数据
//        val jsContent = "(function(ns){'use strict';ns.token='${LoginUtils.mCCGameToken}';ns.user=${getUserBeanInfoStr()};ns.apptype='$APP_TYPE_GAME';ns.game=${getUserGameInfo(bean, true)};ns.asset=${getUserAssetStr()};ns.capsule=${getUserCapsule(userCapsule)};ns.appver='${RequestHeaderInfoUtils.getVersionName()}';ns.apihost='${bean.apiHost}';ns.scene='$scene';})(window.cg||(window.cg={}));"
//        return "javascript:$jsContent${getJsReadStr(context)}"
//    }
//
//
//    /**
//     * 注入h5脚本拼接，拼接需要增加数据模型添加
//     */
//    fun getRealJsWebSpin(context: Context, userCapsule: String = "", chest: Any, apiHost: String = ""): String {
//        val mApiHost = if (apiHost.isNullOrEmpty()) removeApiHost(ClipClapsConstant.getServerAddress) else removeApiHost(apiHost)
//        val jsContent = "(function(ns){'use strict';ns.token='${LoginUtils.token}';ns.user=${getUserBeanInfoStr()};ns.options=${Gson().toJson(chest)};ns.apptype='$APP_TYPE_SIMPLE';ns.asset=${getUserAssetStr()};ns.capsule=${getUserCapsule(userCapsule)};ns.appver='${RequestHeaderInfoUtils.getVersionName()}';ns.apihost='${mApiHost}';ns.scene='$SCENE_MAIN';})(window.cg||(window.cg={}));"
//        return "javascript:$jsContent${getJsReadStr(context)}"
//    }
//
//    /**
//     * 单击小游戏注入问题，不需要注入相关的数据模型
//     */
//    fun getRealJsWebSingleGame(context: Context, userCapsule: String = ""): String {
//        val jsContent = "(function(ns){'use strict';ns.token='${LoginUtils.token}';ns.user=${getUserBeanInfoStr()};ns.apptype='$APP_TYPE_SIMPLE';ns.asset=${getUserAssetStr()};ns.capsule=${getUserCapsule(userCapsule)};ns.appver='${RequestHeaderInfoUtils.getVersionName()}';ns.apihost='${removeApiHost(ClipClapsConstant.getServerAddress)}';ns.scene='$SCENE_MAIN';})(window.cg||(window.cg={}));"
//        return "javascript:$jsContent${getJsReadStr(context)}"
//    }
//
//
//    /**
//     * 获取游戏信息
//     */
//    private fun getUserGameInfo(bean: LuckTimeListBean, isSmallGame: Boolean): String {
//        val userGameInfo: GameInjectionGameBean = if (isSmallGame) {
//            GameInjectionGameBean(bean.group, bean.id.toString(), bean.name, bean.propsPrice, bean.rankDueTime)
//        } else {
//            GameInjectionGameBean("", bean.id.toString(), bean.name, 0, 0)
//        }
//        return Gson().toJson(userGameInfo)
//    }
//
//    /**
//     * 获取测量信息
//     */
//    private fun getInitUserCapsule(): String {
//        val gameJsInjectCapsuleBean = GameJsInjectCapsuleBean(SizeUtils.dp2px(context, 40f), SizeUtils.dp2px(context, 15f), SizeUtils.dp2px(context, 81f), SizeUtils.dp2px(context, 30f))
//        return Gson().toJson(gameJsInjectCapsuleBean)
//    }
//
//    /**
//     * 获取游戏配置
//     */
//    private fun getGameOptions(ticketNum: Int, ticketPrice: Int): String {
//        val gameOptions = GameOptions(ticketNum, ticketPrice)
//        return Gson().toJson(gameOptions)
//    }
//
//    /**
//     * 获取测量距离
//     */
//    private fun getUserCapsule(userCapsule: String = ""): String {
//        return if (userCapsule.isEmpty()) getInitUserCapsule() else userCapsule
//    }
//
//    /**
//     * 返回用户资产信息
//     */
//    fun getUserAssetStr(): String {
//        val cash = try {
//            MoneyFormatUtils.getTwoDecimalDollarsByCents(LoginUtils.centBalance).toFloat()
//        } catch (e: Exception) {
//            0.toFloat()
//        }
//        // 用户资产信息
//        val userAsset = GameJsInjectAssetBean(LoginUtils.coinBalance, cash)
//        return Gson().toJson(userAsset)
//    }
//
//    /**
//     * 返回用户信息
//     */
//    private fun getUserBeanInfoStr(): String {
//        val userBean = GameInjectionUserBean(if (LoginUtils.headpic.isNullOrEmpty().not()) getCompleteImageUrl(LoginUtils.headpic) else "", LoginUtils.userid.toString(), LoginUtils.nickname, LoginUtils.paypal_email, RequestHeaderInfoUtils.getAppSystemLocale(), LoginUtils.countryCode2, LoginUtils.code)
//        return Gson().toJson(userBean)
//    }


    /**
     * 获取本地js注入脚本
     */
    private fun getJsReadStr(context: Context): String {
        return loadFormFile(context, GAME_JSON_FILENAME)
    }

    /**
     * 过滤host
     */
    private fun removeApiHost(url: String): String {
        var apiHost = ""
        if (url.contains("https://")) {
            apiHost = url.replace("https://", "")
        }
        if (url.contains("http://")) {
            apiHost = url.replace("http://", "")
        }
        return apiHost
    }

    @Suppress("SameParameterValue")
    private fun loadFormFile(context: Context, fileName: String): String {
        var jsStr = ""
        var injs: InputStream? = null
        var fromFile: ByteArrayOutputStream? = null
        try {
            injs = context.assets.open(fileName)
            val buff = ByteArray(1024)
            fromFile = ByteArrayOutputStream()
            do {
                val numRead = injs.read(buff)
                if (numRead <= 0) {
                    break
                }
                fromFile.write(buff, 0, numRead)
            } while (true)
            jsStr = fromFile.toString()
            return jsStr
        } catch (e: IOException) {
            e.printStackTrace()
            return jsStr
        } finally {
            injs?.close()
            fromFile?.close()
        }
    }
}