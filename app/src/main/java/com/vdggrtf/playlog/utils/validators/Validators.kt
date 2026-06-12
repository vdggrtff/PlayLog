package com.vdggrtf.playlog.utils.validators

object Validators {

    private  val EMAIL_REGEX = Regex("^[a-z0-9]+@[a-z0-9]+\\.[a-z]{2,}\$")

    private val PASSWORD_REGEX = Regex("^(?=.*[0-9])(?=/*[a-z])(?=./*[A-Z]).{8,}\$")

    fun isValidEmail(email: String): Boolean = email.matches(EMAIL_REGEX)
    fun isValidPassword(password: String): Boolean = password.matches(PASSWORD_REGEX)
}