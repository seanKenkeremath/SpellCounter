package com.kenkeremath.mtgcounter.ui.settings.profiles.manage

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kenkeremath.mtgcounter.R

class ManageProfilesRecyclerAdapter(private val clickListener: OnProfileClickedListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_CREATE = 0
        private const val TYPE_PROFILE = 1
        private const val ID_CREATE = -1L
    }

    init {
        setHasStableIds(true)
    }

    private val profiles: MutableList<ProfileUiModel> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setProfiles(profiles: List<ProfileUiModel>) {
        this.profiles.clear()
        this.profiles.addAll(profiles)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_CREATE) {
            CreateProfileViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_manage_profile_create, parent, false),
                clickListener
            )
        } else {
            ManageProfileViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_manage_profile, parent, false),
                clickListener
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ManageProfileViewHolder) {
            //Offset by one for "Create" button
            holder.bind(profiles[position - 1])
        }
    }

    override fun getItemId(position: Int): Long {
        //Offset by one for "Create" button
        return if (position == 0) ID_CREATE else profiles[position - 1].name.hashCode().toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            TYPE_CREATE
        } else {
            TYPE_PROFILE
        }
    }

    override fun getItemCount(): Int {
        //add one for the "Create" button
        return profiles.size + 1
    }
}

class ManageProfileViewHolder(
    itemView: View,
    private val onProfileClickedListener: OnProfileClickedListener,
) : RecyclerView.ViewHolder(itemView) {

    private val nameTextView = itemView.findViewById<TextView>(R.id.profile_name_text)
    private val deleteButton = itemView.findViewById<View>(R.id.profile_delete)

    private var profileName: String? = null

    init {
        itemView.setOnClickListener {
            profileName?.let {
                onProfileClickedListener.onProfileClicked(it)
            }
        }
        deleteButton.setOnClickListener {
            profileName?.let {
                onProfileClickedListener.onProfileDeleteClicked(it)
            }
        }
    }

    fun bind(model: ProfileUiModel) {
        profileName = model.name
        nameTextView.text = model.name
    }
}

class CreateProfileViewHolder(
    itemView: View,
    private val onProfileClickedListener: OnProfileClickedListener,
) : RecyclerView.ViewHolder(itemView) {

    init {
        itemView.setOnClickListener {
            onProfileClickedListener.onProfileCreateClicked()
        }
    }
}
