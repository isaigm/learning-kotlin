package com.example.myapplication
import android.app.ProgressDialog
import com.example.myapplication.model.Language;
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.widget.*
import com.google.mlkit.common.model.DownloadConditions
import kotlin.collections.ArrayList
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import java.util.Locale
class MainActivity : AppCompatActivity() {
    private var languages: ArrayList<Language> = ArrayList()
    private var selectedSrcLangCode = java.lang.StringBuilder("es")
    private var selectedDestLangCode = java.lang.StringBuilder("en")
    private lateinit var translatorOptions: TranslatorOptions
    private lateinit var translator: Translator
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var codes = TranslateLanguage.getAllLanguages()
        for(code in codes) {
            var name = Locale(code).displayLanguage
            var language: Language = Language()
            language.code = code
            language.name = name
            languages.add(language)
        }
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere...")
        progressDialog.setCanceledOnTouchOutside(false)

        var editText : EditText = findViewById(R.id.editText)
        var btn: Button = findViewById(R.id.button)
        var btnSrcLang: Button = findViewById(R.id.btnSrcLang)
        var btnDestLang: Button = findViewById(R.id.btnDestLang)
        var textView: TextView = findViewById(R.id.tv)
        textView.movementMethod = ScrollingMovementMethod()
        btnSrcLang.setOnClickListener {
           setPopUp(selectedSrcLangCode, btnSrcLang)
        }
        btnDestLang.setOnClickListener {
            setPopUp(selectedDestLangCode, btnDestLang)
        }
        btn.setOnClickListener {
            var msg: String = editText.text.toString()
            println(selectedDestLangCode.toString())
            progressDialog.setMessage("Traduciendo")
            progressDialog.show()
            translatorOptions = TranslatorOptions.Builder().
            setSourceLanguage(selectedSrcLangCode.toString())
                .setTargetLanguage(selectedDestLangCode.toString())
                .build()
            translator = Translation.getClient(translatorOptions)
            val downloadConditions = DownloadConditions.Builder()
                .requireWifi().build()
            translator.downloadModelIfNeeded(downloadConditions)
                .addOnSuccessListener {
                    translator.translate(msg).addOnSuccessListener {
                                translatedText ->
                        textView.setText(translatedText)
                        progressDialog.dismiss()
                    }.addOnFailureListener{
                        Toast.makeText(applicationContext, "Error al traducir", Toast.LENGTH_SHORT)
                    }
                }
                .addOnFailureListener{
                    Toast.makeText(applicationContext, "Error fatal", Toast.LENGTH_SHORT)
                }
        }
    }
    private fun setPopUp(selectedCode: java.lang.StringBuilder, btnLang: Button)
    {
        val popUpMenu = PopupMenu(this, btnLang)
        for(i in languages.indices)
        {
            popUpMenu.menu.add(Menu.NONE, i, i, languages[i].name)
        }
        popUpMenu.show()
        popUpMenu.setOnMenuItemClickListener { menuItem ->
            var pos = menuItem.itemId
            btnLang.text = languages[pos].name
            selectedCode.clear()
            selectedCode.append(languages[pos].code)
            false
        }
    }
}