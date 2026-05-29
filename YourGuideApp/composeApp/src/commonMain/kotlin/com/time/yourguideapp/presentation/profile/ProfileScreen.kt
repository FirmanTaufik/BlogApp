package com.time.yourguideapp.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.time.yourguideapp.AppColors
import com.time.yourguideapp.core.platform.getAppName
import com.time.yourguideapp.core.platform.getAppVersion
import com.time.yourguideapp.core.platform.getPlatform
import com.time.yourguideapp.core.platform.rememberShareAppLauncher
import com.time.yourguideapp.helper.AppManager
import com.time.yourguideapp.helper.glassmorphism
import com.time.yourguideapp.helper.rootBackground
import com.time.yourguideapp.presentation.component.HorizontalSpacer
import com.time.yourguideapp.presentation.component.VerticalSpacer
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch

class ProfileScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val currentUser by Firebase.auth.authStateChanged.collectAsState(Firebase.auth.currentUser)
        val coroutineScope = rememberCoroutineScope()
        val shareApp = rememberShareAppLauncher()
        val appName = getAppName()
        val appVersion = getAppVersion()
        val platformName = getPlatform().name
        val selectedLanguage = AppManager.currentLanguage
        val selectedLanguageLabel = if (selectedLanguage == "id") "Bahasa Indonesia" else "English"
        val userName = currentUser?.displayName ?: "Traveler"
        val userEmail = currentUser?.email ?: currentUser?.uid.orEmpty()
        val userPhotoUrl = currentUser?.photoURL

        var showLogoutDialog by remember { mutableStateOf(false) }
        var showAppInfoDialog by remember { mutableStateOf(false) }
        var showAboutDialog by remember { mutableStateOf(false) }
        var showPrivacyDialog by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .rootBackground()
                .fillMaxSize()
                .padding(10.dp),
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                        ),
                        title = {
                            Text(
                                text = "Profile",
                                color = AppColors.blue123060,
                                fontWeight = FontWeight.Bold,
                            )
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = { navigator.pop() },
                                modifier = Modifier.glassmorphism(
                                    CircleShape,
                                    backgroundColor = Color.White.copy(alpha = 0.30f),
                                ),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBackIosNew,
                                    contentDescription = null,
                                    tint = AppColors.blue123060,
                                )
                            }
                        },
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                ) {
                    ProfileHeader(
                        userName = userName,
                        userEmail = userEmail,
                        userPhotoUrl = userPhotoUrl,
                        selectedLanguageLabel = selectedLanguageLabel,
                    )
                    VerticalSpacer(16)

                    SectionTitle("App")
                    AppSettingRow(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = AppColors.blue123060,
                            )
                        },
                        title = "App Info",
                        subtitle = "Name, version, and platform",
                        onClick = { showAppInfoDialog = true },
                    )
                    VerticalSpacer(12)
                    AppSettingRow(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = AppColors.blue123060,
                            )
                        },
                        title = "Share App",
                        subtitle = "Share this app with others",
                        onClick = {
                            shareApp(
                                "$appName v$appVersion\n" +
                                    "Travel guides, weather, and exchange rates in one app."
                            )
                        },
                    )
                    VerticalSpacer(12)
                    AppSettingRow(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = AppColors.blue123060,
                            )
                        },
                        title = "About",
                        subtitle = "What this app is about",
                        onClick = { showAboutDialog = true },
                    )
                    VerticalSpacer(12)
                    AppSettingRow(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = AppColors.blue123060,
                            )
                        },
                        title = "Privacy Policy",
                        subtitle = "How your data is handled",
                        onClick = { showPrivacyDialog = true },
                    )

                    VerticalSpacer(18)
                    SectionTitle("Account")
                    AppSettingRow(
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = null,
                                tint = Color(0xFFB3261E),
                            )
                        },
                        title = "Logout",
                        subtitle = "Sign out from this account",
                        onClick = { showLogoutDialog = true },
                    )
                    VerticalSpacer(24)
                }
            }
        }

        if (showAppInfoDialog) {
            AppInfoDialog(
                appName = appName,
                appVersion = appVersion,
                platformName = platformName,
                onDismiss = { showAppInfoDialog = false },
            )
        }

        if (showAboutDialog) {
            AboutDialog(
                onDismiss = { showAboutDialog = false },
            )
        }

        if (showPrivacyDialog) {
            PrivacyPolicyDialog(
                onDismiss = { showPrivacyDialog = false },
            )
        }

        if (showLogoutDialog) {
            LogoutDialog(
                onConfirm = {
                    showLogoutDialog = false
                    coroutineScope.launch { Firebase.auth.signOut() }
                },
                onDismiss = { showLogoutDialog = false },
            )
        }
    }
}

@Composable
private fun ProfileHeader(
    userName: String,
    userEmail: String,
    userPhotoUrl: String?,
    selectedLanguageLabel: String,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .glassmorphism(
                shape = RoundedCornerShape(28.dp),
                backgroundColor = Color.White.copy(alpha = 0.24f),
                borderColor = Color.White.copy(alpha = 0.35f),
            )
            .padding(horizontal = 18.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(112.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.88f)),
            contentAlignment = Alignment.Center,
        ) {
            if (userPhotoUrl.isNullOrBlank()) {
                Icon(
                    imageVector = Icons.Outlined.AccountCircle,
                    contentDescription = null,
                    tint = AppColors.blue123060,
                    modifier = Modifier.size(92.dp),
                )
            } else {
                AsyncImage(
                    model = userPhotoUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(102.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
            }
        }

        VerticalSpacer(14)

        Text(
            text = userName,
            color = AppColors.blue123060,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = userEmail.ifBlank { "No email connected" },
            color = AppColors.blue123060.copy(alpha = 0.76f),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        VerticalSpacer(14)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            ProfileChip(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        tint = AppColors.blue123060,
                        modifier = Modifier.size(18.dp),
                    )
                },
                text = "Language: $selectedLanguageLabel",
                onClick = { },
            )
        }
    }
}

@Composable
private fun ProfileChip(
    icon: @Composable () -> Unit,
    text: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.78f))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon()
        HorizontalSpacer(6)
        Text(
            text = text,
            color = AppColors.blue123060,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = AppColors.blue123060,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 14.dp),
    )
    VerticalSpacer(8)
}

@Composable
private fun AppSettingRow(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
            .glassmorphism(
                shape = RoundedCornerShape(22.dp),
                backgroundColor = Color.White.copy(alpha = 0.72f),
                borderColor = Color.White.copy(alpha = 0.85f),
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(AppColors.blueaad2fb.copy(alpha = 0.42f)),
            contentAlignment = Alignment.Center,
        ) {
            icon()
        }

        HorizontalSpacer(14)

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = AppColors.blue123060,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = subtitle,
                color = AppColors.blue123060.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Spacer(modifier = Modifier.size(8.dp))
        Icon(
            imageVector = Icons.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = AppColors.blue123060.copy(alpha = 0.8f),
        )
    }
}

@Composable
private fun AppInfoDialog(
    appName: String,
    appVersion: String,
    platformName: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        modifier = Modifier,
        containerColor = Color.White,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "App Info",
                color = AppColors.blue123060,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column {
                InfoLine("Name", appName)
                InfoLine("Version", appVersion)
                InfoLine("Platform", platformName)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = AppColors.blue123060)
            }
        },
    )
}

@Composable
private fun AboutDialog(
    onDismiss: () -> Unit,
) {
    AlertDialog(
        modifier = Modifier,
        containerColor = Color.White,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "About",
                color = AppColors.blue123060,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Text(
                text = "YourGuide helps travelers explore guides, check weather, and compare USD exchange rates in one place.",
                color = AppColors.blue123060.copy(alpha = 0.82f),
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = AppColors.blue123060)
            }
        },
    )
}

@Composable
private fun PrivacyPolicyDialog(
    onDismiss: () -> Unit,
) {
    AlertDialog(
        modifier = Modifier,
        containerColor = Color.White,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Privacy Policy",
                color = AppColors.blue123060,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column {
                Text(
                    text = "This policy explains how YourGuide handles information inside the app.",
                    color = AppColors.blue123060.copy(alpha = 0.82f),
                )
                VerticalSpacer(12)
                PolicySection(
                    title = "Information we use",
                    body = "We may use your profile details from sign-in, saved items, language preference, search activity inside the app, and content you choose to view.",
                )
                PolicySection(
                    title = "How we use it",
                    body = "We use this information to sign you in, personalize the interface, show your saved content, and provide features such as weather and exchange-rate screens.",
                )
                PolicySection(
                    title = "Third-party services",
                    body = "YourGuide relies on external services such as Firebase for authentication and data storage, weather and exchange-rate APIs, and advertising services if enabled. These providers may process data according to their own policies.",
                )
                PolicySection(
                    title = "Data retention",
                    body = "We keep app data only as long as needed to provide features tied to your account. You can sign out at any time, and local data can be removed by deleting the app.",
                )
                PolicySection(
                    title = "Your choices",
                    body = "You can change language preference, clear the app from your device, and stop using the app at any time. For questions, contact the developer through the store listing or app support channel.",
                )
                PolicySection(
                    title = "Data sharing",
                    body = "We do not sell personal data.",
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = AppColors.blue123060)
            }
        },
    )
}

@Composable
private fun PolicySection(
    title: String,
    body: String,
) {
    VerticalSpacer(10)
    Text(
        text = title,
        color = AppColors.blue123060,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
    )
    VerticalSpacer(6)
    Text(
        text = body,
        color = AppColors.blue123060.copy(alpha = 0.82f),
    )
}

@Composable
private fun LogoutDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        modifier = Modifier,
        containerColor = Color.White,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Confirmation",
                color = AppColors.blue123060,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Text(
                text = "Are you sure you want to logout?",
                color = AppColors.blue123060.copy(alpha = 0.82f),
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.blue123060),
            ) {
                Text("Yes", color = Color.White)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEAF1FB),
                    contentColor = AppColors.blue123060,
                ),
            ) {
                Text("No", color = AppColors.blue123060)
            }
        },
    )
}

@Composable
private fun InfoLine(
    label: String,
    value: String,
) {
    Column(modifier = Modifier.padding(bottom = 10.dp)) {
        Text(
            text = label,
            color = AppColors.blue123060.copy(alpha = 0.72f),
            style = MaterialTheme.typography.labelMedium,
        )
        Text(
            text = value,
            color = AppColors.blue123060,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun ProfileScreenPreview() {
    MaterialTheme {
        ProfileScreen().Content()
    }
}
