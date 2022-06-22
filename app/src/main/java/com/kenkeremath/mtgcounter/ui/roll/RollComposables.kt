@file:OptIn(ExperimentalAnimationApi::class)

package com.kenkeremath.mtgcounter.ui.roll

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kenkeremath.mtgcounter.R
import com.kenkeremath.mtgcounter.compose.ComposeTheme.LocalScColors
import com.kenkeremath.mtgcounter.compose.PolyShape
import com.kenkeremath.mtgcounter.compose.TextStyles

@Composable
fun RollPanel(
    modifier: Modifier = Modifier,
    playerColor: Color? = null
) {
    var rollCount by remember { mutableStateOf(0) }
    /**
     * We need to use a pair here since repeating rolls should not be treated as the same content
     * for the sake of animation transitions
     */
    var rollResult by remember { mutableStateOf(rollCount to "") }
    val lightMode = LocalScColors.current.isLight
//    val surfaceColor = if (lightMode) playerColor
//        ?: LocalScColors.current.scBackgroundColor else LocalScColors.current.scBackgroundColor
    val panelTextColor = LocalScColors.current.scTextColorPrimary
    val diceColor =
        if (lightMode) LocalScColors.current.scBackgroundColor else LocalScColors.current.scOptionButtonColor
    val diceTextColor = LocalScColors.current.scTextColorPrimary

    Column(
        modifier = modifier
            .wrapContentHeight()
            .padding(vertical = dimensionResource(id = R.dimen.default_padding_half)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Dice(
            modifier = modifier
                .weight(1f)
                .wrapContentWidth(),
            diceColor = diceColor,
            diceTextColor = diceTextColor,
            onDiceRoll = {
                rollCount++
                rollResult = rollCount to it
            })
        AnimatedContent(
            targetState = rollResult,
            //Only fade out if there is an existing roll
            transitionSpec = {
                slideInVertically { height ->
                    -2 * height
                }.plus(fadeIn()) with fadeOut(animationSpec = tween(durationMillis = 0))
            }
        ) { result ->
            Text(
                modifier = modifier.padding(top = dimensionResource(id = R.dimen.default_padding_half)),
                text = result.second,
                style = TextStyles.label.copy(color = panelTextColor)
            )
        }
    }
}

@Preview
@Composable
private fun previewRollPanel() {
    Surface(
        modifier = Modifier.height(600.dp)
    ) {
        RollPanel(playerColor = Color.Red)
    }
}

@Composable
private fun Dice(
    modifier: Modifier = Modifier,
    diceColor: Color = LocalScColors.current.scOptionButtonColor,
    diceTextColor: Color = LocalScColors.current.scTextColorPrimary,
    onDiceRoll: (String) -> Unit = {}
) {
    Row(
        modifier
            .padding(start = dimensionResource(id = R.dimen.default_padding))
            .wrapContentSize()
            .aspectRatio(4f, matchHeightConstraintsFirst = false),
        horizontalArrangement = Arrangement.Center
    ) {
        Coin(
            modifier = modifier
                .weight(1f, false)
                .padding(end = dimensionResource(id = R.dimen.default_padding)),
            diceColor = diceColor,
            diceTextColor = diceTextColor,
            handleRollResult = { onDiceRoll(it) }
        )
        SixSidedDie(
            modifier = modifier
                .weight(1f, false)
                .padding(end = dimensionResource(id = R.dimen.default_padding)),
            diceColor = diceColor,
            diceTextColor = diceTextColor,
            handleRollResult = { onDiceRoll(it) }

        )
        TwentySidedDie(
            modifier = modifier
                .weight(1f, false)
                .padding(end = dimensionResource(id = R.dimen.default_padding)),
            diceColor = diceColor,
            diceTextColor = diceTextColor,
            handleRollResult = { onDiceRoll(it) }
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewDice() {
    Dice()
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun Die(
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    text: String,
    sides: Int,
    diceColor: Color = LocalScColors.current.scOptionButtonColor,
    diceTextColor: Color = LocalScColors.current.scTextColorPrimary,
    onRollResult: (Int) -> String = { "" },
    handleRollResult: (String) -> Unit = {},
) {
    Surface(
        elevation = 8.dp,
        color = diceColor,
        shape = shape,
        modifier = modifier
            .aspectRatio(1f)
            .fillMaxWidth(),
        onClick = {
            if (sides > 1) {
                val result = (1..sides).random()
                handleRollResult(onRollResult(result))
            }
        }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.button,
                modifier = Modifier
                    .wrapContentSize(),
                color = diceTextColor
            )
        }
    }
}

@Composable
private fun Coin(
    modifier: Modifier = Modifier,
    diceColor: Color = LocalScColors.current.scOptionButtonColor,
    diceTextColor: Color = LocalScColors.current.scTextColorPrimary,
    handleRollResult: (String) -> Unit = {}
) =
    Die(
        modifier = modifier,
        shape = CircleShape,
        text = "Coin",
        sides = 2,
        diceColor = diceColor,
        diceTextColor = diceTextColor,
        onRollResult = {
            if (it == 2) {
                "You flipped Tails"
            } else {
                "You flipped Heads"
            }
        },
        handleRollResult = handleRollResult
    )

@Composable
private fun SixSidedDie(
    modifier: Modifier = Modifier,
    diceColor: Color = LocalScColors.current.scOptionButtonColor,
    diceTextColor: Color = LocalScColors.current.scTextColorPrimary,
    handleRollResult: (String) -> Unit = {}
) =
    Die(
        modifier,
        RoundedCornerShape(12.dp),
        text = "D6",
        sides = 6,
        diceColor = diceColor,
        diceTextColor = diceTextColor,
        onRollResult = {
            "You rolled a $it on a D6"
        },
        handleRollResult = handleRollResult
    )

@Composable
private fun TwentySidedDie(
    modifier: Modifier = Modifier,
    diceColor: Color = LocalScColors.current.scOptionButtonColor,
    diceTextColor: Color = LocalScColors.current.scTextColorPrimary,
    handleRollResult: (String) -> Unit = {}
) =
    Die(
        modifier,
        PolyShape(8),
        text = "D20",
        sides = 20,
        diceColor = diceColor,
        diceTextColor = diceTextColor,
        onRollResult = {
            "You rolled a $it on a D20"
        },
        handleRollResult = handleRollResult
    )

@Composable
@Preview
private fun PreviewCoin() {
    Surface(color = Color.Red, modifier = Modifier.fillMaxSize()) {
        Coin()
    }
}