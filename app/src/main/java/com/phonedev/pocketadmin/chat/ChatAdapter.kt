package com.phonedev.pocketadmin.chat

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.phonedev.pocketadmin.R
import com.phonedev.pocketadmin.databinding.ItemChatBinding
import com.phonedev.pocketadmin.entities.Message

class ChatAdapter(
    private val messageList: MutableList<Message>,
    private val listener: OnChatListener
) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messageList[position]

        holder.setListener(message)

        var gravity = Gravity.END
        var background = ContextCompat.getDrawable(context, R.drawable.background_chat_support)
        var textColor = ContextCompat.getColor(context, R.color.design_default_color_on_primary)

        val marginHorizontal = context.resources.getDimensionPixelSize(R.dimen.chat_margin_horizontal)
        val params = holder.binding.tvMessage.layoutParams as ViewGroup.MarginLayoutParams
        params.marginStart = marginHorizontal
        params.marginEnd = 0
        params.topMargin = 0

        if (position > 0 && message.isSendByMe() != messageList[position -1].isSendByMe()){
            params.topMargin = context.resources.getDimensionPixelSize(R.dimen.common_padding_min)
        }

        if (!message.isSendByMe()) {
            gravity = Gravity.START
            background = ContextCompat.getDrawable(context, R.drawable.background_chat_client)
            textColor = ContextCompat.getColor(context, R.color.design_default_color_on_secondary)
            params.marginStart = 0
            params.marginEnd = marginHorizontal
        }

        holder.binding.root.gravity = gravity

        holder.binding.tvMessage.layoutParams = params
        holder.binding.tvMessage.setBackground(background)
        holder.binding.tvMessage.setTextColor(textColor)
        holder.binding.tvMessage.text = message.message
    }

    override fun getItemCount(): Int = messageList.size

    fun add(message: Message) {
        if (!messageList.contains(message)) {
            messageList.add(message)
            notifyItemInserted(messageList.size - 1)
        }
    }

    fun update(message: Message) {
        val index = messageList.indexOf(message)
        if (index != -1) {
            messageList.set(index, message)
            notifyItemChanged(index)
        }
    }

    fun delete(message: Message) {
        val index = messageList.indexOf(message)
        if (index != -1) {
            messageList.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemChatBinding.bind(view)

        fun setListener(message: Message) {
            binding.tvMessage.setOnLongClickListener {
                listener.deleteMessage(message)
                true
            }
        }
    }

}