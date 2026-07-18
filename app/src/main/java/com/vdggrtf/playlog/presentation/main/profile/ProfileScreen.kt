package com.vdggrtf.playlog.presentation.main.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vdggrtf.playlog.R
import com.vdggrtf.playlog.presentation.components.profile.GamerPassportUi
import com.vdggrtf.playlog.presentation.dialogs.PassportShareDialog
import com.vdggrtf.playlog.ui.theme.Background

// 1. SMART ROUTE: Handles ViewModel, Context, Intents and Navigation
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ProfileRoute(
    onLogoutSuccess: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    // Redirect to login if user logged out
    LaunchedEffect(state.isLoggedOut) {
        if (state.isLoggedOut) {
            viewModel.resetLogoutState()
            onLogoutSuccess()
        }
    }

    // Launch Boosty intent
    val onDonateClick = {
        val donateUrl = "https://boosty.to/playlog_app/donate"
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(donateUrl)))
    }

    ProfileScreen(
        state = state,
        onLogoutClick = { viewModel.logout() },
        onDonateClick = onDonateClick
    )
}


// 2. DUMB SCREEN: Pure UI, completely stateless!
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ProfileScreen(
    state: ProfileState,
    onLogoutClick: () -> Unit,
    onDonateClick: () -> Unit
) {
    // Local UI state for dialog is perfectly fine to keep in the dumb screen
    var showPassportDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.profile),
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )

            // SHARE BUTTON
            IconButton(
                onClick = { showPassportDialog = true },
                modifier = Modifier.background(Color(0xFF1E1E26), CircleShape)
            ) {
                Icon(Icons.Default.Share, contentDescription = stringResource(R.string.share_id), tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // GAMER PASSPORT
        GamerPassportUi(
            nickname = state.name,
            totalGames = state.totalGames,
            completedGames = state.completedGames,
            customChallengesCount = state.customChallengeCount,
            favDifficulty = state.favDifficulty,
            totalBounty = state.totalBounty
        )

        Spacer(modifier = Modifier.height(32.dp))

        // DONATE BUTTON
        Button(
            onClick = onDonateClick, // Passed from Route
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Favorite, contentDescription = stringResource(R.string.support), tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.support_solo_dev),
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SETTINGS MENU
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E1E26), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            ProfileMenuItem(icon = Icons.Default.Edit, title = stringResource(R.string.edit_profile))
            HorizontalDivider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 12.dp))
            ProfileMenuItem(icon = Icons.Default.Settings, title = stringResource(R.string.settings))
            HorizontalDivider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 12.dp))
            ProfileMenuItem(icon = Icons.Default.Info, title = stringResource(R.string.about_app))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // LOGOUT BUTTON
        Button(
            onClick = onLogoutClick, // Passed from Route
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3B30).copy(alpha = 0.1f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = null,
                tint = Color(0xFFFF3B30)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.log_out),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF3B30)
            )
        }

        Spacer(modifier = Modifier.height(100.dp)) // Margin for Bottom Bar
    }

    // DIALOG FOR SHARING
    if (showPassportDialog) {
        PassportShareDialog(
            nickname = state.name,
            totalGames = state.totalGames,
            completedGames = state.completedGames,
            favDifficulty = state.favDifficulty,
            customChallengesCount = state.customChallengeCount,
            totalBounty = state.totalBounty,
            onDismiss = { showPassportDialog = false }
        )
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {  },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.DarkGray
        )
    }
}
