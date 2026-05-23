package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.*
import com.example.ui.MainViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

// Custom high-performance pixel-perfect Vector flags drawn on Android Canvas
@Composable
fun CountryFlag(
    code: String,
    modifier: Modifier = Modifier
) {
    val team = WorldCupData.teamMap[code]
    if (team == null) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color.Gray)
        )
        return
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Base color
            drawRect(color = team.flagColorPrimary)

            // Pattern stripes
            when (team.flagStyle) {
                "STRIPES_H" -> {
                    drawRect(
                        color = team.flagColorSecondary,
                        topLeft = androidx.compose.ui.geometry.Offset(0f, h * 0.35f),
                        size = androidx.compose.ui.geometry.Size(w, h * 0.3f)
                    )
                }
                "STRIPES_V" -> {
                    // Draw vertical center or side stripe
                    drawRect(
                        color = team.flagColorSecondary,
                        topLeft = androidx.compose.ui.geometry.Offset(w * 0.33f, 0f),
                        size = androidx.compose.ui.geometry.Size(w * 0.34f, h)
                    )
                }
                "TRIANGLE" -> {
                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(0f, 0f)
                        lineTo(w * 0.45f, h / 2)
                        lineTo(0f, h)
                        close()
                    }
                    drawPath(path = path, color = team.flagColorSecondary)
                }
                "CROSS" -> {
                    drawRect(
                        color = team.flagColorSecondary,
                        topLeft = androidx.compose.ui.geometry.Offset(w * 0.4f, 0f),
                        size = androidx.compose.ui.geometry.Size(w * 0.2f, h)
                    )
                    drawRect(
                        color = team.flagColorSecondary,
                        topLeft = androidx.compose.ui.geometry.Offset(0f, h * 0.4f),
                        size = androidx.compose.ui.geometry.Size(w, h * 0.2f)
                    )
                }
                "SOLID" -> {
                    if (code == "JPN") {
                        // Japan sun dot
                        drawCircle(
                            color = Color(0xFFDC2626),
                            radius = h * 0.25f,
                            center = androidx.compose.ui.geometry.Offset(w / 2, h / 2)
                        )
                    } else if (code == "MAR") {
                        // Moroccan Green star representation (nested square rotation)
                        drawCircle(
                            color = team.flagColorSecondary,
                            radius = h * 0.15f,
                            center = androidx.compose.ui.geometry.Offset(w / 2, h / 2)
                        )
                    }
                }
            }
        }
    }
}

// Global visual pulse dot representing matches active right now
@Composable
fun LivePulseDot() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(Color.Red.copy(alpha = alpha))
    )
}

// ---------------- HOME SCREEN ----------------
@Composable
fun WelcomeBannerCard(
    dict: Map<String, String>,
    points: Int,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "🏆 World Cup 2026 Pro",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = dict["app_subtitle"] ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.padding(2.dp)
                ) {
                    Text(
                        text = "${dict["reward_points"]}$points Pts",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Simulation trigger
                Button(
                    onClick = { viewModel.triggerSimulatedGoal() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                    modifier = Modifier.testTag("sim_goal_button")
                ) {
                    Text(
                        text = dict["goals_alert_sim"] ?: "",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteTeamSummaryCard(
    favTeam: Team,
    lang: String,
    dict: Map<String, String>,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CountryFlag(favTeam.code, modifier = Modifier.size(54.dp, 36.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dict["fav_team"] ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Text(
                    text = if (lang == "VI") favTeam.nameVI else favTeam.nameEN,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "FIFA Rank: #${favTeam.rank} | Star: ${favTeam.popularPlayer}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            IconButton(
                onClick = { viewModel.navigateTo("community") }
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Edit favorite")
            }
        }
    }
}

@Composable
fun TransferNewsCard(
    news: TransferNews,
    lang: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = if (lang == "VI") news.categoryVI else news.categoryEN,
                    color = Color(0xFFF59E0B),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${news.source} • ${news.timeLabel}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (lang == "VI") news.titleVI else news.titleEN,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (lang == "VI") news.contentVI else news.contentEN,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    lang: String,
    dict: Map<String, String>
) {
    val matches by viewModel.matchesList.collectAsState()
    val points by viewModel.userPoints.collectAsState()
    val favTeamCode by viewModel.favoriteTeamCode.collectAsState()

    val liveMatches = matches.filter { it.status == "LIVE" }
    val upcomingMatches = matches.filter { it.status == "UPCOMING" }.take(3)

    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isWideScreen = configuration.screenWidthDp >= 600

    if (isWideScreen) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left Column: Welcome Banner, Live Matches, Key Upcoming matches
            Column(
                modifier = Modifier
                    .weight(1.2f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                WelcomeBannerCard(dict, points, viewModel)

                if (liveMatches.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        LivePulseDot()
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (lang == "VI") "ĐANG DIỄN RA TRỰC TIẾP" else "LIVE MATCHES ONGOING",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Red
                        )
                    }

                    liveMatches.forEach { m ->
                        MatchCard(
                            match = m,
                            lang = lang,
                            onClick = { viewModel.selectMatchDetails(m.id) }
                        )
                    }
                }

                Text(
                    text = if (lang == "VI") "Trận Đấu Sắp Diễn Ra" else "Upcoming Crucial Fixtures",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )

                upcomingMatches.forEach { m ->
                    MatchCard(
                        match = m,
                        lang = lang,
                        onClick = { viewModel.selectMatchDetails(m.id) }
                    )
                }
            }

            // Right Column: Favourite Team summary and Transfers News
            Column(
                modifier = Modifier
                    .weight(1.0f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val favTeam = WorldCupData.teamMap[favTeamCode]
                if (favTeam != null) {
                    FavoriteTeamSummaryCard(favTeam, lang, dict, viewModel)
                }

                Text(
                    text = dict["transfer_news"] ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )

                WorldCupData.transfers.forEach { news ->
                    TransferNewsCard(news, lang)
                }
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                WelcomeBannerCard(dict, points, viewModel)
            }

            if (liveMatches.isNotEmpty()) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        LivePulseDot()
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (lang == "VI") "ĐANG DIỄN RA TRỰC TIẾP" else "LIVE MATCHES ONGOING",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Red
                        )
                    }
                }

                items(liveMatches) { m ->
                    MatchCard(
                        match = m,
                        lang = lang,
                        onClick = { viewModel.selectMatchDetails(m.id) }
                    )
                }
            }

            item {
                val favTeam = WorldCupData.teamMap[favTeamCode]
                if (favTeam != null) {
                    FavoriteTeamSummaryCard(favTeam, lang, dict, viewModel)
                }
            }

            item {
                Text(
                    text = if (lang == "VI") "Trận Đấu Sắp Diễn Ra" else "Upcoming Crucial Fixtures",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(upcomingMatches) { m ->
                MatchCard(
                    match = m,
                    lang = lang,
                    onClick = { viewModel.selectMatchDetails(m.id) }
                )
            }

            item {
                Text(
                    text = dict["transfer_news"] ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(WorldCupData.transfers) { news ->
                TransferNewsCard(news, lang)
            }
        }
    }
}

// Standard Match Card reusable layout
@Composable
fun MatchCard(
    match: Match,
    lang: String,
    onClick: () -> Unit
) {
    val teamA = WorldCupData.teamMap[match.teamACode]
    val teamB = WorldCupData.teamMap[match.teamBCode]
    if (teamA == null || teamB == null) return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("match_card_${match.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (match.status == "LIVE") {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Group and Time Info Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ElevatedCard(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "${if (lang == "VI") "Bảng" else "Group"} ${match.group}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    if (match.status == "LIVE") {
                        Spacer(modifier = Modifier.width(8.dp))
                        ServicePulseIndicator()
                    }
                }

                // Match schedule/time state
                when (match.status) {
                    "LIVE" -> {
                        Text(
                            text = "LIVE ${match.minute}'",
                            color = Color.Red,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    "FINISHED" -> {
                        Text(
                            text = if (lang == "VI") "HẾT GIỜ" else "FT",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    else -> {
                        Text(
                            text = match.kickoffTime,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Score & Team Match rows
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Team A flag + name
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CountryFlag(match.teamACode, modifier = Modifier.size(45.dp, 30.dp))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (lang == "VI") teamA.nameVI else teamA.nameEN,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Score banner
                Column(
                    modifier = Modifier.width(100.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (match.status == "UPCOMING") {
                        Text(
                            text = "VS",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = match.scoreA.toString(),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (match.status == "LIVE") Color.Red else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = " - ",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                            Text(
                                text = match.scoreB.toString(),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (match.status == "LIVE") Color.Red else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Team B flag + name
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CountryFlag(match.teamBCode, modifier = Modifier.size(45.dp, 30.dp))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (lang == "VI") teamB.nameVI else teamB.nameEN,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Scorers display
            if (match.status != "UPCOMING" && (match.scorersA.isNotEmpty() || match.scorersB.isNotEmpty())) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    // Scorers Team A
                    Text(
                        text = match.scorersA.joinToString(", "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontSize = 10.sp,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Start
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    // Scorers Team B
                    Text(
                        text = match.scorersB.joinToString(", "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontSize = 10.sp,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

@Composable
fun ServicePulseIndicator() {
    val transition = rememberInfiniteTransition(label = "pulse_live")
    val alpha by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(800, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "pulse_alpha"
    )
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = Color.Red.copy(alpha = alpha)
    ) {
        Text(
            text = "LIVE",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
        )
    }
}

// ---------------- FIXTURES CALENDAR MATCHES SCREEN ----------------
@Composable
fun FixturesScreen(
    viewModel: MainViewModel,
    lang: String,
    dict: Map<String, String>
) {
    val matches by viewModel.matchesList.collectAsState()
    var selectedGroupTab by remember { mutableStateOf("ALL") }

    val distinctGroups = listOf("ALL", "A", "B", "C", "D", "E", "F", "G")

    Column(modifier = Modifier.fillMaxSize()) {
        // Group horizontal selection tabs
        ScrollableTabRow(
            selectedTabIndex = distinctGroups.indexOf(selectedGroupTab),
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[distinctGroups.indexOf(selectedGroupTab)]),
                    color = MaterialTheme.colorScheme.primary
                )
            },
            divider = {}
        ) {
            distinctGroups.forEach { gp ->
                Tab(
                    selected = selectedGroupTab == gp,
                    onClick = { selectedGroupTab = gp },
                    text = {
                        Text(
                            text = if (gp == "ALL") (if (lang == "VI") "Tất cả" else "All") else "Group $gp",
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            }
        }

        val configuration = androidx.compose.ui.platform.LocalConfiguration.current
        val isWideScreen = configuration.screenWidthDp >= 600

        val filteredMatches = if (selectedGroupTab == "ALL") {
            matches
        } else {
            matches.filter { it.group == selectedGroupTab }
        }

        val chunkedMatches = filteredMatches.chunked(if (isWideScreen) 2 else 1)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (filteredMatches.isEmpty()) {
                item {
                    Text(
                        text = if (lang == "VI") "Không tìm thấy lỗi dữ liệu / Trận đấu trống." else "No fixtures found for selection.",
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            items(chunkedMatches) { rowMatches ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    rowMatches.forEach { m ->
                        Box(modifier = Modifier.weight(1f)) {
                            MatchCard(
                                match = m,
                                lang = lang,
                                onClick = { viewModel.selectMatchDetails(m.id) }
                            )
                        }
                    }
                    if (isWideScreen && rowMatches.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

// ---------------- MATCH DETAIL: STATS, MVP VOTING, AI, LIVE-CHAT ----------------
@Composable
fun MatchDetailScreen(
    viewModel: MainViewModel,
    matchId: String,
    lang: String,
    dict: Map<String, String>
) {
    val matches by viewModel.matchesList.collectAsState()
    val match = matches.find { it.id == matchId }
    val aiResponse by viewModel.aiPredictionText.collectAsState()
    val aiSearchQueries by viewModel.aiSearchQueries.collectAsState()
    val aiSearchSources by viewModel.aiSearchSources.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    val chatMessages by viewModel.activeChatMessages.collectAsState()
    val userVotedMatches by viewModel.userVotedMatches.collectAsState()
    val mvpVotesMap by viewModel.mvpVotesMap.collectAsState()

    var chatText by remember { mutableStateOf("") }
    var detailTab by remember { mutableStateOf("STATS") } // "STATS", "AI", "LINEUPS", "CHAT"

    if (match == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Không tìm thấy trận đấu / Match not found.")
        }
        return
    }

    val teamA = WorldCupData.teamMap[match.teamACode]
    val teamB = WorldCupData.teamMap[match.teamBCode]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
    ) {
        // Detailed scoreboard header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Back button
                Row(modifier = Modifier.fillMaxWidth()) {
                    IconButton(onClick = { viewModel.selectMatchDetails(null) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Go back")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        CountryFlag(match.teamACode, modifier = Modifier.size(60.dp, 40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (lang == "VI") teamA?.nameVI ?: "" else teamA?.nameEN ?: "",
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(120.dp)) {
                        if (match.status == "UPCOMING") {
                            Text(
                                "VS",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Text(
                                "${match.scoreA} - ${match.scoreB}",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                if (match.status == "LIVE") "LIVE ${match.minute}'" else (if (lang == "VI") "KẾT THÚC" else "FINISHED"),
                                color = if (match.status == "LIVE") Color.Red else Color.Gray,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        CountryFlag(match.teamBCode, modifier = Modifier.size(60.dp, 40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (lang == "VI") teamB?.nameVI ?: "" else teamB?.nameEN ?: "",
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Subtabs selection
        val tabs = listOf(
            "STATS" to (if (lang == "VI") "Thống Kê" else "Stats"),
            "AI" to "AI Prediction",
            "LINEUPS" to (if (lang == "VI") "Đội Hình" else "Lineups"),
            "CHAT" to (if (lang == "VI") "Thảo Luận" else "Discussion")
        )

        ScrollableTabRow(
            selectedTabIndex = tabs.indexOfFirst { it.first == detailTab },
            modifier = Modifier.fillMaxWidth(),
            divider = {}
        ) {
            tabs.forEach { tab ->
                Tab(
                    selected = detailTab == tab.first,
                    onClick = { detailTab = tab.first },
                    text = { Text(tab.second, fontWeight = FontWeight.Bold) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Render Active detail sub-tab
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            when (detailTab) {
                "STATS" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        val activeStats = match.stats ?: WorldCupData.defaultStats
                        
                        Text(
                            text = dict["player_spec"] ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        SegmentedStatsRow(dict["possession"] ?: "", activeStats.possessionA, activeStats.possessionB, isPercentage = true)
                        SegmentedStatsRow(dict["shots"] ?: "", activeStats.shotsA, activeStats.shotsB)
                        SegmentedStatsRow(dict["shots_target"] ?: "", activeStats.shotsOnTargetA, activeStats.shotsOnTargetB)
                        SegmentedStatsRow(dict["passes"] ?: "", activeStats.passesA, activeStats.passesB)
                        SegmentedStatsRow(dict["passes_success"] ?: "", activeStats.passesSuccessRateA, activeStats.passesSuccessRateB, isPercentage = true)
                        SegmentedStatsRow(dict["fouls"] ?: "", activeStats.foulsA, activeStats.foulsB)
                        SegmentedStatsRow(dict["cards"] ?: "", activeStats.yellowCardsA, activeStats.yellowCardsB)

                        Spacer(modifier = Modifier.height(12.dp))

                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = if (lang == "VI") "Lịch sử đối đầu & Phong độ" else "Head-To-Head & Recent form",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = match.h2hHistory,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                "AI" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {
                        Text(
                            text = dict["heading_ai_predict"] ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        if (isAiLoading) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = dict["loading_ai"] ?: "",
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        } else {
                            if (aiResponse.isBlank()) {
                                Button(
                                    onClick = { viewModel.loadAiPrediction(match.id) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("ai_predict_button")
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = "AI Analyze")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(dict["btn_ai_predict"] ?: "")
                                }
                            } else {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                                    border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.3f))
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(
                                                Icons.Default.AutoAwesome,
                                                contentDescription = "AI Icon",
                                                tint = Color(0xFFFBBF24)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                "WC-Predictor-AI 🤖",
                                                color = Color(0xFFFBBF24),
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.weight(1f))
                                            if (aiSearchSources.isNotEmpty()) {
                                                Box(
                                                    modifier = Modifier
                                                        .background(Color(0xFF10B981).copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                                ) {
                                                    Text(
                                                        text = if (lang == "VI") "Kế nối Live" else "Google Grounded",
                                                        color = Color(0xFF34D399),
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }

                                        if (aiSearchQueries.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Text(
                                                text = if (lang == "VI") "🔍 Từ khóa đã tìm kiếm thực tế:" else "🔍 Google Search Queries Used:",
                                                color = Color.LightGray,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .horizontalScroll(rememberScrollState()),
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                aiSearchQueries.forEach { query ->
                                                    Box(
                                                        modifier = Modifier
                                                            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                                    ) {
                                                        Text(
                                                            text = query,
                                                            color = Color.White,
                                                            style = MaterialTheme.typography.bodySmall,
                                                            fontSize = 11.sp
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(14.dp))
                                        Text(
                                            text = aiResponse,
                                            color = Color(0xFFE2E8F0),
                                            style = MaterialTheme.typography.bodyMedium,
                                            lineHeight = 22.sp
                                        )

                                        if (aiSearchSources.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(16.dp))
                                            HorizontalDivider(color = Color.White.copy(alpha = 0.12f))
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Text(
                                                text = if (lang == "VI") "📡 Nguồn dữ liệu thực (Lưu SQLite/Room DB):" else "📡 Verified Sources (Cached in SQLite/Room DB):",
                                                color = Color(0xFFFBBF24),
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            
                                            val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
                                            aiSearchSources.forEach { source ->
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            try {
                                                                uriHandler.openUri(source.second)
                                                            } catch (e: Exception) {
                                                            }
                                                        }
                                                        .padding(vertical = 4.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        Icons.Default.CheckCircle,
                                                        contentDescription = "Source Checked",
                                                        tint = Color(0xFF34D399),
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = source.first,
                                                        color = Color(0xFF60A5FA),
                                                        style = MaterialTheme.typography.bodySmall,
                                                        maxLines = 1,
                                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                                        textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Button(
                                    onClick = { viewModel.loadAiPrediction(match.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(if (lang == "VI") "🔄 Cập nhật qua Google Search (Live)" else "🔄 Update via Google Search (Live)")
                                }
                            }
                        }
                    }
                }

                "LINEUPS" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            text = dict["mvp_voting"] ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        val hasVoted = userVotedMatches.contains(match.id)

                        // If user voted, show current ballot statistics
                        if (hasVoted) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Success", tint = StadiumGreen)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        dict["vote_cast"] ?: "Vote registered! +20 Points.",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }

                        // Team Lineups listings & clickable votes
                        Text(
                            text = if (lang == "VI") "Đội hình ${teamA?.nameVI}" else "Starting Lineup ${teamA?.nameEN}",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        match.lineupA.forEach { player ->
                            LineupPlayerRow(
                                player = player,
                                hasVoted = hasVoted,
                                votes = mvpVotesMap[match.id]?.get(player.name) ?: Random.nextInt(3, 15),
                                onVote = { viewModel.castMvpVote(match.id, player.name) }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (lang == "VI") "Đội hình ${teamB?.nameVI}" else "Starting Lineup ${teamB?.nameEN}",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )

                        match.lineupB.forEach { player ->
                            LineupPlayerRow(
                                player = player,
                                hasVoted = hasVoted,
                                votes = mvpVotesMap[match.id]?.get(player.name) ?: Random.nextInt(2, 12),
                                onVote = { viewModel.castMvpVote(match.id, player.name) }
                            )
                        }
                    }
                }

                "CHAT" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = if (lang == "VI") "Đối thoại hâm mộ cùng trận đấu" else "Fanzone live match chat",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        // Render active comments lists
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 240.dp)
                                .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                        ) {
                            if (chatMessages.isEmpty()) {
                                Text(
                                    text = if (lang == "VI") "Chưa có cuộc thảo luận nào. Hãy bắt đầu ngay!" else "Chat list empty. Speak first!",
                                    modifier = Modifier.padding(20.dp),
                                    color = Color.Gray
                                )
                            } else {
                                LazyColumn(
                                    modifier = Modifier.padding(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    items(chatMessages) { chat ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(4.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .clip(CircleShape)
                                                    .background(if (chat.avatarIndex == 0) Color(0xFF6366F1) else Color(0xFF10B981))
                                            ) {
                                                Text(
                                                    text = chat.userNickname.take(1).uppercase(),
                                                    color = Color.White,
                                                    modifier = Modifier.align(Alignment.Center),
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(chat.userNickname, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                                Text(chat.message, fontSize = 13.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Send block
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = chatText,
                                onValueChange = { chatText = it },
                                placeholder = { Text(dict["chat_placeholder"] ?: "") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("chat_input"),
                                shape = RoundedCornerShape(24.dp),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    viewModel.sendChatMessage(chatText)
                                    chatText = ""
                                },
                                modifier = Modifier.background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LineupPlayerRow(
    player: LineupPlayer,
    hasVoted: Boolean,
    votes: Int,
    onVote: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(
                            player.number.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(player.name, fontWeight = FontWeight.Bold)
                    Text("Pos: ${player.position} | Rating: ${player.rating} ★", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }

            if (!hasVoted) {
                Button(
                    onClick = onVote,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                ) {
                    Text("Vote MVP", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Text(
                    text = "🗳️ $votes votes",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun SegmentedStatsRow(
    label: String,
    valA: Int,
    valB: Int,
    isPercentage: Boolean = false
) {
    val total = if (valA + valB == 0) 1 else valA + valB
    val percentA = valA.toFloat() / total

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = if (isPercentage) "$valA%" else valA.toString(),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = if (isPercentage) "$valB%" else valB.toString(),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(percentA.coerceAtLeast(0.01f))
                        .background(StadiumGreen)
                )
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight((1f - percentA).coerceAtLeast(0.01f))
                        .background(TrophyGold)
                )
            }
        }
    }
}


// ---------------- STANDINGS, PLAYER METRICS, POINTS LEADERBOARD ----------------
@Composable
fun StandingsScreen(
    viewModel: MainViewModel,
    lang: String,
    dict: Map<String, String>
) {
    val groupStandings by viewModel.standings.collectAsState()
    val pointLeaderboard by viewModel.pointsLeaderboard.collectAsState()
    var selectedTab by remember { mutableStateOf("STANDINGS") } // "STANDINGS", "PLAYERS", "REWARDS"

    val sortedGroups = groupStandings.keys.sorted()
    val isWideScreen = androidx.compose.ui.platform.LocalConfiguration.current.screenWidthDp >= 600

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = when (selectedTab) {
                "STANDINGS" -> 0
                "PLAYERS" -> 1
                else -> 2
            },
            divider = {}
        ) {
            Tab(
                selected = selectedTab == "STANDINGS",
                onClick = { selectedTab = "STANDINGS" },
                text = { Text(if (lang == "VI") "Nhóm Bảng" else "Group tables", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == "PLAYERS",
                onClick = { selectedTab = "PLAYERS" },
                text = { Text(if (lang == "VI") "Cầu Thủ" else "Player stats", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == "REWARDS",
                onClick = { selectedTab = "REWARDS" },
                text = { Text(if (lang == "VI") "Cúp Thưởng" else "Fan Points", fontWeight = FontWeight.Bold) }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (selectedTab) {
                "STANDINGS" -> {
                    val chunkedGroups = sortedGroups.chunked(if (isWideScreen) 2 else 1)
                    chunkedGroups.forEach { groupRow ->
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                groupRow.forEach { grpName ->
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            "Group $grpName",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(vertical = 4.dp),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                        ) {
                                            Column(modifier = Modifier.padding(8.dp)) {
                                                // Row Headers
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text("#", widthWeight = 0.08f, header = true)
                                                    Text(dict["team"] ?: "Team", widthWeight = 0.37f, header = true)
                                                    Text("P", widthWeight = 0.11f, header = true)
                                                    Text("W", widthWeight = 0.11f, header = true)
                                                    Text("GD", widthWeight = 0.18f, header = true)
                                                    Text(dict["pts"] ?: "Pts", widthWeight = 0.15f, header = true)
                                                }
                                                
                                                Divider(modifier = Modifier.padding(vertical = 6.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                                                val listStandings = groupStandings[grpName] ?: emptyList()
                                                listStandings.forEachIndexed { idx, st ->
                                                    val team = WorldCupData.teamMap[st.teamCode]
                                                    if (team != null) {
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(vertical = 4.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            // Rank and flag
                                                            Text(text = (idx + 1).toString(), widthWeight = 0.08f)
                                                            
                                                            Row(
                                                                modifier = Modifier.fillMaxWidth(0.37f / 0.92f), // adapt weights
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                CountryFlag(st.teamCode, modifier = Modifier.size(24.dp, 16.dp))
                                                                Spacer(modifier = Modifier.width(6.dp))
                                                                Text(
                                                                    text = if (lang == "VI") team.nameVI else team.nameEN,
                                                                    fontWeight = FontWeight.SemiBold,
                                                                    fontSize = 11.sp,
                                                                    maxLines = 1,
                                                                    overflow = TextOverflow.Ellipsis
                                                                )
                                                            }
                                                            
                                                            Spacer(modifier = Modifier.width(4.dp))
                                                            Text(text = st.played.toString(), widthWeight = 0.11f)
                                                            Text(text = st.won.toString(), widthWeight = 0.11f)
                                                            
                                                            val gd = st.gs - st.gc
                                                            Text(text = (if (gd > 0) "+$gd" else gd.toString()), widthWeight = 0.18f)
                                                            Text(text = st.points.toString(), widthWeight = 0.15f, highlight = true)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                if (isWideScreen && groupRow.size < 2) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                "PLAYERS" -> {
                    item {
                        Text(
                            text = dict["player_spec"] ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    val chunkedPlayers = WorldCupData.players.chunked(if (isWideScreen) 2 else 1)
                    items(chunkedPlayers) { rowPlayers ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            rowPlayers.forEach { p ->
                                Box(modifier = Modifier.weight(1f)) {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                shape = CircleShape,
                                                color = p.imagePlaceholder,
                                                modifier = Modifier.size(44.dp)
                                            ) {
                                                Box(modifier = Modifier.fillMaxSize()) {
                                                    Text(
                                                        text = p.name.take(1),
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.align(Alignment.Center)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.width(16.dp))

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(p.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                                Text(
                                                    "Pos: ${if (lang == "VI") p.positionVI else p.positionEN} | Nat: ${if (lang == "VI") p.nationalityVI else p.nationalityEN}",
                                                    style = MaterialTheme.typography.bodySmall, color = Color.Gray
                                                )
                                                Text(
                                                    "Club: ${p.club} | Value: ${p.marketValue}",
                                                    style = MaterialTheme.typography.bodySmall, color = Color.Gray
                                                )
                                            }

                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text("⭐ ${p.rating}", fontWeight = FontWeight.Bold, color = TrophyGold)
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    "⚽ ${p.goals} G | 🅰️ ${p.assists} A",
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            if (isWideScreen && rowPlayers.size < 2) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                "REWARDS" -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "✨ ${dict["points_lead"]}",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    if (lang == "VI") "Tích lũy điểm thưởng bằng cách bình chọn MVP (+20), dự đoán AI (+50) và tham gia thảo luận cộng đồng (+10)."
                                    else "Collect points by voting MVP (+20), fetching AI predictive stats (+50) and typing in community chats (+10).",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    val chunkedLeaderboard = pointLeaderboard.chunked(if (isWideScreen) 2 else 1)
                    items(chunkedLeaderboard) { rowLeaders ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            rowLeaders.forEach { leader ->
                                Box(modifier = Modifier.weight(1f)) {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(36.dp)
                                                        .clip(CircleShape)
                                                        .background(MaterialTheme.colorScheme.secondaryContainer),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = leader.userNickname.take(1).uppercase(),
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Column {
                                                    Text(leader.userNickname, fontWeight = FontWeight.Bold)
                                                    Text(leader.badgeName, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                                }
                                            }

                                            Text(
                                                text = "🎁 ${leader.points} Pts",
                                                fontWeight = FontWeight.Black,
                                                color = StadiumGreen
                                            )
                                        }
                                    }
                                }
                            }
                            if (isWideScreen && rowLeaders.size < 2) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

// Internal text table utilities with weights
@Composable
fun RowScope.Text(
    text: String,
    widthWeight: Float,
    header: Boolean = false,
    highlight: Boolean = false
) {
    Text(
        text = text,
        modifier = Modifier.weight(widthWeight),
        fontWeight = if (header || highlight) FontWeight.Bold else FontWeight.Normal,
        color = if (header) {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        } else if (highlight) {
            StadiumGreen
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        fontSize = if (header) 11.sp else 13.sp,
        textAlign = TextAlign.Center
    )
}

// ---------------- VIDEO VIDEO RECAP HIGHLIGHTS & VTV GO WEB STREAM ----------------
@Composable
fun HighlightCard(
    clip: com.example.model.HighlightVideo,
    lang: String,
    onPlayClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            // Dummy video image player bar overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(Color(0xFF1E293B))
            ) {
                // Dynamic Green grass play center back drawing
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(color = Color(0xFF0F172A))
                    // draw simple white center line
                    val h = size.height
                    val w = size.width
                    drawLine(
                        color = Color.White.copy(alpha = 0.2f),
                        start = androidx.compose.ui.geometry.Offset(0f, h/2),
                        end = androidx.compose.ui.geometry.Offset(w, h/2),
                        strokeWidth = 3f
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.2f),
                        radius = h * 0.25f,
                        center = androidx.compose.ui.geometry.Offset(w/2, h/2),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(3f)
                    )
                }

                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(60.dp),
                    shape = CircleShape,
                    onClick = { onPlayClick(clip.videoUrl) }
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Play highlight",
                            tint = Color.White,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(36.dp)
                        )
                    }
                }

                Text(
                    text = clip.duration,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                        .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = if (lang == "VI") clip.matchTitleVI else clip.matchTitleEN,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                val bulletPoints = if (lang == "VI") clip.highlightsVI else clip.highlightsEN
                bulletPoints.forEach { pt ->
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text("▶", color = StadiumGreen, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = pt,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HighlightsScreen(
    viewModel: MainViewModel,
    lang: String,
    dict: Map<String, String>
) {
    val context = LocalContext.current
    var activeVtvUrl by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        
        if (activeVtvUrl != null) {
            // Show embedded catalog player
            EmbeddedVtvGoWebView(
                url = activeVtvUrl!!,
                onClose = { activeVtvUrl = null }
            )
        } else {
            // Static headers for highlight clips with external action too
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { activeVtvUrl = viewModel.vtvGoCatalogUrl },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("vtv_go_action_button")
            ) {
                Icon(Icons.Default.Tv, contentDescription = "VTV Play", tint = Color.White)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = dict["vtv_go_action"] ?: "Watch VTV Go Live Stream",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = dict["recaps"] ?: "",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            val isWideScreen = androidx.compose.ui.platform.LocalConfiguration.current.screenWidthDp >= 600
            val chunkedHighlights = WorldCupData.highlights.chunked(if (isWideScreen) 2 else 1)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(chunkedHighlights) { rowClips ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowClips.forEach { clip ->
                            Box(modifier = Modifier.weight(1f)) {
                                HighlightCard(
                                    clip = clip,
                                    lang = lang,
                                    onPlayClick = { activeVtvUrl = it }
                                )
                            }
                        }
                        if (isWideScreen && rowClips.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

// Native lightweight in-app HTML View loader for VTV Go
@Composable
fun EmbeddedVtvGoWebView(
    url: String,
    onClose: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(520.dp)
            .padding(top = 8.dp, bottom = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LiveTv,
                        contentDescription = "VTV Play",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "VTV Go Live Catalog 📺",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close stream",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.useWideViewPort = true
                        settings.loadWithOverviewMode = true
                        loadUrl(url)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}

// ---------------- COMMUNITY DISCUSSION & PROFILE PREFERENCE PANEL ----------------
@Composable
fun CommunityScreen(
    viewModel: MainViewModel,
    lang: String,
    dict: Map<String, String>
) {
    val chatMessages by viewModel.activeChatMessages.collectAsState()
    val nickname by viewModel.currentUserNickname.collectAsState()
    val favTeamCode by viewModel.favoriteTeamCode.collectAsState()
    val points by viewModel.userPoints.collectAsState()

    var editingNickname by remember { mutableStateOf(nickname) }
    var chatText by remember { mutableStateOf("") }

    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isWideScreen = configuration.screenWidthDp >= 600

    @Composable
    fun ProfilePanel() {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = if (lang == "VI") "Cá Nhân Hóa Trải Nghiệm" else "Personalization settings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                // Nickname modifier
                OutlinedTextField(
                    value = editingNickname,
                    onValueChange = { editingNickname = it },
                    label = { Text(dict["user_nickname"] ?: "Nickname") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(
                            onClick = { viewModel.updateNickname(editingNickname) }
                        ) {
                            Icon(Icons.Default.Save, contentDescription = "Save nickname")
                        }
                    },
                    singleLine = true
                )

                // Favorite Team selection grid
                Text(text = dict["favorite_setting"] ?: "", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    WorldCupData.teams.take(12).forEach { team ->
                        val isSelected = team.code == favTeamCode
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.updateFavoriteTeam(team.code) },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CountryFlag(team.code, modifier = Modifier.size(20.dp, 12.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (lang == "VI") team.nameVI else team.nameEN)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun DiscussionPanel(modifier: Modifier = Modifier) {
        Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = if (lang == "VI") "💬 Diễn Đàn Thảo Luận Cộng Đồng" else "💬 Community Discussions Forum",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isWideScreen) 400.dp else 300.dp)
                    .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(chatMessages) { chat ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (chat.avatarIndex == 0) Color(0xFF6366F1) else Color(0xFF10B981),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Text(
                                        text = chat.userNickname.take(1).uppercase(),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.width(10.dp))
                            
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(chat.userNickname, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(chat.timestamp)),
                                        fontSize = 9.sp,
                                        color = Color.Gray
                                    )
                                }
                                Text(chat.message, fontSize = 13.sp, modifier = Modifier.padding(top = 2.dp))
                            }
                        }
                    }
                }
            }

            // Chat Input strip
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = chatText,
                    onValueChange = { chatText = it },
                    placeholder = { Text(dict["chat_placeholder"] ?: "") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        viewModel.sendChatMessage(chatText)
                        chatText = ""
                    },
                    modifier = Modifier.background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send Message", tint = Color.Black)
                }
            }
        }
    }

    if (isWideScreen) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(modifier = Modifier.weight(1f)) {
                ProfilePanel()
            }
            Box(modifier = Modifier.weight(1.2f)) {
                DiscussionPanel()
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            ProfilePanel()
            DiscussionPanel()
        }
    }
}
