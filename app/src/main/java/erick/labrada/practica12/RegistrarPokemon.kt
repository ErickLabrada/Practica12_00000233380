package erick.labrada.practica12

import android.app.Activity
import android.app.ComponentCaller
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegistrarPokemon : AppCompatActivity() {

    val REQUEST_IMAGE_GET = 1
    val UPLOAD_PRESET = "pokemon-preset"
    var imageUri: Uri? = null

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrar_pokemon)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registrarPokemonActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        database = FirebaseDatabase.getInstance().getReference("Pokemons")

        val btnGuardarBoton: Button = findViewById(R.id.guardarPokemon)
        val name: EditText = findViewById(R.id.etName)
        val number: EditText = findViewById(R.id.etNumero)
        val type: EditText = findViewById(R.id.etTipo)
        val btnSubirImagen: Button = findViewById(R.id.subirImagen)

        btnSubirImagen.setOnClickListener {
            val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE_GET)
        }

        btnGuardarBoton.setOnClickListener {
            savePokemon()
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_GET && resultCode == Activity.RESULT_OK) {
            val fullPhotoUri: Uri? = data?.data

            if (fullPhotoUri != null) {
                imageUri = fullPhotoUri
                changeImage(fullPhotoUri)
            }
        }
    }

    fun changeImage(uri: Uri) {
        val thumbnail: ImageView = findViewById(R.id.imgPreview)
        try {
            thumbnail.setImageURI(uri)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    fun savePokemon() {

        val name = findViewById<EditText>(R.id.etName).text.toString()
        val type = findViewById<EditText>(R.id.etTipo).text.toString()
        val number = findViewById<EditText>(R.id.etNumero).text.toString()

        if (imageUri != null) {
            MediaManager.get().upload(imageUri)
                .unsigned(UPLOAD_PRESET)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {
                        Log.d("Cloudinary", "Upload started")
                    }

                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                        Log.d("Cloudinary", "Upload progress")
                    }

                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val url = resultData["secure_url"] as? String ?: ""
                        Log.d("Cloudinary", "Upload successful: $url")

                        val pokemon = PokeData(name = name, type = type, number = number, img = url)

                        val pokemonId = database.push().key
                        if (pokemonId != null) {
                            database.child(pokemonId).setValue(pokemon)
                                .addOnSuccessListener {
                                    Log.d("Firebase", "Pokemon saved successfully")
                                    finish()
                                }
                                .addOnFailureListener { exception ->
                                    Log.d("Firebase", "Failed to save Pokemon: ${exception.message}")
                                }
                        }
                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        Log.d("Cloudinary", "Upload failed: ${error.description}")
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo) {
                        Log.d("Cloudinary", "Upload rescheduled: ${error.description}")
                    }
                })
                .dispatch()
        }
    }
}
