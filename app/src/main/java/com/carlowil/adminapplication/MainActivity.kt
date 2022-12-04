package com.carlowil.adminapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.carlowil.adminapplication.databinding.ActivityMainBinding
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject


class MainActivity : AppCompatActivity() {
    private val tag = "MainActivity"
    private val keyTitle = "title";
    private val keyDescription = "description";
    private val db = FirebaseFirestore.getInstance()
    private val noteRef = db.collection("Notebook").document("MyFirstNode");



    private lateinit var binding : ActivityMainBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.submitButton.setOnClickListener {
            saveData(it)
        }

        binding.loadButton.setOnClickListener {
            loadData(it)
        }

        binding.updateButton.setOnClickListener {
            updateDescription(it)
        }

        binding.delDesc.setOnClickListener {
            deleteDescription(it)
        }

        binding.delNote.setOnClickListener {
            deleteNote(it)
        }
        setContentView(binding.root)


    }

    // Синхронизация изменений с помощью SnapshotListener

    override fun onStart() {
        super.onStart()
        noteRef.addSnapshotListener(this, EventListener { value, error ->
            if (error != null) {
                Toast.makeText(this, "Error while loading", Toast.LENGTH_SHORT).show()
                Log.d(tag, error.toString())
                return@EventListener;
            }
            if(value!!.exists()) {
                val note = value.toObject<Note>()
                binding.titleTextView.text = "Title: ${note?.getTitle()}"
                binding.descTextView.text = "Description: ${note?.getDescription()}"
            } else {
                binding.titleTextView.text = ""
                binding.descTextView.text = ""
            }
        })
    }

    // Обновление описания
    private fun updateDescription(v : View) {
        val desc = binding.descTextField.text.toString()

//        val note = hashMapOf<String, Any>()
//        note[keyDescription] = desc

//        noteRef.set(note, SetOptions.merge()) // создает новый документ при его отсутсвие
//        noteRef.update(note) // можно передать наш hashMap

        noteRef.update(keyDescription, desc) // а можно передать напрямую ключь значение
    }


    // Удаление описания
    private fun deleteDescription(v : View) {

//        val note = hashMapOf<String, Any>()
//        note[keyDescription] = FieldValue.delete()
//        noteRef.update(note)

        noteRef.update(keyDescription, FieldValue.delete())
    }


    // Удаление документа
    private fun deleteNote(v : View) {
        noteRef.delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show()
                Log.i(tag, "Note deleted")
            }
            .addOnFailureListener {
                Log.d(tag, it.toString())
            }
    }
    // Сохранение данных в определенный документ
    private fun saveData(v : View) {
        val title = binding.titleTextField.text.toString()
        val desc = binding.descTextField.text.toString()

        // Сохрание записи с помощью HashMap
//        val note = hashMapOf<String, Any>()
//        note[keyTitle] = title
//        note[keyDescription] = desc

        val note = Note(title, desc)

        noteRef.set(note)
            .addOnSuccessListener {
                Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show()
                Log.d(tag, it.toString())
            }
    }

    // Загрузка данных из определенного документа
    private fun loadData(v : View) {
        noteRef.get()
            .addOnSuccessListener {
                if(it.exists()) {

                    // Получение данных по ключу
//                    val title = it.getString(keyTitle)
//                    val desc = it.getString(keyDescription)
                    // Получение информации с помощью it.data
//                    val note = it.data

                    val note = it.toObject<Note>()
                    binding.titleTextView.text = "Title: ${note?.getTitle()}"
                    binding.descTextView.text = "Description: ${note?.getDescription()}"

                } else {
                    Toast.makeText(this, "Document doesnt exist!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                Log.d(tag, it.toString())
            }
    }
}