package com.kenkeremath.mtgcounter.ui.settings.profiles.edit

import android.annotation.SuppressLint
import android.app.ActionBar.LayoutParams
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.counter.CounterTemplateModel
import com.kenkeremath.mtgcounter.ui.settings.counters.OnCounterClickedListener
import com.kenkeremath.mtgcounter.ui.settings.profiles.edit.SelectCounterRecyclerAdapter.Companion.ID_DEFAULT
import com.kenkeremath.mtgcounter.ui.setup.theme.ScThemeUtils
import com.kenkeremath.mtgcounter.view.counter.CounterIconView


class SelectCounterRecyclerAdapter(
    private val selectedId: Int,
    private val onCounterClickedListener: OnCounterClickedListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        internal const val ID_DEFAULT = Integer.MAX_VALUE
    }

    init {
        setHasStableIds(true)
    }

    private val counters: MutableList<CounterTemplateModel> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setCounters(counters: List<CounterTemplateModel>) {
        this.counters.clear()
        this.counters.addAll(counters)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return SelectCounterViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_counter_selection, parent, false),
            onCounterClickedListener,
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? SelectCounterViewHolder)?.apply {
            if (position == 0) {
                bind(null, selectedId == ID_DEFAULT)
            } else {
                bind(counters[position - 1], selectedId == counters[position - 1].id)
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return if (position == 0) {
            ID_DEFAULT.toLong()
        } else {
            counters[position - 1].id.toLong()
        }
    }

    override fun getItemCount(): Int {
        // Add one for default life counter
        return counters.size + 1
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is EditProfileCounterViewHolder) {
            holder.counterIconView.clearImage()
        }
    }
}

class SelectCounterViewHolder(
    itemView: View,
    private val clickListener: OnCounterClickedListener,
) : RecyclerView.ViewHolder(itemView) {

    private val counterIconView = itemView.findViewById<CounterIconView>(R.id.counter_icon_view)
    private var counterId: Int = ID_DEFAULT

    init {
        itemView.layoutParams.height =
            itemView.resources.getDimensionPixelSize(R.dimen.edit_counters_row_min_height)
        itemView.layoutParams.width = LayoutParams.MATCH_PARENT
        itemView.setOnClickListener {
            clickListener.onCounterClicked(counterId)
        }
    }

    // null value means default life icon
    fun bind(model: CounterTemplateModel? = null, selected: Boolean) {
        counterId = model?.id ?: ID_DEFAULT
        if (model == null) {
            counterIconView.setIconDrawable(R.drawable.ic_heart)
        } else {
            counterIconView.setContent(model, renderFullArt = true)
        }

        itemView.isSelected = selected
        if (selected) {
            itemView.background = ColorDrawable(
                ScThemeUtils.resolveThemeColor(
                    itemView.context,
                    R.attr.scCounterSelectionColor
                )
            )
        } else {
            itemView.background = null
        }
    }
}
