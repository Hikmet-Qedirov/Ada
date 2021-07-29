package com.solidict.ada.util

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.util.Patterns
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.solidict.ada.R
import com.solidict.ada.util.Constants.Companion.CONTACT_US_MAIL
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

fun hasInternetConnection(context: Context): Boolean {
    val connectivityManager = context.getSystemService(
        Context.CONNECTIVITY_SERVICE
    ) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
    return when {
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}

fun String.nameValidation(): Boolean {
    return this.isNotEmpty() && Pattern
        .compile("^[a-zA-Z]{4,}(?: [a-zA-Z]+){0,2}\$").matcher(this).matches()
}

fun String.phoneNumberValidation(context: Context): Boolean {
    val number = this.trim()
    return number.isNotEmpty() && number.length == context.resources.getInteger(R.integer.phone_number_max_length)
}

fun Int.birthWeightValidation(): Boolean {
    return this > 500
}

fun String.emailValidation(): Boolean {
    val email = this.trim()
    return Patterns.EMAIL_ADDRESS.matcher(email).matches() && this.isNotEmpty()
}

fun View.closeKeyboard() {
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(this.windowToken, 0)
}

fun Dialog.showLoadingDialogConfig() {
    this.apply {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        setContentView(R.layout.loading_dialog)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}

fun Dialog.showInternetStateConnection() {
    this.apply {
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        setContentView(R.layout.fragment_dialog)
        val yesButton: Button = findViewById(R.id.customAlertDialogOkButton)
        val noButton: TextView = findViewById(R.id.customAlertDialogCancelButton)
        val content: TextView = findViewById(R.id.customAlertDialogText)
        noButton.visibility = View.GONE
        yesButton.text = context.getString(R.string.ok)
        yesButton.setOnClickListener {
            dismiss()
        }
        content.text = context.getString(R.string.lost_internet_connection)
    }
}

fun Dialog.showErrorMessageDialog(text: String) {
    this.apply {
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        setContentView(R.layout.fragment_dialog)
        val yesButton: Button = findViewById(R.id.customAlertDialogOkButton)
        val noButton: TextView = findViewById(R.id.customAlertDialogCancelButton)
        val content: TextView = findViewById(R.id.customAlertDialogText)
        noButton.visibility = View.GONE
        yesButton.text = context.getString(R.string.ok)
        yesButton.setOnClickListener {
            dismiss()
        }
        content.text = text
        show()
    }
}

fun Activity.changeStatusBarColor(res: Int) {
    this.window?.statusBarColor = ContextCompat.getColor(this.applicationContext, res)
}

fun contactUsChooserOpen(context: Context): Intent {
    val subject = context.getString(R.string.share_app_text)

    val date = Calendar.getInstance().time
    val formatter = SimpleDateFormat.getDateTimeInstance()
    val formattedDate = formatter.format(date)

    return Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf(CONTACT_US_MAIL))
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, formattedDate)
    }
}

fun String.getCurrentDateFormat(): String {
    val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return formatter.format(parser.parse(this)!!)
}