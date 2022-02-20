package com.kenkeremath.mtgcounter.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.kenkeremath.mtgcounter.R

class ComposeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)
        val composeView = findViewById<ComposeView>(R.id.compose_view)
        composeView.setContent {
            AppCompatTheme {
                Greeting()
            }
        }
    }

    @Composable
    private fun Greeting() {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.h5,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.default_padding))
                .wrapContentWidth(Alignment.CenterHorizontally)
        )
    }
}