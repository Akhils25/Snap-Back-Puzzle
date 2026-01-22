Snap Back Puzzle Game

A simple drag-and-drop puzzle game built with Jetpack Compose for Android. Players solve a jigsaw-style puzzle by dragging pieces into their correct positions before time runs out.

<img width="150" height="350" alt="Screenshot_20260122_124108" src="https://github.com/user-attachments/assets/8cb44013-7326-467b-973f-4867eff35597" />
<img width="150" height="350" alt="Screenshot_20260122_124122" src="https://github.com/user-attachments/assets/9c7abe88-8d86-4a58-a56d-14582fb8a417" />
<img width="150" height="350" alt="Screenshot_20260122_124137" src="https://github.com/user-attachments/assets/bb4f90ea-2e72-4a01-863e-3e9f7919eeab" />


Features

Drag and Drop Puzzle Pieces – Move pieces freely across the screen.

Snap to Place – Pieces automatically snap to their correct position when close enough.

Countdown Timer – Complete the puzzle before time runs out.

Dynamic Puzzle Generation – Supports splitting any image into a grid of pieces (default 3x3).

Composable UI – Built entirely using Jetpack Compose.

Back Navigation – Pressing the back button returns to the previous screen.

How It Works

The game splits an image into multiple puzzle pieces using the splitImage function.

Each piece is randomly positioned on the screen.

Players drag pieces using touch gestures.

When a piece is close enough to its correct position, it snaps into place.

The game finishes when all pieces are placed or the timer reaches zero.
