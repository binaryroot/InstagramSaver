package com.example.instagramsaver.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.instagramsaver.FileUtils
import com.example.instagramsaver.R
import com.example.instagramsaver.Utility
import com.example.instagramsaver.adapters.PhotosAdapter
import com.example.instagramsaver.command.ContentCommand
import com.example.instagramsaver.entity.Content
import com.example.instagramsaver.utility.L
import kotlin.properties.Delegates

/**
 * Created by binary on 3/7/17.
 */
class PhotosFragment() : Fragment() {

    val mItems: MutableList<Content> = mutableListOf()

    var mPhotosRecycleView: RecyclerView? = null

    companion object {
        fun newInstance(): Fragment = PhotosFragment()
    }

    //region Fragment
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_photos, container, false)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
    }

    override fun onStart() {
        super.onStart()
        mItems.clear()
        mItems.addAll(FileUtils.loadSavedPhotos())
        mPhotosRecycleView?.let {
            it.adapter.notifyDataSetChanged()
        }

    }
    //endregion

    //region Utility API
    fun initView(view: View?) {
        view?.let {
            mPhotosRecycleView = it.findViewById(R.id.photosRecyclerView) as RecyclerView
            mItems.addAll(FileUtils.loadSavedPhotos())
            val adapter = PhotosAdapter(mItems, ContentCommand::execute)
            mPhotosRecycleView?.let {
                it.layoutManager = GridLayoutManager(context, 2)
                it.adapter = adapter
            }
        }
    }
    //endregion
}