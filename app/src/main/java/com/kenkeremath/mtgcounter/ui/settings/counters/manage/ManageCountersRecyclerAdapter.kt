package com.kenkeremath.mtgcounter.ui.settings.counters.manage

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.view.counter.CounterIconView

class ManageCountersRecyclerAdapter(private val clickListener: OnManageCounterClickedListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_CREATE = 0
        private const val TYPE_COUNTER = 1
        private const val ID_CREATE = -1L
    }


    init {
        setHasStableIds(true)
    }

    private val counters: MutableList<ManageCounterUiModel> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setCounters(counters: List<ManageCounterUiModel>) {
        this.counters.clear()
        this.counters.addAll(counters)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return if (viewType == TYPE_CREATE) {
            CreateCounterViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_manage_counter_create, parent, false),
                clickListener
            )
        } else {
            ManageCounterViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_manage_counter, parent, false),
                clickListener
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ManageCounterViewHolder) {
            //Offset by one for "Create" button
            holder.bind(counters[position - 1])
        }
    }

    override fun getItemId(position: Int): Long {
        //Offset by one for "Create" button
        return if (position == 0) ID_CREATE else counters[position - 1].template.id.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            TYPE_CREATE
        } else {
            TYPE_COUNTER
        }
    }

    override fun getItemCount(): Int {
        //Add 1 for "Create" Button
        return counters.size + 1
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is ManageCounterViewHolder) {
            holder.counterIconView.clearImage()
        }
    }
}

class ManageCounterViewHolder(
    itemView: View,
    private val clickListener: OnManageCounterClickedListener,
) : RecyclerView.ViewHolder(itemView) {

    val counterIconView = itemView.findViewById<CounterIconView>(R.id.counter_icon_view)
    private val counterRemoveButton = itemView.findViewById<ImageView>(R.id.counter_delete)

    private var counterId: Int = -1

    init {
        itemView.setOnClickListener {
            clickListener.onCounterClicked(counterId)
        }
        counterRemoveButton.setOnClickListener {
            clickListener.onCounterRemoveClicked(counterId)
        }
    }

    fun bind(model: ManageCounterUiModel) {
        this.counterId = model.template.id
        counterIconView.setContent(model.template, renderFullArt = true)
        counterRemoveButton.visibility = if (model.template.deletable) View.VISIBLE else View.GONE
    }
}

class CreateCounterViewHolder(
    itemView: View,
    private val onCounterClickedListener: OnManageCounterClickedListener,
) : RecyclerView.ViewHolder(itemView) {

    init {
        itemView.setOnClickListener {
            onCounterClickedListener.onCounterCreateClicked()
        }
    }

    fun bind() {
        //no op
    }
}
