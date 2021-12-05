package com.kenkeremath.mtgcounter.ui.dialog

import android.content.Context
import androidx.appcompat.app.AppCompatDialogFragment
import com.kenkeremath.mtgcounter.ui.game.OnPlayerUpdatedListener

/**
 * Legacy Code
 */
abstract class BaseCounterDialogFragment : AppCompatDialogFragment() {
    protected var playerListener: OnPlayerUpdatedListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (activity is OnPlayerUpdatedListener) {
            playerListener = activity as OnPlayerUpdatedListener?
        }
    }

    override fun onDetach() {
        super.onDetach()
        playerListener = null
    }
}