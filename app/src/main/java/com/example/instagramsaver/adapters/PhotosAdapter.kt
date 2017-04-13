package com.example.instagramsaver.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.instagramsaver.R
import com.example.instagramsaver.command.ContentCommand
import com.example.instagramsaver.command.OpenContent
import com.example.instagramsaver.command.RemoveContent
import com.example.instagramsaver.command.ShareContent
import com.example.instagramsaver.entity.Content


/**
 * Created by binary on 3/7/17.
 */
class PhotosAdapter(val list: MutableList<Content>, val listener: (ContentCommand) -> Unit) : RecyclerView.Adapter<PhotosAdapter.Holder>() {

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bingPhoto(list[position], listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val li = LayoutInflater.from(parent.context)
        return Holder(li.inflate(R.layout.item_photo, parent,false)) { removeAt(it) }
    }

    override fun getItemCount(): Int = list.size

     class Holder(itemView: View, val removeAt: (content:Content)->Unit) : RecyclerView.ViewHolder(itemView) {
         private var mImageView: ImageView
         private var mIsVideoContent: ImageView

         private var mShare: View
         private var mDelete: View

         init {
             mImageView = itemView.findViewById(R.id.preview) as ImageView
             mIsVideoContent = itemView.findViewById(R.id.play) as ImageView
             mShare = itemView.findViewById(R.id.share_photo)
             mDelete = itemView.findViewById(R.id.delete)
         }

        fun bingPhoto(photo: Content, listener: (ContentCommand) -> Unit) = with(itemView) {
            mIsVideoContent.visibility = if(photo.isPhoto)  View.GONE else View.VISIBLE
            Glide.with(itemView.context).load(photo.path).fitCenter().into(mImageView)
            setOnClickListener {
                listener(OpenContent(photo, context))
            }

            mShare.setOnClickListener {
                listener(ShareContent(photo, context))
            }

            mDelete.setOnClickListener {
                listener(RemoveContent(photo, context) {
                    if(it) {
                        removeAt.invoke(photo)
                    }
                })
            }
        }
     }

    fun removeAt(content: Content) {
        val position = list.indexOf(content)
        list.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, list.size)
    }

}