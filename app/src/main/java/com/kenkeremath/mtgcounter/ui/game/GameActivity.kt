package com.kenkeremath.mtgcounter.ui.game

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.ui.setup.SetupTabletopLayoutAdapter
import com.kenkeremath.mtgcounter.view.TabletopLayout
import dagger.android.AndroidInjection
import javax.inject.Inject

class GameActivity : AppCompatActivity() {

    private lateinit var tabletopLayout: TabletopLayout
    private lateinit var tabletopLayoutAdapter: SetupTabletopLayoutAdapter

    @Inject
    lateinit var gameViewModelFactory: GameViewModelFactory

    private lateinit var viewModel: GameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        viewModel = ViewModelProvider(this, gameViewModelFactory).get(GameViewModel::class.java)

        tabletopLayout = findViewById(R.id.tabletop_layout)

        tabletopLayoutAdapter = SetupTabletopLayoutAdapter(tabletopLayout)
        tabletopLayoutAdapter.setPositions(viewModel.tabletopType)
    }
}
