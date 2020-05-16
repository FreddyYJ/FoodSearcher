package com.github.freddyyj.foodsearcher.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PageViewModel : ViewModel() {

    var recognizedText:String=""
    fun setText(text: String) {
        this.recognizedText = text
    }
}