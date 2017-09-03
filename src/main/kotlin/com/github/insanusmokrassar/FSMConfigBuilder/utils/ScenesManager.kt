package com.github.insanusmokrassar.FSMConfigBuilder.utils

import javafx.scene.Scene
import java.util.EmptyStackException
import java.io.IOException
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.stage.Stage
import java.net.URL
import java.util.Stack


class ScenesManager
constructor(private var stage: Stage) {
    private var scenes = Stack<Scene>()

    fun addScene(scenePath: URL) {
        try {
            val root: Parent = FXMLLoader.load(scenePath)
            addScene(Scene(root), false)
        } catch (e: IOException) {
            throw IllegalArgumentException("Can't get file of Scene", e)
        }

    }

    fun addScene(scenePath: URL, fullscreen: Boolean?) {
        try {
            val root: Parent = FXMLLoader.load(scenePath)
            addScene(Scene(root), fullscreen)
        } catch (e: IOException) {
            throw IllegalArgumentException("Can't get file of Scene", e)
        }

    }

    fun addScene(scene: Scene, fullscreen: Boolean? = false) {

        scenes.add(scene)
        stage.scene = current
        stage.show()

        stage.isFullScreen = fullscreen!!
    }

    fun back(): Scene? {
        try {
            scenes.pop()
            stage.scene = current
            stage.show()
            return current
        } catch (e: EmptyStackException) {
            System.exit(0)
        }

        return null
    }

    val current: Scene
        get() = scenes.peek()

}
