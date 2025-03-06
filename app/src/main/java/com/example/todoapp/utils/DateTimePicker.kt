package com.example.todoapp.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

fun showDateTimePickerDialog(
    context: Context,
    onDateTimeSelected: (LocalDateTime) -> Unit
) {
    val currentDate = LocalDate.now()
    val currentTime = LocalTime.now()

    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    val selectedDateTime = LocalDateTime.of(
                        year, month + 1, dayOfMonth,
                        hourOfDay, minute
                    )
                    onDateTimeSelected(selectedDateTime)
                },
                currentTime.hour,
                currentTime.minute,
                true
            ).show()
        },
        currentDate.year,
        currentDate.monthValue - 1,
        currentDate.dayOfMonth
    ).show()
}
