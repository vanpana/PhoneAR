package com.cyberschnitzel.phonear

import android.content.ContentValues.TAG
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.net.Uri
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ScaleController
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.ux.TransformationSystem

class PhoneDialog(context: Context, transformationSystem: TransformationSystem) :
        TransformableNode(transformationSystem), InputChangedTrigger {

    lateinit var inputRenderable: ViewRenderable
    lateinit var parentPhoneNameInput: EditText
    private lateinit var phoneNameInput: EditText
    private var phoneAdapter: AutoCompletePhoneAdapter? = null
    private var suggestionList: List<PhoneData> = mutableListOf()
    lateinit var phoneSelectedTrigger: PhoneSelectedTrigger

    init {
        fillSugestionsList()
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

                    // TODO delete this dummy
                    val suggestionList = view.findViewById(R.id.suggestion_list) as RecyclerView
                    suggestionList.layoutManager = LinearLayoutManager(context)
                    phoneAdapter = AutoCompletePhoneAdapter(this.suggestionList, context)
                    suggestionList.adapter = phoneAdapter

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
        if (phoneNameInput.text.toString() != "") {
            (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(parentPhoneNameInput.windowToken, 0)

            // Find the phone and build it
            ModelRenderable.builder()
                    .setSource(context, Uri.parse("Phone_01.sfb"))
                    .build()
                    .thenAccept { model ->
                        val phone = Phone(context, transformationSystem,
                                PhoneData("samsung_note_9", Size(70.0f, 143.0f, 7.7f),
                                        "android", "snapdragon", "12mp"),
                                model)
                        phoneSelectedTrigger.onPhoneSelected(phone)
                        phoneNameInput.setText("")
                    }
                    .exceptionally { throwable ->
                        Log.d(TAG, throwable.localizedMessage)
                        val toast = Toast.makeText(context, "Unable to load andy renderable", Toast.LENGTH_LONG)
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()
                        return@exceptionally null
                    }
        }

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


    fun fillSugestionsList() = {
        this.suggestionList = listOf(
                PhoneData("Iphone X", Size(1f, 2f, 3f)),
                PhoneData("Samsung S9", Size(2f, 3f, 4f)),
                PhoneData("Samsung S3", Size(2f, 3f, 4f)),
                PhoneData("Samsung S6", Size(2f, 3f, 4f)),
                PhoneData("Samsung S7", Size(2f, 3f, 4f)),
                PhoneData("Oneplus 6", Size(2f, 3f, 4f)),
                PhoneData("Lenovo A1", Size(2f, 3f, 4f)),
                PhoneData("Blueberry Privo", Size(2f, 3f, 4f)),
                PhoneData("Samsung A3", Size(2f, 3f, 4f)),
                PhoneData("Iphone 6", Size(3f, 4f, 5f))
        )
    }
}