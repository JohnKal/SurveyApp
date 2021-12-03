package com.example.surveyapp.extensions

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText


fun EditText.onTextChanged(action: (Editable?)-> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(editable: Editable?) {
            action(editable)
        }
    })
}