package com.kenkeremath.mtgcounter.ui.settings.profiles.edit

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.ImageView
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.counter.CounterTemplateModel
import com.kenkeremath.mtgcounter.ui.settings.counters.manage.CreateCounterViewHolder
import com.kenkeremath.mtgcounter.ui.settings.counters.manage.OnManageCounterClickedListener
import com.kenkeremath.mtgcounter.ui.setup.theme.ScThemeUtils
import com.kenkeremath.mtgcounter.ui.setup.theme.previewBackgroundColor
import com.kenkeremath.mtgcounter.view.counter.CounterIconView
import com.kenkeremath.mtgcounter.view.counter.LifeCounterView
import com.kenkeremath.mtgcounter.view.counter.edit.CounterSelectionUiModel

class EditProfileContentAdapter(
    private val editProfileCounterClickedListener: OnEditProfileCounterClickedListener,
    private val onManageCounterClickedListener: OnManageCounterClickedListener,
    private val onNameChangedListener: (String) -> Unit,
    private val onEditLifeCounterClickListener: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    init {
        setHasStableIds(true)
    }

    companion object {
        private const val TYPE_NAME = 0
        private const val TYPE_LIFE_COUNTER = 1
        private const val TYPE_COUNTERS_HEADER = 2
        private const val TYPE_CREATE_COUNTER = 3
        private const val TYPE_COUNTER = 4
    }

    private val items = mutableListOf<EditProfileItem>()

    fun setItems(
        profileName: String,
        isNameChangeEnabled: Boolean,
        lifeCounter: CounterTemplateModel?,
        counterSelections: List<CounterSelectionUiModel>
    ) {
        items.clear()
        
        // Add name section
        items.add(EditProfileItem.Name(profileName, isNameChangeEnabled))
        
        // Add life counter section
        items.add(EditProfileItem.LifeCounter(lifeCounter))
        
        // Add counters header
        items.add(EditProfileItem.CountersHeader)
        
        // Add create counter button
        items.add(EditProfileItem.CreateCounter)
        
        // Add counter items
        counterSelections.forEach { counter ->
            items.add(EditProfileItem.Counter(counter))
        }
        
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_NAME -> NameViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_edit_profile_name, parent, false),
                onNameChangedListener
            )
            TYPE_LIFE_COUNTER -> LifeCounterViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_edit_profile_life_counter, parent, false),
                onEditLifeCounterClickListener
            )
            TYPE_COUNTERS_HEADER -> CountersHeaderViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_edit_profile_counters_header, parent, false)
            )
            TYPE_CREATE_COUNTER -> CreateCounterViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_manage_counter_create, parent, false),
                onManageCounterClickedListener
            )
            TYPE_COUNTER -> EditProfileCounterViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_profile_counter, parent, false),
                editProfileCounterClickedListener
            )
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is EditProfileItem.Name -> (holder as NameViewHolder).bind(item.name, item.isNameChangeEnabled)
            is EditProfileItem.LifeCounter -> (holder as LifeCounterViewHolder).bind(item.lifeCounter)
            is EditProfileItem.CountersHeader -> (holder as CountersHeaderViewHolder).bind()
            is EditProfileItem.CreateCounter -> (holder as CreateCounterViewHolder).bind()
            is EditProfileItem.Counter -> (holder as EditProfileCounterViewHolder).bind(item.counter)
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is EditProfileItem.Name -> TYPE_NAME
            is EditProfileItem.LifeCounter -> TYPE_LIFE_COUNTER
            is EditProfileItem.CountersHeader -> TYPE_COUNTERS_HEADER
            is EditProfileItem.CreateCounter -> TYPE_CREATE_COUNTER
            is EditProfileItem.Counter -> TYPE_COUNTER
        }
    }

    override fun getItemId(position: Int): Long {
        return when (val item = items[position]) {
            is EditProfileItem.Name -> "name".hashCode().toLong()
            is EditProfileItem.LifeCounter -> "life_counter".hashCode().toLong()
            is EditProfileItem.CountersHeader -> "counters_header".hashCode().toLong()
            is EditProfileItem.CreateCounter -> "create_counter".hashCode().toLong()
            is EditProfileItem.Counter -> item.counter.template.id.toLong()
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is EditProfileCounterViewHolder) {
            holder.counterIconView.clearImage()
        }
    }
}

sealed class EditProfileItem {
    data class Name(val name: String, val isNameChangeEnabled: Boolean) : EditProfileItem()
    data class LifeCounter(val lifeCounter: CounterTemplateModel?) : EditProfileItem()
    data object CountersHeader : EditProfileItem()
    data object CreateCounter : EditProfileItem()
    data class Counter(val counter: CounterSelectionUiModel) : EditProfileItem()
}

class NameViewHolder(
    itemView: View,
    private val onNameChangedListener: (String) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val nameEditText: EditText = itemView.findViewById(R.id.profile_name_edit_text)
    private val textChangedListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onNameChangedListener(s.toString())
        }
        override fun afterTextChanged(s: Editable?) {}
    }

    init {
        nameEditText.addTextChangedListener(textChangedListener)
    }

    fun bind(name: String, isNameChangeEnabled: Boolean) {
        nameEditText.isEnabled = isNameChangeEnabled
        nameEditText.isFocusable = isNameChangeEnabled
        // Prevent updates while user is typing
        if (!nameEditText.isFocused) {
            nameEditText.removeTextChangedListener(textChangedListener)
            nameEditText.setText(name)
            nameEditText.addTextChangedListener(textChangedListener)
        }
    }
}

class LifeCounterViewHolder(
    itemView: View,
    private val onEditLifeCounterClickListener: () -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val lifeCounterView: LifeCounterView = itemView.findViewById(R.id.life_counter_preview_view)
    private val editButton: ImageView = itemView.findViewById(R.id.edit_life_counter_button)

    init {
        editButton.setOnClickListener {
            onEditLifeCounterClickListener()
        }
        if (ScThemeUtils.isLightTheme(itemView.context)) {
            lifeCounterView.background = itemView.context.previewBackgroundColor.toDrawable()
        }
    }

    fun bind(lifeCounter: CounterTemplateModel?) {
        lifeCounterView.setCustomCounter(
            counter = lifeCounter,
            playerTint = itemView.context.previewBackgroundColor
        )
    }
}

class CountersHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind() {
        // No binding needed for static text
    }
}

class EditProfileCounterViewHolder(
    itemView: View,
    private val clickListener: OnEditProfileCounterClickedListener,
) : RecyclerView.ViewHolder(itemView), CompoundButton.OnCheckedChangeListener {

    val counterIconView = itemView.findViewById<CounterIconView>(R.id.counter_icon_view)
    private val counterCheckbox = itemView.findViewById<CheckBox>(R.id.counter_checkbox)

    private var counterId: Int = -1

    init {
        itemView.setOnClickListener {
            clickListener.onCounterSelected(counterId, !itemView.isSelected)
        }
    }

    fun bind(model: CounterSelectionUiModel) {
        this.counterId = model.template.id
        counterIconView.setContent(model.template, renderFullArt = true)
        itemView.isSelected = model.selected

        //Remove listener before setting to avoid loop
        counterCheckbox.setOnCheckedChangeListener(null)
        counterCheckbox.isChecked = model.selected
        counterCheckbox.setOnCheckedChangeListener(this)
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        clickListener.onCounterSelected(counterId, isChecked)
    }
}