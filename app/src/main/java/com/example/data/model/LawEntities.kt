package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * يمثل مادة قانونية في المكتبة القانونية اليمنية الرقمية
 */
@Entity(tableName = "law_articles")
data class LawArticle(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val lawName: String,         // اسم القانون مثل (القانون المدني، قانون الجرائم والعقوبات)
    val category: String,        // التصنيف مثل (جنائي، أحوال شخصية، عقاري)
    val articleNumber: String,   // رقم المادة مثل "مادة (105)"
    val content: String,         // نص المادة القانونية
    val keywords: String         // الكلمات الدلالية للفهرسة والبحث السريع
)

/**
 * يمثل خصومة أو دعوى في واجهات المدعين
 */
@Entity(tableName = "plaintiffs")
data class Plaintiff(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,             // اسم المدعي
    val opponentName: String,     // اسم المدعى عليه
    val caseTitle: String,        // موضوع القضية
    val submissionDate: String,   // تاريخ تقديم الدعوى (YYYY-MM-DD)
    val lastHearingDate: String,  // تاريخ آخر جلسة
    val status: String,           // حالة القضية: مفتوحة، قيد النظر، محكومة، معلقة
    val governorate: String,      // المحافظة اليمنية
    val details: String           // تفاصيل إضافية
)

/**
 * يمثل سجين في واجه السجون والشرطة القضائية
 */
@Entity(tableName = "prisoners")
data class Prisoner(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,             // اسم السجين
    val charge: String,           // التهمة الموجهة له
    val detentionDate: String,     // تاريخ إيداعه السجن (YYYY-MM-DD)
    val status: String,           // الحالة: موقوف احتياطياً، رهن التحقيق، مقضي بحكم
    val lastHearingDate: String,  // تاريخ آخر جلسة محاكمة (للتحقق من إهمال الجلسات)
    val prisonName: String,       // السجن (مثلا: السجن المركزي بصنعاء)
    val governorate: String,      // المحافظة
    val notes: String             // ملاحظات أو وضع صحي
)

/**
 * سجل التدقيق والنزاهة غير القابل للتعديل
 */
@Entity(tableName = "admin_audits")
data class AdminAudit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val actionType: String,       // نوع الإجراء (بحث قانوني، فحص انتهاك سجين، تصدير إكسل)
    val details: String,          // ملخص وتفاصيل العمل
    val operatorRole: String      // دور القائم بالعملية (قاضي، نيابة، مراقب)
)

/**
 * التقارير والمذكرات القضائية المنتجة بالذكاء الاصطناعي
 */
@Entity(tableName = "ai_reports")
data class AIReport(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val targetType: String,       // سجين أم مدعي
    val targetName: String,       // اسم الشخص المعني
    val reportTitle: String,      // عنوان التقرير الاستشاري
    val generatedText: String,    // التدفق الذكي للتقرير ومقترحات القوانين
    val createdDate: String       // تاريخ الإنشاء
)
