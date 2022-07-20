package com.example.imgtotext

import android.R.attr.label
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.method.ScrollingMovementMethod
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions


class MainActivity : AppCompatActivity() {

    private val pickImage = 100
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTheme(R.style.Theme_ImgToText)

        val selectBut = findViewById<Button>(R.id.button)
        val pBar =findViewById<ProgressBar>(R.id.progressBar)
        pBar.isVisible = false
            selectBut.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery,pickImage)
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            val imageUri = data?.data
            val pBar =findViewById<ProgressBar>(R.id.progressBar)
            pBar.isVisible=true
            recognizeText(imageUri)
        }
    }

    private fun recognizeText(img: Uri?) {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val pBar =this.findViewById<ProgressBar>(R.id.progressBar)
        val image: InputImage
        var resultString:String=""
        image = img?.let { InputImage.fromFilePath(this, it) }!!
        val result =recognizer.process(image)
            .addOnSuccessListener { visionText->
                for(block in visionText.textBlocks) {
                    resultString = resultString+"\n"+block.text.toString()
                }
                pBar.isVisible=false
                showDialog(resultString)

            }
            .addOnFailureListener{
                pBar.isVisible=false
                Toast.makeText(this,"Task failed",Toast.LENGTH_SHORT).show()
            }
    }
    private fun showDialog(title: String) {
        val pBar =this.findViewById<ProgressBar>(R.id.progressBar)
        pBar.isVisible=false

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog)

        var textV = dialog.findViewById<TextView>(R.id.editTextTextMultiLine)
        val copyBut = dialog.findViewById<Button>(R.id.button2)
        textV.setTextIsSelectable(true)

        copyBut.setOnClickListener {
            var clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            var clip = ClipData.newPlainText("label", textV.text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this,"Copied",Toast.LENGTH_SHORT).show()
        }
        textV.movementMethod = ScrollingMovementMethod.getInstance()
        textV.setText(title)
        dialog.show()
    }

}