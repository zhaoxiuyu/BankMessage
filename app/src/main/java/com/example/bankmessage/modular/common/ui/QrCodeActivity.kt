package com.example.bankmessage.modular.common.ui

import android.content.Intent
import cn.bingoogolapple.qrcode.core.QRCodeView
import com.base.library.mvvm.core.VMActivity
import com.blankj.utilcode.util.StringUtils
import com.example.bankmessage.R
import kotlinx.android.synthetic.main.activity_qrcode.*

class QrCodeActivity : VMActivity(), QRCodeView.Delegate {

    override fun initArgs(intent: Intent?) = null

    override fun initView() {
        super.initView()
        setContentView(R.layout.activity_qrcode)

        zxingview.setDelegate(this)
    }

    override fun initData() {
        zxingview.startCamera() // 打开后置摄像头开始预览，但是并未开始识别
        zxingview.startSpotAndShowRect()
    }

    override fun onScanQRCodeSuccess(result: String?) {
        if (StringUtils.isEmpty(result)) {
            // 没有扫描到就继续扫描
            zxingview.startSpotAndShowRect()
        } else {
            val intent = Intent()
            intent.putExtra("qrcode", result)
            setResult(1, intent)
            finish()
        }
    }

    override fun onCameraAmbientBrightnessChanged(isDark: Boolean) {
    }

    override fun onScanQRCodeOpenCameraError() {
    }

    override fun onStop() {
        zxingview.stopCamera() // 关闭摄像头预览，并且隐藏扫描框
        super.onStop()
    }

    override fun onDestroy() {
        zxingview.onDestroy() // 销毁二维码扫描控件
        super.onDestroy()
    }
}