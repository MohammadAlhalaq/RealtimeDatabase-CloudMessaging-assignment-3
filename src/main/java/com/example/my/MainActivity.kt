package com.example.my

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.asignment1.adapter.RecyclerBookAdapter
import com.example.asignment1.adapter.model.Book
import com.example.my.adapter.AddBookActivity
import com.example.my.databinding.ActivityMainBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso


import kotlin.collections.ArrayList
lateinit var mStorage: StorageReference

class MainActivity : AppCompatActivity() {
    val db = Firebase.firestore;
    var books = ArrayList<Book>()

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        mStorage = FirebaseStorage.getInstance().reference

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerView.adapter = RecyclerBookAdapter(this, books) //for put data
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this) // for way to display elements

        binding.floatingActionButton.setOnClickListener {
            startActivity(Intent(this, AddBookActivity::class.java))

        }
        getBooks()
    }

    fun getBooks() {
        db.collection("books")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {

                        books.add(
                            Book(
                                document.id,
                                document["name"].toString(),
                                document["author"].toString(),
                                (document["year"] as Timestamp).toDate(),
                                document["rates"].toString().toFloat(),
                                document["price"].toString().toInt(),
                            )
                        )
                    }

                binding.recyclerView.adapter!!.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
//                Log.w(TAG, "Error getting documents.", exception)
            }
    }
}