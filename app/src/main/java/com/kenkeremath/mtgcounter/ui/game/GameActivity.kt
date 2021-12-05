package com.kenkeremath.mtgcounter.ui.game

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.CounterModel
import com.kenkeremath.mtgcounter.model.TabletopType
import com.kenkeremath.mtgcounter.ui.dialog.EditCounterDialogFragment
import com.kenkeremath.mtgcounter.ui.dialog.OnGameDialogListener
import com.kenkeremath.mtgcounter.ui.game.rv.GamePlayerRecyclerAdapter
import com.kenkeremath.mtgcounter.ui.game.tabletop.GameTabletopLayoutAdapter
import com.kenkeremath.mtgcounter.view.TabletopLayout
import dagger.android.AndroidInjection
import javax.inject.Inject

class GameActivity : AppCompatActivity(), OnPlayerUpdatedListener, OnGameDialogListener {

    private lateinit var tabletopContainer: ViewGroup
    private lateinit var tabletopLayout: TabletopLayout
    private lateinit var tabletopLayoutAdapter: GameTabletopLayoutAdapter

    private lateinit var playersRecyclerView: RecyclerView
    private lateinit var playersRecyclerAdapter: GamePlayerRecyclerAdapter

    @Inject
    lateinit var gameViewModelFactory: GameViewModelFactory

    private lateinit var viewModel: GameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        viewModel = ViewModelProvider(this, gameViewModelFactory).get(GameViewModel::class.java)

        tabletopContainer = findViewById(R.id.tabletop_container)
        tabletopLayout = findViewById(R.id.tabletop_layout)
        tabletopLayoutAdapter = GameTabletopLayoutAdapter(tabletopLayout, this, this)
        tabletopLayoutAdapter.setPositions(viewModel.tabletopType)

        playersRecyclerView = findViewById(R.id.players_recycler_view)
        playersRecyclerView.layoutManager = LinearLayoutManager(this)
        playersRecyclerAdapter = GamePlayerRecyclerAdapter(this, this)
        playersRecyclerView.adapter = playersRecyclerAdapter

        if (viewModel.tabletopType == TabletopType.LIST) {
            playersRecyclerView.visibility = View.VISIBLE
            tabletopContainer.visibility = View.GONE
        } else {
            playersRecyclerView.visibility = View.GONE
            tabletopContainer.visibility = View.VISIBLE
        }

        viewModel.players.observe(this) {
            tabletopLayoutAdapter.updateAll(viewModel.tabletopType, it)
            playersRecyclerAdapter.setData(it)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_FULLSCREEN)

        if (viewModel.hideNavigation.value == true) {
            // Conditionally hide nav bar
            window.decorView.systemUiVisibility = (window.decorView.systemUiVisibility
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        }
    }

    override fun onLifeIncremented(playerId: Int, amountDifference: Int) {
        viewModel.incrementPlayerLife(playerId, amountDifference)
    }

    override fun onLifeAmountSet(playerId: Int, amount: Int) {
        //TODO
    }

    override fun onCounterIncremented(playerId: Int, counterId: Int, amountDifference: Int) {
        viewModel.incrementCounter(playerId, counterId, amountDifference)
    }

    override fun onCounterAmountSet(playerId: Int, counterId: Int, amount: Int) {
        //TODO
    }

    override fun onCounterAdded(playerId: Int, counterModel: CounterModel) {
        viewModel.addCounter(playerId, counterModel)
    }

    override fun onCounterEdited(playerId: Int, counterPosition: Int, counterModel: CounterModel) {
        //TODO
    }

    override fun onOpenAddCounterDialog(playerId: Int) {
        val dialog = EditCounterDialogFragment.newCreateInstance(playerId)
        dialog.show(supportFragmentManager, "add_counter")
    }
}
