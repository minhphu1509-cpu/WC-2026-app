package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.GeminiPredictor
import com.example.data.*
import com.example.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val chatDao = database.chatDao()
    private val predictionDao = database.predictionDao()
    private val userPointsDao = database.userPointsDao()
    private val prefDao = database.userPreferencesDao()

    // UI Configuration States
    private val _currentLanguage = MutableStateFlow("VI") // "VI" or "EN"
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true) // Battery saver Dark Mode enabled by default
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _activeScreen = MutableStateFlow("home") // "home", "matches", "standings", "highlights", "community"
    val activeScreen: StateFlow<String> = _activeScreen.asStateFlow()

    private val _favoriteTeamCode = MutableStateFlow("VIE")
    val favoriteTeamCode: StateFlow<String> = _favoriteTeamCode.asStateFlow()

    private val _currentUserNickname = MutableStateFlow("Fan_BóngĐá_2026")
    val currentUserNickname: StateFlow<String> = _currentUserNickname.asStateFlow()

    // Match Details selection
    private val _selectedMatchId = MutableStateFlow<String?>(null)
    val selectedMatchId: StateFlow<String?> = _selectedMatchId.asStateFlow()

    // Dynamic Live Matches List
    private val _matchesList = MutableStateFlow<List<Match>>(WorldCupData.matches)
    val matchesList: StateFlow<List<Match>> = _matchesList.asStateFlow()

    // Live standigns state
    private val _standings = MutableStateFlow<Map<String, List<TeamStanding>>> (emptyMap())
    val standings: StateFlow<Map<String, List<TeamStanding>>> = _standings.asStateFlow()

    // AI Prediction states
    private val _aiPredictionText = MutableStateFlow<String>("")
    val aiPredictionText: StateFlow<String> = _aiPredictionText.asStateFlow()

    private val _aiSearchQueries = MutableStateFlow<List<String>>(emptyList())
    val aiSearchQueries: StateFlow<List<String>> = _aiSearchQueries.asStateFlow()

    private val _aiSearchSources = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val aiSearchSources: StateFlow<List<Pair<String, String>>> = _aiSearchSources.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    // Local Chat messages list
    private val _activeChatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val activeChatMessages: StateFlow<List<ChatMessage>> = _activeChatMessages.asStateFlow()

    // User reward points state
    private val _userPoints = MutableStateFlow(180)
    val userPoints: StateFlow<Int> = _userPoints.asStateFlow()

    // Leaderboard of reward points
    private val _pointsLeaderboard = MutableStateFlow<List<UserPoint>>(emptyList())
    val pointsLeaderboard: StateFlow<List<UserPoint>> = _pointsLeaderboard.asStateFlow()

    // Action Goal Real-time Notifications Alert Flow
    private val _goalAlertChannel = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val goalAlertChannel: SharedFlow<String> = _goalAlertChannel.asSharedFlow()

    // MVP Votes Storage (MatchId -> Map<PlayerName, VoteCount>)
    private val _mvpVotesMap = MutableStateFlow<Map<String, Map<String, Int>>>(emptyMap())
    val mvpVotesMap: StateFlow<Map<String, Map<String, Int>>> = _mvpVotesMap.asStateFlow()

    private val _userVotedMatches = MutableStateFlow<Set<String>>(emptySet()) // set of matchIds voted
    val userVotedMatches: StateFlow<Set<String>> = _userVotedMatches.asStateFlow()

    // Current Web Highlights stream catalog url
    val vtvGoCatalogUrl = "https://vtvgo.vn/video/catalog/list/b05616fa-ba8e-4b93-98c5-e7860e0049ca/cong-noi-dung-19,b05616fa-ba8e-4b93-98c5-e7860e0049ca.html"

    init {
        loadSettingsFromDb()
        computeStandings()
        observeChatAndPoints()
        startScoresMinuteTicking()
    }

    private fun loadSettingsFromDb() {
        viewModelScope.launch {
            prefDao.getPreference("language")?.let { _currentLanguage.value = it.prefValue }
            prefDao.getPreference("dark_mode")?.let { _isDarkMode.value = it.prefValue.toBoolean() }
            prefDao.getPreference("favorite_team")?.let { _favoriteTeamCode.value = it.prefValue }
            prefDao.getPreference("user_nickname")?.let { _currentUserNickname.value = it.prefValue }

            // Pre-seed some chat messages if DB is empty
            chatDao.getMessagesForMatch("global").collect { list ->
                if (list.isEmpty()) {
                    seedInitialChat()
                } else {
                    _activeChatMessages.value = list
                }
            }
        }
    }

    private fun observeChatAndPoints() {
        // Collect points and populate leaderboard
        viewModelScope.launch {
            userPointsDao.getAllUserPoints().collect { dbPoints ->
                val merged = mutableListOf<UserPoint>()
                merged.addAll(dbPoints)
                
                // Add some default competitive mock users to make leaderboard interesting
                val mockPlayers = listOf(
                    UserPoint("messifan10@gmail.com", "LeoKing99", 520, "Huyền Thoại Fan"),
                    UserPoint("ronaldogoat@outlook.com", "CR7_Lover", 340, "Siêu Cổ Động Viên"),
                    UserPoint("vnfootball@vff.org.vn", "Ngôi_Sao_Sao_Vàng", 310, "Bậc Thầy Dự Đoán"),
                    UserPoint("pulisiccaptain@usa.com", "CaptainAmerica", 220, "Nhà Phân Tích"),
                    UserPoint("fanreal@barca.com", "ElClasicoExpert", 195, "Cựu Chiến Binh")
                )
                mockPlayers.forEach { mock ->
                    if (merged.none { it.userEmail == mock.userEmail }) {
                        merged.add(mock)
                    }
                }
                _pointsLeaderboard.value = merged.sortedByDescending { it.points }
            }
        }

        // Active chat message tracking based on Match UI Selection
        viewModelScope.launch {
            _selectedMatchId.collect { matchId ->
                val targetId = matchId ?: "global"
                chatDao.getMessagesForMatch(targetId).collect { messages ->
                    _activeChatMessages.value = messages
                }
            }
        }
    }

    private suspend fun seedInitialChat() {
        val initialMessages = listOf(
            ChatMessage(matchId = "global", userEmail = "messifan10@gmail.com", userNickname = "LeoKing99", message = "Xin chào anh em hâm mộ bóng đá! World Cup 2026 năm nay xem phê quá, nhiều đội mạnh tưng bừng.", avatarIndex = 1),
            ChatMessage(matchId = "global", userEmail = "vnfootball@vff.org.vn", userNickname = "Ngôi_Sao_Sao_Vàng", message = "Mong Việt Nam giữ vững phong độ đấu Mỹ. Phép thuật thầy Gong/Kim hy vọng tạo kỳ tích!", avatarIndex = 2),
            ChatMessage(matchId = "global", userEmail = "pulisiccaptain@usa.com", userNickname = "CaptainAmerica", message = "Great game tonight vs Vietnam, truly fought like heroes. Respect from San Francisco!", avatarIndex = 3),
            ChatMessage(matchId = "M1", userEmail = "vnfootball@vff.org.vn", userNickname = "Ngôi_Sao_Sao_Vàng", message = "Quang Hải đá quả phạt góc mẫu mực quá, quả bóng bay lịm luôn anh em ơi!", avatarIndex = 2),
            ChatMessage(matchId = "M1", userEmail = "messifan10@gmail.com", userNickname = "LeoKing99", message = "Tuyển Việt Nam đá phòng ngự phản công đỉnh, giữ cự ly đội hình khít khao.", avatarIndex = 1)
        )
        initialMessages.forEach { chatDao.insertMessage(it) }
    }

    // Dynamic minute ticking simulator for Live matches
    private fun startScoresMinuteTicking() {
        viewModelScope.launch {
            while (true) {
                delay(12000) // tick match details every 12 seconds
                _matchesList.value = _matchesList.value.map { match ->
                    if (match.status == "LIVE") {
                        val nextMin = match.minute + 1
                        if (nextMin >= 90) {
                            match.copy(minute = 90, status = "FINISHED")
                        } else {
                            match.copy(minute = nextMin)
                        }
                    } else {
                        match
                    }
                }
            }
        }
    }

    // SIMULATED REAL-TIME GOAL SIMULATOR FEATURE!
    fun triggerSimulatedGoal() {
        viewModelScope.launch {
            val liveMatch = _matchesList.value.find { it.status == "LIVE" }
            if (liveMatch != null) {
                val scA_wins = Random.nextBoolean()
                val newScoreA = if (scA_wins) liveMatch.scoreA + 1 else liveMatch.scoreA
                val newScoreB = if (!scA_wins) liveMatch.scoreB + 1 else liveMatch.scoreB
                val scorer = if (scA_wins) {
                    val p = WorldCupData.teamMap[liveMatch.teamACode]?.popularPlayer ?: "Striker"
                    "$p ${liveMatch.minute}'"
                } else {
                    val p = WorldCupData.teamMap[liveMatch.teamBCode]?.popularPlayer ?: "Striker"
                    "$p ${liveMatch.minute}'"
                }

                val newScorersA = if (scA_wins) liveMatch.scorersA + scorer else liveMatch.scorersA
                val newScorersB = if (!scA_wins) liveMatch.scorersB + scorer else liveMatch.scorersB

                val updatedMatch = liveMatch.copy(
                    scoreA = newScoreA,
                    scoreB = newScoreB,
                    scorersA = newScorersA,
                    scorersB = newScorersB
                )

                _matchesList.value = _matchesList.value.map {
                    if (it.id == liveMatch.id) updatedMatch else it
                }

                computeStandings()

                // Emit beautiful live visual notification alert!
                val alertMsg = if (_currentLanguage.value == "VI") {
                    "⚽ VÀOOO! [${liveMatch.teamACode}] $newScoreA - $newScoreB [${liveMatch.teamBCode}] tại phút ${liveMatch.minute}' ($scorer)"
                } else {
                    "⚽ GOALLL! [${liveMatch.teamACode}] $newScoreA - $newScoreB [${liveMatch.teamBCode}] at ${liveMatch.minute}' ($scorer)"
                }
                _goalAlertChannel.emit(alertMsg)

                // Reward active predicting user with 30 bonus points for live tracking!
                addPoints(30)
            } else {
                // If no live match, turn an upcoming one to Live!
                val upcoming = _matchesList.value.firstOrNull { it.status == "UPCOMING" }
                if (upcoming != null) {
                    val activatedMat = upcoming.copy(status = "LIVE", minute = 1, scoreA = 0, scoreB = 0)
                    _matchesList.value = _matchesList.value.map {
                        if (it.id == upcoming.id) activatedMat else it
                    }
                    val msg = if (_currentLanguage.value == "VI") {
                        "🔥 Trận đấu chính thức BẮT ĐẦU: ${activatedMat.teamACode} đối đầu ${activatedMat.teamBCode}!"
                    } else {
                        "🔥 MATCH STARTED LIVE: ${activatedMat.teamACode} vs ${activatedMat.teamBCode}!"
                    }
                    _goalAlertChannel.emit(msg)
                }
            }
        }
    }

    // Standings calculation based on results
    private fun computeStandings() {
        val calculated = mutableMapOf<String, MutableMap<String, TeamStanding>>()

        // Initialize groups
        WorldCupData.teams.forEach { team ->
            val gp = team.group
            val gpMap = calculated.getOrPut(gp) { mutableMapOf() }
            gpMap[team.code] = TeamStanding(teamCode = team.code, played = 0, won = 0, drawn = 0, lost = 0, gs = 0, gc = 0, points = 0)
        }

        // Apply finished matches
        _matchesList.value.forEach { m ->
            if (m.status == "FINISHED" || m.status == "LIVE") {
                val groupCalculated = calculated[m.group]
                if (groupCalculated != null) {
                    val stA = groupCalculated[m.teamACode] ?: TeamStanding(m.teamACode)
                    val stB = groupCalculated[m.teamBCode] ?: TeamStanding(m.teamBCode)

                    val teamAPlayed = stA.played + 1
                    val teamBPlayed = stB.played + 1

                    val newGS_A = stA.gs + m.scoreA
                    val newGC_A = stA.gc + m.scoreB
                    val newGS_B = stB.gs + m.scoreB
                    val newGC_B = stB.gc + m.scoreA

                    val isDraw = m.scoreA == m.scoreB
                    val isWonA = m.scoreA > m.scoreB

                    val wonA = if (isWonA && !isDraw) stA.won + 1 else stA.won
                    val drawnA = if (isDraw) stA.drawn + 1 else stA.drawn
                    val lostA = if (!isWonA && !isDraw) stA.lost + 1 else stA.lost

                    val wonB = if (!isWonA && !isDraw) stB.won + 1 else stB.won
                    val drawnB = if (isDraw) stB.drawn + 1 else stB.drawn
                    val lostB = if (isWonA && !isDraw) stB.lost + 1 else stB.lost

                    val ptsA = wonA * 3 + drawnA
                    val ptsB = wonB * 3 + drawnB

                    groupCalculated[m.teamACode] = stA.copy(played = teamAPlayed, won = wonA, drawn = drawnA, lost = lostA, gs = newGS_A, gc = newGC_A, points = ptsA)
                    groupCalculated[m.teamBCode] = stB.copy(played = teamBPlayed, won = wonB, drawn = drawnB, lost = lostB, gs = newGS_B, gc = newGC_B, points = ptsB)
                }
            }
        }

        // Transform and sort
        val sortedGroups = calculated.mapValues { entry ->
            entry.value.values.sortedWith(
                compareByDescending<TeamStanding> { it.points }
                    .thenByDescending { it.gs - it.gc }
                    .thenByDescending { it.gs }
            )
        }
        _standings.value = sortedGroups
    }

    // Community Chat Actions
    fun sendChatMessage(msg: String) {
        if (msg.isBlank()) return
        val currentMatch = _selectedMatchId.value ?: "global"
        
        viewModelScope.launch {
            val userMsg = ChatMessage(
                matchId = currentMatch,
                userEmail = "user_" + _currentUserNickname.value.lowercase() + "@aistudio.com",
                userNickname = _currentUserNickname.value,
                message = msg,
                avatarIndex = 0
            )
            chatDao.insertMessage(userMsg)
            addPoints(10) // reward user points for chatting!

            // Fire automatic witty simulation fan reply based on the context!
            delay(1500)
            val replyMessage = generateSimulatedReply(msg, currentMatch)
            chatDao.insertMessage(replyMessage)
        }
    }

    private fun generateSimulatedReply(userMsg: String, matchId: String): ChatMessage {
        val mockReplies = listOf(
            "Cực kỳ đồng tình với nhận định của bạn luôn!",
            "Tôi thì lại nghĩ khác chút, chiến thuật của tuyến giữa cần linh hoạt hơn.",
            "Chuẩn rồi! Chạy chỗ thông minh dã man luôn.",
            "Anh em bình tĩnh, trận đấu vẫn còn khá dài mà, cơ hội lội ngược dòng vẫn sáng.",
            "Sút quả đấy trúng biên dọc mà xém rớt tim ra ngoài. Bóng đá đúng kịch tính!",
            "Nhìn lối đá này chắc chắn AI dự đoán không sai lầm.",
            "Amazing match, let's keep the fire burning! 🔥",
            "Đội Việt Nam đá thế này là quá tuyệt vời rồi, cố lên các chiến binh sao vàng!"
        )
        val selectedText = mockReplies[Random.nextInt(mockReplies.size)]
        val mockNicknames = listOf("Blaster_Beto", "JohnSoccer_99", "Mỹ_Linh_CầuThủ", "TacticsExpert", "BànThắng_Vàng")
        val randNick = mockNicknames[Random.nextInt(mockNicknames.size)]

        return ChatMessage(
            matchId = matchId,
            userEmail = randNick.lowercase() + "@fastfan.org",
            userNickname = randNick,
            message = selectedText,
            avatarIndex = Random.nextInt(1, 4)
        )
    }

    // MVP voting cast action
    fun castMvpVote(matchId: String, playerName: String) {
        viewModelScope.launch {
            val currentMatchVotes = _mvpVotesMap.value[matchId]?.toMutableMap() ?: mutableMapOf()
            val existing = currentMatchVotes[playerName] ?: 0
            currentMatchVotes[playerName] = existing + 1

            _mvpVotesMap.value = _mvpVotesMap.value + (matchId to currentMatchVotes)
            _userVotedMatches.value = _userVotedMatches.value + matchId

            addPoints(20) // reward points for casting MVP vote!
        }
    }

    // AI Prediction triggers
    fun loadAiPrediction(matchId: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            _aiPredictionText.value = ""
            _aiSearchQueries.value = emptyList()
            _aiSearchSources.value = emptyList()

            val cached = predictionDao.getPrediction(matchId)
            if (cached != null) {
                // If cached is recent, pull from DB to save token quota
                _aiPredictionText.value = cached.predictionText
                _aiSearchQueries.value = if (cached.searchQueriesText.isNotEmpty()) cached.searchQueriesText.split(",") else emptyList()
                _aiSearchSources.value = if (cached.searchSourcesText.isNotEmpty()) {
                    cached.searchSourcesText.split("\n").mapNotNull { line ->
                        val parts = line.split("|")
                        if (parts.size >= 2) Pair(parts[0], parts[1]) else null
                    }
                } else emptyList()
                _isAiLoading.value = false
                return@launch
            }

            // Not cached, invoke Gemini 3.5 Flash Model!
            val match = _matchesList.value.find { it.id == matchId }
            if (match != null) {
                val teamA = WorldCupData.teamMap[match.teamACode]?.let { if (_currentLanguage.value == "VI") it.nameVI else it.nameEN } ?: match.teamACode
                val teamB = WorldCupData.teamMap[match.teamBCode]?.let { if (_currentLanguage.value == "VI") it.nameVI else it.nameEN } ?: match.teamBCode
                
                val result = withContext(Dispatchers.IO) {
                    GeminiPredictor.analyzeMatch(
                        teamA = teamA,
                        teamB = teamB,
                        history = match.h2hHistory,
                        formTeamA = match.formTeamA,
                        formTeamB = match.formTeamB,
                        language = _currentLanguage.value
                    )
                }

                _aiPredictionText.value = result.predictionText
                _aiSearchQueries.value = result.searchQueries
                _aiSearchSources.value = result.searchSources

                // Save to local cache
                val cacheObj = PredictionCache(
                    matchId = matchId,
                    predictionText = result.predictionText,
                    analysisText = "Deep Tactical Form Analysis cached.",
                    tacticsText = "Board formation summary.",
                    searchQueriesText = result.searchQueries.joinToString(","),
                    searchSourcesText = result.searchSources.joinToString("\n") { "${it.first}|${it.second}" }
                )
                predictionDao.insertPrediction(cacheObj)
                addPoints(50) // Reward points for utilizing the high-end tactical AI!
            } else {
                _aiPredictionText.value = "Match not found for AI analysis."
            }
            _isAiLoading.value = false
        }
    }

    // Reward Point Increment and save
    fun addPoints(amount: Int) {
        viewModelScope.launch {
            val nextPoints = _userPoints.value + amount
            _userPoints.value = nextPoints

            // Save player details into database
            val userPt = UserPoint(
                userEmail = "user_" + _currentUserNickname.value.lowercase() + "@aistudio.com",
                userNickname = _currentUserNickname.value,
                points = nextPoints,
                badgeName = when {
                    nextPoints > 300 -> "Huyền Thoại Fan"
                    nextPoints > 200 -> "Siêu Cổ Động Viên"
                    else -> "Bậc Thầy Dự Đoán"
                }
            )
            userPointsDao.insertOrUpdatePoints(userPt)
        }
    }

    // Config updating
    fun updateFavoriteTeam(teamCode: String) {
        viewModelScope.launch {
            _favoriteTeamCode.value = teamCode
            prefDao.insertPreference(UserPreference("favorite_team", teamCode))
        }
    }

    fun updateLanguage(lang: String) {
        viewModelScope.launch {
            _currentLanguage.value = lang
            prefDao.insertPreference(UserPreference("language", lang))
            computeStandings() // recalculate representations in the text
        }
    }

    fun updateDarkModeSetting(enabled: Boolean) {
        viewModelScope.launch {
            _isDarkMode.value = enabled
            prefDao.insertPreference(UserPreference("dark_mode", enabled.toString()))
        }
    }

    fun updateNickname(newName: String) {
        if (newName.isBlank()) return
        viewModelScope.launch {
            val cachedOldName = _currentUserNickname.value
            _currentUserNickname.value = newName
            prefDao.insertPreference(UserPreference("user_nickname", newName))

            // Replace old point row in room
            val oldEmail = "user_" + cachedOldName.lowercase() + "@aistudio.com"
            val newEmail = "user_" + newName.lowercase() + "@aistudio.com"
            
            val userPt = UserPoint(
                userEmail = newEmail,
                userNickname = newName,
                points = _userPoints.value,
                badgeName = "Báo Đốm Dự Đoán"
            )
            userPointsDao.insertOrUpdatePoints(userPt)
        }
    }

    fun navigateTo(screen: String) {
        _activeScreen.value = screen
        _selectedMatchId.value = null // reset details view when resetting tab
    }

    fun selectMatchDetails(matchId: String?) {
        _selectedMatchId.value = matchId
        if (matchId != null) {
            _aiPredictionText.value = "" // clear prediction box on switch
            _aiSearchQueries.value = emptyList()
            _aiSearchSources.value = emptyList()
        }
    }
}

// Struct for calculated Group Standing
data class TeamStanding(
    val teamCode: String,
    val played: Int = 0,
    val won: Int = 0,
    val drawn: Int = 0,
    val lost: Int = 0,
    val gs: Int = 0, // Goals Scored
    val gc: Int = 0, // Goals Conceded
    val points: Int = 0
)
