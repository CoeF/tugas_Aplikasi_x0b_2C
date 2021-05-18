package ubaidillah.qowwim.aplikasi_x0b_2c

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MediaHelper {
    var namaFile = ""
    var fileUri = Uri.parse("")
    val RC_CAMERA = 100
    fun getMyFileName() : String{
        return this.namaFile
    }

    fun getRcCamera() : Int{
        return this.RC_CAMERA
    }
    fun getOutputMediaFile() : File?{
        val mediaStorageDir = File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DCIM),"aplikasix0b")
        if(!mediaStorageDir.exists())
            if(!mediaStorageDir.mkdirs()){
                Log.e("mkdir", "Gagal membuat direktori")
            }
        val mediaFile = File(mediaStorageDir.path+ File.separator+"${this.namaFile}")
        return  mediaFile
    }
    fun getOutputMediaFileUri(): Uri{
        val timeStamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
        this.namaFile = "DC_${timeStamp}.jpg"
        this.fileUri = Uri.fromFile(getOutputMediaFile())
        return this.fileUri
    }
    fun bitmapToString(bmp : Bitmap) : String{
        val outputStream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG,60,outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
    fun getBitmapToString(imV : ImageView, uri : Uri) : String{
        var bmp = Bitmap.createBitmap(100,100,Bitmap.Config.ARGB_8888)
        bmp = BitmapFactory.decodeFile(this.fileUri.path)
        var dim = 720
        if(bmp.height > bmp.width){
            bmp = Bitmap.createScaledBitmap(bmp,(bmp.width*dim).div(bmp.height),dim,true)
        }
        else{
            bmp = Bitmap.createScaledBitmap(bmp,dim,(bmp.height*dim).div(bmp.width),true)
        }

        imV.setImageBitmap(bmp)
        return bitmapToString(bmp)
        return ""
    }
}