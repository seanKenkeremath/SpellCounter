package com.kenkeremath.mtgcounter.ui.settings.profiles.manage

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kenkeremath.mtgcounter.R

class ManageProfilesRecyclerAdapter(private val clickListener: OnProfileClickedListener) :
    RecyclerView.Adapter<ProfileViewHolder>() {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        return ProfileViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_profile, parent, false),
            clickListener
        )
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        holder.bind(profiles[position])
    }

    override fun getItemId(position: Int): Long {
        return profiles[position].name.hashCode().toLong()
    }

    override fun getItemCount(): Int {
        return profiles.size
    }
}

class ProfileViewHolder(
    itemView: View,
    private val onProfileClickedListener: OnProfileClickedListener,
) : RecyclerView.ViewHolder(itemView) {

    private val nameTextView = itemView.findViewById<TextView>(R.id.profile_name_text)

    private var profileName: String? = null

    init {
        itemView.setOnClickListener {
            profileName?.let {
                onProfileClickedListener.onProfileClicked(it)
            }
        }
    }

    fun bind(model: ProfileUiModel) {
        profileName = model.name
        nameTextView.text = model.name
    }
}
