package com.rrtech.btechbookstore.fragments

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.rrtech.btechbookstore.R
import com.rrtech.btechbookstore.adapter.FavouriteRecyclerAdapter
import com.rrtech.btechbookstore.database.BookDatabase
import com.rrtech.btechbookstore.database.BookEntity


class FavroiteFragment : Fragment() {

    lateinit var recyclerFavoriteView: RecyclerView

    lateinit var progressLayoutFav: RelativeLayout

    lateinit var progressBarfav: ProgressBar

    lateinit var recyclerAdaptorFav: FavouriteRecyclerAdapter

    lateinit var layoutManager: RecyclerView.LayoutManager

    var dbBookList = listOf<BookEntity>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_favroite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)


        progressLayoutFav = view.findViewById(R.id.progress_layout_fav)
        progressBarfav = view.findViewById(R.id.progressbar_recycler_view_fav)

        progressLayoutFav.visibility = View.VISIBLE

        recyclerFavoriteView = view.findViewById(R.id.recycler_fav)


        layoutManager = GridLayoutManager(activity as Context, 2)

//        (layoutManager as GridLayoutManager).orientation = GridLayoutManager.HORIZONTAL

        dbBookList = RetriveFavourites(activity as Context).execute().get()

//        Log.d("favlistitem", dbBookList.toString())

        if (activity != null) {

            progressLayoutFav.visibility = View.GONE


            recyclerAdaptorFav = FavouriteRecyclerAdapter(activity as Context, dbBookList)

            recyclerFavoriteView.adapter = recyclerAdaptorFav
            recyclerFavoriteView.layoutManager = layoutManager

        } else {
            Toast.makeText(activity as Context, "DB Null", Toast.LENGTH_SHORT).show()

        }
    }

    class RetriveFavourites( val context: Context) : AsyncTask<Void, Void, List<BookEntity>>() {


        override fun doInBackground(vararg params: Void?): List<BookEntity> {
            val db = Room.databaseBuilder(context, BookDatabase::class.java, "books-db").build()


            return db.bookDao().getAllBooks()


        }
    }
}