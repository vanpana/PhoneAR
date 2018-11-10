package com.cyberschnitzel.phonear

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.ux.TransformationSystem
import java.util.function.Consumer

class Phone(private val context: Context, transformationSystem: TransformationSystem,
            private val phoneData: PhoneData, phoneRenderable: ModelRenderable, private val comparable: Boolean = true)
    : TransformableNode(transformationSystem), Node.OnTapListener {

    private lateinit var phoneActionsPopup: Node
    private var canGoBack: Boolean = false

    companion object {
        private const val INFO_CARD_Y_POS_COEFF = 0.06f
    }


    init {
        setOnTapListener(this)
        renderable = phoneRenderable

        scaleController.minScale = phoneData.minScale
        scaleController.maxScale = phoneData.minScale + 0.01f
    }

    override fun onActivate() {
        if (scene == null)
            throw IllegalStateException("Scene is null!")

        if (!::phoneActionsPopup.isInitialized) {
            phoneActionsPopup = Node()
            phoneActionsPopup.setParent(this)
            phoneActionsPopup.isEnabled = false
            initMenuPopup()
        }
    }

    private val compareButtonClick = View.OnClickListener {

    }

    private val specsButtonClick = View.OnClickListener {
        initSpecsPopup()
    }

    private val handsModeButtonClick = View.OnClickListener {

    }

    private fun initPopup(layout: Int, then: Consumer<ViewRenderable>) {
        val dimension = phoneData.size.h * INFO_CARD_Y_POS_COEFF + 1.0f
        phoneActionsPopup.localPosition = Vector3(0.0f, dimension, 0.0f)
        phoneActionsPopup.localScale = Vector3(dimension, dimension, dimension)

        ViewRenderable.builder()
                .setView(context, layout)
                .build()
                .thenAccept(then)
                .exceptionally { throwable ->
                    throw AssertionError("Could not load plane card view.", throwable)
                }
    }

    private fun initMenuPopup() {
        initPopup(R.layout.menu_phone_tap, Consumer { renderable ->
            phoneActionsPopup.renderable = renderable
            val view = renderable.view  // Get the phoneActionsPopup view

            val compareButton = view.findViewById(R.id.compare) as Button
            compareButton.setOnClickListener(compareButtonClick)
            if (!this.comparable) {
                compareButton.visibility = View.GONE
            }
            val specsButton = view.findViewById(R.id.specs) as Button
            specsButton.setOnClickListener(specsButtonClick)
            val handsModeButton = view.findViewById(R.id.hands_mode) as Button
            handsModeButton.setOnClickListener(handsModeButtonClick)
        })
        canGoBack = false
    }

    private fun initSpecsPopup() {
        initPopup(R.layout.fragment_specs, Consumer { renderable ->
            phoneActionsPopup.renderable = renderable
            val view = renderable.view  // Get the view

            val phoneName = view.findViewById(R.id.phone_name) as TextView
            phoneName.text = phoneData.phoneName

            val os = view.findViewById(R.id.os) as TextView
            os.text = phoneData.os

            val cpu = view.findViewById(R.id.cpu) as TextView
            cpu.text = phoneData.cpu

            val camera = view.findViewById(R.id.camera) as TextView
            camera.text = phoneData.camera
        })
        canGoBack = true
    }

    override fun onTap(p0: HitTestResult?, p1: MotionEvent?) {
        if (!::phoneActionsPopup.isInitialized) {
            return
        }

        phoneActionsPopup.isEnabled = !phoneActionsPopup.isEnabled
    }

    override fun onUpdate(p0: FrameTime?) {
        if (!::phoneActionsPopup.isInitialized || !phoneActionsPopup.isEnabled) {
            return
        }

        if (scene == null) {
            return
        }

        val cameraPosition = scene.camera.worldPosition
        val cardPosition = phoneActionsPopup.worldPosition
        val direction = Vector3.subtract(cameraPosition, cardPosition)
        val lookRotation = Quaternion.lookRotation(direction, Vector3.up())
        phoneActionsPopup.worldRotation = lookRotation
    }

    fun onBackPressed(): Boolean {
        return if (canGoBack) {
            initMenuPopup()
            false
        } else {
            true
        }
    }
}