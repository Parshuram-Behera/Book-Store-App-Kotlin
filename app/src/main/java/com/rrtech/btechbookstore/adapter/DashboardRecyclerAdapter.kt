package com.rrtech.btechbookstore.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.rrtech.btechbookstore.R
import com.rrtech.btechbookstore.activity.DescriptionActivity
import com.rrtech.btechbookstore.model.Book
import com.squareup.picasso.Picasso

class DashboardRecyclerAdapter ( val context : Context , val itemList : ArrayList<Book>) : RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>() {

    class DashboardViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val txtBookName : TextView = view.findViewById(R.id.txtBookName)
        val txtbookAuther : TextView = view.findViewById(R.id.txtBookAuthor)
        val txtbookPrice : TextView = view.findViewById(R.id.txtBookPrice)
        val txtbookratings : TextView = view.findViewById(R.id.txtBookRating)
        val txtbookImage : ImageView = view.findViewById(R.id.imgBookImage)

        val itemclick : LinearLayout = view.findViewById(R.id.layout_sample_parent)

    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sample_ui, parent , false)

        return DashboardViewHolder(view)
    }



    override fun getItemCount(): Int {

        return itemList.size

    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {

        val book = itemList[position]

        holder.txtBookName.text = book.bookName
        holder.txtbookAuther.text = book.bookAuthor
        holder.txtbookPrice.text = book.bookPrice
        holder.txtbookratings.text = book.bookRating
//        holder.txtbookImage.setImageResource(book.bookImage)
        Picasso.get()
            .load(book.bookImage)
            .error(R.drawable.default_book_cover)
            .into(holder.txtbookImage)


        holder.itemclick.setOnClickListener{
//            Toast.makeText(context , " ${holder.txtBookName.text}" , Toast.LENGTH_SHORT).show()


            val intent = Intent(context , DescriptionActivity::class.java)
            intent.putExtra("book_id" , book.bookId)
            context.startActivity(intent)
        }


    }


}