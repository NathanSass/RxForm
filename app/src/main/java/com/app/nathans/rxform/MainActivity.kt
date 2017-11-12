package com.app.nathans.rxform

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

class MainActivity : AppCompatActivity() {

    private lateinit var loginEt: EditText
    private lateinit var passwordEt: EditText
    private lateinit var loginBtn: View
    private lateinit var invalidField: Drawable
    private lateinit var validField: Drawable

    private companion object {
        val emailRegex = "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"" +
                "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"
        val passwordRegex = "^(?=.*\\d).{4,8}$"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loginBtn = findViewById(R.id.loginBtn)
        loginEt = findViewById(R.id.loginEt)
        passwordEt = findViewById(R.id.passwordEt)
        invalidField = ContextCompat.getDrawable(this, android.R.drawable.presence_busy)
        validField = ContextCompat.getDrawable(this, android.R.drawable.presence_online)

        val loginObservable = RxTextView.textChanges(loginEt)
        loginObservable.map { text -> isValidLogin(text) }
                .subscribe { isValid -> setValidationStateDrawable(loginEt, isValid) }

        val passwordObservable = RxTextView.textChanges(passwordEt)
        passwordObservable.map { text -> isValidPassword(text) }
                .subscribe { isValid -> setValidationStateDrawable(passwordEt, isValid) }

        val combinedObservable: Observable<Boolean> = Observable.combineLatest(
                loginObservable,
                passwordObservable,
                BiFunction { u, p -> isValidLogin(u) && isValidPassword(p) })
        combinedObservable.subscribe { isVisible -> loginBtn.visibility = if (isVisible) View.VISIBLE else View.GONE }

    }

    private fun setValidationStateDrawable(view: EditText, isValid: Boolean) {
        view.setCompoundDrawablesRelativeWithIntrinsicBounds(null,
                null,
                if (isValid) validField else invalidField,
                null)
    }


    private fun isValidLogin(value: CharSequence): Boolean {
        val regex = Regex(emailRegex)
        return regex.matches(value)
    }

    private fun isValidPassword(value: CharSequence): Boolean {
        val regex = Regex(passwordRegex)
        return regex.matches(value)

    }
}
