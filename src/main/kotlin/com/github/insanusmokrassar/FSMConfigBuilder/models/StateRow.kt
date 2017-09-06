package com.github.insanusmokrassar.FSMConfigBuilder.models

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ObservableValue

data class StateRow(
        var numField: Int,
        var acceptField: SimpleBooleanProperty = SimpleBooleanProperty(),
        var errorField: Boolean = false,
        var returnField: Boolean = false,
        var nextField: Int? = null,
        var regexField: String = "",
        var callbackField: String? = null
)
