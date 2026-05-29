package com.time.yourguideapp.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import com.time.yourguideapp.helper.AppManager
import com.time.yourguideapp.helper.glassmorphism
import com.time.yourguideapp.helper.rootBackground
import com.time.yourguideapp.presentation.component.HorizontalSpacer
import com.time.yourguideapp.presentation.component.VerticalSpacer
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth


class ProfileScreen(

) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val currentUser by Firebase.auth.authStateChanged.collectAsState(Firebase.auth.currentUser)
        var showLanguageDialog by remember { mutableStateOf(false) }
        val selectedLanguage = AppManager.currentLanguage
        val selectedLanguageLabel = if (selectedLanguage == "id") "Bahasa Indonesia" else "English"
        val userName = currentUser?.displayName ?: "Traveler"
        val userEmail = currentUser?.email ?: currentUser?.uid.orEmpty()
        val userPhotoUrl = currentUser?.photoURL

        Box(
            modifier = Modifier
                .rootBackground()
                .fillMaxSize()
                .padding(10.dp),
        ) {
            Scaffold(
                modifier = Modifier,
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
                                onClick = {
                                    navigator.pop()
                                },
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
                        onChangeLanguage = {
                            showLanguageDialog = true
                        },
                    )
                    VerticalSpacer(16)
                    SectionTitle("Pengaturan")
                    LanguageSettingRow(
                        selectedLanguageLabel = selectedLanguageLabel,
                        onClick = {
                            showLanguageDialog = true
                        },
                    )
                    VerticalSpacer(24)
                }
            }
        }

        if (showLanguageDialog) {
            LanguageDialog(
                selectedLanguage = selectedLanguage,
                onSelectLanguage = { language ->
                    AppManager.currentLanguage = language
                    showLanguageDialog = false
                },
                onDismiss = {
                    showLanguageDialog = false
                },
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
    onChangeLanguage: () -> Unit,
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
                text = "Ubah Bahasa: $selectedLanguageLabel",
                onClick = onChangeLanguage,
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
private fun LanguageSettingRow(
    selectedLanguageLabel: String,
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
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = null,
                tint = AppColors.blue123060,
            )
        }

        HorizontalSpacer(14)

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Ubah Bahasa",
                color = AppColors.blue123060,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Saat ini: $selectedLanguageLabel",
                color = AppColors.blue123060.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Spacer(modifier = Modifier.size(8.dp))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = AppColors.blue123060.copy(alpha = 0.8f),
        )
    }
}

@Composable
private fun LanguageDialog(
    selectedLanguage: String,
    onSelectLanguage: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        modifier = Modifier.glassmorphism(),
        containerColor = Color.Transparent,
        onDismissRequest = onDismiss,
        title = {
            Text("Choose language", color = AppColors.white)
        },
        text = {
            Column {
                LanguageOption(
                    code = "id",
                    flag = "🇮🇩",
                    label = "Bahasa Indonesia",
                    selectedLanguage = selectedLanguage,
                    onSelectLanguage = onSelectLanguage,
                )
                LanguageOption(
                    code = "en",
                    flag = "🇬🇧",
                    label = "English",
                    selectedLanguage = selectedLanguage,
                    onSelectLanguage = onSelectLanguage,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = AppColors.white)
            }
        },
    )
}

@Composable
private fun LanguageOption(
    code: String,
    flag: String,
    label: String,
    selectedLanguage: String,
    onSelectLanguage: (String) -> Unit,
) {
    val selected = code == selectedLanguage

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onSelectLanguage(code)
            }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = {
                onSelectLanguage(code)
            },
            colors = RadioButtonDefaults.colors(
                selectedColor = AppColors.white,
                unselectedColor = AppColors.white.copy(alpha = 0.7f),
            ),
        )
        HorizontalSpacer(6)
        Text(
            text = flag,
            style = MaterialTheme.typography.titleMedium,
        )
        HorizontalSpacer(10)
        Text(
            text = label,
            color = AppColors.white,
            modifier = Modifier.weight(1f),
        )
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = AppColors.white,
            )
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun ProfileScreenPreview(){
    MaterialTheme {
        ProfileScreen().Content()
    }
}
