package com.kenkeremath.mtgcounter.ui.settings.profiles.edit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.model.player.PlayerTemplateModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileActivity : AppCompatActivity() {

    companion object {
        fun getStartIntent(context: Context, profile: PlayerTemplateModel? = null): Intent {
            return Intent(context, EditProfileActivity::class.java).putExtra(ARGS_PROFILE, profile)
        }

        private const val ARGS_PROFILE = "args_profile"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.container, EditProfileDialogFragment.newInstance(
                        intent.getParcelableExtra(
                            ARGS_PROFILE
                        )
                    ),
                    EditProfileDialogFragment.TAG
                )
                .commit()
        }
    }

    override fun onBackPressed() {
        supportFragmentManager.findFragmentByTag(EditProfileDialogFragment.TAG)?.let {
            if (it is EditProfileDialogFragment) {
                if (!it.isBackEnabled()) {
                    val dialog = AlertDialog.Builder(this)
                        .setTitle(R.string.edit_profile_save_changes_title)
                        .setMessage(R.string.edit_profile_save_changes_message)
                        .setPositiveButton(R.string.edit_profile_save_changes_discard) { dialog, _ ->
                            dialog.dismiss()
                            super.onBackPressed()
                        }
                        .setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }

                    dialog.show()
                } else {
                    super.onBackPressed()
                }
            }
        } ?: super.onBackPressed()
    }
}