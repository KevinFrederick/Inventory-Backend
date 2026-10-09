package util

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

fun convertToJpgBytes(originalBytes: ByteArray): ByteArray {
    val inputStream = ByteArrayInputStream(originalBytes)
    val originalImage: BufferedImage = ImageIO.read(inputStream)
        ?: throw IllegalStateException("Invalid image")

    val newJpgImage = BufferedImage(
        originalImage.width,
        originalImage.height,
        BufferedImage.TYPE_INT_RGB
    )

    val graphics = newJpgImage.createGraphics()
    graphics.color = Color(240, 240, 240)
    graphics.fillRect(0, 0, newJpgImage.width, newJpgImage.height)

    graphics.drawImage(originalImage, 0, 0, null)
    graphics.dispose()

    val outputStream = ByteArrayOutputStream()
    ImageIO.write(newJpgImage, "jpg", outputStream)

    return outputStream.toByteArray()
}