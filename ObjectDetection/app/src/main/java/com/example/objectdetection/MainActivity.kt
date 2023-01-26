package com.example.objectdetection
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

class MainActivity : AppCompatActivity() {
    private lateinit var image: ImageView
    private lateinit var btnLabelImage: Button
    private lateinit var results: TextView
    private lateinit var imageLabeler: ImageLabeler
    private lateinit var progressDialog: ProgressDialog
    private var imageUri: Uri?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)
        imageLabeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
        image = findViewById(R.id.image)
        btnLabelImage = findViewById(R.id.labelImage)
        results = findViewById(R.id.results)

        btnLabelImage.setOnClickListener {
            if(image.drawable != null)
            {
                val bitmapDrawable = image.drawable as BitmapDrawable
                val bitmap = bitmapDrawable.bitmap
                labelImage(bitmap)
            }
        }
        image.setOnClickListener()
        {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            galleryResult.launch(intent)
        }
    }
    private val galleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback { result ->
        if(result.resultCode == Activity.RESULT_OK){
            val data = result.data
            imageUri = data!!.data
            Glide.with(this).load(imageUri).into(image)
        }else
        {
            Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
        }
    })
    private fun labelImage(bitmap: Bitmap)
    {
        progressDialog.setMessage("Detectando objetos")
        progressDialog.show()
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        imageLabeler.process(inputImage)
            .addOnSuccessListener { labels ->
                results.text = "Resultados de la imagen"
                for(imageLabel in labels)
                {
                    val text = imageLabel.text
                    val confidence = imageLabel.confidence
                    val index = imageLabel.index
                    results.append("\nEtiqueta: $text\nConfianza: $confidence\nIndice: $index\n")
                }
                progressDialog.dismiss()
            }
            .addOnFailureListener()
            {
                e-> progressDialog.dismiss()
                Toast.makeText(applicationContext, "Error : ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}