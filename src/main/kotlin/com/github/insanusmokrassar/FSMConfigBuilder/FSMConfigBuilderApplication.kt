package com.github.insanusmokrassar.FSMConfigBuilder

import com.github.insanusmokrassar.FSMConfigBuilder.utils.ScenesManager
import javafx.application.Application
import javafx.stage.Stage

var scenesManager: ScenesManager? = null
class FSMConfigBuilderApplication(
        private val title: String = "FSM Config Builder",
        private val version: Float = 0.1F
) : Application() {
    override fun start(primaryStage: Stage?) {
        primaryStage?.let {
            it.title = "$title $version"
            scenesManager = ScenesManager(it)
            scenesManager?.addScene(javaClass.classLoader.getResource("main.fxml"))
        }
    }
}
