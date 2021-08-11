package com.kenkeremath.mtgcounter.ui.game

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.view.TabletopLayout
import dagger.android.AndroidInjection
import javax.inject.Inject

class GameActivity : AppCompatActivity(), OnPlayerUpdatedListener {

    private lateinit var tabletopLayout: TabletopLayout
    private lateinit var tabletopLayoutAdapter: GameTabletopLayoutAdapter

    @Inject
    lateinit var gameViewModelFactory: GameViewModelFactory

    private lateinit var viewModel: GameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        viewModel = ViewModelProvider(this, gameViewModelFactory).get(GameViewModel::class.java)

        tabletopLayout = findViewById(R.id.tabletop_layout)

        tabletopLayoutAdapter = GameTabletopLayoutAdapter(tabletopLayout, this)
        tabletopLayoutAdapter.setPositions(viewModel.tabletopType)

        viewModel.players.observe(this) {
            tabletopLayoutAdapter.updateAll(viewModel.tabletopType, it)
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

    override fun onCounterAdded(playerId: Int) {
        viewModel.addCounter(playerId)
    }
}
