package erick.labrada.practica12

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cloudinary.android.MediaManager
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private val pokemonRef = FirebaseDatabase.getInstance().getReference("Pokemons")
    private var pokemonModels = ArrayList<PokeData>()
    val CLOUD_NAME = "djqbuobln"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        initCloudinary()

        // Set up RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.PlanetsRecicleViewer)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = PokeAdapter(this, pokemonModels)
        recyclerView.adapter = adapter

        // Listen for changes in the Firebase database
        pokemonRef.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("MainActivity", "loadPokemons:onCancelled", databaseError.toException())
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousName: String?) {}

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousName: String?) {}

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousName: String?) {
                // Get Pokémon data from the snapshot
                val pokemon = dataSnapshot.getValue(PokeData::class.java)
                if (pokemon != null) {
                    // Add the new Pokémon to the list and update the adapter
                    pokemonModels.add(pokemon)
                    adapter.notifyItemInserted(pokemonModels.size - 1)
                }
            }
        })

        // Handle button click for adding new Pokémon
        val btnRegistrarPokemon = findViewById<Button>(R.id.registrarPokemon)
        btnRegistrarPokemon.setOnClickListener {
            val intent = Intent(this, RegistrarPokemon::class.java)
            startActivity(intent)
        }

        // Optional: Handle padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initCloudinary() {
        val config: MutableMap<String, String> = HashMap()
        config["cloud_name"] = CLOUD_NAME
        MediaManager.init(this, config)
    }
}
