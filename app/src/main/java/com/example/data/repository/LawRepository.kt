package com.example.data.repository

import com.example.data.dao.LawDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class LawRepository(private val lawDao: LawDao) {

    val allArticles: Flow<List<LawArticle>> = lawDao.getAllArticles()
    val allPlaintiffs: Flow<List<Plaintiff>> = lawDao.getAllPlaintiffs()
    val allPrisoners: Flow<List<Prisoner>> = lawDao.getAllPrisoners()
    val allAudits: Flow<List<AdminAudit>> = lawDao.getAllAudits()
    val allAIReports: Flow<List<AIReport>> = lawDao.getAllAIReports()

    suspend fun checkAndSeedDatabase() {
        val count = lawDao.getArticlesCount()
        if (count == 0) {
            val seedList = createSeededLaws()
            lawDao.insertArticles(seedList)
            logAudit("تهيئة النظام", "تم تعبئة المكتبة القانونية الرقمية بـ ${seedList.size} مادة تشريعية يمنية بنجاح.", "نظام")
        }
    }

    suspend fun searchArticles(query: String): List<LawArticle> {
        val searchQuery = "%$query%"
        return lawDao.searchArticles(searchQuery)
    }

    fun searchPlaintiffs(query: String): Flow<List<Plaintiff>> {
        return lawDao.searchPlaintiffs("%$query%")
    }

    fun searchPrisoners(query: String): Flow<List<Prisoner>> {
        return lawDao.searchPrisoners("%$query%")
    }

    suspend fun insertPlaintiff(plaintiff: Plaintiff) {
        val id = lawDao.insertPlaintiff(plaintiff)
        logAudit("إضافة مدعي", "تم تسجيل عريضة ادعاء جديدة للمدعي: ${plaintiff.name} وموضوعها: ${plaintiff.caseTitle}", "قاضي/مكتب")
    }

    suspend fun insertPlaintiffs(plaintiffs: List<Plaintiff>) {
        lawDao.insertPlaintiffs(plaintiffs)
        logAudit("استيراد مدعين", "تم استيراد ${plaintiffs.size} سجل مدعي وإضافتهم لقاعدة البيانات بنجاح.", "إداري")
    }

    suspend fun updatePlaintiff(plaintiff: Plaintiff) {
        lawDao.updatePlaintiff(plaintiff)
        logAudit("تعديل مدعي", "تم تحديث بيانات القضية الخاصة بالمدعي: ${plaintiff.name}", "إداري")
    }

    suspend fun deletePlaintiff(plaintiff: Plaintiff) {
        lawDao.deletePlaintiff(plaintiff)
        logAudit("حذف مدعي", "تم شطب سجل القضية للمدعي: ${plaintiff.name}", "إداري")
    }

    suspend fun clearPlaintiffs() {
        lawDao.clearPlaintiffs()
        logAudit("مسح المدعين", "تم تنظيف كافة سجلات المدعين من النظام.", "مسؤول نظام")
    }

    suspend fun insertPrisoner(prisoner: Prisoner) {
        val id = lawDao.insertPrisoner(prisoner)
        logAudit("حبس موقوف", "تم قيد سجين جديد: ${prisoner.name} بتهمة: ${prisoner.charge} بسجن: ${prisoner.prisonName}", "نيابة/سجن")
    }

    suspend fun insertPrisoners(prisoners: List<Prisoner>) {
        lawDao.insertPrisoners(prisoners)
        logAudit("استيراد مساجين", "تم استيراد ${prisoners.size} من سجلات السجون وتحليل النواقص الإجرائية.", "مراقب قضائي")
    }

    suspend fun updatePrisoner(prisoner: Prisoner) {
        lawDao.updatePrisoner(prisoner)
        logAudit("تعديل سجين", "تحديث ملف السجين: ${prisoner.name} وتحديث حالة المحاكمة.", "نيابة")
    }

    suspend fun deletePrisoner(prisoner: Prisoner) {
        lawDao.deletePrisoner(prisoner)
        logAudit("إفراج سجين", "تم قيد إخلاء سبيل أو حذف ملف السجين: ${prisoner.name}", "مدير السجن")
    }

    suspend fun clearPrisoners() {
        lawDao.clearPrisoners()
        logAudit("مسح السجناء", "تم تفريغ وحذف سجلات السجناء من النظام المؤقت.", "مسؤول نظام")
    }

    suspend fun logAudit(actionType: String, details: String, role: String) {
        val audit = AdminAudit(
            actionType = actionType,
            details = details,
            operatorRole = role
        )
        lawDao.insertAudit(audit)
    }

    suspend fun saveAIReport(targetType: String, targetName: String, title: String, text: String): Long {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val dateStr = dateFormat.format(Date())
        val report = AIReport(
            targetType = targetType,
            targetName = targetName,
            reportTitle = title,
            generatedText = text,
            createdDate = dateStr
        )
        val id = lawDao.insertAIReport(report)
        logAudit("حفظ تقرير ذكي", "تم حفظ تقرير المستشار القضائي الذكي للمواطن/السجين $targetName بنجاح.", "مستشار AGIX")
        return id
    }

    suspend fun deleteAIReport(report: AIReport) {
        lawDao.deleteAIReport(report)
        logAudit("حذف تقرير", "تم إزالة حذف التقرير الذكي للمواطن/السجين ${report.targetName}", "إداري")
    }

    /**
     * إعداد كافة القوانين والتشريعات اليمنية الأساسية بشكل متكامل غير متصل
     */
    private fun createSeededLaws(): List<LawArticle> {
        return listOf(
            // --- الدستور اليمني ---
            LawArticle(
                lawName = "دستور الجمهورية اليمنية",
                category = "قوانين دستورية",
                articleNumber = "المادة (3)",
                content = "الشريعة الإسلامية هي مصدر جميع التشريعات.",
                keywords = "الشريعة الإسلامية مصدر الدستور التشريعات المرجعية الهوية"
            ),
            LawArticle(
                lawName = "دستور الجمهورية اليمنية",
                category = "قوانين دستورية",
                articleNumber = "المادة (47)",
                content = "المسؤولية الجنائية شخصية. ولا جريمة ولا عقوبة إلا بنص شرعي أو قانوني، وكل متهم بريء حتى تثبت إدانته بحكم قضائي بات، ولا يجوز سن قانون يعاقب على أفعال سابقة على تاريخ صدوره.",
                keywords = "براءة المتهم المسؤولية الشخصية الجنائية حكم بات رجعية القوانين حماية حقوق"
            ),
            LawArticle(
                lawName = "دستور الجمهورية اليمنية",
                category = "قوانين دستورية",
                articleNumber = "المادة (48)",
                content = "أ- الدولة تكفل للمواطنين حريتهم الشخصية وتحافظ على كرامتهم وأمنهم. ب- لا يجوز القبض على أي شخص أو تفتيشه أو حجزه إلا متلبساً بالجرم أو بأمر تستلزمه ضرورة التحقيق وصيانة الأمن يصدر من القاضي أو النيابة العامة. ج- لا يجوز احتجاز أي مواطن بأي حال في غير السجون وأماكن الاحتجاز المحددة قانوناً والخاضعة لرقابة القضاء والنيابة.",
                keywords = "الحرية الشخصية كرامة المواطن الأماكن المحددة التفتيش القبض السجون الرقابة"
            ),
            LawArticle(
                lawName = "دستور الجمهورية اليمنية",
                category = "قوانين دستورية",
                articleNumber = "المادة (149)",
                content = "القضاء سلطة مستقلة قضائياً ومالياً وإدارياً، والنيابة العامة شعبة من شعبه، وتتولى المحاكم الفصل في جميع الخصومات والجرائم، والقضاة مستقلون لا سلطان عليهم في قضائهم لغير القانون.",
                keywords = "استقلال القضاء استقلالية النيابة العامة سلطة المحاكم نزاهة القضاة"
            ),

            // --- قانون الإجراءات الجزائية ---
            LawArticle(
                lawName = "قانون الإجراءات الجزائية",
                category = "قوانين قضائية وجنائية",
                articleNumber = "المادة (11)",
                content = "لا يجوز حبس أي شخص أو تقييد حريته إلا في الأماكن المحددة مسبقاً بموجب قانون السجون، ويجب معاملته بما يحفظ كرامته الإنسانية ويحظر تعذيبه أو إيذائه مادياً أو معنوياً.",
                keywords = "تقييد الحرية التعذيب الإيذاء كرامة السجين السجون المعاملة الإنسانية"
            ),
            LawArticle(
                lawName = "قانون الإجراءات الجزائية",
                category = "قوانين قضائية وجنائية",
                articleNumber = "المادة (105)",
                content = "على مأمور الضبط القضائي (الشرطة القضائية) سماع أقوال المتهم المقبوض عليه فوراً، وإذا لم يأت بما يبرئه، يرسله خلال أربع وعشرين (24) ساعة إلى النيابة العامة المختصة. ولا يجوز حجز المتهم لدى الشرطة أكثر من هذه المدة.",
                keywords = "ضبط قضائي مأمور أقوال 24 ساعة النيابة تجاوز مدة الاحتجاز الشرطة"
            ),
            LawArticle(
                lawName = "قانون الإجراءات الجزائية",
                category = "قوانين قضائية وجنائية",
                articleNumber = "المادة (186)",
                content = "يجب على النيابة العامة استجواب المتهم فور إرساله إليها، والأمر بحبسه احتياطياً لا يجوز أن يزيد على سبعة أيام. وإذا اقتضت مصلحة التحقيق تمديد الحبس، يرفع الأمر للقاضي المختص للتمديد مدداً متتالية لا تتجاوز في مجموعها ثلاثين (30) يوماً.",
                keywords = "استجواب النيابة حبس احتياطي تمديد سبعة أيام ثلاثين يوماً القاضي"
            ),
            LawArticle(
                lawName = "قانون الإجراءات الجزائية",
                category = "قوانين قضائية وجنائية",
                articleNumber = "المادة (191)",
                content = "الحد الأقصى للحبس الاحتياطي في مرحلة التحقيق الابتدائي لا يجوز أن يتجاوز بأي حال من الأحوال ستة (6) أشهر. وإذا انقضت هذه المدة دون إحالته للمحاكمة، يجب الإفراج الفوري عن المتهم بضمان أو بدونه.",
                keywords = "الحد الأقصى الحبس الاحتياطي ستة أشهر تجاوز الإفراج الفوري ضمان"
            ),

            // --- قانون السجون ---
            LawArticle(
                lawName = "قانون تنظيم السجون",
                category = "قوانين قضائية وجنائية",
                articleNumber = "المادة (8)",
                content = "يتعرض للمسؤولية الجنائية والتأديبية كل مدير سجن أو مسؤول يقبل إيداع شخص في السجن دون أمر كتابي مسبب وموقع عليه من النيابة العامة أو المحكمة المختصة، أو يبقيه بعد المدة المحددة بالأمر.",
                keywords = "القبول غير القانوني أمر كتابي النيابة المحكمة مسبب انتهاء المدة مسؤولية"
            ),
            LawArticle(
                lawName = "قانون تنظيم السجون",
                category = "قوانين قضائية وجنائية",
                articleNumber = "المادة (24)",
                content = "يجب على إدارة السجن تشغيل وتدريب السجناء، وتوفير العناية الطبية الدورية لهم، وتمكينهم من الاجتماع بمحاميهم وأقاربهم دورياً بشكل خاص وآمن.",
                keywords = "العناية الطبية تشغيل السجناء المحامي زيارات الأقارب الخصوصية"
            ),

            // --- قانون المرافعات والتنفيذ المدني ---
            LawArticle(
                lawName = "قانون المرافعات والتنفيذ المدني",
                category = "قوانين قضائية",
                articleNumber = "المادة (18)",
                content = "يتعين على القاضي تطبيق مبدأ المواجهة وتكافؤ الفرص في الدعوة، وتبليغ الخصوم بالإجراءات ومواعيد الجلسات قانوناً، وأي تهاون بتسليم الإعلانات القضائية يبطل الحكم الصادر تبعا له.",
                keywords = "مبدأ المواجهة تكافؤ الفرص التبليغ بطلان الحكم الإعلانات القضائية الخصوم"
            ),
            LawArticle(
                lawName = "قانون المرافعات والتنفيذ المدني",
                category = "قوانين قضائية",
                articleNumber = "المادة (215)",
                content = "يجب الفصل في القضايا المستعجلة خلال مدة لا تتجاوز خمسة عشر يوماً من تاريخ تقديم الدعوى، ولا يجوز تأجيل الجلسات لأكثر من مرتين متتاليتين لذات السبب دون موجب قاهر يثبت بمحضر الجلسة.",
                keywords = "تأجيل مستعجل خمسة عشر يوما الجلسات محضر تأخير إداري خصومة"
            ),

            // --- قانون الجرائم والعقوبات ---
            LawArticle(
                lawName = "قانون الجرائم والعقوبات",
                category = "قوانين جنائية",
                articleNumber = "المادة (162)",
                content = "يعاقب بالحبس مدة لا تقل عن سنة ولا تزيد عن خمس سنوات كل موظف عام عطل حكماً قضائياً واجباً النفاذ أو تعمد الامتناع عن تنفيذ الأوامر الصادرة من محكمة أو سلطة قضائية مختصة.",
                keywords = "تعطيل حكم قضائي موظف عام إهمال النفاذ امتناع عقوبة حبس"
            ),
            LawArticle(
                lawName = "قانون الجرائم والعقوبات",
                category = "قوانين جنائية",
                articleNumber = "المادة (251)",
                content = "السرقة هي أخذ مال منقول مملوك للغير خفية بنية تملكه. وتكون حدية مع توافر الشروط الشرعية، أو تعزيرية بالحبس والغرامة مع عدم توفر شروط الحد الشرعي.",
                keywords = "جريمة السرقة حد السرقة تعزير مال منقول شروط شرعية الحبس"
            ),
            LawArticle(
                lawName = "قانون الجرائم والعقوبات",
                category = "قوانين جنائية",
                articleNumber = "المادة (312)",
                content = "كل من زور أو قلد أو اصطنع ختماً للدولة أو توقيعاً رسمياً للمحافظ أو القاضي يعاقب بالحبس مدة لا تقل عن ثلاث سنوات ولا تزيد عن سبع سنوات.",
                keywords = "التزوير الاختام التوقيع الرسمي تقليد اصطناع حكم تجريد عقوبة"
            ),

            // --- القانون المدني ---
            LawArticle(
                lawName = "القانون المدني اليمني",
                category = "قوانين مدنية وعقارية",
                articleNumber = "المادة (19)",
                content = "العقد هو شريعة المتعاقدين، ولا يجوز نقضه ولا تعديله إلا باتفاق الطرفين أو للأسباب التي يقررها القانون، ويجب تنفيذ العقد بحسن نية وبما يتفق مع متطلبات الأمانة التجارية والشرعية.",
                keywords = "شريعة المتعاقدين حسن نية العقد اتفاق نقض وفاء عهد معاهدة"
            ),
            LawArticle(
                lawName = "القانون المدني اليمني",
                category = "قوانين مدنية وعقارية",
                articleNumber = "المادة (902)",
                content = "تثبت الملكية العقارية بالسجل العقاري الرسمي أو بموجب وثائق نقل ملكية معمدة شرعاً ومسجلة في التوثيق القضائي، وكل بيع لعقار دون توثيقه أو تعميده يعتبر ناقص الحماية القانونية وخلل بين الخصوم.",
                keywords = "الملكية العقارية وثائق معمدة السجل العقاري التوثيق بيع عقار"
            ),
            LawArticle(
                lawName = "القانون المدني اليمني",
                category = "قوانين مدنية وعقارية",
                articleNumber = "المادة (1110)",
                content = "يجب على المستأجر لعين عقارية إخلاؤها فور انتهاء عقد الإيجار المتفق عليه، وفي حال امتناعه يحق للمالك رفع دعوى مستعجلة للمطالبة بالإخلاء وبدل المنفعة عما لحقه من أضرار.",
                keywords = "مستأجر عقار إيجار إخلاء انتهاء عقد دعوى مستعجلة للمالك"
            ),

            // --- قانون الأحوال الشخصية ---
            LawArticle(
                lawName = "قانون الأحوال الشخصية",
                category = "قوانين الأحوال الشخصية والوقف",
                articleNumber = "المادة (45)",
                content = "النفقة والكسوة والسكنى لزوجة هي واجب شرعي على الزوج من تاريخ العقد الصحيح والتمكين، وتعتبر ديناً ممتازاً في ذمته لا يسقط إلا بالأداء أو الإبراء التام.",
                keywords = "نفقة الزوجة كسوة مسكن واجب شرعي دين ممتاز الزوج"
            ),
            LawArticle(
                lawName = "قانون الأحوال الشخصية",
                category = "قوانين الأحوال الشخصية والوقف",
                articleNumber = "المادة (138)",
                content = "الحضانة هي حفظ الولد وتربيته ورعايته في أول أمره وهي حق للام ثم للحاضنة الشريعة، وتنتهي حضانة النساء للأولاد ببلوغ الذكر تسع سنوات والأنثى اثنتي عشرة سنة ما لم تقتض مصلحة المحضون خلاف ذلك.",
                keywords = "الحضانة حق الأم مصلحة المحضون بلوغ تسع سنوات رعاية تربية"
            ),

            // --- قانون مكافحة الفساد الكوني ---
            LawArticle(
                lawName = "قانون مكافحة الفساد",
                category = "قوانين دستورية وإدارية",
                articleNumber = "المادة (30)",
                content = "تعتبر جرائم فساد خاضعة لأحكام هذا القانون: الرشوة، الاختلاس، استغلال النفوذ الوظيفي، الكسب غير المشروع، الإضرار بمصالح الدولة المالية، غسل الأموال وتزوير المحررات الرسمية للنيابات.",
                keywords = "جرائم الفساد الرشوة الاختلاس كسب غير مشروع غسل أموال كسب وظيفي"
            ),

            // --- قانون قضايا الدولة ---
            LawArticle(
                lawName = "قانون قضايا الدولة",
                category = "قوانين دستورية وإدارية",
                articleNumber = "المادة (5)",
                content = "تمثل وزارة العدل وقسم قضايا الدولة كافة الوزارات والهيئات العامة والمحلية في الدعاوى المقامة منها أو عليها أمام المحاكم، وتبليغها ضروري في كل خصومة تمس المال العام.",
                keywords = "تمثيل الدولة وزارة العدل خصومة مال عام محاكم هيئات عامة"
            ),

            // --- قانون الإثبات اليمني ---
            LawArticle(
                lawName = "قانون الإثبات",
                category = "قوانين قضائية",
                articleNumber = "المادة (14)",
                content = "البينة على من ادعى واليمين على من أنكر، وتثبت الحقوق بشهادة الشهود، أو المقارير الكتابية المعمدة، أو القرائن القاطعة، والمعاينة الفنية بتقرير الخبير المعتمد قضائياً.",
                keywords = "الإثبات يمين إنكار شهادة الشهود تقرير الخبير معاينة تزوير أدلة"
            ),

            // --- قانون السلطة القضائية ---
            LawArticle(
                lawName = "قانون السلطة القضائية",
                category = "قوانين دستورية وقضائية",
                articleNumber = "المادة (20)",
                content = "يحظر على القضاة وأعضاء النيابة العامة الاشتغال بالعمل التجاري أو التحزب السياسي أو إفشاء أسرار القضايا للصحافة أو الخصوم، وتعليق عمل القاضي يتم بقرار مسبب من رئيس مجلس القضاء.",
                keywords = "السلطة القضائية النيابة التحزب التجارة إفشاء الأسرار مجلس القضاء"
            ),

            // --- قانون العمل اليمني ---
            LawArticle(
                lawName = "قانون العمل اليمني",
                category = "قوانين اجتماعية وعمالية",
                articleNumber = "المادة (55)",
                content = "يستحق العامل مكافأة نهاية خدمة تعادل أجر نصف شهر عن كل سنة من السنوات الخمس الأولى، وأجر شهر كامل عن كل سنة تلتها، وتحسب على أساس آخر أجر أساسي تقاضاه العامل.",
                keywords = "نهاية خدمة مكافأة تقاعد أجر العامل السنوات الخمس العمل اليمني"
            )
        )
    }
}
