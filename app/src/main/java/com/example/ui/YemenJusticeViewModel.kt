package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiLegalClient
import com.example.data.database.LawDatabase
import com.example.data.model.*
import com.example.data.repository.LawRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class YemenJusticeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LawRepository
    
    init {
        val database = LawDatabase.getDatabase(application)
        repository = LawRepository(database.lawDao())
        
        // التحقق الذاتي من تعبئة المكتبة وتغذيتها في الخلفية عند بدء التشغيل
        viewModelScope.launch {
            repository.checkAndSeedDatabase()
        }
    }

    // --- تدفق القوانين والأدلة في واجهات التطبيق ---
    val allArticles: StateFlow<List<LawArticle>> = repository.allArticles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // تصفية وبحث القوانين
    private val _lawSearchQuery = MutableStateFlow("")
    val lawSearchQuery = _lawSearchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("الكل")
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _searchedArticles = MutableStateFlow<List<LawArticle>>(emptyList())
    val searchedArticles = _searchedArticles.asStateFlow()

    // --- تدفق عارض واجهة المدعين (Plaintiffs) ---
    private val _plaintiffSearchQuery = MutableStateFlow("")
    val plaintiffSearchQuery = _plaintiffSearchQuery.asStateFlow()

    val filteredPlaintiffs: StateFlow<List<Plaintiff>> = _plaintiffSearchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.allPlaintiffs
            } else {
                repository.searchPlaintiffs(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- تدفق عارض واجهة المساجين (Prisoners) ---
    private val _prisonerSearchQuery = MutableStateFlow("")
    val prisonerSearchQuery = _prisonerSearchQuery.asStateFlow()

    val filteredPrisoners: StateFlow<List<Prisoner>> = _prisonerSearchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.allPrisoners
            } else {
                repository.searchPrisoners(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- تدفق سجل التدقيق والنزاهة ---
    val auditLogs: StateFlow<List<AdminAudit>> = repository.allAudits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- تدفق تقارير الذكاء الاصطناعي السابقة ---
    val aiReports: StateFlow<List<AIReport>> = repository.allAIReports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // حالات التفاعل والاستشارات الجارية
    private val _aiLoading = MutableStateFlow(false)
    val aiLoading = _aiLoading.asStateFlow()

    private val _currentAiResult = MutableStateFlow<String?>(null)
    val currentAiResult = _currentAiResult.asStateFlow()

    init {
        // تحديث البحث الأولي للقوانين
        viewModelScope.launch {
            combine(_lawSearchQuery, _selectedCategory, allArticles) { query, cat, articles ->
                if (query.isBlank() && cat == "الكل") {
                    articles
                } else {
                    articles.filter {
                        (cat == "الكل" || it.category == cat) &&
                        (it.content.contains(query, true) || 
                         it.keywords.contains(query, true) || 
                         it.lawName.contains(query, true) || 
                         it.articleNumber.contains(query, true))
                    }
                }
            }.collect {
                _searchedArticles.value = it
            }
        }
    }

    fun updateLawSearch(query: String) {
        _lawSearchQuery.value = query
        viewModelScope.launch {
            repository.logAudit("بحث قانوني", "قام المستخدم بالاستعلام واستخراج القوانين للعبارة: '$query'", "مستخدم")
        }
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun updatePlaintiffSearch(query: String) {
        _plaintiffSearchQuery.value = query
    }

    fun updatePrisonerSearch(query: String) {
        _prisonerSearchQuery.value = query
    }

    // --- عمليات التعديل والتحكم للمدعين ---
    fun addPlaintiff(plaintiff: Plaintiff) = viewModelScope.launch {
        repository.insertPlaintiff(plaintiff)
    }

    fun updatePlaintiff(plaintiff: Plaintiff) = viewModelScope.launch {
        repository.updatePlaintiff(plaintiff)
    }

    fun deletePlaintiff(plaintiff: Plaintiff) = viewModelScope.launch {
        repository.deletePlaintiff(plaintiff)
    }

    fun clearPlaintiffs() = viewModelScope.launch {
        repository.clearPlaintiffs()
    }

    // --- عمليات التعديل والتحكم للمساجين ---
    fun addPrisoner(prisoner: Prisoner) = viewModelScope.launch {
        repository.insertPrisoner(prisoner)
    }

    fun updatePrisoner(prisoner: Prisoner) = viewModelScope.launch {
        repository.updatePrisoner(prisoner)
    }

    fun deletePrisoner(prisoner: Prisoner) = viewModelScope.launch {
        repository.deletePrisoner(prisoner)
    }

    fun clearPrisoners() = viewModelScope.launch {
        repository.clearPrisoners()
    }

    fun deleteReport(report: AIReport) = viewModelScope.launch {
        repository.deleteAIReport(report)
    }

    // --- محاكي استيراد وتفسير ملفات Excel/CSV للمدعين ---
    fun importPlaintiffsFromCSV(content: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val lines = content.split("\n")
            val plaintiffs = mutableListOf<Plaintiff>()
            
            for (line in lines) {
                if (line.isBlank() || line.startsWith("المدعي") || line.startsWith("name")) continue
                val tokens = splitCSVLine(line)
                if (tokens.size >= 3) {
                    val name = tokens[0].trim()
                    val opponent = tokens.getOrNull(1)?.trim() ?: "غير محدد"
                    val caseTitle = tokens.getOrNull(2)?.trim() ?: "نزاع عام"
                    val subDate = tokens.getOrNull(3)?.trim() ?: getTodayDateString()
                    val lastHearing = tokens.getOrNull(4)?.trim() ?: getTodayDateString()
                    val status = tokens.getOrNull(5)?.trim() ?: "مفتوحة"
                    val gov = tokens.getOrNull(6)?.trim() ?: "صنعاء"
                    val details = tokens.getOrNull(7)?.trim() ?: "تم الاستيراد التلقائي وتحليل ملفات الدعوى."

                    plaintiffs.add(
                        Plaintiff(
                            name = name,
                            opponentName = opponent,
                            caseTitle = caseTitle,
                            submissionDate = subDate,
                            lastHearingDate = lastHearing,
                            status = status,
                            governorate = gov,
                            details = details
                        )
                    )
                }
            }
            if (plaintiffs.isNotEmpty()) {
                repository.insertPlaintiffs(plaintiffs)
            }
        } catch (e: Exception) {
            repository.logAudit("فشل استيراد مدعين", "حدث خطأ أثناء معالجة ملف الإكسل: ${e.localizedMessage}", "نظام")
        }
    }

    // --- محاكي استيراد وتفسير ملفات Excel/CSV للمساجين ---
    fun importPrisonersFromCSV(content: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val lines = content.split("\n")
            val prisoners = mutableListOf<Prisoner>()
            
            for (line in lines) {
                if (line.isBlank() || line.startsWith("السجين") || line.startsWith("name")) continue
                val tokens = splitCSVLine(line)
                if (tokens.size >= 3) {
                    val name = tokens[0].trim()
                    val charge = tokens.getOrNull(1)?.trim() ?: "على ذمة التحقيق"
                    val detDate = tokens.getOrNull(2)?.trim() ?: getTodayDateString()
                    val status = tokens.getOrNull(3)?.trim() ?: "موقوف احتياطياً"
                    val lastHearing = tokens.getOrNull(4)?.trim() ?: getTodayDateString()
                    val prison = tokens.getOrNull(5)?.trim() ?: "السجن المركزي"
                    val gov = tokens.getOrNull(6)?.trim() ?: "أمانة العاصمة"
                    val notes = tokens.getOrNull(7)?.trim() ?: "استيراد جدولة رعاية النيابة."

                    prisoners.add(
                        Prisoner(
                            name = name,
                            charge = charge,
                            detentionDate = detDate,
                            status = status,
                            lastHearingDate = lastHearing,
                            prisonName = prison,
                            governorate = gov,
                            notes = notes
                        )
                    )
                }
            }
            if (prisoners.isNotEmpty()) {
                repository.insertPrisoners(prisoners)
            }
        } catch (e: Exception) {
            repository.logAudit("فشل استيراد مساجين", "حدث خطأ أثناء معالجة ملف الإكسل للمساجين: ${e.localizedMessage}", "نظام")
        }
    }

    private fun splitCSVLine(line: String): List<String> {
        // تقسيم السطر متوافق مع الفاصلة، الفاصلة المنقوطة، والـ Tab
        val delimiter = when {
            line.contains("\t") -> "\t"
            line.contains(";") -> ";"
            else -> ","
        }
        return line.split(delimiter)
    }

    private fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    // --- محرك تحليل واستخراج التجاوزات قانونياً بالكامل دون إنترنت ---
    fun analyzePrisonerViolationsOffline(prisoner: Prisoner): List<String> {
        val violations = mutableListOf<String>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        
        try {
            val detDate = dateFormat.parse(prisoner.detentionDate)
            val today = Date()
            val diffMs = today.time - (detDate?.time ?: today.time)
            val daysInDetention = (diffMs / (1000 * 60 * 60 * 24)).toInt()

            if (prisoner.status.contains("احتياطي") || prisoner.status.contains("التحقيق")) {
                // تجاوز 24 ساعة للضبط بدون النيابة
                if (daysInDetention >= 1 && prisoner.notes.contains("لم يحال للنيابة")) {
                    violations.add("⚠️ انتهاك المادة (105) إجراءات جزائية: احتجاز السجين لدى مأموري الضبط لأكثر من 24 ساعة دون إحالته للنيابة العامة المختصة!")
                }
                // تجاوز 30 يوماً من النيابة بدون تجديد القاضي
                if (daysInDetention > 30) {
                    violations.add("⚠️ انتهاك المادة (186) إجراءات جزائية: استمرار الحبس الاحتياطي لأكثر من 30 يوماً (${daysInDetention} يوم) دون صدور أمر تمديد مسبب ومقيد قانوناً من القاضي المختص!")
                }
                // تجاوز الحد الأقصى للحبس الاحتياطي (6 أشهر)
                if (daysInDetention > 180) {
                    violations.add("❌ تجاوز صارخ للمادة (191) إجراءات جزائية: تعدى الحبس الاحتياطي السقف الدستوري الأقصى (6 أشهر / 180 يوماً)! يتعين الإفراج الوجوبي فوراً عن السجين.")
                }
            }

            // تحليل تأخر المحاكمة والجلسات المهملة
            val lastHearing = dateFormat.parse(prisoner.lastHearingDate)
            val hearingDiffMs = today.time - (lastHearing?.time ?: today.time)
            val daysSinceHearing = (hearingDiffMs / (1000 * 60 * 60 * 24)).toInt()

            if (daysSinceHearing > 45 && !prisoner.status.contains("حكم")) {
                violations.add("⏳ إهمال وتأخير المحاكمة: مر أكثر من ${daysSinceHearing} يوماً منذ آخر جلسة محاكمة دون عقد مواجهة قضائية أو اتخاذ قرار، ما ينافي مبدأ كفالة العدالة الناجزة!")
            }

        } catch (e: Exception) {
            // تجاهل أخطاء التواريخ المكتوبة يدوياً وعرض نصائح عامة
            if (prisoner.status.contains("احتياطي")) {
                violations.add("ℹ️ يوصى بالتحقق من مطابقة مدة التوقيف قيادياً بالباب الاحتياطي للمادة (191) إجراءات جزائية.")
            }
        }
        return violations
    }

    // --- محرك تحليل وتنبيه تجاوزات المدعين دون إنترنت ---
    fun analyzePlaintiffViolationsOffline(plaintiff: Plaintiff): List<String> {
        val violations = mutableListOf<String>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        
        try {
            val subDate = dateFormat.parse(plaintiff.submissionDate)
            val today = Date()
            val diffMs = today.time - (subDate?.time ?: today.time)
            val daysSinceSubmission = (diffMs / (1000 * 60 * 60 * 24)).toInt()

            if (plaintiff.status == "مفتوحة" || plaintiff.status == "قيد النظر") {
                if (daysSinceSubmission > 60) {
                    violations.add("⏳ تأخر البت بالخصومة: تجاوز تاريخ تقديم الدعوى 60 يوماً دون صدور منطوق تمهيدي أو حكم.")
                }
            }

            val lastHearing = dateFormat.parse(plaintiff.lastHearingDate)
            val hearingDiffMs = today.time - (lastHearing?.time ?: today.time)
            val daysSinceHearing = (hearingDiffMs / (1000 * 60 * 60 * 24)).toInt()

            if (daysSinceHearing > 30 && plaintiff.status != "محكومة") {
                violations.add("⚠️ ركود القضية: مر أكثر من ${daysSinceHearing} يوماً دون عقد جلسة مرافعات جديدة، مخالفاً للمادة (215) مرافعات.")
            }
        } catch (e: Exception) {
            // إخطار عام
        }
        return violations
    }

    // --- استدعاء الذكاء الاصطناعي (AGIX AI) لصياغة تقرير استشاري شامل وعميق للمحكمة ---
    fun askGeminiForPrisonerDossier(prisoner: Prisoner, offlineViolations: List<String>, onCompleted: (String) -> Unit) {
        viewModelScope.launch {
            _aiLoading.value = true
            _currentAiResult.value = null

            val violationsFormatted = if (offlineViolations.isNotEmpty()) {
                offlineViolations.joinToString("\n") { "- $it" }
            } else {
                "لا توجد تجاوزات واضحة مسجلة بالنظام التلقائي حتى الآن، يرجى فحص ملفه قانونياً."
            }

            val prompt = """
                تحية العدل والميزان،
                يرجى تفحص الملف القضائي التالي للسجين اليمني وإصدار تقرير قانوني رسمي شامل واستمارة حالته مبيناً التجاوزات الدستورية بدقة:
                
                - لاسم السجين: ${prisoner.name}
                - التهمة المسندة: ${prisoner.charge}
                - تاريخ الحبس الاحتياطي: ${prisoner.detentionDate}
                - السجن ومكانه: ${prisoner.prisonName} (${prisoner.governorate})
                - الحالة القضائية الحالية: ${prisoner.status}
                - تاريخ آخر جلسة انعقاد: ${prisoner.lastHearingDate}
                - ملاحظات مسجلة: ${prisoner.notes}
                
                التحليل التلقائي الأولي للنظام يشير للتجاوزات التالية:
                $violationsFormatted
                
                المطلوب منك:
                1. تكييف القضية وتحديد الأثر التشريعي لمخالفة القوانين اليمنية (قانون الإجراءات الجزائية، قانون السجون، والدستور).
                2. صياغة "استمارة حالة السجين الشاملة" بصيغة رسمية بليغة وجاهزة للتقديم لرئيس النيابة أو المحكمة العليا.
                3. تعيين المواد والحلول الوجوبية العاجلة كالإفراج الفوري بضمانة، والتحقيق الإداري مع جهة الضبط.
            """.trimIndent()

            val response = GeminiLegalClient.consult(prompt)
            _currentAiResult.value = response
            _aiLoading.value = false
            
            // حفظ التقرير في قاعدة البيانات تلقائياً
            saveAIReport("سجين", prisoner.name, "استمارة تقصي حالة: ${prisoner.name}", response)
            onCompleted(response)
        }
    }

    fun askGeminiForPlaintiffDossier(plaintiff: Plaintiff, offlineViolations: List<String>, onCompleted: (String) -> Unit) {
        viewModelScope.launch {
            _aiLoading.value = true
            _currentAiResult.value = null

            val violationsFormatted = if (offlineViolations.isNotEmpty()) {
                offlineViolations.joinToString("\n") { "- $it" }
            } else {
                "لا توجد ركودات أو تأخير إداري معلن بالنظام."
            }

            val prompt = """
                تحية العدل والميزان،
                يرجى فحص عرائض وملفات القضية للمدعي التالي وإصدار تقرير فني واستبيان قانوني لقضيته ومخاطبة هيئة التفتيش القضائي اليمني:
                
                - اسم المدعي: ${plaintiff.name}
                - المدعى عليه: ${plaintiff.opponentName}
                - موضوع وعنوان الخصومة: ${plaintiff.caseTitle}
                - تاريخ قيد عريضة الدعوى: ${plaintiff.submissionDate}
                - تاريخ آخر جلسة مسجلة: ${plaintiff.lastHearingDate}
                - حالة سير القضية: ${plaintiff.status}
                - محافظة الاختصاص: ${plaintiff.governorate}
                - شرح إضافي للخصوم: ${plaintiff.details}
                
                المؤشرات المسجلة للنظام:
                $violationsFormatted
                
                المطلوب منك:
                1. تحديد المسؤولية القانونية ومقترحات التسريع وحسم الخصومة قياساً بالقانون المدني وقانون المرافعات اليمني والمواريث والأحوال الشخصية الفقهية.
                2. صياغة مذكرة تذكيرية رسمية بليغة لإثبات الحقوق وتكافؤ الفرص تبين بطلان التأخير أو أي تجاوز للآجال.
            """.trimIndent()

            val response = GeminiLegalClient.consult(prompt)
            _currentAiResult.value = response
            _aiLoading.value = false
            
            // حفظ التقرير
            saveAIReport("مدعي", plaintiff.name, "تقرير تسريع قضية: ${plaintiff.name}", response)
            onCompleted(response)
        }
    }

    private fun saveAIReport(type: String, target: String, title: String, text: String) = viewModelScope.launch {
        repository.saveAIReport(type, target, title, text)
    }
}
