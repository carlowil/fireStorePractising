package com.carlowil.adminapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.carlowil.adminapplication.databinding.ActivityMainBinding
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects


class MainActivity : AppCompatActivity() {
    private val tag = "MainActivity"
    private val keyTitle = "title";
    private val keyDescription = "description";
    private val db = FirebaseFirestore.getInstance()
    private val noteBookRef = db.collection("Notebook")
    private val noteRef = db.collection("Notebook").document("MyFirstNode");



    private lateinit var binding : ActivityMainBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.addButton.setOnClickListener {
            addNote(it)
        }

        binding.loadButton.setOnClickListener {
            loadNotes(it)
        }

    }

    // Синхронизация изменений с помощью SnapshotListener

    override fun onStart() {
        super.onStart()
        noteBookRef.addSnapshotListener(this, EventListener { value, error ->
            if (error != null) {
                Toast.makeText(this, "Error while loading", Toast.LENGTH_SHORT).show()
                Log.d(tag, error.toString())
                return@EventListener;
            }
            var data = ""
            value?.forEach {
                val note = it.toObject<Note>()
                note.id = it.id
                data += "DocumentId: ${note.id}\nTitle: ${note.title}\nDescription: ${note.description}\nPriority: ${note.priority}\n\n"
            }
            binding.infoTextView.text = data
        })
    }

    // Сохранение данных в определенный документ
    private fun addNote(v : View) {
        val title = binding.editTitle.text.toString()
        val desc = binding.editDesc.text.toString()
        if(binding.editPriority.length() == 0) {
            binding.editPriority.setText("0")
        }

        val priority = binding.editPriority.text.toString().toInt()

        val note = Note(title, desc, priority)

        noteBookRef.add(note);
    }

    // Загрузка данных из определенного документа
    private fun loadNotes(v : View) {
        noteBookRef.whereGreaterThanOrEqualTo("priority",  1)
            .orderBy("priority", Query.Direction.DESCENDING)
            .limit(3)
            .get()
            .addOnSuccessListener {
                var data = ""
                for (querySnapshot in it) {
                    val note = querySnapshot.toObject<Note>()
                    note.id = querySnapshot.id
                    val id = note.id
                    data += "DocumentId: ${id}\nTitle: ${note.title}\nDescription: ${note.description}\nPriority: ${note.priority}\n\n"
                }
                binding.infoTextView.text = data
            }
    }
}