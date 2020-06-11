package com.github.freddyyj.foodsearcher.ui.main

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.github.freddyyj.foodsearcher.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import kotlinx.android.synthetic.main.fragment_photo.*

/**
 * A simple [Fragment] subclass.
 * Use the [PhotoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PhotoFragment : Fragment() {
    val REQUEST_IMAGE_CAPTURE = 1
    lateinit var viewModel: PageViewModel
    lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PageViewModel::class.java)
        dispatchTakePictureIntent()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photo, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        takePhotoAgain.setOnClickListener {
            dispatchTakePictureIntent()
        }
        database = FirebaseFirestore.getInstance()
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            //takePictureIntent.resolveActivity(this.packageManager)?.also {
            //    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            //}
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            val image = FirebaseVisionImage.fromBitmap(imageBitmap)
            val detector = FirebaseVision.getInstance().cloudTextRecognizer
            // Or, to change the default settings:
            // val detector = FirebaseVision.getInstance().getCloudTextRecognizer(options)

            // Or, to provide language hints to assist with language detection:
            // See https://cloud.google.com/vision/docs/languages for supported languages
            val result = detector.processImage(image)
                .addOnSuccessListener { firebaseVisionText ->
                    // Task completed successfully
                    viewModel.recognizedText = firebaseVisionText.text
                    resultText.text=viewModel.recognizedText
                    requestTranslation(viewModel.recognizedText)
                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                    // ...
                }
        }
    }
    private var isChanged=0
    fun requestTranslation(text: String){
        Log.i("Translation","translation started")
        val user: HashMap<String, Any> = HashMap()
        user.put("input",text)
        val document=database.collection("translations").document("translations")

        document.update(user)
        document.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("Translation", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                if (isChanged==1){
                    Log.d("translation", "Current data: ${snapshot.data?.getValue("translated")}")
                    val result= snapshot.data?.getValue("translated") as Map<*, *>
                    Log.d("translation",result.get("ko") as String)
                    translatedResult.text=result.get("ko") as String
                    viewModel.translatedText=result.get("ko") as String
                    isChanged=0
                }
                else{
                    isChanged=1
                }
            } else {
                Log.d("translation", "Current data: null")
            }
        }
    }

    companion object {
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PhotoFragment.
     */
    @JvmStatic
    fun newInstance() =
        PhotoFragment().apply {
            arguments = Bundle()
        }
    }
}
