package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.WorldCupData
import com.example.ui.MainViewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.StadiumGreen
import com.example.ui.theme.TrophyGold
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = viewModel()
            val isDarkMode by viewModel.isDarkMode.collectAsState()

            MyApplicationTheme(darkTheme = isDarkMode) {
                MainAppLayout(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MainAppLayout(viewModel: MainViewModel) {
    val activeScreen by viewModel.activeScreen.collectAsState()
    val selectedMatchId by viewModel.selectedMatchId.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    
    val dict = WorldCupData.translations[currentLanguage] ?: WorldCupData.translations["VI"]!!
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isWideScreen = configuration.screenWidthDp >= 600

    // Real-Time Goal Score sliding alert logic
    var goalAlertText by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.goalAlertChannel.collect { message ->
            goalAlertText = message
            delay(5000) // autodismiss after 5s
            goalAlertText = null
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                // Main Header Row with toggles for Lang & Battery saver
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    tonalElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "⚽",
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(end = 6.dp)
                                )
                                Text(
                                    text = "WORLD CUP 2026",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(
                                text = "AI predictions & community forum",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }

                        // System Toggles Panel
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Language Toggle
                            IconButton(
                                onClick = {
                                    val nextLang = if (currentLanguage == "VI") "EN" else "VI"
                                    viewModel.updateLanguage(nextLang)
                                    Toast.makeText(context, if (nextLang == "VI") "Ngôn ngữ: Tiếng Việt" else "Language: English", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.testTag("language_toggle")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = "Language toggle",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            
                            // Dark Mode / Save battery icon switch
                            IconButton(
                                onClick = {
                                    viewModel.updateDarkModeSetting(!isDarkMode)
                                },
                                modifier = Modifier.testTag("dark_mode_toggle")
                            ) {
                                Icon(
                                    imageVector = if (isDarkMode) Icons.Default.BatteryChargingFull else Icons.Default.BatteryFull,
                                    contentDescription = "Battery saving toggle",
                                    tint = if (isDarkMode) StadiumGreen else TrophyGold
                                )
                            }
                        }
                    }
                }
                Divider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f))
            }
        },
        bottomBar = {
            if (!isWideScreen) {
                NavigationBar(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    val navItems = listOf(
                        Triple("home", dict["tab_home"] ?: "", Icons.Default.Dashboard),
                        Triple("matches", dict["tab_matches"] ?: "", Icons.Default.EventNote),
                        Triple("standings", dict["tab_standings"] ?: "", Icons.Default.Leaderboard),
                        Triple("highlights", dict["tab_highlights"] ?: "", Icons.Default.VideoLibrary),
                        Triple("community", dict["tab_community"] ?: "", Icons.Default.Forum)
                    )

                    navItems.forEach { (route, label, icon) ->
                        NavigationBarItem(
                            selected = activeScreen == route && selectedMatchId == null,
                            onClick = { viewModel.navigateTo(route) },
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label, fontSize = 10.sp, maxLines = 1) },
                            alwaysShowLabel = true,
                            modifier = Modifier.testTag("nav_item_$route")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                if (isWideScreen) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxHeight(),
                        header = {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(vertical = 16.dp)
                            ) {
                                Text("⚽", fontSize = 24.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("WC2026", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    ) {
                        val navItems = listOf(
                            Triple("home", dict["tab_home"] ?: "", Icons.Default.Dashboard),
                            Triple("matches", dict["tab_matches"] ?: "", Icons.Default.EventNote),
                            Triple("standings", dict["tab_standings"] ?: "", Icons.Default.Leaderboard),
                            Triple("highlights", dict["tab_highlights"] ?: "", Icons.Default.VideoLibrary),
                            Triple("community", dict["tab_community"] ?: "", Icons.Default.Forum)
                        )

                        navItems.forEach { (route, label, icon) ->
                            NavigationRailItem(
                                selected = activeScreen == route && selectedMatchId == null,
                                onClick = { viewModel.navigateTo(route) },
                                icon = { Icon(icon, contentDescription = label) },
                                label = { Text(label, fontSize = 10.sp, maxLines = 1) },
                                alwaysShowLabel = true,
                                modifier = Modifier.testTag("nav_item_$route")
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    // Main Display Screen Switcher
                    if (selectedMatchId != null) {
                        MatchDetailScreen(
                            viewModel = viewModel,
                            matchId = selectedMatchId!!,
                            lang = currentLanguage,
                            dict = dict
                        )
                    } else {
                        when (activeScreen) {
                            "home" -> HomeScreen(viewModel, currentLanguage, dict)
                            "matches" -> FixturesScreen(viewModel, currentLanguage, dict)
                            "standings" -> StandingsScreen(viewModel, currentLanguage, dict)
                            "highlights" -> HighlightsScreen(viewModel, currentLanguage, dict)
                            "community" -> CommunityScreen(viewModel, currentLanguage, dict)
                        }
                    }
                }
            }

            // High Fidelity sliding goal notifications alert banner
            AnimatedVisibility(
                visible = goalAlertText != null,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    border = BorderStroke(1.5.dp, StadiumGreen),
                    modifier = Modifier.testTag("goal_alert_banner")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(StadiumGreen, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.NotificationsActive,
                                contentDescription = "Active Notifications",
                                tint = Color.Black
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (currentLanguage == "VI") "⚡ THÔNG BÁO Tỉ Số Mới Nhất" else "⚡ Score Alert Update",
                                fontWeight = FontWeight.Bold,
                                color = TrophyGold,
                                fontSize = 11.sp
                            )
                            Text(
                                text = goalAlertText ?: "",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        IconButton(onClick = { goalAlertText = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close notifications", tint = Color.LightGray)
                        }
                    }
                }
            }
        }
    }
}
