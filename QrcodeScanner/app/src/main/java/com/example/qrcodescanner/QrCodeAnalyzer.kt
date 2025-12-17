package com.example.qrcodescanner

import android.graphics.ImageFormat
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.nio.ByteBuffer

class QrCodeAnalyzer(
    private val onQrcodeScanned: (String) -> Unit
) : ImageAnalysis.Analyzer{
    private val supportedImageFormats = listOf(
        ImageFormat.YUV_420_888,
        ImageFormat.YUV_422_888,
        ImageFormat.YUV_444_888
    )
    override fun analyze(image: ImageProxy) {
        if(image.format in supportedImageFormats) {
            val bytes = image.planes.first().buffer.toByteArray()
            val source = PlanarYUVLuminanceSource(
                bytes,
                image.width, image.height,
                0, 0,
                image.width, image.height, false
            )
            val binaryBmp = BinaryBitmap(HybridBinarizer(source))
            try{
                val result = MultiFormatReader().apply {
                    setHints(
                        mapOf(
                            DecodeHintType.POSSIBLE_FORMATS to arrayListOf(
                                BarcodeFormat.QR_CODE
                            )
                        )
                    )
                }.decode(binaryBmp)
                onQrcodeScanned(result.text)
            } catch(e: Exception){
                e.printStackTrace()
            } finally{
                image.close()
            }
        }
    }

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        return ByteArray(remaining()).also {
            get(it)
        }
    }
}

//class QrCodeAnalyzer(
//    private val onQrcodeScanned: (String) -> Unit
//) : ImageAnalysis.Analyzer {
//
//    private val reader = MultiFormatReader().apply {
//        setHints(
//            mapOf(
//                DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE)
//            )
//        )
//    }
//
//    override fun analyze(image: ImageProxy) {
//        try {
//            val buffer = image.planes[0].buffer
//            val bytes = ByteArray(buffer.remaining())
//            buffer.get(bytes)
//
//            val source = PlanarYUVLuminanceSource(
//                bytes,
//                image.width,
//                image.height,
//                0,
//                0,
//                image.width,
//                image.height,
//                false
//            )
//
//            val bitmap = BinaryBitmap(HybridBinarizer(source))
//            val result = reader.decodeWithState(bitmap)
//
//            onQrcodeScanned(result.text)
//
//        } catch (e: com.google.zxing.NotFoundException) {
//            // ✅ NORMAL – ignore
//        } catch (e: Exception) {
//            e.printStackTrace()
//        } finally {
//            reader.reset()
//            image.close()
//        }
//    }
//}


