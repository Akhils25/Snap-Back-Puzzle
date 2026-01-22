package com.game.snapbackpuzzle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.game.snapbackpuzzle.pages.GameScreen
import com.game.snapbackpuzzle.pages.HomeScreen
import com.game.snapbackpuzzle.pages.SplashScreen
import com.game.snapbackpuzzle.ui.theme.SnapBackPuzzleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SnapBackPuzzleTheme {
                GameRoot()
            }
        }
    }
}

@Composable
fun GameRoot() {

    var currentScreen by remember { mutableStateOf(AppScreen.SPLASH) }

    when (currentScreen) {

        AppScreen.SPLASH -> {
            SplashScreen(
                onFinish = {
                    currentScreen = AppScreen.HOME
                }
            )
        }

        AppScreen.HOME -> {
            HomeScreen(
                onStartGame = {
                    currentScreen = AppScreen.GAME
                }
            )
        }

        AppScreen.GAME -> {
            GameScreen(
                onGameFinished = {
                    currentScreen = AppScreen.HOME
                }
            )
        }
    }
}

private enum class AppScreen {
    SPLASH,
    HOME,
    GAME
}
