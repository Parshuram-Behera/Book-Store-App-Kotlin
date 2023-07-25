package com.rrtech.btechbookstore.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.rrtech.btechbookstore.R
import com.rrtech.btechbookstore.database.BookDatabase
import com.rrtech.btechbookstore.database.BookEntity
import com.rrtech.btechbookstore.util.ConnectionManager
import com.squareup.picasso.Picasso
import org.json.JSONObject

class DescriptionActivity : AppCompatActivity() {

    lateinit var txtBookName: TextView
    lateinit var txtBookAuthor: TextView
    lateinit var txtBookPrice: TextView
    lateinit var txtBookRating: TextView
    lateinit var txtBookImage: ImageView
    lateinit var txtBookDescription: TextView
    lateinit var btnAddToFav: Button
    lateinit var progressBar: ProgressBar
    lateinit var progressBarLayout: RelativeLayout
    lateinit var toolbar: Toolbar

    var bookId: String? = "100"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        txtBookName = findViewById(R.id.txtBookName)
        txtBookAuthor = findViewById(R.id.txtBookAuthor)
        txtBookPrice = findViewById(R.id.txtBookPrice)
        txtBookRating = findViewById(R.id.txtBookRating)
        txtBookImage = findViewById(R.id.description_imageView)
        txtBookDescription = findViewById(R.id.bookDescription)
        btnAddToFav = findViewById(R.id.btnAddToFav)

        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        progressBarLayout = findViewById(R.id.progressLayout_description)
        progressBarLayout.visibility = View.VISIBLE

        toolbar = findViewById(R.id.description_toolbar)

        setUpToolbar()



        if (intent != null) {
            bookId = intent.getStringExtra("book_id")
        } else {
            finish()
            Toast.makeText(this, "Error Occurred", Toast.LENGTH_SHORT).show()
        }

        if (bookId == "100") {
            finish()
            Toast.makeText(this, "unexpected Error", Toast.LENGTH_SHORT).show()
        }


        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v1/book/get_book/"
        val jsonParams = JSONObject()
        jsonParams.put("book_id", bookId)

        if (ConnectionManager().checkConnectivity(this)) {


            val jsonRequest =
                object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {

                    try {
                        val success = it.getBoolean("success")

                        if (success) {
                            val bookJsonObject = it.getJSONObject("book_data")
                            progressBarLayout.visibility = View.GONE

                            val bookImageUrl = bookJsonObject.getString("image")

                            Picasso.get().load(bookJsonObject.getString("image"))
                                .error(R.drawable.default_book_cover).into(txtBookImage)

                            txtBookName.text = bookJsonObject.getString("name")
                            txtBookAuthor.text = bookJsonObject.getString("author")
                            txtBookPrice.text = bookJsonObject.getString("price")
                            txtBookRating.text = bookJsonObject.getString("rating")
                            txtBookDescription.text = bookJsonObject.getString("description")

                            val bookEntity = BookEntity(
                                bookId?.toInt() as Int,
                                txtBookName.text.toString(),
                                txtBookAuthor.text.toString(),
                                txtBookPrice.text.toString(),
                                txtBookRating.text.toString(),
                                txtBookDescription.text.toString(),
                                bookImageUrl
                            )

                            val checkFav = DBAsyncTask(applicationContext, bookEntity, 1).execute()
                            val isfav = checkFav.get()

                            if (isfav) {
                                btnAddToFav.text = "Remove from favourite"
                                val favcolour = ContextCompat.getColor(applicationContext, R.color.colorFavourite)
                                btnAddToFav.setBackgroundColor(favcolour)


                            } else {
                                btnAddToFav.text = "Add to favourite"
                                val noFavColour =
                                    ContextCompat.getColor(applicationContext, R.color.AppColor)
                                btnAddToFav.setBackgroundColor(noFavColour)


                            }

                            btnAddToFav.setOnClickListener {


                                if (!DBAsyncTask(applicationContext, bookEntity, 1).execute()
                                        .get()
                                ) {


                                    val async = DBAsyncTask(applicationContext, bookEntity, 2).execute()
                                    val result = async.get()

                                    if (result) {
                                        Toast.makeText(this, "Added to favourite", Toast.LENGTH_SHORT).show()

                                        btnAddToFav.text = "Remove from favourite"
                                        val favcolour = ContextCompat.getColor(applicationContext, R.color.colorFavourite)
                                        btnAddToFav.setBackgroundColor(favcolour)

                                    } else {
                                        Toast.makeText(
                                            this, "error occurred", Toast.LENGTH_SHORT
                                        ).show()

                                    }

                                } else {
                                    val async = DBAsyncTask(applicationContext, bookEntity, 3).execute()
                                    val result = async.get()

                                    if (result) {
                                        Toast.makeText(this, "Book removed", Toast.LENGTH_SHORT)
                                            .show()

                                        btnAddToFav.text = "Add to favourite"

                                        val nofavcolour = ContextCompat.getColor(applicationContext, R.color.AppColor)
                                        btnAddToFav.setBackgroundColor(nofavcolour)
                                    } else {
                                        Toast.makeText(this, "error occurred", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }


                        } else {
                            Toast.makeText(this, "unexpected Error", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {

                        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()

                    }

                }, Response.ErrorListener {

                    Toast.makeText(this, "Volley Error $it", Toast.LENGTH_SHORT).show()

                }) {


                    override fun getHeaders(): MutableMap<String, String> {

                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "e667d3c7c97566"

                        return headers
                    }
                }

            queue.add(jsonRequest)


        } else {
            progressBarLayout.visibility = View.GONE
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Error")
            dialog.setMessage("No Internet")
            dialog.setPositiveButton("Check Setting") { text, listener ->

//             ### Opening The Internet Connection Setting
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                finish()

            }
            dialog.setNegativeButton("Close") { text, listener ->

                onBackPressed()

            }
            dialog.create().show()
        }


    }

    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Book Details"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.navigationIcon?.setTint(Color.WHITE)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    class DBAsyncTask(val context: Context, val entity: BookEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {


        val db = Room.databaseBuilder(context, BookDatabase::class.java, "books-db").build()


        override fun doInBackground(vararg params: Void?): Boolean {

            when (mode) {
                1 -> {
                    val book: BookEntity? = db.bookDao().getBookById(entity.book_id.toString())
                    db.close()

                    return book != null

                }

                2 -> {

                    db.bookDao().insertBook(entity)
                    db.close()

                    return true

                }

                3 -> {
                    db.bookDao().deleteBook(entity)
                    db.close()

                    return true

                }
            }

            return false
        }

    }
}