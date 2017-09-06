package com.github.insanusmokrassar.FSMConfigBuilder

import com.github.insanusmokrassar.FSMConfigBuilder.utils.ScenesManager
import javafx.application.Application
import javafx.stage.Stage

class FSMConfigBuilderApplication(
        private val title: String = "FSM Config Builder",
        private val version: Float = 0.1F
) : Application() {
    private var scenesManager: ScenesManager? = null
    override fun start(primaryStage: Stage?) {
        primaryStage?.let {
            it.title = "$title $version"
            scenesManager = ScenesManager(it)
            scenesManager?.addScene(javaClass.classLoader.getResource("main.fxml"))
        }
    }
}
