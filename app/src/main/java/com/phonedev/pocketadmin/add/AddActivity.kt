package com.phonedev.pocketadmin.add

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.phonedev.pocketadmin.databinding.ActivityAddBinding
import com.phonedev.pocketadmin.entities.Constants
import com.phonedev.pocketadmin.entities.Product

class AddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBinding

    private var product: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "Agregar un producto"

        setButtons()
    }

    private fun setButtons() {
        binding.btnSave.setOnClickListener {
            if (product == null) {
                val product = Product(
                    name = binding.etName.text.toString().trim(),
                    description = binding.etDesciption.text.toString().trim(),
                    quantity = binding.etCantidad.text.toString().toInt(),
                    price = binding.etPrice.text.toString().toDouble(),
                    imgUrl = binding.etImgUrl.text.toString().trim(),
                    imgMap = binding.etImgMap.text.toString().trim(),
                    facebook = binding.etFacebook.text.toString().trim(),
                    phone = binding.etPhone.text.toString().trim(),
                    disponible = binding.etDisponible.text.toString().trim()
                )
                save(product)
            } else {
                product?.apply {
                    name = binding.etName.text.toString().trim()
                    description = binding.etDesciption.text.toString().trim()
                    quantity = binding.etCantidad.text.toString().toInt()
                    price = binding.etPrice.text.toString().toDouble()
                    imgUrl = binding.etImgUrl.text.toString().trim()
                    imgMap = binding.etImgMap.text.toString().trim()
                    facebook = binding.etFacebook.text.toString().trim()
                    phone = binding.etPhone.text.toString().trim()
                    disponible = binding.etDisponible.text.toString().trim()
                }
            }
        }
    }

    private fun save(product: Product) {
        val db = FirebaseFirestore.getInstance()
        db.collection(Constants.COLL_PRODUCTS)
            .document()
            .set(product)
            .addOnSuccessListener {
                Toast.makeText(this, "Producto Agregado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al agregar", Toast.LENGTH_SHORT).show()
            }
    }

}