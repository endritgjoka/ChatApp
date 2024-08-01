package com.endritgjoka.chatapp.data.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController

fun Fragment.safeNavigate(destination: NavDirections) = with(findNavControllerSafely()) {
    this?.currentDestination?.getAction(destination.actionId)
        ?.let { navigate(destination) }
}

fun Fragment.findNavControllerSafely(): NavController? {
    return if (isAdded) {
        findNavController()
    } else {
        null
    }
}

fun <T : Activity> navigate(activityClass: Class<T>, context: Context, extras: Bundle? = null) {
    Intent(context, activityClass).also { intent ->
        extras?.let {
            intent.putExtras(it)
        }
        ContextCompat.startActivity(context, intent, null)
    }
}