package com.cyberschnitzel.phonear

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ScaleController
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.ux.TransformationSystem

class PhoneDialog(context: Context, transformationSystem: TransformationSystem) :
        TransformableNode(transformationSystem), InputChangedTrigger {

    lateinit var inputRenderable: ViewRenderable
    lateinit var parentPhoneNameInput: EditText
    private lateinit var phoneNameInput: EditText

    init {
        ViewRenderable.builder()
                .setView(context, R.layout.dialog_phone_searcher)
                .build()
                .thenAccept { renderable ->
                    inputRenderable = renderable

                    val view = renderable.view  // Get the view

                    // Set the edit text
                    phoneNameInput = view.findViewById(R.id.phone_name_input) as EditText
                    phoneNameInput.setOnClickListener(phoneNameInputClickListener)

                    // Set the button
                    val actionButton = view.findViewById(R.id.action_button) as Button
                    actionButton.setOnClickListener(onShowPhoneClickListener)

                    // Set the Node rendarable
                    this.renderable = inputRenderable
                }
                .exceptionally { throwable ->
                    throw AssertionError("Could not load plane card view.", throwable)
                }

        scaleController.minScale = ScaleController.DEFAULT_MIN_SCALE
        scaleController.maxScale = ScaleController.DEFAULT_MAX_SCALE
    }

    private val phoneNameInputClickListener = View.OnClickListener {
        if (::parentPhoneNameInput.isInitialized) {
            parentPhoneNameInput.isFocusableInTouchMode = true
            parentPhoneNameInput.requestFocusFromTouch()
            (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(parentPhoneNameInput, 0)

        }
    }

    private val onShowPhoneClickListener = View.OnClickListener {
        Toast.makeText(context, "SHOW MY PHONE ALREADY", Toast.LENGTH_LONG).show()
    }

    override fun updateText(text: String) {
        phoneNameInput.setText(text)
    }

    override fun onUpdate(p0: FrameTime?) {
        if (!::inputRenderable.isInitialized) {
            return
        }

        if (scene == null) {
            return
        }

        val cameraPosition = scene.camera.worldPosition
        val cardPosition = worldPosition
        val direction = Vector3.subtract(cameraPosition, cardPosition)
        val lookRotation = Quaternion.lookRotation(direction, Vector3.up())
        worldRotation = lookRotation
    }
}