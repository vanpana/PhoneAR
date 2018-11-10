package com.cyberschnitzel.phonear

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.widget.Toast
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import kotlinx.android.synthetic.main.activity_ux.*

/**
 * This is an example activity that uses the Sceneform UX package to make common AR tasks easier.
 */
class HelloSceneformActivity : AppCompatActivity() {

    private var arFragment: ArFragment? = null

    private var phoneDialog: PhoneDialog? = null

    override// CompletableFuture requires api level 24
    // FutureReturnValueIgnored is not valid
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!checkIsSupportedDeviceOrFinish(this)) {
            return
        }
        setContentView(R.layout.activity_ux)

        arFragment = supportFragmentManager.findFragmentById(R.id.ux_fragment) as ArFragment?

        phoneDialog = PhoneDialog(applicationContext, arFragment!!.transformationSystem)
        phoneDialog!!.parentPhoneNameInput = phone_name_input
        phone_name_input.addTextChangedListener(PhoneInputWatcher())

        arFragment!!.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane, _: MotionEvent ->
            if (phoneDialog == null) {
                return@setOnTapArPlaneListener
            }

            // Create the Anchor.
            val anchor = hitResult.createAnchor()
            val anchorNode = AnchorNode(anchor)
            anchorNode.setParent(arFragment!!.arSceneView.scene)

            // Create the transformable andy and add it to the anchor.
            phoneDialog!!.setParent(anchorNode)
            phoneDialog!!.select()
        }
    }

    companion object {
        private val TAG = HelloSceneformActivity::class.java.simpleName
        private const val MIN_OPENGL_VERSION = 3.0

        fun checkIsSupportedDeviceOrFinish(activity: Activity): Boolean {
            val openGlVersionString = (activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                    .deviceConfigurationInfo
                    .glEsVersion
            if (openGlVersionString.toDouble() < MIN_OPENGL_VERSION) {
                Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later")
                Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG).show()
                activity.finish()
                return false
            }
            return true
        }
    }

    inner class PhoneInputWatcher : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {

        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            phoneDialog!!.updateText(p0.toString())
        }

    }

}
