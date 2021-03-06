package com.base.library.base

import androidx.core.content.ContextCompat
import androidx.multidex.MultiDexApplication
import com.base.library.BuildConfig
import com.base.library.R
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.Utils
import com.lxj.xpopup.XPopup
import com.tencent.bugly.Bugly
import org.litepal.LitePal
import rxhttp.RxHttpPlugins
import rxhttp.wrapper.param.RxHttp
import java.io.File

/**
 * 作用: 程序的入口
 */
open class BApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        utilcode()
        XPopup.setPrimaryColor(ContextCompat.getColor(this, R.color.base_sb_pressed))
        initRxHttp()
        LitePal.initialize(this)
        Bugly.init(applicationContext, "ca8945593e", false)
    }

    /**
     * 初始化打印日志
     */
    private fun utilcode() {
        Utils.init(this)
        LogUtils.getConfig().setLogSwitch(BuildConfig.DEBUG)//总开关
            .setConsoleSwitch(BuildConfig.DEBUG)//控制台开关
            .setLogHeadSwitch(BuildConfig.DEBUG)//控制台开关
            .setGlobalTag("IZXY")//全局 Tag
            .setFilePrefix("AndroidUtilCode") // Log 文件前缀
            .setBorderSwitch(BuildConfig.DEBUG)//边框开关
            .stackDeep = 1 //栈深度
    }

    /**
     * 初始化R下Http
     */
    open fun initRxHttp() {
        RxHttp.setDebug(BuildConfig.DEBUG)
        // 目录为 Android/data/{app包名目录}/cache/RxHttpCache
        val cacheDir = File(Utils.getApp().externalCacheDir, "RxHttpCache")
        // 目录,缓存大小10M,默认不缓存,且缓存永久有效
        RxHttpPlugins.setCache(cacheDir, 10 * 1024 * 1024)
    }

}
