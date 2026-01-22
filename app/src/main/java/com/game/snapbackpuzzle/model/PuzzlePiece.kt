package com.game.snapbackpuzzle.model

import androidx.compose.ui.graphics.ImageBitmap

data class PuzzlePiece(
    val id: Int,
    val bitmap: ImageBitmap,
    val correctX: Float,
    val correctY: Float,
    var x: Float,
    var y: Float,
    var isPlaced: Boolean = false
)

