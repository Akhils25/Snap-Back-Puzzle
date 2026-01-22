package com.game.snapbackpuzzle.pages

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
    val puzzleImages = listOf(
        R.drawable.ic_snap_game,
        R.drawable.ic_second_img,
        R.drawable.ic_third_img,
        R.drawable.ic_fourth_img,
        R.drawable.ic_fifth_img,
        R.drawable.ic_sixth_img
    )
    val randomImageRes = remember {
        puzzleImages.random()
    }

    val imageBitmap = ImageBitmap.imageResource(
        context.resources,
        randomImageRes
    )

    val pieces = remember {
        mutableStateListOf<PuzzlePiece>().apply {
            addAll(splitImage(imageBitmap, rows = 3, cols = 3))
        }
    }

    var timeLeft by remember { mutableIntStateOf(240) }
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

        Spacer(modifier = Modifier
            .height(50.dp)
            .fillMaxWidth())

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
            SuccessDialog {
                showSuccessDialog = false
                onGameFinished()
            }
        }
        if (showFailedDialog) {
            FailedDialog {
                showFailedDialog = false
                onGameFinished()
            }
        }

    }
}

@Composable
fun FailedDialog(
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { /* block dismiss */ },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp),
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_failed),
                    contentDescription = "Failed",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(bottom = 16.dp)
                )

                Text(
                    text = "Sorry!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "You have failed the puzzle!",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Try Again")
            }
        }
    )
}

@Composable
fun SuccessDialog(
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp),
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_success),
                    contentDescription = "Success",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(bottom = 16.dp)
                )

                Text(
                    text = "Congratulations!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "You have completed the puzzle!",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Continue",
                    fontSize = 16.sp
                )
            }
        }
    )
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
