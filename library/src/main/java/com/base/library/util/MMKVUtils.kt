package com.base.library.util

import com.blankj.utilcode.util.SPStaticUtils

/**
 * MMKV 统一存储工具类,以后方便替换成其他的存储方式,只需要替换实现即可
 */
object MMKVUtils {

//    private val mmkv: MMKV

    init {
//        MMKV.initialize(Utils.getApp())
//        mmkv = MMKV.defaultMMKV()
    }

    fun put(key: String, value: Any) {

        when (value) {
            is Boolean -> {
                SPStaticUtils.put(key, value)
//                mmkv.encode(key, value)
            }
            is String -> {
                SPStaticUtils.put(key, value)
//                mmkv.encode(key, value)
            }
            is Int -> {
                SPStaticUtils.put(key, value)
//                mmkv.encode(key, value)
            }

            is Long -> {
                SPStaticUtils.put(key, value)
//                mmkv.encode(key, value)
            }
            is Float -> {
                SPStaticUtils.put(key, value)
//                mmkv.encode(key, value)
            }
            is ByteArray -> {
//                mmkv.encode(key, value)
            }
        }

    }

    fun getBool(key: String) = SPStaticUtils.getBoolean(key, false)
//    fun getBool(key: String) = mmkv.decodeBool(key, false)

    fun getStr(key: String) = SPStaticUtils.getString(key, "")
//    fun getStr(key: String) = mmkv.decodeString(key, "")

    fun getInt(key: String) = SPStaticUtils.getInt(key, 0)
//    fun getInt(key: String) = mmkv.decodeInt(key, 0)

    fun getLong(key: String) = SPStaticUtils.getLong(key, 0)
//    fun getLong(key: String) = mmkv.decodeLong(key, 0)

    fun getFloat(key: String) = SPStaticUtils.getFloat(key, 0f)
//    fun getFloat(key: String) = mmkv.decodeFloat(key, 0f)

//    fun getBytes(key: String) = mmkv.decodeBytes(key)

//    fun removeValueForKey(key: String) {
//        mmkv.removeValueForKey(key)
//    }
//
//    fun removeValuesForKeys(keys: Array<String>) {
//        mmkv.removeValuesForKeys(keys)
//    }

}