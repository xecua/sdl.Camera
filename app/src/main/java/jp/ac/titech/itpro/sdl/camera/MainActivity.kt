package jp.ac.titech.itpro.sdl.camera

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private var currentPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val photoButton = findViewById<Button>(R.id.photo_button)
        photoButton.setOnClickListener { v: View? ->
            val distFile = try {
                createTempFile()
            } catch (e: Exception) {
                return@setOnClickListener
            }
            currentPhotoUri = FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.provider", distFile)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
                it.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri)
                it.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            }
            val manager = packageManager
            @SuppressLint("QueryPermissionsNeeded") val activities = manager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            if (activities.isNotEmpty()) {
                startActivityForResult(intent, REQ_PHOTO)
            } else {
                Toast.makeText(this@MainActivity, R.string.toast_no_activities, Toast.LENGTH_LONG).show()
            }
        }
    }

    @Throws(IOException::class)
    private fun createTempFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        return File.createTempFile("temp_${timeStamp}", ".png", cacheDir)
    }

    private fun showPhoto() {
        if (currentPhotoUri == null) {
            return
        }
        val photoView = findViewById<ImageView>(R.id.photo_view)
        photoView.setImageURI(currentPhotoUri)
    }

    override fun onResume() {
        super.onResume()
        showPhoto()
    }

    companion object {
        private const val REQ_PHOTO = 1234
    }
}