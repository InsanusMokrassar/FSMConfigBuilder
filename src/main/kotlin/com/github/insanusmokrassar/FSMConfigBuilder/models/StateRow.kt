package com.github.insanusmokrassar.FSMConfigBuilder.models

import com.github.insanusmokrassar.FSMConfigBuilder.argumentsField
import com.github.insanusmokrassar.FSMConfigBuilder.packageField
import com.github.insanusmokrassar.IObjectKRealisations.JSONIObject
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import org.json.JSONObject

data class StateRow(
        var numProperty: SimpleIntegerProperty = SimpleIntegerProperty(-1),
        var acceptProperty: SimpleBooleanProperty = SimpleBooleanProperty(),
        var errorProperty: SimpleBooleanProperty = SimpleBooleanProperty(),
        var stackProperty: SimpleBooleanProperty = SimpleBooleanProperty(),
        var nextProperty: SimpleIntegerProperty = SimpleIntegerProperty(-1),
        var regexProperty: SimpleStringProperty = SimpleStringProperty("."),
        var callbackProperty: SimpleStringProperty = SimpleStringProperty(),
        var callbackArguments: MutableList<Any> = ArrayList()
) {
    fun isValid(): Boolean {
        return numProperty.get() >= 0 &&
                nextProperty.get() >= -1 &&
                !(nextProperty.get() == -1 && stackProperty.get()) &&
                try {
                    Regex(regexProperty.get())
                    true
                } catch (e: Exception) {
                    false
                }
    }

    override fun toString(): String {
        return "[" +
                "${acceptProperty.get()}," +
                "${errorProperty.get()}," +
                "${stackProperty.get()}," +
                "\"${Regex(regexProperty.get()).pattern.replace("\\", "\\\\")}\"," +
                (if (nextProperty.get() == -1) "null" else nextProperty.get().toString()) +
                (
                        if (callbackProperty.get() != null && callbackProperty.get().isNotEmpty()) {
                            val callbackConfig = JSONIObject(JSONObject())
                            callbackConfig.put(packageField, callbackProperty.get())
                            if (callbackArguments.isNotEmpty()) {
                                if (callbackArguments.size == 1) {
                                    callbackConfig.put(argumentsField, callbackArguments[0])
                                } else {
                                    callbackConfig.put(argumentsField, callbackArguments)
                                }
                            }
                            callbackConfig.toString()
                        } else {
                            ""
                        }) +
                "]"
    }
}
