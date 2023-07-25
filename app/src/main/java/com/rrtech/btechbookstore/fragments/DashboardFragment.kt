package com.rrtech.btechbookstore.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.rrtech.btechbookstore.R
import com.rrtech.btechbookstore.adapter.DashboardRecyclerAdapter
import com.rrtech.btechbookstore.model.Book
import com.rrtech.btechbookstore.util.ConnectionManager
import org.json.JSONException
import java.util.Collections

class DashboardFragment : Fragment() {

    lateinit var dashboardRecyclerView: RecyclerView

    lateinit var layoutManager: RecyclerView.LayoutManager

    val bookInfoList = arrayListOf<Book>()

    lateinit var recyclerAdapter: DashboardRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar


    var ratingCaparator = Comparator<Book> { book1, book2 ->

        if (book1.bookRating.compareTo(book2.bookRating, true) == 0) {
            book1.bookName.compareTo(book2.bookName, true)
        } else {
            book1.bookRating.compareTo(book2.bookRating, true)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_dashboard, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setHasOptionsMenu(true)

        val context2 = requireContext()

        progressBar = view.findViewById(R.id.progressbar_recycler_view)
        progressLayout = view.findViewById(R.id.progress_layout)

        progressLayout.visibility = View.VISIBLE

//       if (sortItem.itemId == R.menu.menu_dashboard){
//
//           MenuItem item =
//       }




        dashboardRecyclerView = view.findViewById(R.id.recyclerViewDashboard)

        layoutManager = LinearLayoutManager(context2)



        when {
            ConnectionManager().checkConnectivity(context2) -> {


                val queue = Volley.newRequestQueue(activity as Context)
                val url = "http://13.235.250.119/v1/book/fetch_books/"

                val jsonObjectRequest = object : JsonObjectRequest(Method.GET, url, null,
                    Response.Listener {

                        try {

                            progressLayout.visibility = View.GONE

//                            itemMenuSort?.isVisible = true


                            val success = it.getBoolean("success")
                            if (success) {
                                val data = it.getJSONArray("data")

                                for (i in 0 until data.length()) {
                                    val bookJsonObject = data.getJSONObject(i)

                                    val bookobject = Book(
                                        bookJsonObject.getString("book_id"),
                                        bookJsonObject.getString("name"),
                                        bookJsonObject.getString("author"),
                                        bookJsonObject.getString("rating"),
                                        bookJsonObject.getString("price"),
                                        bookJsonObject.getString("image")
                                    )

                                    bookInfoList.add(bookobject)

                                    recyclerAdapter =
                                        DashboardRecyclerAdapter(context2, bookInfoList)

                                    dashboardRecyclerView.adapter = recyclerAdapter

                                    dashboardRecyclerView.layoutManager = layoutManager


                                    /*    ###  Added For A Dark Divider Line Between Two Recycler Element

                                                           dashboardRecyclerView.addItemDecoration(
                                                               DividerItemDecoration(
                                                                   dashboardRecyclerView.context,
                                                                   (layoutManager as LinearLayoutManager).orientation
                                                               )
                                                           )
                                     */

                                }
                            } else {
                                Toast.makeText(
                                    activity as Context,
                                    "Error Occured",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: JSONException) {

                            Toast.makeText(context2, "Response not found", Toast.LENGTH_SHORT)
                                .show()
                        }
                    },

                    Response.ErrorListener {

                        if (activity != null) {
                            Toast.makeText(
                                context2,
                                "Some error occurred (Volley)",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }) {

                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "e667d3c7c97566"

                        return headers
                    }
                }

                queue.add(jsonObjectRequest)


            }

            else -> {
                progressLayout.visibility = View.GONE

//                itemMenuSort?.isVisible = false

                val dialog = AlertDialog.Builder(context2)
                dialog.setTitle("Error")
                dialog.setMessage("No Internet")
                dialog.setPositiveButton("Check Setting") { text, listener ->

//             ### Opening The Internet Connection Setting
                    val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingIntent)
                    activity?.finish()

                }
                dialog.setNegativeButton("Exit") { text, listener ->

                    ActivityCompat.finishAffinity(context2 as Activity)

                }
                dialog.create().show()

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater?.inflate(R.menu.menu_dashboard, menu)

        if (!ConnectionManager().checkConnectivity(activity as Context)){
            menu.findItem(R.id.item_sort).isVisible = false
        }



    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        var id = item?.itemId

        if (id == R.id.item_sort) {
            item.icon?.setTint(Color.WHITE)

            Collections.sort(bookInfoList, ratingCaparator)
            bookInfoList.reverse()
            recyclerAdapter.notifyDataSetChanged()
        }

        return super.onOptionsItemSelected(item)
    }
}