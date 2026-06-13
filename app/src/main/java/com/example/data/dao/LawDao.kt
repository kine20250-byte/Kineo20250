package com.example.data.dao

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LawDao {
    
    // --- قانون ومواد المكتبة ---
    @Query("SELECT * FROM law_articles")
    fun getAllArticles(): Flow<List<LawArticle>>

    @Query("SELECT * FROM law_articles WHERE content LIKE :query OR keywords LIKE :query OR lawName LIKE :query OR articleNumber LIKE :query")
    suspend fun searchArticles(query: String): List<LawArticle>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<LawArticle>)

    @Query("SELECT COUNT(*) FROM law_articles")
    suspend fun getArticlesCount(): Int

    // --- واجهات المدعين ---
    @Query("SELECT * FROM plaintiffs ORDER BY id DESC")
    fun getAllPlaintiffs(): Flow<List<Plaintiff>>

    @Query("SELECT * FROM plaintiffs WHERE name LIKE :query OR opponentName LIKE :query OR caseTitle LIKE :query OR governorate LIKE :query")
    fun searchPlaintiffs(query: String): Flow<List<Plaintiff>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaintiff(plaintiff: Plaintiff): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaintiffs(plaintiffs: List<Plaintiff>)

    @Update
    suspend fun updatePlaintiff(plaintiff: Plaintiff)

    @Delete
    suspend fun deletePlaintiff(plaintiff: Plaintiff)

    @Query("DELETE FROM plaintiffs")
    suspend fun clearPlaintiffs()

    // --- واجهات المساجين ---
    @Query("SELECT * FROM prisoners ORDER BY id DESC")
    fun getAllPrisoners(): Flow<List<Prisoner>>

    @Query("SELECT * FROM prisoners WHERE name LIKE :query OR charge LIKE :query OR prisonName LIKE :query OR governorate LIKE :query")
    fun searchPrisoners(query: String): Flow<List<Prisoner>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrisoner(prisoner: Prisoner): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrisoners(prisoners: List<Prisoner>)

    @Update
    suspend fun updatePrisoner(prisoner: Prisoner)

    @Delete
    suspend fun deletePrisoner(prisoner: Prisoner)

    @Query("DELETE FROM prisoners")
    suspend fun clearPrisoners()

    // --- سجل التدقيق والنزاهة ---
    @Query("SELECT * FROM admin_audits ORDER BY timestamp DESC")
    fun getAllAudits(): Flow<List<AdminAudit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudit(audit: AdminAudit)

    // --- تقارير الذكاء الاصطناعي ---
    @Query("SELECT * FROM ai_reports ORDER BY id DESC")
    fun getAllAIReports(): Flow<List<AIReport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAIReport(report: AIReport): Long

    @Delete
    suspend fun deleteAIReport(report: AIReport)
}
