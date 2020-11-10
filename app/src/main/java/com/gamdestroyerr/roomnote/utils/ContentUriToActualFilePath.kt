package com.gamdestroyerr.roomnote.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.fragment.app.FragmentActivity
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

/*Converts the content Uri to InputStream
 and copies/writes it to file obtained from getPhotoFile()
 and finally returns its absolute path */

fun getImageUrlWithAuthority(
    context: Context,
    uri: Uri,
    activity: FragmentActivity
): String? {
    var inputStream: InputStream? = null
    if (uri.authority != null) {
        try {
            inputStream = context.contentResolver.openInputStream(uri)
            val file = getPhotoFile(activity)
            val out = FileOutputStream(file)
            inputStream.use { input ->
                out.use {
                    input?.copyTo(it)
                }
            }
            return file.absolutePath
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } finally {
            try {
                inputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    return null
}

/** Creates a temp file,
 * this file is in app specific storage so other app cannot access it,
 * and starting from android 11 no file manager has access to android
 * directory in internal storage.
 *@param activity Takes a FragmentActivity
 *@return File - Takes a File */
fun getPhotoFile(
    activity: FragmentActivity
): File {
    val privateStorageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val timeStamp: String =
        SimpleDateFormat(
            "yyyy_MM_dd_HH:mm:ss",
            Locale.getDefault()
        ).format(Date())
    val file = File.createTempFile(
        "IMG_${timeStamp}_",
        ".jpg",
        privateStorageDir
    )

    if (Integer.parseInt(file.length().toString()) == 0) {
        file.delete()
    }
    return file
}