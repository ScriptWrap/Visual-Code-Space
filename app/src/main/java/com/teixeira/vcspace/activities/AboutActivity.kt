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

package com.teixeira.vcspace.activities

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.teixeira.vcspace.activities.base.BaseComposeActivity
import com.teixeira.vcspace.core.components.common.VCSpaceTopBar
import com.teixeira.vcspace.ui.screens.about.SocialCard
import com.teixeira.vcspace.ui.screens.about.VersionCard

class AboutActivity : BaseComposeActivity() {
  @Composable
  override fun MainScreen() {
    val backPressedDispatcherOwner = LocalOnBackPressedDispatcherOwner.current
    val backPressedDispatcher = backPressedDispatcherOwner?.onBackPressedDispatcher

    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        VCSpaceTopBar(title = "About",
          navigationIcon = {
            IconButton(onClick = {
              backPressedDispatcher?.onBackPressed()
            }) {
              Icon(
                Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = null
              )
            }
          })
      }
    ) { innerPadding ->
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        VersionCard(modifier = Modifier.fillMaxWidth())
        SocialCard(modifier = Modifier.fillMaxWidth())
      }
    }
  }
}