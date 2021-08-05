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
import android.os.Environment
import android.util.Patterns
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.solidict.ada.R
import com.solidict.ada.util.Constants.Companion.CONTACT_US_MAIL
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


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
    val regex = "^[a-zA-ZğüşöçİĞÜŞÖÇ\\s]+$".toRegex()
    return this.isNotEmpty()
            && this.trim().length >= 2
            && !this.emailValidation()
            && this.matches(regex)
}

fun String.phoneNumberValidation(context: Context): Boolean {
    val number = this.trim()
    val regex = "^[0-9-]+$".toRegex()
    return number.isNotEmpty()
            && number.length == context.resources.getInteger(R.integer.phone_number_max_length)
            && this.matches(regex)
}

fun Int.birthWeightValidation(): Boolean {
    return "$this".isNotEmpty() && this < 10000 && this > 777
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

fun Dialog.showMessageDialog(text: String) {
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

fun getVideoFile(context: Context): File {
    val fileName = "${UUID.randomUUID()}"
    val storageDirectory = context.getExternalFilesDir(
        Environment.DIRECTORY_MOVIES
    )
    return File.createTempFile(fileName, ".mp4", storageDirectory)
}

fun makeMultiPartBodyPart(filePart: String): MultipartBody.Part {
    val path = filePart.toUri().path!!
    val file = File(path)

    return MultipartBody.Part.createFormData(
        "file",
        file.name,
        file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
    )
}