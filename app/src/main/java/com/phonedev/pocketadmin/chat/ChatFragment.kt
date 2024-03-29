package com.phonedev.pocketstore.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.phonedev.pocketadmin.R
import com.phonedev.pocketadmin.chat.ChatAdapter
import com.phonedev.pocketadmin.chat.OnChatListener
import com.phonedev.pocketadmin.databinding.FragmentChatBinding
import com.phonedev.pocketadmin.entities.Constants
import com.phonedev.pocketadmin.entities.Message
import com.phonedev.pocketadmin.entities.Order
import com.phonedev.pocketadmin.order.OrderAux

class ChatFragment : Fragment(), OnChatListener {

    private var binding: FragmentChatBinding? = null

    private lateinit var adapter: ChatAdapter

    private var order: Order? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        binding?.let {
            return it.root
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getOrder()
        setupRecyclerView()
        setupButtons()
    }

    private fun getOrder(){
        order = (activity as OrderAux)?.getOrderSelected()
        order?.let {
            setupActionBar()
            setupRealtimeDatabase()
        }
    }

    private fun setupRealtimeDatabase(){
        order?.let {
            val database = Firebase.database
            val chatRef = database.getReference(Constants.PATH_CHATS).child(it.id)
            val childListener = object : ChildEventListener{
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    getMessage(snapshot)?.let {
                        adapter.add(it)
                        binding?.recyclerView?.scrollToPosition(adapter.itemCount -1)
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    getMessage(snapshot)?.let {
                        adapter.update(it)
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    getMessage(snapshot)?.let {
                        adapter.delete(it)
                    }
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onCancelled(error: DatabaseError) {
                    binding?.let {
                        Snackbar.make(it.root, "Error al cargar chat.", Snackbar.LENGTH_LONG).show()
                    }
                }
            }
            chatRef.addChildEventListener(childListener)
        }
    }

    private fun getMessage(snapshot: DataSnapshot) : Message? {
        snapshot.getValue(Message::class.java)?.let { message ->
            snapshot.key?.let {
                message.id = it
            }
            FirebaseAuth.getInstance().currentUser?.let { user ->
                message.myUId = user.uid
            }
            return message
        }
        return null
    }

    private fun setupRecyclerView(){
        adapter = ChatAdapter(mutableListOf(), this)
        binding?.let {
            it.recyclerView.apply {
                layoutManager = LinearLayoutManager(context).also {
                    it.stackFromEnd = true
                }
                adapter = this@ChatFragment.adapter
            }
        }

    /*    (1..20).forEach {
            adapter.add(Message(it.toString(),if (it%4 == 0)"Hola, Como estas?, Hola, Como estas?" else "Hola, Como estas?",
                if (it%3 == 0)"tu" else "yo", "yo"))
        }*/
    }

    private fun setupButtons(){
        binding?.let { binding ->
            binding.ibSend.setOnClickListener {
                sendMessage()
            }
        }
    }

    private fun sendMessage(){
        binding?.let { binding ->
            order?.let {
                val database = Firebase.database
                val chatRef = database.getReference(Constants.PATH_CHATS).child(it.id)
                val user = FirebaseAuth.getInstance().currentUser
                user?.let {
                    val message = Message(message = binding.etMessage.text.toString().trim(),
                        sender = it.uid)

                    binding.ibSend.isEnabled = false

                    chatRef.push().setValue(message)
                        .addOnSuccessListener {
                            binding.etMessage.setText("")
                        }
                        .addOnCompleteListener {
                            binding.ibSend.isEnabled = true
                        }
                }
            }
        }

    }

    private fun setupActionBar(){
        (activity as? AppCompatActivity)?.let {
            it.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            it.supportActionBar?.title = getString(R.string.chat_title)
            setHasOptionsMenu(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            activity?.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onDestroy() {
        (activity as? AppCompatActivity)?.let {
            it.supportActionBar?.setDisplayHomeAsUpEnabled(false)
            it.supportActionBar?.title = getString(R.string.order_title)
            setHasOptionsMenu(false)
        }
        super.onDestroy()
    }

    override fun deleteMessage(message: Message) {
        order?.let {

            val database = Firebase.database
            val messageRef = database.getReference(Constants.PATH_CHATS).child(it.id).child(message.id)
            messageRef.removeValue { error, ref ->
                binding?.let {
                    if (error != null){
                        Snackbar.make(it.root, "Error al eliminar mensaje.", Snackbar.LENGTH_LONG).show()
                    } else {
                        Snackbar.make(it.root, "Mensaje Borrado.", Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}