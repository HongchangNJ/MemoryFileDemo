package com.example.memoryfiledemo

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.Parcel
import android.os.RemoteException
import android.util.Log
import androidx.annotation.Nullable
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets.UTF_8


class AshmemService: Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return AshmenBinder()
    }

    inner class AshmenBinder : Binder() {
        override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
            if (code == 8888) {
                try {
                    var pfd = data.readFileDescriptor()
                    var size = data.readInt()
                    var inputStream = FileInputStream(pfd.fileDescriptor)
                    var bytes = ByteArray(1024)
                    inputStream.read(bytes, 0, size)
                    var message = String(bytes, 0, size, UTF_8)
                    Log.d("Han", message)
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }

                return true
            } else {
                return super.onTransact(code, data, reply, flags)
            }
        }
    }
}