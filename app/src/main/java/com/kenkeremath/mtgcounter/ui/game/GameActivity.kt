package com.kenkeremath.mtgcounter.ui.game

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.rongi.rotate_layout.layout.RotateLayout
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.TabletopType
import com.kenkeremath.mtgcounter.model.player.PlayerSetupModel
import com.kenkeremath.mtgcounter.ui.game.rv.GamePlayerRecyclerAdapter
import com.kenkeremath.mtgcounter.ui.game.tabletop.GameTabletopLayoutAdapter
import com.kenkeremath.mtgcounter.view.TabletopLayout
import com.kenkeremath.mtgcounter.view.counter.edit.OnCounterSelectionListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameActivity : AppCompatActivity(), OnPlayerUpdatedListener,
    OnCounterSelectionListener {

    companion object {
        const val ARGS_SETUP_PLAYERS = "args_setup_players"
        fun startIntentFromSetup(context: Context, players: List<PlayerSetupModel>): Intent {
            return Intent(context, GameActivity::class.java).putParcelableArrayListExtra(
                ARGS_SETUP_PLAYERS, ArrayList(players))
        }
    }

    private lateinit var tabletopContainer: RotateLayout
    private lateinit var tabletopLayout: TabletopLayout
    private lateinit var tabletopLayoutAdapter: GameTabletopLayoutAdapter

    private lateinit var playersRecyclerView: RecyclerView
    private lateinit var playersRecyclerAdapter: GamePlayerRecyclerAdapter

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        hideSystemUI()

        tabletopContainer = findViewById(R.id.tabletop_container)
        tabletopLayout = findViewById(R.id.tabletop_layout)
        tabletopLayoutAdapter = GameTabletopLayoutAdapter(tabletopLayout, this, this)
        tabletopLayoutAdapter.setPositions(viewModel.tabletopType)

        playersRecyclerView = findViewById(R.id.players_recycler_view)
        playersRecyclerView.layoutManager = LinearLayoutManager(this)
        playersRecyclerAdapter = GamePlayerRecyclerAdapter(this, this)
        playersRecyclerView.adapter = playersRecyclerAdapter
        val decoration = DividerItemDecoration(
            this,
            RecyclerView.VERTICAL
        )
        decoration.setDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.divider
            )!!
        )
        playersRecyclerView.addItemDecoration(
            decoration
        )

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

        viewModel.keepScreenOn.observe(this) {
            if (it) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
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

    override fun onCounterSelected(playerId: Int, templateId: Int) {
        viewModel.selectCounter(playerId, templateId)
    }

    override fun onCounterDeselected(playerId: Int, templateId: Int) {
        viewModel.deselectCounter(playerId, templateId)
    }

    override fun onCancelCounterChanges(playerId: Int) {
        viewModel.cancelCounterChanges(playerId)
    }

    override fun onConfirmCounterChanges(playerId: Int) {
        viewModel.confirmCounterChanges(playerId)
    }
}
