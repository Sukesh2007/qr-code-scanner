package com.example.qrcodescanner

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun CameraScreen(entry: PaddingValues, onIncrease: (content: String)->Unit) {
    var code by remember {
        mutableStateOf("")
    }
    var finalText by remember {mutableStateOf("")}
    var isScanned by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }
    var hasCamPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            hasCamPermission = it
        }
    )
    LaunchedEffect(true) {
        launcher.launch(Manifest.permission.CAMERA)
    }
    LaunchedEffect(isScanned){
        if(code.isNotEmpty()){
            if(Networking.checkHtml(code)){
                withContext(Dispatchers.IO){
                    val response = Networking.clientCall(code, context)
                    try {
                        if(response is String) finalText = response
                        else if (response is Map<*, *>) {
                            code = mapToText(response)
                        }

                    }catch(e: Exception){
                        code = "Error in Camera Screen"
                        e.printStackTrace()
                        println(e.message)
                    }
                }
            }
            finalText = code
            onIncrease(finalText)
            isScanned = false
        }
    }
    Column(
        modifier = Modifier.padding(entry).fillMaxSize()
    ){
        if(hasCamPermission) {
            AndroidView<PreviewView>(factory = { context ->
                val previewView = PreviewView(context)
                val preview = Preview.Builder().build()
                val selector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()
                preview.surfaceProvider = previewView.surfaceProvider
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                imageAnalysis.setAnalyzer(
                    ContextCompat.getMainExecutor(context),
                    QrCodeAnalyzer {result ->
                        if(!isScanned) {
                            code = result
                            isScanned = true
                        }
                    }
                )
                try {
                    cameraProviderFuture.get().bindToLifecycle(
                        lifecycleOwner,
                        selector,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                previewView
            },
                modifier = Modifier.weight(1f)
            )
            Text(
                text = code,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth().padding(32.dp)
            )
        }
    }
}

fun mapToText(map: Map<*, *>, indent: String = "   "): String {
    val sb = StringBuilder()

    for ((key, value) in map) {
        if (value is Map<*, *>) {
            sb.append("$indent$key:\n")
            sb.append(mapToText(value, "$indent  "))
        } else {
            sb.append("$indent$key: $value\n")
        }
    }
    return sb.toString()
}