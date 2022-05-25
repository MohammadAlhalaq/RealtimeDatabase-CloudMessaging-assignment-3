package com.example.my.adapter

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.asignment1.adapter.model.Book
import com.example.my.MainActivity
import com.example.my.R
import com.example.my.databinding.ActivityAddBookBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat

class AddBookActivity : AppCompatActivity() {
    lateinit var imageUri: Uri
    val db = Firebase.firestore
    lateinit var mStorage: StorageReference
    lateinit var binding: ActivityAddBookBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        mStorage = FirebaseStorage.getInstance().reference
        super.onCreate(savedInstanceState)
        binding = ActivityAddBookBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (intent.getSerializableExtra("book") !== null) {
            var book = intent.getSerializableExtra("book") as Book
            binding.editOrAddBook.text = "Edit Book"
            binding.editOrAddAddBtn.text = "Edit Book"
            binding.editOrAddBookName.setText(book.name)
            binding.editOrAddBookAuthor.setText(book.author)
            binding.editOrAddBookLaunchYear.setText((book.year!!.year + 1900).toString())
            binding.editOrAddBookPrice.setText(book.price.toString())
            binding.editOrAddBookRatingBar.rating = book.rates
            binding.editOrAddUpload.setOnClickListener {
                val intentImg = Intent(Intent.ACTION_PICK)
                intentImg.type = "image/*"
                startActivityForResult(intentImg, 2)
            }

            binding.editOrAddAddBtn.setOnClickListener {
                addOrEditMethod(true)
            }
            binding.editOrAddDeleteBtn.setOnClickListener {
                deleteBook(book.id!!)
            }
//            edit and delete
        } else {
            binding.editOrAddDeleteBtn.visibility = View.GONE

            binding.editOrAddAddBtn.setOnClickListener {
                addOrEditMethod(false)
            }

            binding.editOrAddUpload.setOnClickListener {
                val intentImg = Intent(Intent.ACTION_PICK)
                intentImg.type = "image/*"
                startActivityForResult(intentImg, 2)

            }
        }
    }

    private fun deleteBook(id: String) {
        db.collection("books").document(id).delete().addOnSuccessListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }.addOnFailureListener { e ->
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun addbook(book: Book) {
        // Create a new user with a first, middle, and last name
        val book = hashMapOf(
            "name" to book.name,
            "author" to book.author,
            "price" to book.price,
            "rates" to book.rates,
            "year" to Timestamp(book.year!!)
        )

        // Add a new document with a generated ID
        db.collection("books")
            .add(book)
            .addOnSuccessListener { it ->
                Toast.makeText(this, "book done", Toast.LENGTH_SHORT).show()
                val filePath = mStorage.child("books").child(it.id)
                if( imageUri != null) {
                    filePath.putFile(imageUri).addOnCompleteListener {fromFirebase ->
                        if (fromFirebase.isSuccessful) {
                            Toast.makeText(applicationContext, "Uploaded Image", Toast.LENGTH_LONG).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }

                    }
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()

            }
    }

    private fun updateBook(book: Book) {

        // Create a new user with a first, middle, and last name
        val updateBook = hashMapOf(
            "name" to book.name,
            "author" to book.author,
            "price" to book.price,
            "rates" to book.rates,
            "year" to Timestamp(book.year!!)
        )

        // Add a new document with a generated ID
        db.collection("books")
            .document(book.id!!)
            .update(updateBook as Map<String, Any>).addOnSuccessListener {
                Toast.makeText(this, "update book done", Toast.LENGTH_SHORT).show()
                val filePath = mStorage.child("books").child(book.id!!)
                if( imageUri != null) {
                    filePath.putFile(imageUri).addOnCompleteListener {fromFirebase ->
                        if (fromFirebase.isSuccessful) {
                            Toast.makeText(applicationContext, "Uploaded Image", Toast.LENGTH_LONG).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }

                    }
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()

            }
    }

    private fun addOrEditMethod(isEdit: Boolean) {
        if (
            binding.editOrAddBookName.text.isNotEmpty() &&
            binding.editOrAddBookAuthor.text.isNotEmpty() &&
            binding.editOrAddBookLaunchYear.text.isNotEmpty() &&
            binding.editOrAddBookPrice.text.isNotEmpty() &&
            binding.editOrAddBookRatingBar.rating != 0f
        ) {
            var dateFormat = SimpleDateFormat("yyyy-MM-dd")
            val year = binding.editOrAddBookLaunchYear.text.toString().toInt() + 1
            val month = 0
            val day = 0
            val date = dateFormat.parse("$year-$month-$day")
            Toast.makeText(this, "binding done", Toast.LENGTH_SHORT).show()

            if (isEdit) {
                updateBook(
                    Book(
                        (intent.getSerializableExtra("book") as Book).id,
                        binding.editOrAddBookName.text.toString(),
                        binding.editOrAddBookAuthor.text.toString(),
                        date,
                        binding.editOrAddBookRatingBar.rating.toString().toFloat(),
                        binding.editOrAddBookPrice.text.toString().toInt()
                    )
                )
            } else addbook(
                Book(
                    null,
                    binding.editOrAddBookName.text.toString(),
                    binding.editOrAddBookAuthor.text.toString(),
                    date,
                    binding.editOrAddBookRatingBar.rating.toString().toFloat(),
                    binding.editOrAddBookPrice.text.toString().toInt(),
                )
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data!!
        }

    }
}