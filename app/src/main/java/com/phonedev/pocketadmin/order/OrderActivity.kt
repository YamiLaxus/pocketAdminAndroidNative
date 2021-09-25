package com.phonedev.pocketadmin.order

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.phonedev.pocketadmin.R
import com.phonedev.pocketadmin.databinding.ActivityOrderBinding
import com.phonedev.pocketadmin.entities.Order

class OrderActivity : AppCompatActivity(), OnOrderListener, OrderAux {

    private lateinit var binding: ActivityOrderBinding

    private lateinit var adapter: OrderAdapter

    private lateinit var orderSelected: Order

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupFirestore()
    }

    private fun setupRecyclerView(){
        adapter = OrderAdapter(mutableListOf(), this)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@OrderActivity)
            adapter = this@OrderActivity.adapter
        }
    }

    private fun setupFirestore(){
        val db = FirebaseFirestore.getInstance()

        db.collection(Constants.COLL_REQUEST)
            .get()
            .addOnSuccessListener {
                for (document in it){
                    val order = document.toObject(Order::class.java)
                    order.id = document.id
                    adapter.add(order)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al consultar los datos", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onStartChat(order: Order) {

    }

    override fun onStatusChange(order: Order) {

    }

    override fun getOrderSelected(): Order = orderSelected
}