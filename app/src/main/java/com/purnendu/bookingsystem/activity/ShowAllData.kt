package com.purnendu.bookingsystem.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.purnendu.bookingsystem.Adapter
import com.purnendu.bookingsystem.R
import com.purnendu.bookingsystem.database.Database

class ShowAllData : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var database: Database
    private lateinit var adapter: Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_all_data)


        database = Database.getDataBase(this)
        recyclerView = findViewById(R.id.recyclerView)


        adapter = Adapter(this)
        recyclerView.adapter=adapter
        val layoutManager = LinearLayoutManager(this@ShowAllData)
        recyclerView.layoutManager=layoutManager


        database.taskDao().getDetails().observe(this) { outerIt ->
            adapter.submitList(outerIt.reversed())
        }


    }
}



