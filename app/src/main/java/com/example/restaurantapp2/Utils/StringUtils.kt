package com.example.restaurantapp2.Utils

import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun convertedPrice(price: Double): String {
    val format = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    format.minimumFractionDigits = 0
    format.maximumFractionDigits = 2   // show up to 2 decimals if needed

    return "${format.format(price)}đ"
}

fun convertedDateTime(datetime: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy", Locale.getDefault())
    return datetime.format(formatter)
}

fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun isValidPhoneNumber(phone: String): Boolean {
    val cleaned = phone.trim()

    return cleaned.matches(Regex("^[+]?[0-9]{9,15}$"))
}