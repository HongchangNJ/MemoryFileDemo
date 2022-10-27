package com.example.memoryfiledemo

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.MemoryFile
import android.os.Parcel
import android.os.RemoteException
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import java.io.FileDescriptor


class MainActivity : AppCompatActivity() {

    var mBinder: IBinder? = null
    var mServiceConnection = object: ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            mBinder = p1
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
l
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        var intent = Intent(this, AshmemService::class.java)
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)

        findViewById<View>(R.id.button_send).setOnClickListener {
            sendData()
        }
    }

    private fun sendData() {

        try {
            // 创建匿名共享内存，并写入数据
            var data = "这是从进程A发送到进程B的数据"
            var memoryFile = MemoryFile("", data.toByteArray().size)
            memoryFile.writeBytes(data.toByteArray(), 0, 0, data.toByteArray().size)

            // 获取文件描述符
            var method = MemoryFile::class.java.getDeclaredMethod("getFileDescriptor")
            var fd = method.invoke(memoryFile) as FileDescriptor


            var parcelData = Parcel.obtain()
            parcelData.writeFileDescriptor(fd)
            parcelData.writeInt(data.toByteArray().size) // data.lenght 可以吗？有何区别？

            var reply = Parcel.obtain()
            mBinder?.transact(8888, parcelData, reply, 0)
        } catch (ex: RemoteException) {
            ex.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(mServiceConnection)
    }
}