package com.example.progr3ss.utils

import com.example.progr3ss.R

object CategoryIcons {

    fun iconFor(categoryName: String?): Int = when (categoryName) {
        "Exercise" -> R.drawable.ic_exercise
        "Reading" -> R.drawable.ic_reading
        "Study" -> R.drawable.ic_study
        "Hydration" -> R.drawable.ic_hydration
        "Writing" -> R.drawable.ic_writing
        "Running" -> R.drawable.ic_running
        "Other" -> R.drawable.ic_other
        else -> R.drawable.ic_add
    }
}