package com.example.insclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.insclone.fragmen.HomeFragment
import com.example.insclone.fragmen.NotificationFragment
import com.example.insclone.fragmen.ProfileFragment
import com.example.insclone.fragmen.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
//ketika layout menggunakan material, maka di style harus di ganti ke material design
class MainActivity : AppCompatActivity() {
//untuk mengaktifkan bottom navigation
    public val onNavigationItemSelectedListener  = BottomNavigationView.OnNavigationItemSelectedListener {item ->
        when(item.itemId){
            R.id.nav_home ->{
                moveToFragment(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_search -> {
                moveToFragment(SearchFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_add_post -> {
                item.isChecked = false
                startActivity(Intent(this@MainActivity, TambahPostActivity::class.java))
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_notification -> {
                moveToFragment(NotificationFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_user -> {
                moveToFragment(ProfileFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
    false
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //semua yang masuk ke onCreate akan di build dan akan tampil di dalam projek kita nanti.
        val navView:BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        //agar ketika di buka tampilan awal adalah home fragment
        moveToFragment(HomeFragment())
    }

    //aksi pindah fragment ke fragment lain
    private fun moveToFragment(fragment: Fragment) {
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.fragmen_container, fragment)
        fragmentTrans.commit()
    }

}