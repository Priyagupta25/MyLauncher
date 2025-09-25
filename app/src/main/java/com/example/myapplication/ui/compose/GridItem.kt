package com.example.myapplication.ui.compose

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.myapplication.data.local.entity.AppInfo
import kotlin.math.roundToInt


// Top-level composable: items is a MutableState<List<GridItem>>
@Composable
fun ReorderableGrid(
    itemsState: MutableState<List<AppInfo>>,
    columns: Int = 4,
    cellSpacingDp: Int = 8
) {
    val items = itemsState.value

    // Track measured bounds of each item (by index)
    val itemBounds = remember { mutableStateMapOf<Int, Rect>() }

    // Currently dragging index (null = none)
    var draggingIndex by remember { mutableStateOf<Int?>(null) }

    // Offset (in px) of the drag relative to the original item position
    var dragOffset by remember { mutableStateOf(Offset.Zero) }

    val density = LocalDensity.current
    val spacingPx = with(density) { cellSpacingDp.dp.toPx() }

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        contentPadding = PaddingValues(4.dp)
    ) {
        itemsIndexed(items, key = { index, item -> item.packageName }) { index, item ->
            // calculate per-item offset only for the item being dragged
            val isDragging = draggingIndex == index
            val z = if (isDragging) 2f else 0f

            // Visual scaling when dragging (simple animation)
            val scale by animateFloatAsState(if (isDragging) 1.05f else 1f)

            Box(
                modifier = Modifier
                    .padding(cellSpacingDp.dp / 2)
                    // measure layout bounds so we know centers
                    .onGloballyPositioned { coords ->
                        val pos = coords.localToWindow(Offset.Zero)
                        val size = coords.size
                        itemBounds[index] = Rect(
                            offset = pos,
                            size = androidx.compose.ui.geometry.Size(
                                size.width.toFloat(),
                                size.height.toFloat()
                            )
                        )
                    }
                    // Make the item respond to drag gestures
                    .pointerInput(index) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                // start dragging this item
                                draggingIndex = index
                                // reset offset
                                dragOffset = Offset.Zero
                            },
                            onDragEnd = {
                                // drop: reset drag state
                                draggingIndex = null
                                dragOffset = Offset.Zero
                            },
                            onDragCancel = {
                                draggingIndex = null
                                dragOffset = Offset.Zero
                            },
                            onDrag = { change, dragAmount ->
                                change.consume() // consume input
                                // accumulate pixel offset
                                dragOffset += dragAmount

                                // check potential swap
                                val currentIndex = draggingIndex
                                if (currentIndex == null) return@detectDragGestures

                                // compute dragged item center (window coordinates)
                                val draggedBounds =
                                    itemBounds[currentIndex] ?: return@detectDragGestures
                                val draggedCenter = draggedBounds.center + dragOffset

                                // find index whose center contains draggedCenter OR nearest center
                                val targetIndex = itemBounds.entries
                                    .map { (i, r) -> i to r.center }
                                    .minByOrNull { (_, center) ->
                                        (center - draggedCenter).getDistance()
                                    }?.first

                                if (targetIndex != null && targetIndex != currentIndex) {
                                    // swap items in list
                                    val mutable = items.toMutableList()
                                    val tmp = mutable[targetIndex]
                                    mutable[targetIndex] = mutable[currentIndex]
                                    mutable[currentIndex] = tmp
                                    // update the state (this will recompose)
                                    itemsState.value = mutable.toList()
                                    // update dragging index to new position so drag stays associated
                                    draggingIndex = targetIndex
                                }
                            }
                        )
                    }
                    // Show the dragged item as offset from its original position
                    .then(
                        if (isDragging) {
                            Modifier.offset {
                                // convert Offset (Float px) to IntOffset for Compose offset
                                IntOffset(dragOffset.x.roundToInt(), dragOffset.y.roundToInt())
                            }
                        } else Modifier
                    )
                    .zIndex(z)
                    .aspectRatio(1f) // make grid cell square
                    .background(MaterialTheme.colorScheme.surface)
                    .fillMaxWidth() // item will size inside grid cell constraints
                    .padding(8.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
            ) {
                // Content of each cell
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    androidx.compose.material3.Text(item.label, color = Color.Black)
                }
            }
        }
    }
}
