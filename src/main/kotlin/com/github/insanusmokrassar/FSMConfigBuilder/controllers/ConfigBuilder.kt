package com.github.insanusmokrassar.FSMConfigBuilder.controllers

import com.github.insanusmokrassar.FSMConfigBuilder.argumentsField
import com.github.insanusmokrassar.FSMConfigBuilder.models.StateRow
import com.github.insanusmokrassar.FSMConfigBuilder.packageField
import com.github.insanusmokrassar.FSMConfigBuilder.scenesManager
import com.github.insanusmokrassar.FSMConfigBuilder.utils.ScenesManager
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.control.cell.CheckBoxTableCell
import javafx.scene.control.cell.TextFieldTableCell
import javafx.stage.FileChooser
import javafx.util.Callback
import javafx.util.converter.DefaultStringConverter
import javafx.util.converter.IntegerStringConverter
import org.json.JSONArray
import org.json.JSONException
import java.io.File
import java.net.URL
import java.nio.charset.Charset
import java.util.*
import java.util.logging.Logger
import kotlin.collections.ArrayList

class ConfigBuilder : Initializable {

    private val statesList: ObservableList<StateRow> = observableArrayList()

    @FXML
    var infoLabel: Label? = null

    @FXML
    var verifyBtn: Button? = null

    @FXML
    var saveBtn: Button? = null

    @FXML
    var clearBtn: Button? = null

    @FXML
    var loadBtn: Button? = null

    @FXML
    var statesTableView: TableView<StateRow>? = null

    @FXML
    var numColumn: TableColumn<StateRow, Int>? = null

    @FXML
    var acceptColumn: TableColumn<StateRow, Boolean>? = null

    @FXML
    var errorColumn: TableColumn<StateRow, Boolean>? = null

    @FXML
    var stackColumn: TableColumn<StateRow, Boolean>? = null

    @FXML
    var nextColumn: TableColumn<StateRow, Int>? = null

    @FXML
    var regexColumn: TableColumn<StateRow, String>? = null

    @FXML
    var callbackColumn: TableColumn<StateRow, String>? = null

    @FXML
    var addBtn: Button? = null

    @FXML
    var removeBtn: Button? = null

    override fun initialize(location: URL?, resources: ResourceBundle?) {

        //Say me how to do better??? -----
        numColumn?.cellValueFactory = Callback {
            it.value.numProperty as? ObservableValue<Int>
        }
        nextColumn?.let {
            it.cellValueFactory = Callback {
                it.value.nextProperty as? ObservableValue<Int>
            }
            it.cellFactory = Callback {
                TextFieldTableCell<StateRow, Int>(IntegerStringConverter())
            }
            it.onEditCommit = EventHandler {
                it.rowValue.nextProperty.set(it.newValue)
                checkConfigAndShowError()
            }
        }
        regexColumn?.let {
            it.cellValueFactory = Callback {
                it.value.regexProperty
            }
            it.cellFactory = Callback {
                TextFieldTableCell<StateRow, String>(DefaultStringConverter())
            }
            it.onEditCommit = EventHandler {
                it.rowValue.regexProperty.set(it.newValue)
                checkConfigAndShowError()
            }
        }
        callbackColumn?.let {
            it.cellValueFactory = Callback {
                it.value.callbackProperty
            }
            it.cellFactory = Callback {
                TextFieldTableCell<StateRow, String>(DefaultStringConverter())
            }
            it.onEditCommit = EventHandler {
                it.rowValue.callbackProperty.set(it.newValue)
                checkConfigAndShowError()
            }
        }

        acceptColumn?.cellFactory = Callback {
            CheckBoxTableCell<StateRow, Boolean>(Callback {
                statesList[it].acceptProperty
            })
        }
        errorColumn?.cellFactory = Callback {
            CheckBoxTableCell<StateRow, Boolean>(Callback {
                statesList[it].errorProperty
            })
        }
        stackColumn?.cellFactory = Callback {
            CheckBoxTableCell<StateRow, Boolean>(Callback {
                statesList[it].stackProperty
            })
        }
        //-------

        statesTableView?.items = statesList

        addBtn?.onAction = EventHandler {
            if (!checkConfigAndShowError()) {
                val newState = StateRow()
                newState.numProperty.set(statesList.size)
                statesList.add(newState)
            }
        }

        removeBtn?.onAction = EventHandler {
            val inFocus = statesTableView?.focusModel?.focusedItem
            inFocus?.let {
                statesList.remove(it)
                recalculateNums()
            }
        }

        verifyBtn?.onAction = EventHandler {
            if (!checkConfigAndShowError()) {
                sendInfo("All right")
            }
        }

        clearBtn?.onAction = EventHandler {
            statesList.clear()
            sendInfo("Cleaned")
        }

        saveBtn?.onAction = EventHandler {
            if (!checkConfigAndShowError()) {
                scenesManager?.let {
                    val file = chooseFile(
                            "Save config",
                            it,
                            "If you want to save config, you must choose target file path"
                    )
                    file?.let {
                        val config = generateConfig()
                        if (!it.exists()) {
                            it.createNewFile()
                        }
                        val stream = it.outputStream()
                        stream.write("$config\n".toByteArray())
                        stream.flush()
                        stream.close()
                        return@EventHandler
                    }
                }
            }
        }

        loadBtn?.onAction = EventHandler {
            scenesManager?.let {
                val file = chooseFile(
                        "Save config",
                        it,
                        "If you want to load config, you must choose target file")
                file?.let {
                    if (it.exists()) {
                        if (loadConfig(it.readBytes().toString(Charset.defaultCharset()))) {
                            sendInfo("Successfuly loaded")
                        } else {
                            sendInfo("Not loaded")
                        }
                    }
                }
            }
        }
    }

    private fun loadConfig(config: String): Boolean {
        try {
            val configList = JSONArray(config)
            val states = ArrayList<StateRow>()
            for (i: Int in 0 until configList.length()) {
                val currentStateConfig = configList.getJSONArray(i)
                val currentState = StateRow()
                currentState.numProperty.set(i)
                currentState.acceptProperty.set(currentStateConfig.getBoolean(0))
                currentState.errorProperty.set(currentStateConfig.getBoolean(1))
                currentState.stackProperty.set(currentStateConfig.getBoolean(2))
                currentState.regexProperty.set(currentStateConfig.getString(3))
                try {
                    currentState.nextProperty.set(currentStateConfig.getInt(4))
                } catch (e: Exception) {
                    currentState.nextProperty.set(-1)
                }
                try {
                    val callbackConfig = currentStateConfig.getJSONObject(5)
                    currentState.callbackProperty.set(callbackConfig.getString(packageField))
                    try {
                        val argumentsConfig = callbackConfig.getJSONArray(argumentsField)
                        for (argumentNum: Int in 0 until argumentsConfig.length()) {
                            currentState.callbackArguments.add(argumentsConfig.get(argumentNum))
                        }
                    } catch(e: Exception) {
                        currentState.callbackArguments.add(callbackConfig.get(argumentsField))
                    }
                } catch (e: Exception) {

                }
                states.add(currentState)
            }
            statesList.clear()
            statesList.addAll(states)
            return true
        } catch (e: JSONException) {
            sendInfo("Can't load config")
        }
        return false
    }

    private fun chooseFile(
            title: String,
            scenesManager: ScenesManager,
            cancelMessage: String,
            extensions: Map<String, String> = mapOf(
                    Pair("Any file", "*")
            )
    ): File? {
        val fileChooser = FileChooser()
        fileChooser.title = title
        extensions.forEach { k, v -> fileChooser.extensionFilters.add(
                FileChooser.ExtensionFilter(k, v)
        ) }
        return try {
            fileChooser.showOpenDialog(scenesManager.current.window)
        } catch (e: IllegalStateException) {
            sendInfo(cancelMessage)
            null
        }
    }

    private fun recalculateNums() {
        var i = 0
        statesList.forEach {
            it.numProperty.set(i)
            i++
        }
    }

    private fun generateConfig(): String {
        val config = ArrayList<String>()
        statesList.forEach {
            config.add(it.toString())
        }
        return config.toString()
    }

    /**
     * return true if was error
     */
    private fun checkConfigAndShowError(): Boolean {
        val error = checkConfig()
        error?.let {
            infoLabel?.text = error
            return true
        }
        return false
    }

    private fun checkConfig(): String? {
        val buffer = StringBuilder()
        statesList.forEach {
            if (!it.isValid()) {
                buffer.append("${statesList.indexOf(it)}\n")
            }
        }
        return (if (buffer.isEmpty()) null else "Next states are invalid:\n$buffer")
    }

    private fun sendInfo(info: String) {
        infoLabel?.let {
            it.text = info
        }
        Logger.getGlobal().info(info)
    }
}