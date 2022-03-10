package pt.isel.leic.pc.imageviewer

import androidx.compose.ui.window.FrameWindowScope
import java.awt.FileDialog
import java.io.File
import java.io.FilenameFilter

/**
 * Function that opens a system file dialog allowing the selection of an image file (png, jpg).
 *
 * @param onImageFilePicked function to be called once the user makes the selection. The function receives
 * the [File] instance representing the selected file, or null if no file has been selected.
 */
fun FrameWindowScope.openImageFilePicker(onImageFilePicked: (File?) -> Unit) {
    with(FileDialog(window, "Select image file", FileDialog.LOAD)) {
        isMultipleMode = false
        filenameFilter = FilenameFilter { _, name -> name.endsWith("png", true) }
        isVisible = true
        onImageFilePicked(if (directory != null && file != null) File(directory, file) else null)
    }
}
