package com.phonedev.pocketadmin.add

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.phonedev.pocketadmin.databinding.FragmentDialogAddBinding
import com.phonedev.pocketadmin.entities.Constants
import com.phonedev.pocketadmin.entities.EventPost
import com.phonedev.pocketadmin.entities.Product
import com.phonedev.pocketadmin.products.MainAux

class AddDialogFragment : DialogFragment(), DialogInterface.OnShowListener {

    private var binding: FragmentDialogAddBinding? = null

    private var positiveButton: Button? = null
    private var negativeButton: Button? = null

    private var product: Product? = null

    private var photoSelectedUri: Uri? = null

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                photoSelectedUri = it.data?.data

                //binding?.imgProductPrevie?.setImageURI(photoSelectedUri)
                binding?.let {
                    Glide.with(this)
                        .load(photoSelectedUri)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .into(it.imgProductPrevie)
                }
            }
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let { Activity ->
            binding = FragmentDialogAddBinding.inflate(LayoutInflater.from(context))

            binding?.let {
                var builder = AlertDialog.Builder(Activity)
                    .setTitle("Agregar Producto")
                    .setPositiveButton("Agregar", null)
                    .setNegativeButton("Cancelar", null)
                    .setView(it.root)

                val dialog = builder.create()
                dialog.setOnShowListener(this)

                return dialog
            }
        }
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onShow(dialogInterface: DialogInterface?) {
        initProduct()
        configButtons()

        val dialog = dialog as? AlertDialog
        dialog?.let {
            positiveButton = it.getButton(Dialog.BUTTON_POSITIVE)
            negativeButton = it.getButton(Dialog.BUTTON_NEGATIVE)

            positiveButton?.setOnClickListener {
                binding?.let {
                    enableUI(false)

                    uploadImage(product?.id) { eventPost ->

                        if (eventPost.isSuccess) {
                            if (product == null) {
                                val product = Product(
                                    name = it.etName.text.toString().trim(),
                                    description = it.etDesciption.text.toString().trim(),
                                    imgUrl = eventPost.phtoUrl,
                                    quantity = it.etCantidad.text.toString().toInt(),
                                    price = it.etPrice.text.toString().toDouble()
                                )

                                save(product, eventPost.documentId!!)
                            } else {
                                product?.apply {
                                    name = it.etName.text.toString().trim()
                                    description = it.etDesciption.text.toString().trim()
                                    imgUrl = eventPost.phtoUrl
                                    quantity = it.etCantidad.text.toString().toInt()
                                    price = it.etPrice.text.toString().toDouble()

                                    update(this)
                                }
                            }
                        }
                    }
                }
            }

            negativeButton?.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun initProduct() {
        product = (activity as? MainAux)?.getProductSelected()
        product?.let { product ->
            binding?.let {
                it.etName.setText(product.name)
                it.etDesciption.setText(product.description)
                it.etCantidad.setText(product.quantity.toString())
                it.etPrice.setText(product.price.toString())

                Glide.with(this)
                    .load(product.imgUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(it.imgProductPrevie)
            }
        }
    }

    private fun configButtons() {
        binding?.let {
            it.ibProduct.setOnClickListener {
                openGallery()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(intent)
    }

    private fun uploadImage(productId: String?, callback: (EventPost) -> Unit) {
        val eventPost = EventPost()
        eventPost.documentId = productId ?: FirebaseFirestore.getInstance()
            .collection(Constants.COLL_PRODUCTS)
            .document().id
        var storageRef =
            FirebaseStorage.getInstance().reference.child(Constants.PATH_PRODUCT_IMGAES)

        photoSelectedUri?.let { uri ->
            binding?.let { binding ->

                binding.progressBar.visibility = View.VISIBLE

                val photoRef = storageRef.child(eventPost.documentId!!)

                photoRef.putFile(uri)
                    .addOnProgressListener {
                        val progress = (100 * it.bytesTransferred / it.totalByteCount).toInt()
                        it.run {
                            binding.progressBar.progress = progress
                            binding.tvProgress.text = String.format("%s%%", progress)
                        }
                    }
                    .addOnSuccessListener {
                        it.storage.downloadUrl.addOnSuccessListener { downloadUrl ->
                            Log.i("url", downloadUrl.toString())
                            eventPost.isSuccess = true
                            eventPost.phtoUrl = downloadUrl.toString()
                            callback(eventPost)
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(activity, "Error al subir imagen", Toast.LENGTH_SHORT).show()
                        enableUI(true)

                        eventPost.isSuccess = false
                        callback(eventPost)
                    }
            }
        }
    }

    private fun save(product: Product, documentId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection(Constants.COLL_PRODUCTS)
            .document(documentId)
            .set(product)
            //.add(product)
            .addOnSuccessListener {
                Toast.makeText(activity, "Producto Agregado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(activity, "Error al agregar", Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener {
                enableUI(true)
                binding?.progressBar?.visibility = View.INVISIBLE
                dismiss()
            }
    }

    private fun update(product: Product) {
        val db = FirebaseFirestore.getInstance()
        product.id?.let { id ->
            db.collection(Constants.COLL_PRODUCTS)
                .document(id)
                .set(product).addOnSuccessListener {
                    Toast.makeText(activity, "Producto Modificado", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(activity, "Error al modificar", Toast.LENGTH_SHORT).show()
                }
                .addOnCompleteListener {
                    enableUI(true)
                    binding?.progressBar?.visibility = View.INVISIBLE
                    dismiss()
                }
        }
    }

    private fun enableUI(enable: Boolean) {
        positiveButton?.isEnabled = enable
        negativeButton?.isEnabled = enable
        binding?.let {
            with(it) {
                etName.isEnabled = enable
                etDesciption.isEnabled = enable
                etCantidad.isEnabled = enable
                etPrice.isEnabled = enable
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}