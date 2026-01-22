package com.game.snapbackpuzzle.pages

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.game.snapbackpuzzle.R
import com.game.snapbackpuzzle.model.PuzzlePiece
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.random.Random

@Composable
fun GameScreen(
    onGameFinished: () -> Unit
) {
    val context = LocalContext.current

    val imageBitmap = ImageBitmap.imageResource(
        context.resources,
        R.drawable.ic_snap_game
    )

    val pieces = remember {
        mutableStateListOf<PuzzlePiece>().apply {
            addAll(splitImage(imageBitmap, rows = 3, cols = 3))
        }
    }

    var timeLeft by remember { mutableIntStateOf(30) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showFailedDialog by remember { mutableStateOf(false) }

    BackHandler {
        onGameFinished()
    }

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
        showFailedDialog = true
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Text(
            text = "Time: ${timeLeft}s",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 30.dp, bottom = 20.dp),
            color = Color.Black,
            fontSize = 18.sp
        )

        pieces.forEach { piece ->
            PuzzlePieceView(
                piece = piece,
                onPlaced = {
                    if (pieces.all { it.isPlaced }) {
                        showSuccessDialog = true
                    }
                }
            )
        }
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text(text = "Congratulations!") },
                text = { Text(text = "You have completed the puzzle!") },
                confirmButton = {
                    Button(onClick = {
                        showSuccessDialog = false
                        onGameFinished()
                    }) {
                        Text("OK")
                    }
                }
            )
        }
        if (showFailedDialog) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text(text = "Sorry!") },
                text = { Text(text = "You have Failed the puzzle!") },
                confirmButton = {
                    Button(onClick = {
                        showFailedDialog = false
                        onGameFinished()
                    }) {
                        Text("OK")
                    }
                }
            )
        }

    }
}

@Composable
fun PuzzlePieceView(
    piece: PuzzlePiece,
    onPlaced: () -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(piece.x) }
    var offsetY by remember { mutableFloatStateOf(piece.y) }

    Image(
        bitmap = piece.bitmap,
        contentDescription = null,
        modifier = Modifier
            .offset { IntOffset(offsetX.toInt(), offsetY.toInt()) }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    },
                    onDragEnd = {
                        val dx = abs(offsetX - piece.correctX)
                        val dy = abs(offsetY - piece.correctY)

                        if (dx < 40 && dy < 40) {
                            offsetX = piece.correctX
                            offsetY = piece.correctY
                            piece.isPlaced = true
                            onPlaced()
                        }
                    }
                )
            }
    )
}


fun splitImage(
    bitmap: ImageBitmap,
    rows: Int,
    cols: Int
): List<PuzzlePiece> {

    val pieces = mutableListOf<PuzzlePiece>()

    val pieceWidth = bitmap.width / cols
    val pieceHeight = bitmap.height / rows

    var id = 0

    for (row in 0 until rows) {
        for (col in 0 until cols) {

            val pieceBitmap = Bitmap.createBitmap(
                bitmap.asAndroidBitmap(),
                col * pieceWidth,
                row * pieceHeight,
                pieceWidth,
                pieceHeight
            ).asImageBitmap()

            pieces.add(
                PuzzlePiece(
                    id = id++,
                    bitmap = pieceBitmap,
                    correctX = col * pieceWidth.toFloat(),
                    correctY = row * pieceHeight.toFloat(),
                    x = Random.nextFloat() * 600f,
                    y = Random.nextFloat() * 1000f
                )
            )
        }
    }
    return pieces
}
