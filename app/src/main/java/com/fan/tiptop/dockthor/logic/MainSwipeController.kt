package com.fan.tiptop.dockthor.logic

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Parcelable
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView


internal enum class ButtonsState {
    GONE, LEFT_VISIBLE, RIGHT_VISIBLE
}

class MainSwipeController() : ItemTouchHelper.Callback() {
    private var swipeBack: Boolean = false
    private var buttonShowedState: ButtonsState = ButtonsState.GONE
    private val buttonWidth = 300f
    private var buttonInstance: Parcelable? = null
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(0, LEFT or RIGHT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false;
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        if (actionState == ACTION_STATE_SWIPE) {
            setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        drawButtons(c, viewHolder)
    }

    private fun setTouchListener(
        c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                swipeBack =
                    event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP
                if (swipeBack) {
                    if (dX < -buttonWidth) buttonShowedState = ButtonsState.RIGHT_VISIBLE;
                    else if (dX > buttonWidth) buttonShowedState = ButtonsState.LEFT_VISIBLE;

                }
                return false
            }
        })
    }


    private fun drawButtons(c: Canvas, viewHolder: RecyclerView.ViewHolder) {
        val buttonWidthWithoutPadding = buttonWidth - 20
        val corners = 16f
        val itemView = viewHolder.itemView
        val p = Paint()
        val leftButton = RectF(
            itemView.left.toFloat(),
            itemView.top.toFloat(), itemView.left + buttonWidthWithoutPadding,
            itemView.bottom.toFloat()
        )
        p.setColor(Color.MAGENTA)
        c.drawRoundRect(leftButton, corners, corners, p)
        drawText("CLOSEST\nDOCK", c, leftButton, p)
        val rightButton = RectF(
            itemView.right - buttonWidthWithoutPadding,
            itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat()
        )
        p.setColor(Color.BLUE)
        c.drawRoundRect(rightButton, corners, corners, p)
        drawText("CLOSEST BIKE", c, rightButton, p)
        buttonInstance = null
        if (buttonShowedState === ButtonsState.LEFT_VISIBLE) {
            buttonInstance = leftButton
        } else if (buttonShowedState === ButtonsState.RIGHT_VISIBLE) {
            buttonInstance = rightButton
        }
    }

    private fun drawText(text: String, c: Canvas, button: RectF, p: Paint) {
        val textSize = 60f
        p.setColor(Color.WHITE)
        p.setAntiAlias(true)
        p.setTextSize(textSize)
        val textWidth: Float = p.measureText(text)
        c.drawText(text, button.centerX() - textWidth / 2, button.centerY() + textSize / 2, p)
    }

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        if (swipeBack) {
            swipeBack = false
            return 0
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }
}