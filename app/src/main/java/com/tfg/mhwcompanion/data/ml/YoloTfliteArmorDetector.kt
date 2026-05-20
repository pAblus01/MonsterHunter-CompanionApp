package com.tfg.mhwcompanion.data.ml

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.NormalizeOp
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import kotlin.math.max
import kotlin.math.min

class YoloTfliteArmorDetector(context: Context) {

    private val appContext = context.applicationContext
    private val labels: List<ParsedArmorLabel> = loadLabels()
    private val interpreter: Interpreter = Interpreter(
        FileUtil.loadMappedFile(appContext, ModelAssetNames.DETECTOR_MODEL),
        Interpreter.Options().apply {
            setNumThreads(4)
        }
    )

    private val inputShape = interpreter.getInputTensor(0).shape()
    private val inputDataType = interpreter.getInputTensor(0).dataType()
    private val inputWidth: Int
    private val inputHeight: Int
    private val expectsNhwc: Boolean

    private val imageProcessor: ImageProcessor

    init {
        val shape = inputShape
        expectsNhwc = shape.size == 4 && shape[3] == 3
        inputHeight = if (expectsNhwc) shape[1] else shape[2]
        inputWidth = if (expectsNhwc) shape[2] else shape[3]

        val builder = ImageProcessor.Builder().add(
            ResizeOp(inputHeight, inputWidth, ResizeOp.ResizeMethod.BILINEAR)
        )
        if (inputDataType == DataType.FLOAT32) {
            builder.add(NormalizeOp(0f, 255f))
        }
        imageProcessor = builder.build()
    }

    fun detect(bitmap: Bitmap, confidenceThreshold: Float = 0.10f, maxDetections: Int = 10): List<DetectorResult> {
        val safeBitmap = bitmap.ensureArgb8888()
        val tensorImage = TensorImage(inputDataType).apply { load(safeBitmap) }
        val processedImage = imageProcessor.process(tensorImage)

        val outputShape = interpreter.getOutputTensor(0).shape()
        val outputType = interpreter.getOutputTensor(0).dataType()
        val outputBuffer = TensorBuffer.createFixedSize(outputShape, outputType)
        interpreter.run(processedImage.buffer, outputBuffer.buffer)

        val candidates = decodeOutput(
            values = outputBuffer.floatArray,
            outputShape = outputShape,
            originalWidth = safeBitmap.width,
            originalHeight = safeBitmap.height,
            confidenceThreshold = confidenceThreshold
        )

        return nonMaxSuppression(candidates, maxDetections = maxDetections)
            .map { candidate ->
                DetectorResult(
                    armorClassId = candidate.armorClassId,
                    label = candidate.label,
                    boundingBox = candidate.boundingBox,
                    confidence = candidate.confidence
                )
            }
    }

    private fun decodeOutput(
        values: FloatArray,
        originalWidth: Int,
        originalHeight: Int,
        confidenceThreshold: Float
    ): List<DetectionCandidate> {
        if (values.size < 5) return emptyList()

        val results = mutableListOf<DetectionCandidate>()
        val rowCount = 8400

        for (rowIndex in 0 until rowCount) {
            val xCenter = values[rowIndex]
            val yCenter = values[rowCount + rowIndex]
            val width = values[rowCount * 2 + rowIndex]
            val height = values[rowCount * 3 + rowIndex]
            val score = values[rowCount * 4 + rowIndex]

            if (score < confidenceThreshold) continue

            val parsed = labelForIndex(0)
            val bbox = decodeCenterBox(xCenter, yCenter, width, height, originalWidth, originalHeight)
            results += DetectionCandidate(parsed.armorId, parsed.displayName, score, bbox)
        }

        return results
    }

    private fun decodeCenterBox(
        xCenter: Float,
        yCenter: Float,
        width: Float,
        height: Float,
        originalWidth: Int,
        originalHeight: Int
    ): RectF {
        val scaled = shouldScaleToInput(xCenter, yCenter, width, height)
        val centerX = if (scaled) xCenter * inputWidth else xCenter
        val centerY = if (scaled) yCenter * inputHeight else yCenter
        val boxWidth = if (scaled) width * inputWidth else width
        val boxHeight = if (scaled) height * inputHeight else height

        val scaleX = originalWidth / inputWidth.toFloat()
        val scaleY = originalHeight / inputHeight.toFloat()

        val left = ((centerX - boxWidth / 2f) * scaleX).coerceIn(0f, originalWidth.toFloat())
        val top = ((centerY - boxHeight / 2f) * scaleY).coerceIn(0f, originalHeight.toFloat())
        val right = ((centerX + boxWidth / 2f) * scaleX).coerceIn(0f, originalWidth.toFloat())
        val bottom = ((centerY + boxHeight / 2f) * scaleY).coerceIn(0f, originalHeight.toFloat())

        return RectF(left, top, max(left, right), max(top, bottom))
    }

    private fun shouldScaleToInput(vararg values: Float): Boolean {
        return values.all { it in 0f..2f }
    }

    private fun labelForIndex(index: Int): ParsedArmorLabel {
        return labels.getOrNull(index)
            ?: ParsedArmorLabel(
                armorClassId = ModelAssetNames.TARGET_ARMOR_CLASS_ID,
                armorId = ModelAssetNames.TARGET_ARMOR_CLASS_ID,
                armorSlug = ModelAssetNames.TARGET_ARMOR_CLASS_ID.toString(),
                displayName = ModelAssetNames.TARGET_ARMOR_CLASS_ID.toString()
            )
    }

    private fun loadLabels(): List<ParsedArmorLabel> {
        return runCatching {
            appContext.assets.open(ModelAssetNames.DETECTOR_LABELS).bufferedReader().useLines { lines ->
                lines
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .map(ArmorLabelParser::parse)
                    .toList()
            }
        }.getOrElse {
            emptyList()
        }
    }

    private fun nonMaxSuppression(
        candidates: List<DetectionCandidate>,
        iouThreshold: Float = 0.45f,
        maxDetections: Int = 10
    ): List<DetectionCandidate> {
        val ordered = candidates.sortedByDescending { it.confidence }.toMutableList()
        val selected = mutableListOf<DetectionCandidate>()

        while (ordered.isNotEmpty() && selected.size < maxDetections) {
            val current = ordered.removeAt(0)
            selected += current
            ordered.removeAll { other -> iou(current.boundingBox, other.boundingBox) >= iouThreshold }
        }

        return selected
    }

    private fun iou(first: RectF, second: RectF): Float {
        val intersectionLeft = max(first.left, second.left)
        val intersectionTop = max(first.top, second.top)
        val intersectionRight = min(first.right, second.right)
        val intersectionBottom = min(first.bottom, second.bottom)

        val intersectionWidth = max(0f, intersectionRight - intersectionLeft)
        val intersectionHeight = max(0f, intersectionBottom - intersectionTop)
        val intersectionArea = intersectionWidth * intersectionHeight

        val firstArea = max(0f, first.width()) * max(0f, first.height())
        val secondArea = max(0f, second.width()) * max(0f, second.height())
        val unionArea = firstArea + secondArea - intersectionArea

        return if (unionArea <= 0f) 0f else intersectionArea / unionArea
    }

    private fun Bitmap.ensureArgb8888(): Bitmap {
        return if (config == Bitmap.Config.ARGB_8888) this else copy(Bitmap.Config.ARGB_8888, false)
    }

    private data class DetectionCandidate(
        val armorClassId: Int,
        val label: String,
        val confidence: Float,
        val boundingBox: RectF
    )
}