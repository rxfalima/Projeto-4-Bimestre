package com.example.projeto4bim

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projeto4bim.Adapter.PesaAdapter
import com.example.projeto4bim.DataBase

class MainActivity : AppCompatActivity() {

    private lateinit var btn: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private var myDB: DataBase? = null

    private val bookId: ArrayList<String> = ArrayList()
    private val bookTitle: ArrayList<String> = ArrayList()
    private val bookAuthor: ArrayList<String> = ArrayList()
    private val bookPages: ArrayList<String> = ArrayList()
    private val bookData: ArrayList<String> = ArrayList()
    private val tags: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.Ryc)
        btn = findViewById(R.id.add)
        searchView = findViewById(R.id.searchView)

        myDB = DataBase(this)

        // Load data from database
        storeDataInArrays()

        // Set up the RecyclerView with the adapter
        val pesaAdapter = PesaAdapter(
            this,
            bookId,
            bookTitle,
            bookData,
            bookPages,
            bookAuthor,
            tags
        ) { position ->
            val intent = Intent(this, PesaDetails::class.java)
            intent.putExtra("bookId", bookId[position])
            intent.putExtra("bookTitle", bookTitle[position])
            intent.putExtra("bookAuthor", bookAuthor[position])
            intent.putExtra("bookPages", bookPages[position])
            intent.putExtra("bookData", bookData[position])
            intent.putExtra("tags", tags[position])
            startActivity(intent)
        }
        recyclerView.adapter = pesaAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        btn.setOnClickListener {
            val intent = Intent(this, Add::class.java)
            startActivity(intent)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })
    }

    private fun filterList(query: String?) {
        val filteredIds = ArrayList<String>()
        val filteredTitles = ArrayList<String>()
        val filteredAuthors = ArrayList<String>()
        val filteredPages = ArrayList<String>()
        val filteredData = ArrayList<String>()
        val filteredTags = ArrayList<String>()

        if (query.isNullOrEmpty()) {
            filteredIds.addAll(bookId)
            filteredTitles.addAll(bookTitle)
            filteredAuthors.addAll(bookAuthor)
            filteredPages.addAll(bookPages)
            filteredData.addAll(bookData)
            filteredTags.addAll(tags)
        } else {
            for (i in bookTitle.indices) {
                val matchesTitle = bookTitle[i].contains(query, ignoreCase = true)
                val matchesDate = bookData[i].contains(query, ignoreCase = true)
                val matchesAuthor = bookAuthor[i].contains(query, ignoreCase = true)

                if (matchesTitle || matchesDate || matchesAuthor) {
                    filteredIds.add(bookId[i])
                    filteredTitles.add(bookTitle[i])
                    filteredAuthors.add(bookAuthor[i])
                    filteredPages.add(bookPages[i])
                    filteredData.add(bookData[i])
                    filteredTags.add(tags[i])
                }
            }
        }

        // Update the adapter with the filtered list
        (recyclerView.adapter as PesaAdapter).updateList(filteredIds, filteredTitles, filteredAuthors, filteredPages, filteredData, filteredTags)
    }

    private fun storeDataInArrays() {
        val cursor = myDB?.readAllData()
        if (cursor?.count == 0) {
            Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show()
        } else {
            // Clear previous data
            bookId.clear()
            bookTitle.clear()
            bookPages.clear()
            bookData.clear()
            bookAuthor.clear()
            tags.clear()

            // Store new data
            while (cursor?.moveToNext() == true) {
                bookId.add(cursor.getString(0))
                bookTitle.add(cursor.getString(1))
                bookPages.add(cursor.getString(2))
                bookData.add(cursor.getString(3))
                bookAuthor.add(cursor.getString(4))
                tags.add(cursor.getString(5))
            }
        }
    }
}
