package com.gioppl.messageproving

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import cn.smssdk.EventHandler
import cn.smssdk.SMSSDK

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initSDK()
    }

    private fun initSDK() {
        val eh = object : EventHandler() {

            override fun afterEvent(event: Int, result: Int, data: Any?) {

                val msg = Message()
                msg.arg1 = event
                msg.arg2 = result
                msg.obj = data
                handler.sendMessage(msg)

            }

        }
        SMSSDK.registerEventHandler(eh)

    }

    public fun MessageConfirm(view:View){
        object : Thread() {
            override fun run() {
                try {
                    SMSSDK.getVerificationCode("86", "15620520750")
                    toast("短信发送成功")
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                }
            }
        }.start()
    }

    private var handler = object : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val event = msg.arg1
            val result = msg.arg2
            val data = msg.obj
            Log.e("event", "event=" + event)
            if (result == SMSSDK.RESULT_COMPLETE) {
                //短信注册成功后，返回MainActivity,然后提示新好友
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功
                    toast("提交验证码成功")
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        Toast.makeText(applicationContext, "依然走短信验证", Toast.LENGTH_SHORT).show()
                    }

                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    Toast.makeText(applicationContext, "验证码已经发送", Toast.LENGTH_SHORT).show()
//                        textView2.setText("验证码已经发送")
                    toast("验证码已经发送")
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {//返回支持发送验证码的国家列表
//                        Toast.makeText(applicationContext, "获取国家列表成功", Toast.LENGTH_SHORT).show()
//                        countryTextView.setText(data.toString())
                    println("+++" + applicationContext)
                } else if (event == SMSSDK.RESULT_ERROR) {
                    Toast.makeText(applicationContext, "------", Toast.LENGTH_SHORT).show()
                }
            } else {
                (data as Throwable).printStackTrace()
                Toast.makeText(applicationContext, "错误" + data, Toast.LENGTH_SHORT).show()
            }

        }

    }

    public fun ProvingMessage(view: View){
        var  ed :EditText= findViewById(R.id.ed_main) as EditText
        SMSSDK.submitVerificationCode("86", "15620520750", ed.getText().toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        SMSSDK.unregisterAllEventHandler()
    }

    private fun toast(msg:String){
        Toast.makeText(this@MainActivity,msg,Toast.LENGTH_SHORT).show()
    }


}
