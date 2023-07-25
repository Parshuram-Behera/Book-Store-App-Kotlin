package com.rrtech.btechbookstore.activity

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.rrtech.btechbookstore.R
import com.rrtech.btechbookstore.fragments.AboutFragment
import com.rrtech.btechbookstore.fragments.DashboardFragment
import com.rrtech.btechbookstore.fragments.FavroiteFragment
import com.rrtech.btechbookstore.fragments.ProfileFragment

class MainActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var drawer: DrawerLayout
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    lateinit var coordinatorlayout: CoordinatorLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        drawer = findViewById(R.id.drawer)
        frameLayout = findViewById(R.id.frameLayout)
        navigationView = findViewById(R.id.navigationView)
        coordinatorlayout = findViewById(R.id.cordinatorLayout)


        var previousItem: MenuItem? = null


        setUpToolbar()


        val actionBarDrawerTogel = ActionBarDrawerToggle(
            this,
            drawer, R.string.open_drawer,
            R.string.close_drawer
        )
        drawer.addDrawerListener(actionBarDrawerTogel)
        actionBarDrawerTogel.syncState()


        openDashboard()


        navigationView.setNavigationItemSelectedListener {


            if (previousItem != null) {
                previousItem?.isChecked = false
            }
            it.isCheckable = true

            it.isChecked = true
            previousItem = it

            when (it.itemId) {
                R.id.dashboard -> {
                    openDashboard()
                    drawer.closeDrawers()

                }

                R.id.favorite -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout, FavroiteFragment()).commit()
                    supportActionBar?.title = "Favorite"
                    drawer.closeDrawers()
                }

                R.id.profile -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout, ProfileFragment()).commit()

                    supportActionBar?.title = "Profile"
                    drawer.closeDrawers()
                }

                R.id.about -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout, AboutFragment()).commit()
                    supportActionBar?.title = "About"
                    drawer.closeDrawers()
                }
            }
            return@setNavigationItemSelectedListener true
        }

    }

    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Books"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.navigationIcon?.setTint(Color.WHITE)


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            drawer.openDrawer(GravityCompat.START)
        }

        return super.onOptionsItemSelected(item)
    }

    fun openDashboard() {
        val fragment = DashboardFragment()
        val transition =
            supportFragmentManager.beginTransaction().replace(
                R.id.frameLayout,
                DashboardFragment()
            )
                .commit()
        supportActionBar?.title = "Dashboard"
        navigationView.setCheckedItem(R.id.dashboard)
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frameLayout)
        when (frag) {
            !is DashboardFragment -> openDashboard()
            else -> super.onBackPressed()
        }
    }
}