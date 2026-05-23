package com.example.model

import androidx.compose.ui.graphics.Color

data class Team(
    val code: String,
    val nameVI: String,
    val nameEN: String,
    val group: String,
    val flagColorPrimary: Color,
    val flagColorSecondary: Color,
    val flagStyle: String, // "STRIPES_H", "STRIPES_V", "TRIANGLE", "SOLID", "CROSS"
    val rank: Int,
    val stars: Int, // 1 to 5
    val popularPlayer: String
)

data class MatchStats(
    val possessionA: Int, // e.g. 52
    val possessionB: Int, // e.g. 48
    val shotsA: Int,
    val shotsB: Int,
    val shotsOnTargetA: Int,
    val shotsOnTargetB: Int,
    val passesA: Int,
    val passesB: Int,
    val passesSuccessRateA: Int,
    val passesSuccessRateB: Int,
    val foulsA: Int,
    val foulsB: Int,
    val yellowCardsA: Int,
    val yellowCardsB: Int,
    val redCardsA: Int,
    val redCardsB: Int
)

data class LineupPlayer(
    val name: String,
    val number: Int,
    val position: String, // "GK", "DF", "MF", "FW"
    val rating: Float
)

data class Match(
    val id: String,
    val group: String,
    val teamACode: String,
    val teamBCode: String,
    val kickoffTime: String, // format "15 June 2026, 18:00"
    val dateLabel: String, // "June 11", "June 12" etc.
    val status: String, // "UPCOMING", "LIVE", "FINISHED"
    val scoreA: Int = 0,
    val scoreB: Int = 0,
    val minute: Int = 0,
    val scorersA: List<String> = emptyList(),
    val scorersB: List<String> = emptyList(),
    val stats: MatchStats? = null,
    val h2hHistory: String = "Chưa có đối đầu gần đây trong các giải chính thức / No recent head-to-head in official tournaments.",
    val formTeamA: String = "Thắng, Hòa, Thắng (W-D-W)",
    val formTeamB: String = "Thắng, Thắng, Thắng (W-W-W)",
    val lineupA: List<LineupPlayer> = emptyList(),
    val lineupB: List<LineupPlayer> = emptyList()
)

data class PlayerDetail(
    val name: String,
    val imagePlaceholder: Color,
    val age: Int,
    val positionVI: String,
    val positionEN: String,
    val nationalityVI: String,
    val nationalityEN: String,
    val club: String,
    val rating: Float,
    val goals: Int,
    val assists: Int,
    val marketValue: String
)

data class HighlightVideo(
    val id: String,
    val matchTitleVI: String,
    val matchTitleEN: String,
    val duration: String,
    val videoUrl: String,
    val highlightsVI: List<String>,
    val highlightsEN: List<String>
)

data class TransferNews(
    val id: String,
    val titleVI: String,
    val titleEN: String,
    val categoryVI: String,
    val categoryEN: String,
    val source: String,
    val timeLabel: String,
    val contentVI: String,
    val contentEN: String
)

// Prepopulated static and highly realistic data
object WorldCupData {

    val teams = listOf(
        Team("USA", "Hoa Kỳ", "USA", "A", Color(0xFF0F172A), Color(0xFFEF4444), "STRIPES_H", 11, 4, "Christian Pulisic"),
        Team("MEX", "Mexico", "Mexico", "A", Color(0xFF15803D), Color(0xFFDC2626), "STRIPES_V", 15, 3, "Santiago Giménez"),
        Team("CAN", "Canada", "Canada", "A", Color(0xFFDC2626), Color(0xFFFFFFFF), "TRIANGLE", 38, 3, "Alphonso Davies"),
        Team("VIE", "Việt Nam", "Vietnam", "A", Color(0xFFDC2626), Color(0xFFFACC15), "TRIANGLE", 95, 2, "Nguyễn Quang Hải"), // friendly qualifier!
        
        Team("ARG", "Argentina", "Argentina", "B", Color(0xFF38BDF8), Color(0xFFFFFFFF), "STRIPES_V", 1, 5, "Lionel Messi"),
        Team("FRA", "Pháp", "France", "B", Color(0xFF1D4ED8), Color(0xFFFFFFFF), "STRIPES_V", 2, 5, "Kylian Mbappé"),
        Team("BRA", "Brazil", "Brazil", "C", Color(0xFFEAB308), Color(0xFF15803D), "TRIANGLE", 5, 5, "Vinícius Júnior"),
        Team("ENG", "Anh", "England", "C", Color(0xFFFFFFFF), Color(0xFFEF4444), "CROSS", 4, 5, "Jude Bellingham"),
        
        Team("POR", "Bồ Đào Nha", "Portugal", "D", Color(0xFF15803D), Color(0xFFDC2626), "TRIANGLE", 6, 5, "Cristiano Ronaldo"),
        Team("ESP", "Tây Ban Nha", "Spain", "D", Color(0xFFDC2626), Color(0xFFEAB308), "STRIPES_H", 8, 5, "Lamine Yamal"),
        Team("GER", "Đức", "Germany", "E", Color(0xFF000000), Color(0xFFFACC15), "STRIPES_H", 16, 4, "Jamal Musiala"),
        Team("JPN", "Nhật Bản", "Japan", "E", Color(0xFF1E3A8A), Color(0xFFFFFFFF), "SOLID", 17, 4, "Kaoru Mitoma"),
        
        Team("CRO", "Croatia", "Croatia", "F", Color(0xFFEF4444), Color(0xFFFFFFFF), "CROSS", 10, 4, "Luka Modrić"),
        Team("MAR", "Ma-rốc", "Morocco", "F", Color(0xFF991B1B), Color(0xFF15803D), "SOLID", 12, 4, "Achraf Hakimi"),
        Team("ITA", "Ý", "Italy", "G", Color(0xFF1E40AF), Color(0xFFFFFFFF), "STRIPES_V", 9, 4, "Nicolò Barella"),
        Team("NED", "Hà Lan", "Netherlands", "G", Color(0xFFEA580C), Color(0xFFFFFFFF), "SOLID", 7, 4, "Cody Gakpo")
    )

    val teamMap = teams.associateBy { it.code }

    val defaultStats = MatchStats(
        possessionA = 55, possessionB = 45,
        shotsA = 14, shotsB = 9,
        shotsOnTargetA = 6, shotsOnTargetB = 4,
        passesA = 480, passesB = 390,
        passesSuccessRateA = 86, passesSuccessRateB = 81,
        foulsA = 10, foulsB = 12,
        yellowCardsA = 1, yellowCardsB = 2,
        redCardsA = 0, redCardsB = 0
    )

    val matches = listOf(
        Match(
            id = "M1", group = "A", teamACode = "USA", teamBCode = "VIE",
            kickoffTime = "12 June 2026, 17:00", dateLabel = "12 June 2026",
            status = "LIVE", scoreA = 2, scoreB = 1, minute = 68,
            scorersA = listOf("Pulisic 12'", "Balogun 44'"), scorersB = listOf("Quang Hải 58'"),
            stats = MatchStats(
                possessionA = 58, possessionB = 42,
                shotsA = 16, shotsB = 8,
                shotsOnTargetA = 7, shotsOnTargetB = 3,
                passesA = 512, passesB = 356,
                passesSuccessRateA = 88, passesSuccessRateB = 78,
                foulsA = 8, foulsB = 14,
                yellowCardsA = 1, yellowCardsB = 3,
                redCardsA = 0, redCardsB = 0
            ),
            h2hHistory = "Chưa từng gặp nhau tại World Cup nam. Hoa Kỳ thắng trận giao hữu năm 2025 (2-0). / Never met in Men's World Cup. USA won a friendly in 2025 (2-0).",
            formTeamA = "Thắng, Hòa, Thắng (W-D-W)", formTeamB = "Thắng, Thắng, Bại (W-W-L)",
            lineupA = listOf(
                LineupPlayer("Turner", 1, "GK", 6.8f),
                LineupPlayer("Dest", 2, "DF", 7.2f),
                LineupPlayer("Ream", 13, "DF", 7.0f),
                LineupPlayer("Robinson", 5, "DF", 7.5f),
                LineupPlayer("McKennie", 8, "MF", 8.0f),
                LineupPlayer("Adams", 4, "MF", 7.4f),
                LineupPlayer("Reyna", 7, "MF", 7.8f),
                LineupPlayer("Pulisic", 10, "FW", 8.5f),
                LineupPlayer("Weah", 11, "FW", 7.1f),
                LineupPlayer("Balogun", 20, "FW", 8.2f)
            ),
            lineupB = listOf(
                LineupPlayer("Filip Nguyễn", 1, "GK", 7.4f),
                LineupPlayer("Bùi Hoàng Việt Anh", 4, "DF", 7.1f),
                LineupPlayer("Thanh Bình", 3, "DF", 6.9f),
                LineupPlayer("Tuấn Tài", 12, "DF", 6.8f),
                LineupPlayer("Vũ Văn Thanh", 17, "DF", 7.0f),
                LineupPlayer("Đỗ Hùng Dũng", 8, "MF", 7.2f),
                LineupPlayer("Tuấn Anh", 11, "MF", 7.1f),
                LineupPlayer("Nguyễn Quang Hải", 19, "MF", 8.4f),
                LineupPlayer("Phạm Tuấn Hải", 10, "FW", 7.3f),
                LineupPlayer("Tiến Linh", 22, "FW", 7.0f)
            )
        ),
        Match(
            id = "M2", group = "B", teamACode = "ARG", teamBCode = "FRA",
            kickoffTime = "13 June 2026, 20:00", dateLabel = "13 June 2026",
            status = "FINISHED", scoreA = 3, scoreB = 2, minute = 90,
            scorersA = listOf("Messi 24' (pen), 88'", "Alvarez 41'"), scorersB = listOf("Mbappé 10', 74'"),
            stats = MatchStats(
                possessionA = 51, possessionB = 49,
                shotsA = 18, shotsB = 15,
                shotsOnTargetA = 9, shotsOnTargetB = 8,
                passesA = 460, passesB = 445,
                passesSuccessRateA = 87, passesSuccessRateB = 86,
                foulsA = 11, foulsB = 9,
                yellowCardsA = 2, yellowCardsB = 2,
                redCardsA = 0, redCardsB = 0
            ),
            h2hHistory = "Chung kết World Cup 2022 kịch tính: Argentina thắng penalty sau tỷ số 3-3 ở 120 phút. / Dramatic 2022 World Cup Final: Argentina won on penalties after a 3-3 draw in 120 mins.",
            formTeamA = "Thắng, Thắng, Thắng (W-W-W)", formTeamB = "Thắng, Thắng, Bại (W-W-L)",
            lineupA = listOf(
                LineupPlayer("E. Martinez", 23, "GK", 8.0f),
                LineupPlayer("Molina", 26, "DF", 7.2f),
                LineupPlayer("Romero", 13, "DF", 7.5f),
                LineupPlayer("Otamendi", 19, "DF", 7.1f),
                LineupPlayer("Acuna", 8, "DF", 7.0f),
                LineupPlayer("De Paul", 7, "MF", 7.8f),
                LineupPlayer("Mac Allister", 20, "MF", 8.0f),
                LineupPlayer("Enzo", 24, "MF", 7.9f),
                LineupPlayer("Messi", 10, "FW", 9.5f),
                LineupPlayer("Alvarez", 9, "FW", 8.4f)
            ),
            lineupB = listOf(
                LineupPlayer("Maignan", 16, "GK", 7.5f),
                LineupPlayer("Kounde", 5, "DF", 7.2f),
                LineupPlayer("Upamecano", 4, "DF", 6.8f),
                LineupPlayer("Konate", 24, "DF", 7.1f),
                LineupPlayer("Theo Hernandez", 22, "DF", 8.0f),
                LineupPlayer("Tchouameni", 8, "MF", 7.6f),
                LineupPlayer("Rabiot", 14, "MF", 7.3f),
                LineupPlayer("Dembele", 11, "FW", 7.8f),
                LineupPlayer("Griezmann", 7, "FW", 8.2f),
                LineupPlayer("Mbappé", 10, "FW", 9.0f)
            )
        ),
        Match(
            id = "M3", group = "A", teamACode = "CAN", teamBCode = "MEX",
            kickoffTime = "14 June 2026, 16:00", dateLabel = "14 June 2026",
            status = "UPCOMING", scoreA = 0, scoreB = 0, minute = 0,
            stats = defaultStats,
            h2hHistory = "Đối thủ truyền kiếp tại CONCACAF. Trong 5 lần gặp nhau gần đây, Mexico thắng 2, Canada thắng 1, hòa 2. / Classic CONCACAF rivalry. Last 5 matches: Mexico won 2, Canada 1, 2 draws.",
            formTeamA = "Hòa, Hòa, Thắng (D-D-W)", formTeamB = "Thắng, Hòa, Thắng (W-D-W)",
            lineupA = listOf(
                LineupPlayer("Crepeau", 1, "GK", 7.0f),
                LineupPlayer("Johnston", 2, "DF", 7.1f),
                LineupPlayer("Miller", 4, "DF", 7.0f),
                LineupPlayer("Davies", 19, "DF", 8.5f),
                LineupPlayer("Eustaquio", 7, "MF", 7.8f),
                LineupPlayer("Kone", 8, "MF", 7.2f),
                LineupPlayer("Buchanan", 11, "MF", 7.5f),
                LineupPlayer("David", 20, "FW", 8.1f),
                LineupPlayer("Larin", 9, "FW", 7.6f)
            ),
            lineupB = listOf(
                LineupPlayer("Ochoa", 13, "GK", 7.2f),
                LineupPlayer("Sanchez", 19, "DF", 7.1f),
                LineupPlayer("Montes", 3, "DF", 7.3f),
                LineupPlayer("Vasquez", 5, "DF", 7.0f),
                LineupPlayer("Chavez", 18, "MF", 7.9f),
                LineupPlayer("Alvarez", 4, "MF", 8.2f),
                LineupPlayer("Pineda", 17, "MF", 7.4f),
                LineupPlayer("Lozano", 22, "FW", 8.0f),
                LineupPlayer("Giménez", 11, "FW", 8.3f)
            )
        ),
        Match(
            id = "M4", group = "C", teamACode = "BRA", teamBCode = "ENG",
            kickoffTime = "15 June 2026, 19:30", dateLabel = "15 June 2026",
            status = "UPCOMING", scoreA = 0, scoreB = 0, minute = 0,
            stats = defaultStats,
            h2hHistory = "Trận đấu kinh điển quốc tế. Tỷ số giao hữu gần nhất là 1-0 nghiêng về Brazil ngay tại Wembley năm 2024. / Legend matchup. Their last meeting in Wembley (2024) ended with 1-0 victory for Brazil.",
            formTeamA = "Bại, Thắng, Thắng (L-W-W)", formTeamB = "Thắng, Thắng, Thắng (W-W-W)",
            lineupA = listOf(
                LineupPlayer("Alisson", 1, "GK", 8.2f),
                LineupPlayer("Danilo", 2, "DF", 7.0f),
                LineupPlayer("Marquinhos", 3, "DF", 7.8f),
                LineupPlayer("Gabriel", 4, "DF", 7.9f),
                LineupPlayer("Guimaraes", 5, "MF", 8.1f),
                LineupPlayer("Paqueta", 8, "MF", 7.6f),
                LineupPlayer("Rodrygo", 10, "FW", 8.4f),
                LineupPlayer("Vinícius Jr", 11, "FW", 9.0f),
                LineupPlayer("Endrick", 9, "FW", 8.5f)
            ),
            lineupB = listOf(
                LineupPlayer("Pickford", 1, "GK", 7.6f),
                LineupPlayer("Walker", 2, "DF", 8.0f),
                LineupPlayer("Stones", 5, "DF", 7.9f),
                LineupPlayer("Guehi", 6, "DF", 7.4f),
                LineupPlayer("Rice", 4, "MF", 8.2f),
                LineupPlayer("Mainoo", 26, "MF", 7.7f),
                LineupPlayer("Bellingham", 10, "MF", 9.1f),
                LineupPlayer("Saka", 7, "FW", 8.6f),
                LineupPlayer("Foden", 11, "FW", 8.5f),
                LineupPlayer("Kane", 9, "FW", 8.9f)
            )
        ),
        Match(
            id = "M5", group = "D", teamACode = "POR", teamBCode = "ESP",
            kickoffTime = "16 June 2026, 18:00", dateLabel = "16 June 2026",
            status = "UPCOMING", scoreA = 0, scoreB = 0, minute = 0,
            stats = defaultStats,
            h2hHistory = "Bán đảo Iberia rực lửa. Trận World Cup 2018 kết thúc với tỷ số hòa rực lửa 3-3 với cú hattrick của Ronaldo. / Iberian Derby. The 2018 World Cup ended 3-3 with Ronaldo scoring a brilliant hat-trick.",
            formTeamA = "Thắng, Bại, Thắng (W-L-W)", formTeamB = "Thắng, Thắng, Thắng (W-W-W)"
        ),
        Match(
            id = "M6", group = "E", teamACode = "GER", teamBCode = "JPN",
            kickoffTime = "17 June 2026, 15:00", dateLabel = "17 June 2026",
            status = "UPCOMING", scoreA = 0, scoreB = 0, minute = 0,
            stats = defaultStats,
            h2hHistory = "Cú sốc World Cup 2022 khi Nhật Bản hạ gục Đức 2-1 ở vòng bảng. / World Cup 2022 shock when Japan beat Germany 2-1 in group stage.",
            formTeamA = "Hòa, Thắng, Thắng (D-W-W)", formTeamB = "Thắng, Hòa, Thắng (W-D-W)"
        )
    )

    // Player Details
    val players = listOf(
        PlayerDetail("Lionel Messi", Color(0xFF38BDF8), 39, "Tiền đạo", "Forward", "Argentina", "Argentina", "Inter Miami", 9.5f, 2, 1, "€15M"),
        PlayerDetail("Kylian Mbappé", Color(0xFF1D4ED8), 27, "Tiền đạo", "Forward", "Pháp", "France", "Real Madrid", 9.3f, 2, 0, "€180M"),
        PlayerDetail("Nguyễn Quang Hải", Color(0xFFDC2626), 29, "Tiền vệ", "Midfielder", "Việt Nam", "Vietnam", "CAHN FC", 8.4f, 1, 0, "€450K"),
        PlayerDetail("Jude Bellingham", Color(0xFFFFFFFF), 22, "Tiền vệ", "Midfielder", "Anh", "England", "Real Madrid", 9.1f, 0, 0, "€150M"),
        PlayerDetail("Lamine Yamal", Color(0xFFEAB308), 18, "Tiền đạo", "Forward", "Tây Ban Nha", "Spain", "FC Barcelona", 8.9f, 0, 1, "€90M"),
        PlayerDetail("Jamal Musiala", Color(0xFF000000), 23, "Tiền vệ", "Midfielder", "Đức", "Germany", "Bayern Munich", 8.8f, 0, 0, "€110M")
    )

    // Highlights videos structure
    val highlights = listOf(
        HighlightVideo(
            id = "H1",
            matchTitleVI = "Argentina 3 - 2 Pháp | Chung kết tái hiện đỉnh cao",
            matchTitleEN = "Argentina 3 - 2 France | World Cup Classic Repeat",
            duration = "08:15",
            videoUrl = "https://vtvgo.vn/video/catalog/list/b05616fa-ba8e-4b93-98c5-e7860e0049ca/cong-noi-dung-19,b05616fa-ba8e-4b93-98c5-e7860e0049ca.html",
            highlightsVI = listOf("10' Mbappé ghi bàn mở tỷ số thần tốc cho Pháp từ nỗ lực phối hợp bứt phá biên trái.", "24' Lionel Messi sút phạt đền chính xác gỡ hòa sau khi Alvarez bị phạm lỗi.", "41' Alvarez dứt điểm sấm sét nâng tỷ số lên 2-1.", "74' Mbappé solo kỹ thuật vượt qua 2 hậu vệ gỡ hòa 2-2 cho Pháp.", "88' Messi lập siêu phẩm vô lê từ rìa vòng cấm mang lại chiến thắng nghẹt thở 3-2."),
            highlightsEN = listOf("10' Mbappé scoring a lightning fast counter opener down the left wing.", "24' Lionel Messi converting the spot penalty following a foul on Alvarez.", "41' Julian Alvarez striking a missile to elevate Argentina up 2-1.", "74' Mbappé matching scoreline 2-2 with a solo world-class curl.", "88' Lionel Messi scoring a legendary volley at the edge of the penalty box for the crucial 3-2 victory.")
        ),
        HighlightVideo(
            id = "H2",
            matchTitleVI = "Hoa Kỳ 2 - 1 Việt Nam | Nỗ lực quả cảm của sắc đỏ",
            matchTitleEN = "USA 2 - 1 Vietnam | Valiant RED Dragon Effort",
            duration = "06:40",
            videoUrl = "https://vtvgo.vn/video/catalog/list/b05616fa-ba8e-4b93-98c5-e7860e0049ca/cong-noi-dung-19,b05616fa-ba8e-4b93-98c5-e7860e0049ca.html",
            highlightsVI = listOf("12' Christian Pulisic dứt điểm cứa lòng trong mở điểm cho tuyển Mỹ.", "44' Folarin Balogun nhân đôi cách biệt từ đường căng ngang sắc lẹm.", "58' Cú đá phạt trực tiếp tuyệt đẹp cự ly 25m của Nguyễn Quang Hải dội xà vào lưới gỡ lại 1-2.", "85' Thủ môn Filip Nguyễn cản phá xuất sắc cú phạt đền của Pulisic bảo toàn tỷ số không cách biệt lớn."),
            highlightsEN = listOf("12' Christian Pulisic curling an elegant shot to break the deadlock.", "44' Folarin Balogun tapping in a precise low cross from the right flank.", "58' Nguyễn Quang Hải firing a magical 25-meter free kick in the absolute top corner of the net.", "85' Filip Nguyễn blocking Christian Pulisic's late penalty stretch.")
        )
    )

    // Mock transfer news
    val transfers = listOf(
        TransferNews(
            "T1",
            "Real Madrid đạt thỏa thuận gia hạn hợp đồng lịch sử với Jude Bellingham",
            "Real Madrid reaches agreement on historic extension for Jude Bellingham",
            "Gia hạn", "Contract", "Sky Sports", "10 phút trước / 10m ago",
            "Bellingham sẽ cam kết tương lai tại Bernabeu đến năm 2031 với mức lương kỷ lục.",
            "Bellingham will commit his future at Bernabeu until 2031 with record breaking salary."
        ),
        TransferNews(
            "T2",
            "Manchester City gia nhập cuộc đua giành chữ ký 'thần đồng' Lamine Yamal",
            "Manchester City enters race to sign teenage prodigy Lamine Yamal",
            "Tin đồn", "Rumor", "Fabrizio Romano", "3 giờ trước / 3h ago",
            "Mặc dù điều khoản giải phóng hợp đồng của Yamal tại Barca rất cao, Man City vẫn sẵn sàng đàm phán cực khủng.",
            "Despite Yamal's massive buyout clause at Barca, Man City is preparing a mind-blowing offer."
        ),
        TransferNews(
            "T3",
            "Nguyễn Quang Hải nhận đề nghị khủng chuyển nhượng tại Ligue 1",
            "Nguyễn Quang Hải attracts major interests from Ligue 1 side",
            "Chuyển nhượng", "Transfer", "L'Equipe", "Hôm nay / Today",
            "Màn trình diễn chói sáng tại trận đấu ra quân World Cup 2026 giúp tiền vệ Việt Nam chiếm trọn niềm tin của các câu lạc bộ Pháp.",
            "A spectacular opening match performance at the World Cup 2026 generated Ligue 1 interests for the star midfielder."
        )
    )

    // Translate dictionary for multilingual
    val translations = mapOf(
        "VI" to mapOf(
            "app_subtitle" to "Theo dõi Lịch Thi Đấu & Kết Quả",
            "tab_home" to "Trang Chủ",
            "tab_matches" to "Trận Đấu",
            "tab_standings" to "Bảng Xếp Hạng",
            "tab_community" to "Cộng Đồng",
            "tab_highlights" to "Highlights & VTV",
            "btn_ai_predict" to "AI Predict & Phân Tích",
            "loading_ai" to "Trí tuệ nhân tạo đang phân tích trận đấu...",
            "heading_ai_predict" to "🤖 Chuyên Gia AI Dự Đoán & Chiến Thuật",
            "mvp_voting" to "🗳️ Bình Chọn Cầu Thủ Xuất Sắc (MVP)",
            "vote_cast" to "Cảm ơn bạn đã bình chọn!",
            "reward_points" to "🎁 Điểm Thưởng: ",
            "points_lead" to "Cúp Điểm Thưởng Fan Tích Cực",
            "fav_team" to "Đội Bóng Yêu Thích Of Fan",
            "goals_alert_sim" to "⚡ Giả Lập Bàn Thắng Thực Tế",
            "goal_notif" to "⚽ VÀOOO! Bàn thắng được ghi!",
            "chat_placeholder" to "Nhập tin nhắn thảo luận...",
            "btn_send" to "Gửi",
            "vtv_go_action" to "Xem Trực Tiếp Trên VTV Go 📺",
            "transfer_news" to "📰 Tin Tức Chuyển Nhượng Nóng",
            "possession" to "Kiểm soát bóng",
            "shots" to "Tổng số sút",
            "shots_target" to "Sút trúng đích",
            "passes" to "Đường chuyền",
            "passes_success" to "Tỷ lệ chuyền bóng",
            "fouls" to "Phạm lỗi",
            "cards" to "Thẻ phạt (Vàng / Đỏ)",
            "favorite_setting" to "Hãy chọn đội tuyển yêu thích của bạn:",
            "save" to "Lưu Cài Đặt",
            "dark_mode" to "Tiết Kiệm Pin (Chế độ tối)",
            "lang_select" to "Ngôn ngữ / Language",
            "lineup" to "Đội Hình Ra Sân",
            "rank" to "Hạng",
            "team" to "Đội",
            "group" to "Bảng",
            "pts" to "Điểm",
            "goal_diff" to "Hiệu số",
            "player_spec" to "Thống Kê Chi Tiết Cầu Thủ",
            "recaps" to "Diễn Biến Nổi Bật Sau Trận",
            "btn_ai_tactics" to "Phân Tích AI",
            "user_nickname" to "Biệt danh fan hâm mộ"
        ),
        "EN" to mapOf(
            "app_subtitle" to "Schedules, Results & AI Analyst",
            "tab_home" to "Home",
            "tab_matches" to "Matches",
            "tab_standings" to "Standings",
            "tab_community" to "Community",
            "tab_highlights" to "Highlights & VTV",
            "btn_ai_predict" to "AI Prediction",
            "loading_ai" to "Analyzing with Gemini Tactical AI Engine...",
            "heading_ai_predict" to "🤖 AI Analytics & Prediction Board",
            "mvp_voting" to "🗳️ Vote Man of the Match (MVP)",
            "vote_cast" to "Thanks for voting!",
            "reward_points" to "🎁 Fan Reward Point: ",
            "points_lead" to "Top Level Reward Leaderboard",
            "fav_team" to "Your Favorite National Team",
            "goals_alert_sim" to "⚡ Sim Realtime Score & Goal Alerts",
            "goal_notif" to "⚽ GOOOAL! Score updated!",
            "chat_placeholder" to "Type a message in community...",
            "btn_send" to "Send",
            "vtv_go_action" to "Watch Live stream on VTV Go 📺",
            "transfer_news" to "📰 Live Transfer Rumours",
            "possession" to "Possession",
            "shots" to "Shots attempted",
            "shots_target" to "Shots on target",
            "passes" to "Total passes",
            "passes_success" to "Pass success rate",
            "fouls" to "Fouls",
            "cards" to "Cards (Yellow / Red)",
            "favorite_setting" to "Choose your supporting nation:",
            "save" to "Save Config",
            "dark_mode" to "Energy Saver (Dark Mode)",
            "lang_select" to "Language / Ngôn ngữ",
            "lineup" to "Starting Lineups",
            "rank" to "Rank",
            "team" to "Team",
            "group" to "Group",
            "pts" to "Points",
            "goal_diff" to "GD",
            "player_spec" to "Comprehensive Player Metrics",
            "recaps" to "Key Dynamic Recaps",
            "btn_ai_tactics" to "AI Tactics Board",
            "user_nickname" to "Fan Nickname"
        )
    )
}
