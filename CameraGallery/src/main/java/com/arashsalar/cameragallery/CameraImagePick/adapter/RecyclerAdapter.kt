package com.arashsalar.cameragallery.CameraImagePick.adapter

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.arashsalar.cameragallery.R

class RecyclerAdapter(var bitmapList: ArrayList<Bitmap>): RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    private var onClickInterface: OnClickInterface? = null
    private var getList: GetList? = null


    fun setClickListener(onClickInterface: OnClickInterface?) {
        this.onClickInterface = onClickInterface
    }

    fun setListListener(getList: GetList) {
        this.getList = getList
    }
    interface OnClickInterface {
        fun clikced(view: View?, position: Int)
    }
    interface GetList {
        fun getArrayList(list: ArrayList<Bitmap>)
    }
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener{
        private val imageView: ImageView
        init {
            imageView = view.findViewById(R.id.image)
            imageView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            onClickInterface?.clikced(p0, adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_recycler_items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.findViewById<ImageView>(R.id.image).setImageBitmap(bitmapList.get(position))
        getList?.getArrayList(bitmapList)
        Log.d("inOnbind", bitmapList.size.toString());
    }

    override fun getItemCount(): Int {
        return bitmapList.size
    }
}