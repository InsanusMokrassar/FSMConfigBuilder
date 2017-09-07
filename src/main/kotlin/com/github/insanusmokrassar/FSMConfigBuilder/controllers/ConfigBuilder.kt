package com.github.insanusmokrassar.FSMConfigBuilder.controllers

import com.github.insanusmokrassar.FSMConfigBuilder.models.StateRow
import com.github.insanusmokrassar.FSMConfigBuilder.scenesManager
import com.github.insanusmokrassar.FSMConfigBuilder.utils.ScenesManager
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.CheckBoxTableCell
import javafx.scene.control.cell.TextFieldTableCell
import javafx.stage.FileChooser
import javafx.util.Callback
import javafx.util.converter.DefaultStringConverter
import javafx.util.converter.IntegerStringConverter
import java.io.File
import java.net.URL
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
    var newBtn: Button? = null

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
        nextColumn?.cellValueFactory = Callback {
            it.value.nextProperty as? ObservableValue<Int>
        }
        nextColumn?.let {
            it.cellFactory = Callback {
                TextFieldTableCell<StateRow, Int>(IntegerStringConverter())
            }
            it.onEditCommit = EventHandler {
                it.rowValue.nextProperty.set(it.newValue)
                checkConfigAndShowError()
            }
        }
        regexColumn?.let {
            it.cellFactory = Callback {
                TextFieldTableCell<StateRow, String>(DefaultStringConverter())
            }
            it.onEditCommit = EventHandler {
                it.rowValue.regexProperty.set(it.newValue)
                checkConfigAndShowError()
            }
        }
        callbackColumn?.let {
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

        newBtn?.onAction = EventHandler {
            statesList.clear()
            sendInfo("Cleaned")
        }

        saveBtn?.onAction = EventHandler {
            if (!checkConfigAndShowError()) {
                scenesManager?.let {
                    val file = chooseFile("Save config", it)
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
                    sendInfo("If you want to save config, you must choose target file path")
                }
            }
        }
    }

    private fun chooseFile(
            title: String,
            scenesManager: ScenesManager,
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