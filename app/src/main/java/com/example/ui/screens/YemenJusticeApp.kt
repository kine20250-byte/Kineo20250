package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.ui.draw.shadow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.*
import com.example.ui.YemenJusticeViewModel
import com.example.ui.theme.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YemenJusticeApp(
    viewModel: YemenJusticeViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // فرض تدفق التنسيق العربي من اليمين إلى اليسار تلقائياً
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val articles by viewModel.searchedArticles.collectAsStateWithLifecycle()
        val lawSearch by viewModel.lawSearchQuery.collectAsStateWithLifecycle()
        val selectedCat by viewModel.selectedCategory.collectAsStateWithLifecycle()
        
        val plaintiffs by viewModel.filteredPlaintiffs.collectAsStateWithLifecycle()
        val plaintiffSearch by viewModel.plaintiffSearchQuery.collectAsStateWithLifecycle()
        
        val prisoners by viewModel.filteredPrisoners.collectAsStateWithLifecycle()
        val prisonerSearch by viewModel.prisonerSearchQuery.collectAsStateWithLifecycle()
        
        val audits by viewModel.auditLogs.collectAsStateWithLifecycle()
        val aiReports by viewModel.aiReports.collectAsStateWithLifecycle()
        val aiLoading by viewModel.aiLoading.collectAsStateWithLifecycle()
        val aiResult by viewModel.currentAiResult.collectAsStateWithLifecycle()

        // تهيئة بيانات تجريبية وطنية إذا كان البرنامج فارغاً لتبسيط تجربة المستخدم الأولية
        LaunchedEffect(Unit) {
            if (plaintiffs.isEmpty()) {
                viewModel.addPlaintiff(
                    Plaintiff(
                        name = "ياسر أحمد عبد الله الحاشدي",
                        opponentName = "صالح محمد الهمداني",
                        caseTitle = "نزاع على قطعة أرض سكنية بالروضة",
                        submissionDate = "2026-04-10",
                        lastHearingDate = "2026-05-11",
                        status = "قيد النظر",
                        governorate = "صنعاء",
                        details = "عريضة تظلم وتنازع ملكية أرض موروثة من بيت الجد، تتجاوز الأجل القانوني للجلسات."
                    )
                )
                viewModel.addPlaintiff(
                    Plaintiff(
                        name = "أمة اللطيف علي الشرفي",
                        opponentName = "ورثة حمود يحيى الشرفي",
                        caseTitle = "قضية تركة مواريث وتصفيات عقارية",
                        submissionDate = "2026-02-01",
                        lastHearingDate = "2026-03-01",
                        status = "مفتوحة",
                        governorate = "حجة",
                        details = "تقديم لحصر الإرث وتقسيم التركات والمنازل الملحقة، الخصوم يمتنعون عن الحضور وتأخر البت التمهيدي."
                    )
                )
                viewModel.addPlaintiff(
                    Plaintiff(
                        name = "مؤسسة تهامة للتجارة والاستيراد",
                        opponentName = "مكتب المبيعات العام بالحديدة",
                        caseTitle = "مطالبة مالية وتوريدات جمركية معلقة",
                        submissionDate = "2026-05-20",
                        lastHearingDate = "2026-06-11",
                        status = "مفتوحة",
                        governorate = "الحديدة",
                        details = "نزاع تجاري على رسوم تأمين شحنة السكر وفحص معايير الجمارك، القضية نشطة."
                    )
                )
            }
            if (prisoners.isEmpty()) {
                viewModel.addPrisoner(
                    Prisoner(
                        name = "عادل حسين الوعيل",
                        charge = "حيازة وثائق مزورة لمحاضر بلدية الأراضي",
                        detentionDate = "2025-11-20",
                        status = "موقوف احتياطياً",
                        lastHearingDate = "2026-04-10",
                        prisonName = "السجن المركزي بصنعاء",
                        governorate = "صنعاء",
                        notes = "التحقيق الابتدائي مكتمل، السجين رهن الحبس الاحتياطي منذ أشهر دون إرسال ملفه للقضاء."
                    )
                )
                viewModel.addPrisoner(
                    Prisoner(
                        name = "محمد يحيى المطري",
                        charge = "الاشتباه بالمشاركة في نزاع عقاري وتخريب دورية",
                        detentionDate = "2026-06-11",
                        status = "رهن التحقيق",
                        lastHearingDate = "2026-06-11",
                        prisonName = "حجز إدارة البحث الجنائي بصنعاء",
                        governorate = "أمانة العاصمة",
                        notes = "محبوس لدى مأمور الضبط القضائي تجاوز 48 ساعة دون إحالته رسمياً للنيابة، والوضع معلق."
                    )
                )
                viewModel.addPrisoner(
                    Prisoner(
                        name = "إبراهيم ماجد السودي",
                        charge = "العجز عن سداد دين مالي تجاري متراكم (إعسار)",
                        detentionDate = "2024-03-15",
                        status = "مقضي بحكم",
                        lastHearingDate = "2026-05-20",
                        prisonName = "سجن الاحتياط بهبرة",
                        governorate = "أمانة العاصمة",
                        notes = "صدر حكم تعزيري وهو الآن معسر، ينتظر الفرز والإحالة للرعاية أو تبرعات فاعلي الخير."
                    )
                )
            }
        }

        var selectedTab by remember { mutableStateOf(0) }
        var showAddPlaintiffDialog by remember { mutableStateOf(false) }
        var showAddPrisonerDialog by remember { mutableStateOf(false) }
        
        var activeImportType by remember { mutableStateOf<String?>(null) } // "plaintiff" or "prisoner"
        var showImportDialog by remember { mutableStateOf(false) }

        var currentSelectedPlaintiff by remember { mutableStateOf<Plaintiff?>(null) }
        var currentSelectedPrisoner by remember { mutableStateOf<Prisoner?>(null) }
        var showDetailsDialog by remember { mutableStateOf(false) }

        var showAIResultDialog by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                Card(
                    shape = RoundedCornerShape(0.dp),
                    colors = CardDefaults.cardColors(containerColor = CourtGreen),
                    modifier = Modifier.fillMaxWidth().shadow(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 44.dp, start = 16.dp, end = 16.dp, bottom = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AccountBalance,
                                contentDescription = "شعار ميزان العدالة",
                                tint = JudicialGold,
                                modifier = Modifier.size(32.dp).testTag("logo_icon")
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "العدالة اليمنية YemenJusticeAGIX 🏛️",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeutralParchment,
                                    fontFamily = FontFamily.SansSerif
                                )
                                Text(
                                    text = "النظام القضائي الموحد والتحليل الوطني الذكي والأمن المتكامل",
                                    fontSize = 11.sp,
                                    color = JudicialGold,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            },
            bottomBar = {
                NavigationBar(
                    containerColor = CourtGreen,
                    tonalElevation = 8.dp,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        label = { Text("المكتبة القانونية", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Filled.MenuBook, contentDescription = "قوانين") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CourtGreen,
                            selectedTextColor = JudicialGold,
                            unselectedIconColor = NeutralParchment.copy(alpha = 0.6f),
                            unselectedTextColor = NeutralParchment.copy(alpha = 0.6f),
                            indicatorColor = JudicialGold
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        label = { Text("المدّعين", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Filled.PeopleAlt, contentDescription = "المدّعين") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CourtGreen,
                            selectedTextColor = JudicialGold,
                            unselectedIconColor = NeutralParchment.copy(alpha = 0.6f),
                            unselectedTextColor = NeutralParchment.copy(alpha = 0.6f),
                            indicatorColor = JudicialGold
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        label = { Text("المساجين", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Outlined.Gavel, contentDescription = "سجناء") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CourtGreen,
                            selectedTextColor = JudicialGold,
                            unselectedIconColor = NeutralParchment.copy(alpha = 0.6f),
                            unselectedTextColor = NeutralParchment.copy(alpha = 0.6f),
                            indicatorColor = JudicialGold
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        label = { Text("مستشار AGIX", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Filled.Psychology, contentDescription = "مستشار ذكي") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CourtGreen,
                            selectedTextColor = JudicialGold,
                            unselectedIconColor = NeutralParchment.copy(alpha = 0.6f),
                            unselectedTextColor = NeutralParchment.copy(alpha = 0.6f),
                            indicatorColor = JudicialGold
                        )
                    )
                }
            },
            containerColor = NeutralParchment
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // شاشة التحميل للذكاء الاصطناعي
                if (aiLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f))
                            .clickable(enabled = false) {},
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CourtDarkSurface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                            modifier = Modifier.padding(24.dp).border(1.dp, JudicialGold, RoundedCornerShape(12.dp))
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = JudicialGold, strokeWidth = 5.dp)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "جاري تفعيل ذكاء AGIX ودراسة القوانين اليمنية...",
                                    color = NeutralParchment,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "تحليل السجل الجنائي والمطابقة بالمواد القانونية للتفتيش",
                                    color = JudicialGold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(top = 4.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // تبديل الشاشات الأساسية
                when (selectedTab) {
                    0 -> LawLibraryScreen(
                        articles = articles,
                        searchQuery = lawSearch,
                        selectedCat = selectedCat,
                        onQueryChanged = { viewModel.updateLawSearch(it) },
                        onCategorySelected = { viewModel.selectCategory(it) }
                    )
                    1 -> PlaintiffsDashboard(
                        plaintiffs = plaintiffs,
                        searchQuery = plaintiffSearch,
                        onQueryChanged = { viewModel.updatePlaintiffSearch(it) },
                        onAddClick = { showAddPlaintiffDialog = true },
                        onImportClick = {
                            activeImportType = "plaintiff"
                            showImportDialog = true
                        },
                        onRowClick = {
                            currentSelectedPlaintiff = it
                            currentSelectedPrisoner = null
                            showDetailsDialog = true
                        },
                        viewModel = viewModel
                    )
                    2 -> PrisonersDashboard(
                        prisoners = prisoners,
                        searchQuery = prisonerSearch,
                        onQueryChanged = { viewModel.updatePrisonerSearch(it) },
                        onAddClick = { showAddPrisonerDialog = true },
                        onImportClick = {
                            activeImportType = "prisoner"
                            showImportDialog = true
                        },
                        onRowClick = {
                            currentSelectedPrisoner = it
                            currentSelectedPlaintiff = null
                            showDetailsDialog = true
                        },
                        viewModel = viewModel
                    )
                    3 -> AISupportScreen(
                        aiReports = aiReports,
                        audits = audits,
                        viewModel = viewModel
                    )
                }
            }
        }

        // --- 1. حوار إضافة مدعي ---
        if (showAddPlaintiffDialog) {
            AddPlaintiffDialog(
                onDismiss = { showAddPlaintiffDialog = false },
                onSave = {
                    viewModel.addPlaintiff(it)
                    showAddPlaintiffDialog = false
                }
            )
        }

        // --- 2. حوار إضافة سجين ---
        if (showAddPrisonerDialog) {
            AddPrisonerDialog(
                onDismiss = { showAddPrisonerDialog = false },
                onSave = {
                    viewModel.addPrisoner(it)
                    showAddPrisonerDialog = false
                }
            )
        }

        // --- 3. حوار استيراد الإكسل ---
        if (showImportDialog && activeImportType != null) {
            ImportExcelDialog(
                type = activeImportType!!,
                onDismiss = { 
                    showImportDialog = false
                    activeImportType = null
                },
                onImport = { csvText ->
                    if (activeImportType == "plaintiff") {
                        viewModel.importPlaintiffsFromCSV(csvText)
                    } else {
                        viewModel.importPrisonersFromCSV(csvText)
                    }
                    showImportDialog = false
                    activeImportType = null
                    Toast.makeText(context, "تم استقبال وهندسة السجلات بنجاح!", Toast.LENGTH_LONG).show()
                }
            )
        }

        // --- 4. حوار تفاصيل السجل والتحكم بالفصل القانوني ---
        if (showDetailsDialog) {
            DetailsInspectorDialog(
                plaintiff = currentSelectedPlaintiff,
                prisoner = currentSelectedPrisoner,
                onDismiss = { showDetailsDialog = false },
                viewModel = viewModel,
                onAiDossierSuccess = { reportText ->
                    showAIResultDialog = true
                }
            )
        }

        // --- 5. حوار نتيجة مستشار AGIX AI ---
        if (showAIResultDialog && aiResult != null) {
            AIResultDialog(
                resultText = aiResult!!,
                title = if (currentSelectedPrisoner != null) 
                    "استمارة حالة السجين: ${currentSelectedPrisoner?.name}" 
                else 
                    "تقرير تسريع قضية: ${currentSelectedPlaintiff?.name}",
                onDismiss = { showAIResultDialog = false }
            )
        }
    }
}

// ==========================================
// 1. شاشة المكننة والمكتبة القانونية الشاملة
// ==========================================
@Composable
fun LawLibraryScreen(
    articles: List<LawArticle>,
    searchQuery: String,
    selectedCat: String,
    onQueryChanged: (String) -> Unit,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf("الكل", "قوانين دستورية", "قوانين قضائية وجنائية", "قوانين مدنية وعقارية", "قوانين الأحوال الشخصية والوقف")
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // حقل البحث الذكي في الدستور وكل المواد
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChanged,
            placeholder = { Text("ابحث عن أي مادة، تهمة، أو كلمة دلالية (مثال: حبس، سرقة، بطلان)...", fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "بحث", tint = CourtGreen) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onQueryChanged("") }) {
                        Icon(Icons.Filled.Close, contentDescription = "تفريغ")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().testTag("law_search_input"),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = CourtGreen,
                unfocusedIndicatorColor = CourtGreen.copy(alpha = 0.5f)
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // فئات القوانين
        LazyRow(
            modifier = Modifier.fillMaxWidth().height(48.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(categories) { category ->
                val isSelected = category == selectedCat
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) CourtGreen else Color.White
                    ),
                    border = BorderStroke(1.dp, if (isSelected) JudicialGold else CourtGreen.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                        .clickable { onCategorySelected(category) }
                ) {
                    Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                        Text(
                            text = category,
                            color = if (isSelected) JudicialGold else CourtGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "نتائج الفهرسة الذكية (${articles.size} مادة تشريعية يمنية معبأة محلياً):",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = CourtGreen,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (articles.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.HistoryEdu, contentDescription = "خالٍ", modifier = Modifier.size(64.dp), tint = CourtGreen.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("لم يتم العثور على أي قانون متطابق. يرجى تجربة كلمات مفتاحية أخرى.", color = CourtGreen.copy(alpha = 0.6f), fontSize = 12.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(articles) { article ->
                    LawArticleCard(article = article, highlightText = searchQuery)
                }
            }
        }
    }
}

@Composable
fun LawArticleCard(article: LawArticle, highlightText: String) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth().testTag("law_article_${article.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, CourtGreen.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Bookmark, contentDescription = "قانون", tint = JudicialGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = article.lawName,
                        fontWeight = FontWeight.Bold,
                        color = CourtGreen,
                        fontSize = 13.sp
                    )
                }
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = CourtGreen.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = article.articleNumber,
                        color = CourtGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = article.content,
                fontSize = 14.sp,
                color = OnParchmentText,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(10.dp))

            HorizontalDivider(color = CourtGreen.copy(alpha = 0.05f))

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الكلمات: ${article.keywords}",
                    fontSize = 10.sp,
                    color = CourtGreenLight.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("مادة قانونية", "${article.lawName} - ${article.articleNumber}\n\n${article.content}")
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "تم نسخ المادة والفقرة القانونية!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Filled.ContentCopy, contentDescription = "نسخ المادة", tint = CourtGreen, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}


// ==========================================
// 2. واجهه وعارض المدعين مع التنبؤ بالتجاوزات
// ==========================================
@Composable
fun PlaintiffsDashboard(
    plaintiffs: List<Plaintiff>,
    searchQuery: String,
    onQueryChanged: (String) -> Unit,
    onAddClick: () -> Unit,
    onImportClick: () -> Unit,
    onRowClick: (Plaintiff) -> Unit,
    viewModel: YemenJusticeViewModel
) {
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // لوحة الأزرار واستبيان الإكسل
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "جدول المدّعين والقضايا النشطة ⚖️",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = CourtGreen
            )

            Row {
                Button(
                    onClick = onImportClick,
                    colors = ButtonDefaults.buttonColors(containerColor = CourtGreen),
                    modifier = Modifier.padding(end = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(Icons.Filled.FileUpload, contentDescription = "استيراد", tint = JudicialGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("استيراد Excel", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeutralParchment)
                }

                Button(
                    onClick = {
                        // تصدير واجهات المدعين إلى CSV متوافق مع إكسل بالترميز العربي
                        exportPlaintiffsToCSV(context, plaintiffs)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JudicialGold),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(Icons.Filled.FileDownload, contentDescription = "تصدير", tint = CourtGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تصدير Excel", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CourtGreen)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // شريط الاستعلام للمدعي
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChanged,
            placeholder = { Text("ابحث باسم المدعي، الخصم، القضية أو المحافظة...", fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "بحث", tint = CourtGreen) },
            modifier = Modifier.fillMaxWidth().testTag("plaintiff_search_input"),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = CourtGreen,
                unfocusedIndicatorColor = CourtGreen.copy(alpha = 0.5f)
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // قائمة بطاقات المدعين
        if (plaintiffs.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.PersonOff, contentDescription = "لا مدعين", modifier = Modifier.size(64.dp), tint = CourtGreen.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("لم يتم العثور على أي مدعي حالياً.", color = CourtGreen.copy(alpha = 0.6f), fontSize = 12.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(plaintiffs) { plaintiff ->
                    PlaintiffItemCard(
                        plaintiff = plaintiff,
                        onClick = { onRowClick(plaintiff) },
                        viewModel = viewModel
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // زر عائم داخلي لإضافة مدعي
        FloatingActionButton(
            onClick = onAddClick,
            containerColor = CourtGreen,
            contentColor = JudicialGold,
            modifier = Modifier.align(Alignment.End).testTag("add_plaintiff_fab")
        ) {
            Icon(Icons.Filled.Add, contentDescription = "إضافة مدعي", modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
fun PlaintiffItemCard(
    plaintiff: Plaintiff,
    onClick: () -> Unit,
    viewModel: YemenJusticeViewModel
) {
    // تحليل غير متصل لتجاوز الخصومة
    val offlineViolations = viewModel.analyzePlaintiffViolationsOffline(plaintiff)
    val hasViolation = offlineViolations.isNotEmpty()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("plaintiff_card_${plaintiff.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(
            1.dp, 
            if (hasViolation) Color(0xFFD32F2F).copy(alpha = 0.3f) else CourtGreen.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = plaintiff.name,
                    fontWeight = FontWeight.Bold,
                    color = CourtGreen,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (hasViolation) Color(0xFFFFEBEE) else CourtGreen.copy(alpha = 0.05f)
                    ),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = plaintiff.governorate,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (hasViolation) Color(0xFFD32F2F) else CourtGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "ضد: ${plaintiff.opponentName}",
                fontSize = 12.sp,
                color = OnParchmentText.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = NeutralParchment),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "القضية: ${plaintiff.caseTitle}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = CourtGreen
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "التقديم: ${plaintiff.submissionDate} | الجلسة الأخيرة: ${plaintiff.lastHearingDate}",
                        fontSize = 11.sp,
                        color = OnParchmentText.copy(alpha = 0.7f)
                    )
                }
            }

            // عرض تنبيه التجاوز القضائي التلقائي
            if (hasViolation) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFFFEBEE))
                        .padding(8.dp)
                ) {
                    offlineViolations.forEach { violation ->
                        Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(vertical = 1.dp)) {
                            Icon(Icons.Filled.Warning, contentDescription = "تنبيه", tint = Color(0xFFD32F2F), modifier = Modifier.size(14.dp).padding(top = 2.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = violation,
                                fontSize = 11.sp,
                                color = Color(0xFFD32F2F),
                                fontWeight = FontWeight.Bold,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}


// ==========================================
// 3. واجهة وعارض السجون ورقابة الحبس الاحتياطي
// ==========================================
@Composable
fun PrisonersDashboard(
    prisoners: List<Prisoner>,
    searchQuery: String,
    onQueryChanged: (String) -> Unit,
    onAddClick: () -> Unit,
    onImportClick: () -> Unit,
    onRowClick: (Prisoner) -> Unit,
    viewModel: YemenJusticeViewModel
) {
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "جرد وتفتيش الحبس الاحتياطي 🛡️",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = CourtGreen
            )

            Button(
                onClick = onImportClick,
                colors = ButtonDefaults.buttonColors(containerColor = CourtGreen),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
            ) {
                Icon(Icons.Filled.FileUpload, contentDescription = "استيراد مساجين", tint = JudicialGold, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("استيراد كشف Excel", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeutralParchment)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // شريط البحث المطور للسجناء والمحافظات
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChanged,
            placeholder = { Text("ابحث برقم/اسم السجين، التهمة، السجن أو الحالة...", fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "بحث", tint = CourtGreen) },
            modifier = Modifier.fillMaxWidth().testTag("prisoner_search_input"),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = CourtGreen,
                unfocusedIndicatorColor = CourtGreen.copy(alpha = 0.5f)
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // الإحصائيات الوطنية للنزاهة والسجون
        NationalIntegrityStatsCard(prisoners = prisoners, viewModel = viewModel)

        Spacer(modifier = Modifier.height(10.dp))

        // القائمة الرئيسية للسجناء
        if (prisoners.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.SearchOff, contentDescription = "لا مساجين", modifier = Modifier.size(64.dp), tint = CourtGreen.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("لا يوجد مطابقات في سجل السجون الجاري.", color = CourtGreen.copy(alpha = 0.6f), fontSize = 12.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(prisoners) { prisoner ->
                    PrisonerItemCard(
                        prisoner = prisoner,
                        onClick = { onRowClick(prisoner) },
                        viewModel = viewModel
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // إضافة سجين جديد محلياً
        FloatingActionButton(
            onClick = onAddClick,
            containerColor = CourtGreen,
            contentColor = JudicialGold,
            modifier = Modifier.align(Alignment.End).testTag("add_prisoner_fab")
        ) {
            Icon(Icons.Filled.PersonAdd, contentDescription = "إضافة سجين", modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
fun PrisonerItemCard(
    prisoner: Prisoner,
    onClick: () -> Unit,
    viewModel: YemenJusticeViewModel
) {
    val offlineViolations = viewModel.analyzePrisonerViolationsOffline(prisoner)
    val hasViolation = offlineViolations.isNotEmpty()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("prisoner_card_${prisoner.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(
            1.dp, 
            if (hasViolation) Color(0xFFD32F2F).copy(alpha = 0.3f) else CourtGreen.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = prisoner.name,
                    fontWeight = FontWeight.Bold,
                    color = CourtGreen,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (hasViolation) Color(0xFFFFEBEE) else CourtGreen.copy(alpha = 0.05f)
                    ),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = prisoner.status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (hasViolation) Color(0xFFD32F2F) else CourtGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            
            Text(
                text = "التهمة المسندة: ${prisoner.charge}",
                fontSize = 12.sp,
                color = OnParchmentText.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "مقرر الحبس: ${prisoner.prisonName}",
                    fontSize = 11.sp,
                    color = CourtGreenLight.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "التاريخ: ${prisoner.detentionDate}",
                    fontSize = 11.sp,
                    color = OnParchmentText.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold
                )
            }

            // إشعار فوري بالتجاوزات القانونية على السجين
            if (hasViolation) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFFFEBEE))
                        .padding(8.dp)
                ) {
                    offlineViolations.forEach { violation ->
                        Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(vertical = 1.dp)) {
                            Icon(Icons.Filled.ReportProblem, contentDescription = "انتهاك", tint = Color(0xFFD32F2F), modifier = Modifier.size(14.dp).padding(top = 2.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = violation,
                                fontSize = 11.sp,
                                color = Color(0xFFD32F2F),
                                fontWeight = FontWeight.Bold,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NationalIntegrityStatsCard(
    prisoners: List<Prisoner>,
    viewModel: YemenJusticeViewModel
) {
    var violationsCount = 0
    var totalPreTrial = 0
    prisoners.forEach {
        val violations = viewModel.analyzePrisonerViolationsOffline(it)
        if (violations.isNotEmpty()) violationsCount++
        if (it.status.contains("احتياطي") || it.status.contains("التحقيق")) totalPreTrial++
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CourtGreen),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "مؤشرات النزاهة الوطنية للحبس الاحتياطي بالجمهورية 📊",
                color = JudicialGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text("${prisoners.size}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = NeutralParchment)
                    Text("مجموع السجناء", fontSize = 10.sp, color = NeutralParchment.copy(alpha = 0.7f))
                }
                Box(modifier = Modifier.width(1.dp).height(30.dp).background(NeutralParchment.copy(alpha = 0.2f)))
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text("$totalPreTrial", fontSize = 18.sp, fontWeight = FontWeight.Black, color = NeutralParchment)
                    Text("الحبس الاحتياطي", fontSize = 10.sp, color = NeutralParchment.copy(alpha = 0.7f))
                }
                Box(modifier = Modifier.width(1.dp).height(30.dp).background(NeutralParchment.copy(alpha = 0.2f)))
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text(
                        text = "$violationsCount",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = if (violationsCount > 0) Color(0xFFFF5252) else JudicialGold
                    )
                    Text("تجاوزات مرصودة", fontSize = 10.sp, color = NeutralParchment.copy(alpha = 0.7f))
                }
            }
        }
    }
}


// ==========================================
// 4. تقارير الذكاء الاصطناعي ومراقبة النزاهة
// ==========================================
@Composable
fun AISupportScreen(
    aiReports: List<AIReport>,
    audits: List<AdminAudit>,
    viewModel: YemenJusticeViewModel
) {
    var selectedSubTab by remember { mutableStateOf(0) } // 0 -> AI Reports, 1 -> Compliance Audit Trail
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // مفاتيح التبويب الفرعي
        TabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = Color.Transparent,
            contentColor = CourtGreen,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedSubTab]),
                    color = CourtGreen
                )
            }
        ) {
            Tab(
                selected = selectedSubTab == 0,
                onClick = { selectedSubTab = 0 },
                text = { Text("التقارير والاستمارات الذكية المحفوظة", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedSubTab == 1,
                onClick = { selectedSubTab = 1 },
                text = { Text("سجل التدقيق والنزاهة غير القابل للتعديل", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedSubTab == 0) {
            // قائمة تقارير الذكاء الاصطناعي
            if (aiReports.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Psychology, contentDescription = "لا تقارير", modifier = Modifier.size(64.dp), tint = CourtGreen.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("لا يوجد تقارير ذكية محفوظة حالياً. قم بإعداد تقارير من السجناء والمدعين.", color = CourtGreen.copy(alpha = 0.6f), fontSize = 12.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(aiReports) { report ->
                        SavedAIReportCard(report = report, context = context, onDelete = { viewModel.deleteReport(report) })
                    }
                }
            }
        } else {
            // سجل العمليات والنزاهة للتفتيش القضائي
            Column(modifier = Modifier.fillMaxSize().weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "رقابة تدفق البحث والعمليات الحكومية 🛡️",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CourtGreen
                    )
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CourtGreen),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "نظام مشفر ومؤمن مسبقاً",
                            fontSize = 9.sp,
                            color = JudicialGold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (audits.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("سجل التدقيق فارغ حالياً.", color = OnParchmentText.copy(alpha = 0.6f))
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(audits) { audit ->
                            AuditRowItem(audit = audit)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SavedAIReportCard(report: AIReport, context: Context, onDelete: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CourtGreen.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = report.reportTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = CourtGreen
                    )
                    Text(
                        text = "المواطن: ${report.targetName} | التاريخ: ${report.createdDate}",
                        fontSize = 11.sp,
                        color = OnParchmentText.copy(alpha = 0.6f)
                    )
                }

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (report.targetType == "سجين") Color(0xFFFFEBEE) else CourtGreen.copy(alpha = 0.05f)
                    ),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = report.targetType,
                        fontSize = 10.sp,
                        color = if (report.targetType == "سجين") Color(0xFFD32F2F) else CourtGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = report.generatedText,
                fontSize = 13.sp,
                color = OnParchmentText,
                maxLines = if (expanded) Int.MAX_VALUE else 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (expanded) "أغلق التفاصيل" else "إقرأ التقرير كاملاً...",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = JudicialGoldDark,
                    modifier = Modifier.clickable { expanded = !expanded }
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // نسخ
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("تقرير AGIX", report.generatedText)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "تم نسخ التقرير الذكي للحافظة!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Filled.ContentCopy, contentDescription = "نسخ", tint = CourtGreen, modifier = Modifier.size(16.dp))
                    }
                    
                    // تصدير Word
                    IconButton(
                        onClick = {
                            exportAIResultToWord(context, report.reportTitle, report.generatedText)
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Filled.FileDownload, contentDescription = "تنزيل وورد", tint = JudicialGold, modifier = Modifier.size(18.dp))
                    }

                    // حذف
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Filled.DeleteOutline, contentDescription = "حذف التقرير", tint = Color(0xFFD32F2F), modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AuditRowItem(audit: AdminAudit) {
    val formatter = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }
    val timeStr = formatter.format(Date(audit.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CourtGreen.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        when (audit.actionType) {
                            "إضافة مدعي", "حبس موقوف" -> Color(0xFF4CAF50)
                            "تعديل مدعي", "تعديل سجين" -> Color(0xFFFFC107)
                            "حذف مدعي", "إفراج سجين" -> Color(0xFFFF5252)
                            "بحث قانوني" -> JudicialGold
                            else -> CourtGreenLight
                        }
                    )
            )
            
            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "[${audit.actionType}]",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = CourtGreen
                    )
                    Text(
                        text = "الدور: ${audit.operatorRole} • $timeStr",
                        fontSize = 9.sp,
                        color = OnParchmentText.copy(alpha = 0.5f)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = audit.details,
                    fontSize = 11.sp,
                    color = OnParchmentText,
                    lineHeight = 16.sp
                )
            }
        }
    }
}


// ==========================================
// 5. حوارات المدعين والمساجين والإكسل والذكاء الاصطناعي
// ==========================================

@Composable
fun AddPlaintiffDialog(
    onDismiss: () -> Unit,
    onSave: (Plaintiff) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var opponent by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var subDate by remember { mutableStateOf("2026-06-13") }
    var lastHearing by remember { mutableStateOf("2026-06-13") }
    var status by remember { mutableStateOf("مفتوحة") }
    var gov by remember { mutableStateOf("أمانة العاصمة") }
    var details by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().border(1.dp, CourtGreen.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "قيد عريضة ادعاء ومخاصمة جديدة ⚖️",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = CourtGreen,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم المدعي الكامل", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth().testTag("plaintiff_input_name")
                )

                OutlinedTextField(
                    value = opponent,
                    onValueChange = { opponent = it },
                    label = { Text("اسم المدعّى عليه الكامل", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("موضوع الخصومة والقضية", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = subDate,
                        onValueChange = { subDate = it },
                        label = { Text("تاريخ التقديم (YYYY-MM-DD)", fontSize = 9.sp) },
                        modifier = Modifier.weight(1f).padding(end = 4.dp)
                    )
                    OutlinedTextField(
                        value = lastHearing,
                        onValueChange = { lastHearing = it },
                        label = { Text("تاريخ آخر جلسة", fontSize = 9.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = gov,
                        onValueChange = { gov = it },
                        label = { Text("المحافظة", fontSize = 10.sp) },
                        modifier = Modifier.weight(1f).padding(end = 4.dp)
                    )
                    OutlinedTextField(
                        value = status,
                        onValueChange = { status = it },
                        label = { Text("الحالة (مفتوحة/قيد النظر/محكومة)", fontSize = 9.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    label = { Text("شرح تفصيلي للحق والخصم المعتمد", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("إلغاء", color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank() && title.isNotBlank()) {
                                onSave(
                                    Plaintiff(
                                        name = name,
                                        opponentName = opponent,
                                        caseTitle = title,
                                        submissionDate = subDate,
                                        lastHearingDate = lastHearing,
                                        status = status,
                                        governorate = gov,
                                        details = details
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CourtGreen)
                    ) {
                        Text("قيد وحفظ القضية", color = NeutralParchment, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AddPrisonerDialog(
    onDismiss: () -> Unit,
    onSave: (Prisoner) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var charge by remember { mutableStateOf("") }
    var detentionDate by remember { mutableStateOf("2026-06-13") }
    var status by remember { mutableStateOf("موقوف احتياطياً") }
    var lastHearing by remember { mutableStateOf("2026-06-13") }
    var prisonName by remember { mutableStateOf("السجن المركزي") }
    var governorate by remember { mutableStateOf("صنعاء") }
    var notes by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().border(1.dp, CourtGreen.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "قيد موقوف احتياطي / سجين بالنظام 🛡️",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = CourtGreen,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم السجين الرباعي", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth().testTag("prisoner_input_name")
                )

                OutlinedTextField(
                    value = charge,
                    onValueChange = { charge = it },
                    label = { Text("التهمة المسندة بدورية القبض", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = detentionDate,
                        onValueChange = { detentionDate = it },
                        label = { Text("تاريخ الإيداع (YYYY-MM-DD)", fontSize = 9.sp) },
                        modifier = Modifier.weight(1f).padding(end = 4.dp)
                    )
                    OutlinedTextField(
                        value = lastHearing,
                        onValueChange = { lastHearing = it },
                        label = { Text("تاريخ آخر جلسة محاكمة", fontSize = 9.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = prisonName,
                        onValueChange = { prisonName = it },
                        label = { Text("مكان الاحتجاز / السجن", fontSize = 10.sp) },
                        modifier = Modifier.weight(1f).padding(end = 4.dp)
                    )
                    OutlinedTextField(
                        value = governorate,
                        onValueChange = { governorate = it },
                        label = { Text("المحافظة", fontSize = 10.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = status,
                    onValueChange = { status = it },
                    label = { Text("الحالة (موقوف احتياطياً / رهن التحقيق / مقضي بحكم)", fontSize = 10.sp) },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات إجرائية وصحية", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("إلغاء", color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank() && charge.isNotBlank()) {
                                onSave(
                                    Prisoner(
                                        name = name,
                                        charge = charge,
                                        detentionDate = detentionDate,
                                        status = status,
                                        lastHearingDate = lastHearing,
                                        prisonName = prisonName,
                                        governorate = governorate,
                                        notes = notes
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CourtGreen)
                    ) {
                        Text("حفظ وقيد", color = NeutralParchment, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ImportExcelDialog(
    type: String,
    onDismiss: () -> Unit,
    onImport: (String) -> Unit
) {
    // تزويد المستخدم بقوالب جاهزة بالضغط بنقرة واحدة لتبسيط وتسهيل تجربة الاستيراد والمحاكاة التامة
    val templateText = if (type == "plaintiff") {
        """مطلق رجب الصنعاني,حمود الهمداني,نزاع عقاري بسوفيل,2026-03-01,2026-04-01,مفتوحة,صنعاء,قضية أرض قديمة وركود منذ شهرين دون جلسة تفتيش
فاطمة عبد الله ردمان,صندوق الرعاية الاجتماعي,حرمان من الرعاية المستحقة,2026-01-10,2026-02-15,قيد النظر,تعز,تأخر مفرط للمراجعات والدعوى معروضة من 100 يوم
أحمد سيف حيدر,شركة الاستكشافات البترولية,تعويض عمالي إنهاء عقد,2026-05-15,2026-06-11,مستأنفة,مأرب,دعوى تعويض مسرح عمالي وجلسة مؤجلة"""
    } else {
        """زياد عبد الوهاب المحيا,إصدار شيك بدون رصيد تجاري,2026-05-01,موقوف احتياطياً,2026-05-01,سجن الاحتياط بهبرة,صنعاء,تعدى 30 يوما حبس دون أي إحالة للقاضي
خالد نجيب المعمري,النزاع في الشارع العام وقذف ببلطة,2025-10-15,موقوف احتياطياً,2025-10-15,حجز قسم شرطة جمال الجميل,أمانة العاصمة,موقوف احتياطيا منذ 240 يوم دون حكم أو إحالته للمحكمة العليا
مطيع حزام الخولاني,مخالفة أنظمة الصيد في البيئة المائية,2026-06-12,رهن التحقيق,2026-06-12,قسم البحث بالحديدة,الحديدة,توقيف احترازي"""
    }

    var csvInput by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().border(1.dp, CourtGreen.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "استيراد ملف Excel قضائي للـ${if (type == "plaintiff") "مدعين" else "سجناء"} 📂",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = CourtGreen,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))
                
                Text(
                    text = "تنسيق الأعمدة (تفصلها فاصلة أو علامة جدولة):\n${if (type == "plaintiff") "الاسم، الخصم، الموضوع، تاريخ التقديم، آخر جلسة، الحالة، المحافظة، تفاصيل" else "الاسم، التهمة، ت الحبس، الحالة، ت آخر جلسة، السجن، المحافظة، ملاحظات"}",
                    fontSize = 10.sp,
                    color = OnParchmentText.copy(alpha = 0.6f),
                    lineHeight = 14.sp,
                    modifier = Modifier.background(NeutralParchment).padding(8.dp).fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = csvInput,
                    onValueChange = { csvInput = it },
                    placeholder = { Text("مثال:\n$templateText", fontSize = 11.sp, color = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .testTag("excel_import_textarea"),
                    textStyle = TextStyle(fontSize = 12.sp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // زر لتعبئة القالب بنقرة واحدة
                TextButton(
                    onClick = { csvInput = templateText },
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Text("⚡ تعبئة القالب النموذجي اليمني التلقائي", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = JudicialGoldDark)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("إلغاء", color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (csvInput.isNotBlank()) {
                                onImport(csvInput)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CourtGreen)
                    ) {
                        Text("تحليل وفهرسة السجلات", color = NeutralParchment, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun DetailsInspectorDialog(
    plaintiff: Plaintiff?,
    prisoner: Prisoner?,
    onDismiss: () -> Unit,
    viewModel: YemenJusticeViewModel,
    onAiDossierSuccess: (String) -> Unit
) {
    val context = LocalContext.current
    val title = prisoner?.name ?: plaintiff?.name ?: "تفاصيل السجل"
    val subtitle = if (prisoner != null) "بطاقة وبراءة احتجاز سجين" else "ملف عريضة المدّعي"

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().border(1.dp, CourtGreen.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CourtGreen)
                        Text(subtitle, fontSize = 11.sp, color = JudicialGoldDark)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "أغلق")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val offlineViolations = if (prisoner != null) {
                    viewModel.analyzePrisonerViolationsOffline(prisoner)
                } else if (plaintiff != null) {
                    viewModel.analyzePlaintiffViolationsOffline(plaintiff)
                } else {
                    emptyList()
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 250.dp)
                ) {
                    if (prisoner != null) {
                        item {
                            DetailRowLabel(label = "التهمة المسندة:", value = prisoner.charge)
                            DetailRowLabel(label = "تاريخ الحبس الاحتياطي:", value = prisoner.detentionDate)
                            DetailRowLabel(label = "مكان الحجز والسجن:", value = "${prisoner.prisonName} • محافظة ${prisoner.governorate}")
                            DetailRowLabel(label = "الحالة القضائية الحالية:", value = prisoner.status)
                            DetailRowLabel(label = "تاريخ آخر جلسة قضاء:", value = prisoner.lastHearingDate)
                            DetailRowLabel(label = "ملاحظات إجرائية:", value = prisoner.notes)
                        }
                    } else if (plaintiff != null) {
                        item {
                            DetailRowLabel(label = "المدّعى عليه بالملف:", value = plaintiff.opponentName)
                            DetailRowLabel(label = "موضوع وعريضة القضية:", value = plaintiff.caseTitle)
                            DetailRowLabel(label = "تاريخ تقديم القضية للتوثيق:", value = plaintiff.submissionDate)
                            DetailRowLabel(label = "تاريخ آخر جلسة مرافعات:", value = plaintiff.lastHearingDate)
                            DetailRowLabel(label = "حالة القضية والخصومة:", value = plaintiff.status)
                            DetailRowLabel(label = "محافظة الاختصاص القضائي:", value = plaintiff.governorate)
                            DetailRowLabel(label = "الوقائع والشرح الإضافي:", value = plaintiff.details)
                        }
                    }
                }

                // عرض الانتهاكات والتجاوزات المرصودة باللون الأحمر
                if (offlineViolations.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("التجاوزات والانتهاكات المرصودة بالرقابة التلقائية:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                    Spacer(modifier = Modifier.height(4.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFFEBEE))
                            .padding(8.dp)
                    ) {
                        offlineViolations.forEach { violation ->
                            Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(vertical = 2.dp)) {
                                Icon(Icons.Filled.Dangerous, contentDescription = "تجاوز", tint = Color(0xFFD32F2F), modifier = Modifier.size(14.dp).padding(top = 2.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(violation, fontSize = 11.sp, color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold, lineHeight = 16.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // الأزرار التفاعلية لحساب التقرير والطباعة بالوورد والذكاء الاصطناعي
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // زر استشارة الذكاء الاصطناعي AGIX AI
                    Button(
                        onClick = {
                            if (prisoner != null) {
                                viewModel.askGeminiForPrisonerDossier(prisoner, offlineViolations) {
                                    onDismiss()
                                    onAiDossierSuccess(it)
                                }
                            } else if (plaintiff != null) {
                                viewModel.askGeminiForPlaintiffDossier(plaintiff, offlineViolations) {
                                    onDismiss()
                                    onAiDossierSuccess(it)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CourtGreen),
                        modifier = Modifier.weight(1f).testTag("ai_audit_button"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Filled.Psychology, contentDescription = "AGIX AI", tint = JudicialGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (prisoner != null) "توليد استمارة AI" else "تحليل القضية بالـ AI",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeutralParchment
                        )
                    }

                    // زر تصدير Word .doc
                    Button(
                        onClick = {
                            val docTitle = if (prisoner != null) "استمارة فحص حالة سجين: ${prisoner.name}" else "عريضة وتفاصيل قضية: ${plaintiff?.name}"
                            val docContent = buildString {
                                if (prisoner != null) {
                                    append("اسم المعتقل: ${prisoner.name}\n")
                                    append("التهمة الجنائية: ${prisoner.charge}\n")
                                    append("تاريخ الحبس: ${prisoner.detentionDate}\n")
                                    append("سجن مقر التوقيف: ${prisoner.prisonName} (${prisoner.governorate})\n")
                                    append("حالة سير القضية: ${prisoner.status}\n")
                                    append("تاريخ آخر جلسة: ${prisoner.lastHearingDate}\n")
                                    append("ملاحظات المفتش: ${prisoner.notes}\n\n")
                                } else if (plaintiff != null) {
                                    append("اسم المدعي: ${plaintiff.name}\n")
                                    append("المدعى عليه: ${plaintiff.opponentName}\n")
                                    append("موضوع الخصومة: ${plaintiff.caseTitle}\n")
                                    append("تاريخ قيد العريضة: ${plaintiff.submissionDate}\n")
                                    append("جلسة القضاء الأخيرة: ${plaintiff.lastHearingDate}\n")
                                    append("حالة الدعوى: ${plaintiff.status}\n")
                                    append("محافظة الاختصاص: ${plaintiff.governorate}\n")
                                    append("تفاصيل وقائع الحق: ${plaintiff.details}\n\n")
                                }
                                if (offlineViolations.isNotEmpty()) {
                                    append("التجاوزات والانتهاكات الإدارية المرصودة بالجمهورية:\n")
                                    offlineViolations.forEach { append("- $it\n") }
                                } else {
                                    append("حالة السجل تتبع المسار الإجرائي الطبيعي حتى تاريخه.\n")
                                }
                            }
                            exportAIResultToWord(context, docTitle, docContent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = JudicialGold),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Filled.FileDownload, contentDescription = "تصدير Word", tint = CourtGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("تصدير ملف Word", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CourtGreen)
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRowLabel(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = CourtGreenLight)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 13.sp, color = OnParchmentText, lineHeight = 18.sp)
        HorizontalDivider(color = CourtGreen.copy(alpha = 0.05f), modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
fun AIResultDialog(
    resultText: String,
    title: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth(0.95f).border(1.dp, CourtGreen.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Psychology, contentDescription = "AGIX AI", tint = JudicialGold, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "مستند AGIX AI التوجيهي 📜",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = CourtGreen
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "إغلاق")
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(text = title, fontSize = 11.sp, color = JudicialGoldDark, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(10.dp))

                // التقرير القانوني الذكي المصاغ
                Card(
                    colors = CardDefaults.cardColors(containerColor = NeutralParchment),
                    modifier = Modifier.weight(1f, fill = false).fillMaxWidth().border(1.dp, CourtGreen.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                ) {
                    LazyColumn(modifier = Modifier.padding(12.dp)) {
                        item {
                            Text(
                                text = resultText,
                                fontSize = 13.sp,
                                color = OnParchmentText,
                                lineHeight = 20.sp,
                                modifier = Modifier.testTag("ai_result_text")
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // نسخ التقرير
                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("تقرير المستشار القضائي", resultText)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "تم نسخ التقرير بالكامل لصقه في وورد!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CourtGreen),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.ContentCopy, contentDescription = "نسخ", tint = JudicialGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("نسخ النص", fontSize = 11.sp, color = NeutralParchment)
                    }

                    // تصدير كملف Word وحفظه
                    Button(
                        onClick = {
                            exportAIResultToWord(context, title, resultText)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = JudicialGold),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.FileDownload, contentDescription = "وورد", tint = CourtGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("تصدير Word (.doc)", fontSize = 11.sp, color = CourtGreen)
                    }
                }
            }
        }
    }
}


// ==========================================
// 6. دوال تصدير ومشاركة ملفات الإكسل والوورد
// ==========================================

/**
 * تصدير قائمة المدعين والخصوم إلى ملف CSV مهيأ بالكامل لـ Excel باللغة العربية مع BOM
 */
fun exportPlaintiffsToCSV(context: Context, plaintiffs: List<Plaintiff>) {
    val csvHeader = "\uFEFFالمدعي,المدعى عليه,موضوع القضية,تاريخ التقديم,تاريخ آخر جلسة,حالة القضية,المحافظة,التفاصيل والوقائع الجنائية\n"
    val csvBody = plaintiffs.joinToString("\n") { p ->
        // تنظيف الخانات للفاصلة
        val name = p.name.replace(",", " - ")
        val opponent = p.opponentName.replace(",", " - ")
        val title = p.caseTitle.replace(",", " - ")
        val details = p.details.replace(",", " - ").replace("\n", " ")
        "$name,$opponent,$title,${p.submissionDate},${p.lastHearingDate},${p.status},${p.governorate},$details"
    }
    
    val fullCSV = csvHeader + csvBody
    
    try {
        val cacheFile = File(context.cacheDir, "YemenJustice_Plaintiffs_${System.currentTimeMillis()}.csv")
        FileOutputStream(cacheFile).use { fos ->
            fos.write(fullCSV.toByteArray(Charsets.UTF_8))
        }

        val fileUri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            cacheFile
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/comma-separated-values"
            putExtra(Intent.EXTRA_SUBJECT, "جدول المدعين الموحد - اليمن للعدالة")
            putExtra(Intent.EXTRA_STREAM, fileUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "شارك كشف المدعين كملف Excel"))
    } catch (e: Exception) {
        Toast.makeText(context, "فشل تصدير كشف الإكسل: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
    }
}

/**
 * تصدير استمارات السجناء والتقارير الذكية إلى ملف Word (.doc) بترميز HTML غني بالتنسيق العربي
 * حيث يقوم مايكروسوفت وورد وجوجل درايف بفتحه بشكل منسق ورائع ومطابق للتصاميم القضائية
 */
fun exportAIResultToWord(context: Context, documentTitle: String, contentText: String) {
    val formattedDate = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(Date())
    
    // إنشاء كود HTML متقدم متوافق مع وورد لتوفير هوية بصرية مذهلة للقاضي والمحقق اليمني
    val htmlDocument = """
        <!DOCTYPE html>
        <html dir="rtl" lang="ar">
        <head>
            <meta charset="utf-8">
            <style>
                body {
                    font-family: 'Times New Roman', serif, Arial;
                    padding: 30px;
                    background-color: #FAF7F2;
                    color: #1E2822;
                    line-height: 1.6;
                }
                .container {
                    max-width: 800px;
                    margin: 0 auto;
                    background-color: #ffffff;
                    border: 2px solid #163E24;
                    border-radius: 8px;
                    padding: 24px;
                    box-shadow: 0 4px 6px rgba(0,0,0,0.1);
                }
                .header-table {
                    width: 100%;
                    border-collapse: collapse;
                    margin-bottom: 20px;
                }
                .crown {
                    font-size: 24px;
                    text-align: center;
                    color: #C5952B;
                    font-weight: bold;
                }
                h1 {
                    color: #163E24;
                    border-bottom: 3px solid #C5952B;
                    padding-bottom: 12px;
                    text-align: center;
                    font-size: 22px;
                    margin-top: 5px;
                }
                h2 {
                    color: #265A39;
                    font-size: 16px;
                    border-right: 4px solid #C5952B;
                    padding-right: 10px;
                    margin-top: 25px;
                }
                .meta-section {
                    background-color: #F0EDE6;
                    padding: 10px 15px;
                    border-radius: 4px;
                    font-size: 13px;
                    margin-bottom: 20px;
                    border-right: 5px solid #163E24;
                }
                .content-box {
                    font-size: 15px;
                    text-align: justify;
                    white-space: pre-wrap;
                    background-color: #FFFFFF;
                    padding: 15px;
                    border: 1px solid #E5E1D8;
                    border-radius: 6px;
                }
                .violation-highlight {
                    background-color: #FFF2F2;
                    border-right: 5px solid #D32F2F;
                    padding: 12px;
                    color: #D32F2F;
                    font-weight: bold;
                    margin: 15px 0;
                    border-radius: 4px;
                }
                .footer {
                    text-align: center;
                    font-size: 11px;
                    color: #666666;
                    margin-top: 40px;
                    border-top: 1px solid #E5E1D8;
                    padding-top: 15px;
                }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="crown">🏛️</div>
                <h1>الجمهورية اليمنية<br>مجلس القضاء الأعلى ووزارة العدل<br><span style="font-size:14px;color:#C5952B;">نظام التحقيق والرقابة الذكية AGIX</span></h1>
                
                <div class="meta-section">
                    <strong>عنوان المستند:</strong> $documentTitle <br>
                    <strong>تاريخ تحرير التقرير:</strong> $formattedDate <br>
                    <strong>جهة الإصدار والاعتماد:</strong> دائرة التفتيش القضائي والنيابة العامة الموحدة باليمن
                </div>

                <h2>منطوق وأحكام التقرير والتقصّي:</h2>
                <div class="content-box">
                    ${contentText.replace("\n", "<br>")}
                </div>

                <div class="footer">
                    هذا مستند رسمي صادر الكترونياً ومعالج بالذكاء الاصطناعي لمطابقة دستورية السجون والمدعين بالجمهورية اليمنية.<br>
                    <strong>برنامج اليمن للعدالة الموحد YemenJusticeAGIX • حقوق الملكية محفوظة 2026</strong>
                </div>
            </div>
        </body>
        </html>
    """.trimIndent()

    try {
        // كتابة الملف بلاحقة .doc
        val cacheFile = File(context.cacheDir, "YemenJustice_Report_${System.currentTimeMillis()}.doc")
        FileOutputStream(cacheFile).use { fos ->
            fos.write(htmlDocument.toByteArray(Charsets.UTF_8))
        }

        val fileUri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            cacheFile
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/msword"
            putExtra(Intent.EXTRA_SUBJECT, documentTitle)
            putExtra(Intent.EXTRA_STREAM, fileUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "شارك التقرير القضائي كملف Word"))
    } catch (e: Exception) {
        Toast.makeText(context, "فشل تصدير ملف Word: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
    }
}
