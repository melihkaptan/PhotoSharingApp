package com.example.fotografpaylasmafirebase

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_sharing_photo.*
import java.util.*
import kotlin.collections.HashMap

class SharingPhotoActivity : AppCompatActivity() {

    var pickedImage: Uri? = null
    var pickedBitmap: Bitmap? = null
    lateinit var storage: FirebaseStorage
    lateinit var auth: FirebaseAuth
    lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sharing_photo)

        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
    }

    fun share(view: View) {

        //Depolama işlemleri

        val reference = storage.reference // Storage genel referansı
        val imageName = "{${UUID.randomUUID()}}.jpg"         //UUID -> Universal unique id
        val storeImage = reference.child("images").child(imageName)

        pickedImage?.let { it ->
            storeImage.putFile(it).addOnSuccessListener { taskSnapshot ->
                val storedImageReference = storage.reference.child("images").child(imageName)
                storedImageReference.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    val commentText = editTextComment.text.toString()
                    val uploadDate = Timestamp.now()
                    val userEmail = auth.currentUser?.email.toString()

                    //Veritabanı İşlemleri

                    val postHashMap = hashMapOf<String, Any>()
                    postHashMap["imageUrl"] = downloadUrl
                    postHashMap["comment"] = commentText
                    postHashMap["date"] = uploadDate
                    postHashMap["email"] = userEmail

                    database.collection("Post").add(postHashMap).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Kaydetme işlemleri başarılı", Toast.LENGTH_LONG)
                                .show()
                            finish()
                        }
                    }.addOnFailureListener { e ->
                        Toast.makeText(this, e.localizedMessage, Toast.LENGTH_LONG).show()
                    }
                }.addOnFailureListener { e ->
                    Toast.makeText(this, e.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun pickImage(view: View) {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //izin verilmemis
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_EXTERNAL_STORAGE_REQUEST_CODE
            )
        } else {
            //izin zaten varsa
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            pickedImage = data.data
            pickedImage?.let {
                if (Build.VERSION.SDK_INT >= 28) {
                    val source = ImageDecoder.createSource(this.contentResolver, it)
                    pickedBitmap = ImageDecoder.decodeBitmap(source)
                    imageView.setImageBitmap(pickedBitmap)

                } else {
                    pickedBitmap =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, it)
                    imageView.setImageBitmap(pickedBitmap)
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        const val READ_EXTERNAL_STORAGE_REQUEST_CODE = 1
        const val GALLERY_REQUEST_CODE = 2
    }
}
