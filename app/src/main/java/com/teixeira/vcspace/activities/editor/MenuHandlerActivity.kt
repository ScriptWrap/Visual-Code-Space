/*
 * This file is part of Visual Code Space.
 *
 * Visual Code Space is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Visual Code Space is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Visual Code Space.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package com.teixeira.vcspace.activities.editor

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.view.isVisible
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.UriUtils
import com.hzy.libp7zip.P7ZipApi
import com.teixeira.vcspace.R
import com.teixeira.vcspace.activities.TerminalActivity
import com.teixeira.vcspace.preferences.pythonExtracted
import com.teixeira.vcspace.resources.R.string
import com.teixeira.vcspace.utils.launchWithProgressDialog
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlinx.coroutines.launch

/**
 * Base class for EditorActivity. Handles the menu options logic.
 *
 * @author Felipe Teixeira, Vivek
 */
abstract class MenuHandlerActivity : EditorHandlerActivity() {

  private val createFile =
    registerForActivityResult(ActivityResultContracts.CreateDocument("text/*")) {
      if (it != null) openFile(UriUtils.uri2File(it))
    }
  private val openFile =
    registerForActivityResult(ActivityResultContracts.OpenDocument()) {
      if (it != null) openFile(UriUtils.uri2File(it))
    }

  @SuppressLint("RestrictedApi")
  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_editor_activity, menu)
    if (menu is MenuBuilder) {
      menu.setOptionalIconsVisible(true)
    }
    return super.onCreateOptionsMenu(menu)
  }

  override fun onPrepareOptionsMenu(menu: Menu): Boolean {
    val editor = getSelectedEditor()
    if (editor != null) {
      menu.findItem(R.id.menu_execute).isVisible = true
      menu.findItem(R.id.menu_undo).isVisible = KeyboardUtils.isSoftInputVisible(this)
      menu.findItem(R.id.menu_redo).isVisible = KeyboardUtils.isSoftInputVisible(this)
      menu.findItem(R.id.menu_undo).isEnabled = editor.canUndo()
      menu.findItem(R.id.menu_redo).isEnabled = editor.canRedo()
      menu.findItem(R.id.menu_search).isVisible = true
      menu.findItem(R.id.menu_save).isEnabled = editor.modified
      menu.findItem(R.id.menu_save_as).isEnabled = true
      menu.findItem(R.id.menu_save_all).isEnabled = areModifiedFiles()
      menu.findItem(R.id.menu_reload).isEnabled = true
    }
    return super.onPrepareOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val editor = getSelectedEditor()
    when (item.itemId) {
      R.id.menu_execute -> {
        saveAllFilesAsync(false) {
          if (editor?.file?.name?.endsWith(".py") ?: false) {
            extractPythonFile {
              startActivity(
                Intent(this, TerminalActivity::class.java)
                  .putExtra(TerminalActivity.KEY_PYTHON_FILE_PATH, editor?.file?.absolutePath)
              )
            }
          }
        }
      }
      R.id.menu_search -> editor?.beginSearchMode()
      R.id.menu_undo -> editor?.undo()
      R.id.menu_redo -> editor?.redo()
      R.id.menu_new_file -> createFile.launch("filename.txt")
      R.id.menu_open_file -> openFile.launch(arrayOf("text/*"))
      R.id.menu_save -> saveFileAsync(true, editorViewModel.selectedFileIndex)
      R.id.menu_save_all -> saveAllFilesAsync(true)
      R.id.menu_reload -> editor?.confirmReload()
    }
    return true
  }

  private fun extractPythonFile(whenExtractingDone: Runnable) {
    if (pythonExtracted) {
      whenExtractingDone.run()
    } else {
      coroutineScope.launchWithProgressDialog(
        uiContext = this,
        configureBuilder = { builder ->
          builder.setMessage(string.python_extracting_python_compiler)
          builder.setCancelable(false)
        },
        invokeOnCompletion = { throwable ->
          if (throwable == null) {
            pythonExtracted = true
            whenExtractingDone.run()
          }
        },
        action = { _ ->
          val temp7zStream = assets.open("python/python.7z")
          val file = File("${filesDir.absolutePath}/python.7z")
          file.createNewFile()
          Files.copy(temp7zStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING)
          val exitCode =
            P7ZipApi.executeCommand("7z x ${file.absolutePath} -o${filesDir.absolutePath}")
          Log.d("EditorActivity", "extractFiles: $exitCode")
          file.delete()
          temp7zStream.close()
        },
      )
    }
  }
}