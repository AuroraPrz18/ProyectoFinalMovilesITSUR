package com.example.proyectofinalv2.adapters

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.proyectofinalv2.R
import com.example.proyectofinalv2.domain.model.Multimedia

class MediaListAdapter (val onClickListeners: MediaListAdapter.ViewHolder.CardViewClickListener) : RecyclerView.Adapter<MediaListAdapter.ViewHolder>(){
    var mediasList = ArrayList<Multimedia>()
    fun setData(media:ArrayList<Multimedia>){
        this.mediasList = media
    }
    class ViewHolder (view: View, val onClick: MediaListAdapter.ViewHolder.CardViewClickListener): RecyclerView.ViewHolder(view){
        val mediaLayout: LinearLayout
        val audioLayout: LinearLayout
        val nameAudio: TextView
        val deleteBtn: ImageView
        init{
            mediaLayout = view.findViewById(R.id.mediaLayout)
            audioLayout = view.findViewById(R.id.audioLayout)
            nameAudio = view.findViewById(R.id.nameAudioTxt)
            deleteBtn = view.findViewById(R.id.deleteBtn)
        }
        interface CardViewClickListener{
            fun onDeleteClickListener(media: Multimedia)
            fun onEditClickListener(media: Multimedia)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.media_element_v1, parent, false)
        return ViewHolder(view, onClickListeners)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val media: Multimedia = mediasList[position]
        holder.apply {
            if (mediaLayout.getChildCount() > 0)
                mediaLayout.removeAllViews();
            mediaLayout.childCount
            audioLayout.visibility = View.GONE
            if(media.type == 1.toLong()){ // Foto
                val imageView = ImageView(mediaLayout.context)
                imageView.layoutParams = LinearLayout.LayoutParams(800, 800)
                Glide.with(mediaLayout.context)
                    .load(media.path)
                    .fitCenter()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);
                mediaLayout.addView(imageView);
            }else if(media.type == 2.toLong()){ // Video
                val videoView = VideoView(mediaLayout.context)
                videoView.layoutParams = LinearLayout.LayoutParams(800, 800)
                videoView.setVideoPath(media.path)
                videoView.start()
                val mediacontrolleralone = MediaController(mediaLayout.context)
                mediacontrolleralone.setAnchorView(videoView)
                videoView.setMediaController(mediacontrolleralone)
                mediaLayout.addView(videoView);
            }else if(media.type == 3.toLong()){ // Audio
                audioLayout.visibility = View.VISIBLE
                nameAudio.setText(media.path)
            }
            deleteBtn.setOnClickListener {
                onClickListeners.onDeleteClickListener(media)
            }
        }
    }

    override fun getItemCount(): Int {
        return mediasList.size
    }

}