package com.simats.drugssearch

import android.os.Bundle
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import com.simats.drugssearch.ui.*
import com.simats.drugssearch.ui.theme.DrugsSearchTheme
import com.simats.drugssearch.network.SaveReportRequest
import com.simats.drugssearch.network.ReportParameter
import com.simats.drugssearch.network.Drug
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DrugsSearchTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

enum class Screen {
    Splash,
    Welcome,
    Login,
    Register,
    VerifyEmail,
    Dashboard,
    Upload,
    FileSelected,
    ReviewValues,
    ManualEntry,
    BloodCountEntry,
    MetabolicPanelEntry,
    LipidProfileEntry,
    KidneyFunctionEntry,
    LiverFunctionEntry,
    ThyroidPanelEntry,
    ReportAnalysis,
    ReportHistory,
    SearchDrugInformation,
    SearchResults,
    DrugDetails,
    Profile,
    ChangePassword,
    ReportDetail,
    DrugRecommendations,
    ForgotPassword,
    CheckEmail,
    ResetPassword,
    PersonalInformation,
    PrivacySecurity,
    DrugCategories,
    NormalResults,
    AbnormalResults,
    RiskSummary,
    AboutApp,
    SafetyWarnings,
    CounsellingNotes,
    HelpSupport,
    FAQ
}

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val sessionManager = remember { SessionManager(context) }

    var currentScreen by remember { mutableStateOf(Screen.Splash) }
    var previousScreen by remember { mutableStateOf<Screen?>(null) }
    
    // Biometric prompt logic
    fun showBiometricPrompt(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (activity == null) return
        
        val executor = ContextCompat.getMainExecutor(activity)
        val biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError(errString.toString())
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Login")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use Password")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    // Check biometric on splash complete
    fun handleSplashComplete() {
        if (sessionManager.isLoggedIn() && sessionManager.isBiometricEnabled()) {
            val biometricManager = BiometricManager.from(context)
            if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS) {
                showBiometricPrompt(
                    onSuccess = { currentScreen = Screen.Dashboard },
                    onError = { currentScreen = Screen.Login }
                )
            } else {
                currentScreen = Screen.Dashboard
            }
        } else if (sessionManager.isLoggedIn()) {
            currentScreen = Screen.Dashboard
        } else {
            currentScreen = Screen.Welcome
        }
    }
    
    // User Session — initialize from saved session if available
    var userEmail by remember { mutableStateOf(sessionManager.getEmail()) }
    var loggedInUserId by remember { mutableStateOf<Int?>(if (sessionManager.isLoggedIn()) sessionManager.getUserId() else null) }
    var loggedInUserName by remember { mutableStateOf(sessionManager.getUserName()) }
    var userPhone by remember { mutableStateOf(sessionManager.getPhone()) }
    var userDob by remember { mutableStateOf(sessionManager.getDob()) }
    var userGender by remember { mutableStateOf(sessionManager.getGender()) }
    
    // Upload & Report State
    var selectedCategory by remember { mutableStateOf("") }
    var reviewValuesMap by remember { mutableStateOf(mapOf<String, String>()) }
    var currentAnalysis by remember { mutableStateOf<com.simats.drugssearch.network.OcrResponse?>(null) }
    
    // Category Specific State Values
    var bloodCountValues by remember { mutableStateOf(BloodCountValues()) }
    var metabolicPanelValues by remember { mutableStateOf(MetabolicPanelValues()) }
    var lipidProfileValues by remember { mutableStateOf(LipidProfileValues()) }
    var kidneyFunctionValues by remember { mutableStateOf(KidneyFunctionValues()) }
    var liverFunctionValues by remember { mutableStateOf(LiverFunctionValues()) }
    var thyroidPanelValues by remember { mutableStateOf(ThyroidPanelValues()) }
    
    // Report History State
    var selectedReport by remember { mutableStateOf<HealthReport?>(null) }


    // Search State
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Drug>>(emptyList()) }
    var selectedDrug by remember { mutableStateOf<Drug?>(null) }

    val scope = rememberCoroutineScope()

    // Dynamic Recommendations State
    var drugRecommendations by remember { mutableStateOf<List<com.simats.drugssearch.ui.DrugRecommendation>>(emptyList()) }
    

    // Patient Details State
    var currentPatientDetails by remember { mutableStateOf<com.simats.drugssearch.network.PatientDetails?>(null) }
    var currentRemarks by remember { mutableStateOf<String?>(null) }
    var currentReportId by remember { mutableStateOf<Int?>(null) }

    // Dashboard Stats (dynamic)
    var dashboardTotalReports by remember { mutableStateOf(0) }
    var dashboardNormalReports by remember { mutableStateOf(0) }
    var dashboardAbnormalReports by remember { mutableStateOf(0) }

    // Fetch dashboard stats when user logs in
    LaunchedEffect(loggedInUserId) {
        val uid = loggedInUserId
        if (uid != null) {
            try {
                val response = com.simats.drugssearch.network.RetrofitClient.instance.getUserReports(uid)
                if (response.isSuccessful) {
                    val reports = response.body() ?: emptyList()
                    dashboardTotalReports = reports.size
                    dashboardNormalReports = reports.count { it.isNormal }
                    dashboardAbnormalReports = reports.count { !it.isNormal }
                }
            } catch (_: Exception) {
                // Silently fail — stats stay at 0
            }
        } else {
            dashboardTotalReports = 0
            dashboardNormalReports = 0
            dashboardAbnormalReports = 0
        }
    }

    // Derived values for review/analysis
    val valuesForReview = when (selectedCategory) {
        "Blood Count" -> mapOf(
            "Hemoglobin" to bloodCountValues.hemoglobin,
            "WBC" to bloodCountValues.wbcCount,
            "RBC" to bloodCountValues.rbcCount,
            "Platelets" to bloodCountValues.plateletCount,
            "Hematocrit" to bloodCountValues.hematocrit
        )
        "Metabolic Panel" -> mapOf(
            "Blood Glucose" to metabolicPanelValues.bloodGlucose,
            "Sodium" to metabolicPanelValues.sodium,
            "Potassium" to metabolicPanelValues.potassium,
            "Calcium" to metabolicPanelValues.calcium,
            "Bicarbonate" to metabolicPanelValues.bicarbonate
        )
        "Lipid Profile" -> mapOf(
            "Total Cholesterol" to lipidProfileValues.totalCholesterol,
            "HDL Cholesterol" to lipidProfileValues.hdlCholesterol,
            "LDL Cholesterol" to lipidProfileValues.ldlCholesterol,
            "Triglycerides" to lipidProfileValues.triglycerides
        )
        "Kidney Function" -> mapOf(
            "Creatinine" to kidneyFunctionValues.creatinine,
            "BUN" to kidneyFunctionValues.bun,
            "eGFR" to kidneyFunctionValues.egfr,
            "Uric Acid" to kidneyFunctionValues.uricAcid
        )
        "Liver Function" -> mapOf(
            "SGOT" to liverFunctionValues.sgot,
            "SGPT" to liverFunctionValues.sgpt,
            "ALP" to liverFunctionValues.alp,
            "Total Bilirubin" to liverFunctionValues.totalBilirubin,
            "Direct Bilirubin" to liverFunctionValues.directBilirubin,
            "Albumin" to liverFunctionValues.albumin,
            "Total Protein" to liverFunctionValues.totalProtein,
            "GGT" to liverFunctionValues.ggt
        )
        "Thyroid Panel" -> mapOf(
            "TSH" to thyroidPanelValues.tsh,
            "Free T4" to thyroidPanelValues.freeT4,
            "Free T3" to thyroidPanelValues.freeT3,
            "Total T4" to thyroidPanelValues.totalT4,
            "Total T3" to thyroidPanelValues.totalT3
        )
        else -> reviewValuesMap
    }

    // Helper to safely get value from map
    fun getVal(map: Map<String, String>, key: String): String = map[key] ?: ""

    when (currentScreen) {
        Screen.Splash -> SplashScreen(onSplashComplete = {
            handleSplashComplete()
        })
        
        Screen.Welcome -> WelcomeScreen(
            onLoginClick = { currentScreen = Screen.Login },
            onCreateAccountClick = { currentScreen = Screen.Register }
        )
        
        Screen.Login -> LoginScreen(
            onLoginSuccess = { userId, fullName, email, phone, dob, gender -> 
                loggedInUserId = userId
                loggedInUserName = fullName
                userEmail = email
                userPhone = phone
                userDob = dob
                userGender = gender
                sessionManager.saveSession(userId, fullName, email, phone, dob, gender)
                currentScreen = Screen.Dashboard 
            },
            onRegisterClick = { currentScreen = Screen.Register },
            onForgotPasswordClick = { currentScreen = Screen.ForgotPassword }
        )
        
        Screen.Register -> RegisterScreen(
            onCreateAccountClick = { _, email, _, _ -> 
                userEmail = email
                currentScreen = Screen.VerifyEmail 
            },
            onLoginClick = { currentScreen = Screen.Login }
        )
        
        Screen.VerifyEmail -> VerifyEmailScreen(
            email = userEmail,
            onVerifyClick = { _ -> currentScreen = Screen.Login },
            onBackClick = { currentScreen = Screen.Register }
        )
        
        Screen.Dashboard -> DashboardScreen(
            userName = loggedInUserName,
            totalReports = dashboardTotalReports,
            normalReports = dashboardNormalReports,
            abnormalReports = dashboardAbnormalReports,
            onUploadClick = { currentScreen = Screen.Upload },
            onSearchClick = { currentScreen = Screen.SearchDrugInformation },
            onHistoryClick = { currentScreen = Screen.ReportHistory },
            onProfileClick = { currentScreen = Screen.Profile },
            onHomeClick = { /* Already on dashboard */ },
            onViewAllClick = { currentScreen = Screen.ReportHistory }
        )
        
        Screen.Upload -> UploadScreen(
            onBackClick = { currentScreen = Screen.Dashboard },
            onHomeClick = { currentScreen = Screen.Dashboard },
            onUploadReportClick = { currentScreen = Screen.FileSelected },
            onManualEntryClick = { 
                currentReportId = null // Reset for manual entry
                currentScreen = Screen.ManualEntry 
            },
            onSearchClick = { currentScreen = Screen.SearchDrugInformation },
            onHistoryClick = { currentScreen = Screen.ReportHistory },
            onProfileClick = { currentScreen = Screen.Profile }
        )
        
        Screen.FileSelected -> FileSelectedScreen(
            userId = loggedInUserId,
            onBackClick = { currentScreen = Screen.Upload },
            onHomeClick = { currentScreen = Screen.Dashboard },
            onChooseDifferentFileClick = { /* Handled in screen */ },
            onUploadSuccess = { values, category, recommendations, patientDetails, reportId ->
                selectedCategory = category
                drugRecommendations = recommendations
                currentPatientDetails = patientDetails
                currentReportId = reportId
                // For OCR uploads, pass ALL extracted parameters to review/analysis
                // Don't filter through category-specific models — those are for manual entry only
                reviewValuesMap = values
                currentScreen = Screen.ReviewValues
            },
            onSearchClick = { currentScreen = Screen.SearchDrugInformation },
            onHistoryClick = { currentScreen = Screen.ReportHistory },
            onProfileClick = { currentScreen = Screen.Profile }
        )
        
        Screen.ReviewValues -> {
            val editTargetScreen = when (selectedCategory) {
                 "Blood Count" -> Screen.BloodCountEntry
                 "Metabolic Panel" -> Screen.MetabolicPanelEntry
                 "Lipid Profile" -> Screen.LipidProfileEntry
                 "Kidney Function" -> Screen.KidneyFunctionEntry
                 "Liver Function" -> Screen.LiverFunctionEntry
                 "Thyroid Panel" -> Screen.ThyroidPanelEntry
                 else -> Screen.ManualEntry 
            }

            ReviewValuesScreen(
                userId = loggedInUserId ?: 0,
                categoryName = selectedCategory,
                values = valuesForReview,
                initialPatientDetails = currentPatientDetails,
                onBackClick = { currentScreen = Screen.Upload },
                onHomeClick = { currentScreen = Screen.Dashboard },
                onSaveClick = { newValues, patientDetails, remarks ->
                    currentPatientDetails = patientDetails
                    currentRemarks = remarks
                     when (selectedCategory) {
                        "Blood Count" -> {
                            bloodCountValues = BloodCountValues(
                                hemoglobin = getVal(newValues, "Hemoglobin"),
                                wbcCount = getVal(newValues, "WBC"),
                                rbcCount = getVal(newValues, "RBC"),
                                plateletCount = getVal(newValues, "Platelets"),
                                hematocrit = getVal(newValues, "Hematocrit")
                            )
                        }
                        "Metabolic Panel" -> {
                            metabolicPanelValues = MetabolicPanelValues(
                                bloodGlucose = getVal(newValues, "Blood Glucose"),
                                sodium = getVal(newValues, "Sodium"),
                                potassium = getVal(newValues, "Potassium"),
                                calcium = getVal(newValues, "Calcium"),
                                bicarbonate = getVal(newValues, "Bicarbonate")
                            )
                        }
                         "Lipid Profile" -> {
                              lipidProfileValues = LipidProfileValues(
                                  totalCholesterol = getVal(newValues, "Total Cholesterol"),
                                  hdlCholesterol = getVal(newValues, "HDL Cholesterol"),
                                  ldlCholesterol = getVal(newValues, "LDL Cholesterol"),
                                  triglycerides = getVal(newValues, "Triglycerides")
                              )
                          }
                        "Kidney Function" -> {
                             kidneyFunctionValues = KidneyFunctionValues(
                                 creatinine = getVal(newValues, "Creatinine"),
                                 bun = getVal(newValues, "BUN"),
                                 egfr = getVal(newValues, "eGFR"),
                                 uricAcid = getVal(newValues, "Uric Acid")
                             )
                         }
                        "Liver Function" -> {
                             liverFunctionValues = LiverFunctionValues(
                                 sgot = getVal(newValues, "SGOT"),
                                 sgpt = getVal(newValues, "SGPT"),
                                 alp = getVal(newValues, "ALP"),
                                 totalBilirubin = getVal(newValues, "Total Bilirubin"),
                                 directBilirubin = getVal(newValues, "Direct Bilirubin"),
                                 albumin = getVal(newValues, "Albumin"),
                                 totalProtein = getVal(newValues, "Total Protein"),
                                 ggt = getVal(newValues, "GGT")
                             )
                         }
                        "Thyroid Panel" -> {
                             thyroidPanelValues = ThyroidPanelValues(
                                 tsh = getVal(newValues, "TSH"),
                                 freeT4 = getVal(newValues, "Free T4"),
                                 freeT3 = getVal(newValues, "Free T3"),
                                 totalT4 = getVal(newValues, "Total T4"),
                                 totalT3 = getVal(newValues, "Total T3")
                             )
                         }
                        else -> {
                            reviewValuesMap = newValues.toMap()
                        }
                    }
                },
                onSubmitForAnalysisClick = { valuesMap, patientDetails, remarkText ->
                    scope.launch {
                        try {
                            // Clear previous recommendations
                            drugRecommendations = emptyList()
                            
                            // Update local state first to persist revisions
                            currentPatientDetails = patientDetails
                            currentRemarks = remarkText
                            // Update specific category values if needed (optional since we use valuesMap for request)
                            
                            val parameters = valuesMap.map { (k, v) ->
                                ReportParameter(name = k, value = v, unit = "", isNormal = false) 
                            }
                            
                            val req = SaveReportRequest(
                                reportId = currentReportId, // Pass reportId
                                userId = loggedInUserId ?: 0,
                                category = selectedCategory,
                                parameters = parameters,
                                patientName = patientDetails?.name,
                                patientAge = patientDetails?.age,
                                patientGender = patientDetails?.gender,
                                remarks = remarkText
                            )
                            val response = com.simats.drugssearch.network.RetrofitClient.instance.saveReportData(req)
                            if (response.isSuccessful && response.body() != null) {
                                val responseBody = response.body()!!
                                
                                // Process Analysis Result (Recommendations)
                                val analysis = responseBody.analysis
                                if (analysis != null) {
                                    val newRecommendations = mutableListOf<com.simats.drugssearch.ui.DrugRecommendation>()
                                    
                                    analysis.parameters?.forEach { (name, detail) ->
                                        if (detail.recommendation != null) {
                                            val rec = detail.recommendation
                                            val drugsList = rec.drugs?.split(",")?.map { it.trim() } ?: emptyList()
                                            
                                            // Assign a color based on status for now (red for high/low)
                                            val catColor = if (detail.status == "Normal") com.simats.drugssearch.ui.theme.GreenColor else com.simats.drugssearch.ui.theme.RedColor
                                            val catBg = if (detail.status == "Normal") com.simats.drugssearch.ui.theme.GreenBg else com.simats.drugssearch.ui.theme.RedBg

                                            newRecommendations.add(
                                                com.simats.drugssearch.ui.DrugRecommendation(
                                                    parameterName = name,
                                                    name = rec.category ?: "General Recommendation",
                                                    condition = detail.condition ?: "Abnormal Value",
                                                    category = detail.category ?: "General",
                                                    commonDrugs = drugsList,
                                                    categoryColor = catColor,
                                                    categoryBg = catBg
                                                )
                                            )
                                        }
                                    }
                                    // Update Global State
                                    drugRecommendations = newRecommendations
                                    currentAnalysis = analysis
                                } else {
                                     // Clear recommendations if analysis failed or returned nothing
                                     drugRecommendations = emptyList()
                                     currentAnalysis = null
                                }
                                
                                currentScreen = Screen.ReportAnalysis
                            } else {
                                // Request failed
                                currentScreen = Screen.ReportAnalysis 
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            currentScreen = Screen.ReportAnalysis
                        }
                    }
                },
                onSearchClick = { currentScreen = Screen.SearchDrugInformation },
                onHistoryClick = { currentScreen = Screen.ReportHistory },
                onProfileClick = { currentScreen = Screen.Profile }
            )
        }
        
        Screen.ManualEntry -> ManualEntryScreen(
            onBackClick = { currentScreen = Screen.Dashboard },
            onHomeClick = { currentScreen = Screen.Dashboard },
            onCategorySelected = { category -> 
                // Reset state for new entry
                drugRecommendations = emptyList()
                currentAnalysis = null
                currentReportId = null
                currentPatientDetails = null
                currentRemarks = ""
                
                selectedCategory = category
                currentScreen = when (category) {
                    "Blood Count" -> Screen.BloodCountEntry
                    "Metabolic Panel" -> Screen.MetabolicPanelEntry
                    "Lipid Profile" -> Screen.LipidProfileEntry
                    "Kidney Function" -> Screen.KidneyFunctionEntry
                    "Liver Function" -> Screen.LiverFunctionEntry
                    "Thyroid Panel" -> Screen.ThyroidPanelEntry
                    else -> Screen.ManualEntry
                }
            },
            onSearchClick = { currentScreen = Screen.SearchDrugInformation },
            onHistoryClick = { currentScreen = Screen.ReportHistory },
            onProfileClick = { currentScreen = Screen.Profile }
        )
        
        Screen.BloodCountEntry -> BloodCountEntryScreen(
            initialValues = bloodCountValues, 
            onBackClick = { currentScreen = if (previousScreen == Screen.ReviewValues) Screen.ReviewValues else Screen.ManualEntry },
            onHomeClick = { currentScreen = Screen.Dashboard },
            onSubmitClick = { values -> 
                bloodCountValues = values
                selectedCategory = "Blood Count"
                currentScreen = Screen.ReviewValues 
            },
            onSearchClick = { currentScreen = Screen.SearchDrugInformation },
            onHistoryClick = { currentScreen = Screen.ReportHistory },
            onProfileClick = { currentScreen = Screen.Profile }
        )
        
        Screen.MetabolicPanelEntry -> MetabolicPanelEntryScreen(
            initialValues = metabolicPanelValues,
            onSubmitClick = { values ->
                metabolicPanelValues = values
                selectedCategory = "Metabolic Panel"
                currentScreen = Screen.ReviewValues
            },
             onBackClick = { currentScreen = if (previousScreen == Screen.ReviewValues) Screen.ReviewValues else Screen.ManualEntry },
             onHomeClick = { currentScreen = Screen.Dashboard },
             onSearchClick = { currentScreen = Screen.SearchDrugInformation },
            onHistoryClick = { currentScreen = Screen.ReportHistory },
            onProfileClick = { currentScreen = Screen.Profile }
        )
        
        Screen.LipidProfileEntry -> LipidProfileEntryScreen(
             initialValues = lipidProfileValues,
             onSubmitClick = { values ->
                lipidProfileValues = values
                selectedCategory = "Lipid Profile"
                currentScreen = Screen.ReviewValues
            },
             onBackClick = { currentScreen = if (previousScreen == Screen.ReviewValues) Screen.ReviewValues else Screen.ManualEntry },
             onHomeClick = { currentScreen = Screen.Dashboard },
             onSearchClick = { currentScreen = Screen.SearchDrugInformation },
            onHistoryClick = { currentScreen = Screen.ReportHistory },
            onProfileClick = { currentScreen = Screen.Profile }
        )
        
        Screen.KidneyFunctionEntry -> KidneyFunctionEntryScreen(
             initialValues = kidneyFunctionValues,
             onSubmitClick = { values ->
                kidneyFunctionValues = values
                selectedCategory = "Kidney Function"
                currentScreen = Screen.ReviewValues
            },
             onBackClick = { currentScreen = if (previousScreen == Screen.ReviewValues) Screen.ReviewValues else Screen.ManualEntry },
             onHomeClick = { currentScreen = Screen.Dashboard },
             onSearchClick = { currentScreen = Screen.SearchDrugInformation },
            onHistoryClick = { currentScreen = Screen.ReportHistory },
            onProfileClick = { currentScreen = Screen.Profile }
        )
        
        Screen.LiverFunctionEntry -> LiverFunctionEntryScreen(
             initialValues = liverFunctionValues,
             onSubmitClick = { values ->
                liverFunctionValues = values
                selectedCategory = "Liver Function"
                currentScreen = Screen.ReviewValues
            },
             onBackClick = { currentScreen = if (previousScreen == Screen.ReviewValues) Screen.ReviewValues else Screen.ManualEntry },
             onHomeClick = { currentScreen = Screen.Dashboard },
             onSearchClick = { currentScreen = Screen.SearchDrugInformation },
            onHistoryClick = { currentScreen = Screen.ReportHistory },
            onProfileClick = { currentScreen = Screen.Profile }
        )
        
        Screen.ThyroidPanelEntry -> ThyroidPanelEntryScreen(
             initialValues = thyroidPanelValues,
             onSubmitClick = { values ->
                thyroidPanelValues = values
                selectedCategory = "Thyroid Panel"
                currentScreen = Screen.ReviewValues
            },
             onBackClick = { currentScreen = if (previousScreen == Screen.ReviewValues) Screen.ReviewValues else Screen.ManualEntry },
             onHomeClick = { currentScreen = Screen.Dashboard },
             onSearchClick = { currentScreen = Screen.SearchDrugInformation },
            onHistoryClick = { currentScreen = Screen.ReportHistory },
            onProfileClick = { currentScreen = Screen.Profile }
        )
        
        Screen.ReportAnalysis -> ReportAnalysisScreen(
             categoryName = selectedCategory,
             analysis = currentAnalysis,
             onBackClick = { currentScreen = Screen.ReviewValues },
             onHomeClick = { currentScreen = Screen.Dashboard },
             onViewNormalResultsClick = { currentScreen = Screen.NormalResults },
             onViewAbnormalResultsClick = { currentScreen = Screen.AbnormalResults },
             onViewDrugRecommendationsClick = { currentScreen = Screen.DrugRecommendations },
             onViewRiskSummaryClick = { currentScreen = Screen.RiskSummary },
             onSearchClick = { currentScreen = Screen.SearchDrugInformation },
             onHistoryClick = { currentScreen = Screen.ReportHistory },
             onProfileClick = { currentScreen = Screen.Profile }
        )

        Screen.NormalResults -> NormalResultsScreen(
            categoryName = selectedCategory,
            analysis = currentAnalysis,
            onBackClick = { currentScreen = Screen.ReportAnalysis },
            onHomeClick = { currentScreen = Screen.Dashboard },
            onBackToAnalysisClick = { currentScreen = Screen.ReportAnalysis },
            onSearchClick = { currentScreen = Screen.SearchDrugInformation },
            onHistoryClick = { currentScreen = Screen.ReportHistory },
            onProfileClick = { currentScreen = Screen.Profile }
        )

        Screen.AbnormalResults -> AbnormalResultsScreen(
            categoryName = selectedCategory,
            analysis = currentAnalysis,
            onBackClick = { currentScreen = Screen.ReportAnalysis },
            onHomeClick = { currentScreen = Screen.Dashboard },
            onBackToAnalysisClick = { currentScreen = Screen.ReportAnalysis },
            onViewRecommendationsClick = { currentScreen = Screen.DrugRecommendations },
            onSearchClick = { currentScreen = Screen.SearchDrugInformation },
            onHistoryClick = { currentScreen = Screen.ReportHistory },
            onProfileClick = { currentScreen = Screen.Profile }
        )

        Screen.RiskSummary -> RiskSummaryScreen(
            categoryName = selectedCategory,
            analysis = currentAnalysis,
            onBackClick = { currentScreen = Screen.ReportAnalysis },
            onHomeClick = { currentScreen = Screen.Dashboard },
            onBackToAnalysisClick = { currentScreen = Screen.ReportAnalysis },
            onViewRecommendationsClick = { currentScreen = Screen.DrugRecommendations },
            onSearchClick = { currentScreen = Screen.SearchDrugInformation },
            onHistoryClick = { currentScreen = Screen.ReportHistory },
            onProfileClick = { currentScreen = Screen.Profile }
        )
        
        Screen.DrugRecommendations -> DrugRecommendationsScreen(
            categoryName = selectedCategory,
            recommendations = drugRecommendations,
            onBackClick = { currentScreen = Screen.ReportAnalysis },
            onHomeClick = { currentScreen = Screen.Dashboard },
            onSafetyWarningsClick = { currentScreen = Screen.SafetyWarnings },
            onCounsellingNotesClick = { currentScreen = Screen.CounsellingNotes },
            onDrugDetailsClick = { /* recommendation -> Mock Drug Details navigation */ },
            onSearchClick = { currentScreen = Screen.SearchDrugInformation },
            onHistoryClick = { currentScreen = Screen.ReportHistory },
            onProfileClick = { currentScreen = Screen.Profile },
            onUploadClick = { currentScreen = Screen.Upload }
        )

        Screen.SafetyWarnings -> SafetyWarningsScreen(
            onBackClick = { currentScreen = Screen.DrugRecommendations },
            onHomeClick = { currentScreen = Screen.Dashboard },
            onSearchClick = { currentScreen = Screen.SearchDrugInformation },
            onHistoryClick = { currentScreen = Screen.ReportHistory },
            onProfileClick = { currentScreen = Screen.Profile }
        )

        Screen.CounsellingNotes -> CounsellingNotesScreen(
            onBackClick = { currentScreen = Screen.DrugRecommendations },
            onHomeClick = { currentScreen = Screen.Dashboard },
            onSearchClick = { currentScreen = Screen.SearchDrugInformation },
            onHistoryClick = { currentScreen = Screen.ReportHistory },
            onProfileClick = { currentScreen = Screen.Profile }
        )

        Screen.ForgotPassword -> ForgotPasswordScreen(
            onBackClick = { currentScreen = Screen.Login },
            onBackToLoginClick = { currentScreen = Screen.Login },
            onSendOtpClick = { email ->
                userEmail = email
                currentScreen = Screen.CheckEmail
            }
        )

        Screen.CheckEmail -> CheckEmailScreen(
            email = userEmail,
            onBackToLoginClick = { currentScreen = Screen.Login },
            onVerifyOtpClick = { _ ->
                currentScreen = Screen.ResetPassword
            },
            onResendOtpClick = { /* Already handled in screen */ }
        )

        Screen.ResetPassword -> ResetPasswordScreen(
            email = userEmail,
            onBackClick = { currentScreen = Screen.Login },
            onBackToLoginClick = { currentScreen = Screen.Login },
            onResetPasswordClick = {
                currentScreen = Screen.Login
            }
        )
        
        Screen.ReportHistory -> ReportHistoryScreen(
            userId = loggedInUserId ?: 0,
            onBackClick = { currentScreen = Screen.Dashboard },
            onHomeClick = { currentScreen = Screen.Dashboard },
            onReportClick = { report -> 
                selectedReport = report
                currentScreen = Screen.ReportDetail
            },
            onNavigationHomeClick = { currentScreen = Screen.Dashboard },
            onUploadClick = { currentScreen = Screen.Upload },
            onSearchClick = { currentScreen = Screen.SearchDrugInformation },
            onProfileClick = { currentScreen = Screen.Profile }
        )
        
        Screen.SearchDrugInformation -> SearchDrugInformationScreen(
            onBackClick = { currentScreen = Screen.Dashboard },
            onHomeClick = { currentScreen = Screen.Dashboard },
            onSearchClick = { query -> 
                searchQuery = query
                previousScreen = Screen.SearchDrugInformation
                scope.launch {
                    try {
                        val response = com.simats.drugssearch.network.RetrofitClient.instance.searchDrugs(query)
                        if (response.isSuccessful) {
                            searchResults = response.body() ?: emptyList()
                            currentScreen = Screen.SearchResults
                        } else { 
                            searchResults = emptyList() 
                            currentScreen = Screen.SearchResults
                        }
                    } catch (e: Exception) { 
                        e.printStackTrace() 
                        currentScreen = Screen.SearchResults
                    }
                }
            },
            onPopularDrugClick = { drugName ->
                  searchQuery = drugName
                  previousScreen = Screen.SearchDrugInformation
                  scope.launch {
                    try {
                       val response = com.simats.drugssearch.network.RetrofitClient.instance.searchDrugs(drugName)
                       if (response.isSuccessful) {
                           searchResults = response.body() ?: emptyList()
                           currentScreen = Screen.SearchResults
                       } else {
                           searchResults = emptyList()
                           currentScreen = Screen.SearchResults
                       }
                    } catch (e: Exception) { 
                        e.printStackTrace() 
                        currentScreen = Screen.SearchResults
                    }
                }
            },
            onBrowseCategoryClick = { currentScreen = Screen.DrugCategories },
            onNavigationHomeClick = { currentScreen = Screen.Dashboard },
            onUploadClick = { currentScreen = Screen.Upload },
            onHistoryClick = { currentScreen = Screen.ReportHistory },
            onProfileClick = { currentScreen = Screen.Profile }
        )
        
        Screen.SearchResults -> SearchResultsScreen(
            searchQuery = searchQuery,
            drugs = searchResults,
            onBackClick = { currentScreen = previousScreen ?: Screen.SearchDrugInformation },
            onHomeClick = { currentScreen = Screen.Dashboard },
            onDrugClick = { drug -> 
                selectedDrug = drug
                currentScreen = Screen.DrugDetails
            },
            onNavigationHomeClick = { currentScreen = Screen.Dashboard },
            onUploadClick = { currentScreen = Screen.Upload },
            onHistoryClick = { currentScreen = Screen.ReportHistory },
            onProfileClick = { currentScreen = Screen.Profile }
        )
        
        Screen.DrugDetails -> {
            if (selectedDrug != null) {
                val uiDrug = DrugDetails(
                    name = selectedDrug!!.drugName,
                    genericName = selectedDrug!!.genericName ?: "",
                    drugCategory = selectedDrug!!.drugCategory ?: "",
                    indication = selectedDrug!!.indication ?: "",
                    typicalDosage = selectedDrug!!.commonDosage ?: "",
                    sideEffects = selectedDrug!!.sideEffects ?: "",
                    safetyWarnings = selectedDrug!!.safetyWarnings ?: "",
                    storageDetails = selectedDrug!!.storageDetails ?: ""
                )
                DrugDetailsScreen(
                    drug = uiDrug,
                    onBackClick = { currentScreen = Screen.SearchResults },
                    onHomeClick = { currentScreen = Screen.Dashboard },
                    onNavigationHomeClick = { currentScreen = Screen.Dashboard },
                    onUploadClick = { currentScreen = Screen.Upload },
                    onHistoryClick = { currentScreen = Screen.ReportHistory },
                    onProfileClick = { currentScreen = Screen.Profile }
                )
            } else {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) { Text("No drug selected") }
            }
        }
        
        Screen.Profile -> {
            key(loggedInUserName, userEmail, userPhone, userDob, userGender) {
                ProfileScreen(
                     userName = loggedInUserName,
                     userEmail = userEmail,
                     userPhone = userPhone,
                     totalReports = dashboardTotalReports,
                     normalReports = dashboardNormalReports,
                     abnormalReports = dashboardAbnormalReports,
                     onBackClick = { currentScreen = Screen.Dashboard },
                     onPersonalInfoClick = { currentScreen = Screen.PersonalInformation },
                     onPrivacySecurityClick = { currentScreen = Screen.PrivacySecurity },
                     onLogoutClick = {
                          sessionManager.clearSession()
                          loggedInUserId = null
                          loggedInUserName = ""
                          userEmail = ""
                          userPhone = ""
                          userDob = ""
                          userGender = ""
                          currentScreen = Screen.Welcome
                     },
                     onAboutAppClick = { currentScreen = Screen.AboutApp },
                     onHelpSupportClick = { currentScreen = Screen.HelpSupport },
                     onHomeClick = { currentScreen = Screen.Dashboard },
                     onNavigationHomeClick = { currentScreen = Screen.Dashboard },
                     onSearchClick = { currentScreen = Screen.SearchDrugInformation },
                     onHistoryClick = { currentScreen = Screen.ReportHistory },
                     onUploadClick = { currentScreen = Screen.Upload }
                )
            }
        }
        
        Screen.AboutApp -> AboutAppScreen(
            onBackClick = { currentScreen = Screen.Profile },
            onHomeClick = { currentScreen = Screen.Dashboard }
        )

        Screen.PersonalInformation -> PersonalInformationScreen(
            userId = loggedInUserId ?: 0,
            initialName = loggedInUserName,
            initialEmail = userEmail,
            initialPhone = userPhone,
            initialDob = userDob,
            initialGender = userGender,
            onBackClick = { currentScreen = Screen.Profile },
            onHomeClick = { currentScreen = Screen.Dashboard },
            onSaveClick = { name, email, phone, dob, gender ->
                loggedInUserName = name
                userEmail = email
                userPhone = phone
                userDob = dob
                userGender = gender
                sessionManager.saveSession(loggedInUserId ?: 0, name, email, phone, dob, gender)
                currentScreen = Screen.Profile
            },
            onCancelClick = { currentScreen = Screen.Profile }
        )

        Screen.PrivacySecurity -> PrivacySecurityScreen(
            onBackClick = { currentScreen = Screen.Profile },
            onHomeClick = { currentScreen = Screen.Dashboard },
            onChangePasswordClick = { currentScreen = Screen.ChangePassword },
            onProfileClick = { currentScreen = Screen.Profile },
            onUploadClick = { currentScreen = Screen.Upload },
            onSearchClick = { currentScreen = Screen.SearchDrugInformation },
            onHistoryClick = { currentScreen = Screen.ReportHistory },
            onLogoutClick = {
                sessionManager.clearSession()
                loggedInUserId = null
                loggedInUserName = ""
                userEmail = ""
                userPhone = ""
                userDob = ""
                userGender = ""
                currentScreen = Screen.Welcome
            }
        )

        Screen.DrugCategories -> DrugCategoriesScreen(
            onBackClick = { currentScreen = Screen.SearchDrugInformation },
            onHomeClick = { currentScreen = Screen.Dashboard },
            onCategoryClick = { category ->
                val cleanCategoryName = category.name.replace("\n", " ").trim()
                
                searchQuery = cleanCategoryName // Keep the UI label as selected
                previousScreen = Screen.DrugCategories
                scope.launch {
                    try {
                        val response = com.simats.drugssearch.network.RetrofitClient.instance.searchDrugs(
                            query = cleanCategoryName,
                            category = cleanCategoryName
                        )
                        if (response.isSuccessful) {
                            searchResults = response.body() ?: emptyList()
                            currentScreen = Screen.SearchResults
                        } else {
                            searchResults = emptyList()
                            currentScreen = Screen.SearchResults
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        currentScreen = Screen.SearchResults
                    }
                }
            },
            onNavigationHomeClick = { currentScreen = Screen.Dashboard },
            onUploadClick = { currentScreen = Screen.Upload },
            onHistoryClick = { currentScreen = Screen.ReportHistory },
            onProfileClick = { currentScreen = Screen.Profile }
        )

        Screen.ChangePassword -> ChangePasswordScreen(
            userId = loggedInUserId ?: 0,
            onBackClick = { currentScreen = Screen.PrivacySecurity }
        )
        
        Screen.ReportDetail -> {
             if (selectedReport != null) {
                 ReportDetailScreen(
                     report = selectedReport!!,
                     onBackClick = { currentScreen = Screen.ReportHistory },
                     onHomeClick = { currentScreen = Screen.Dashboard },
                     onNavigationHomeClick = { currentScreen = Screen.Dashboard },
                    onUploadClick = { currentScreen = Screen.Upload },
                    onSearchClick = { currentScreen = Screen.SearchDrugInformation },
                    onProfileClick = { currentScreen = Screen.Profile }
                 )
             }
        }
        
        Screen.HelpSupport -> HelpSupportScreen(
            onBackClick = { currentScreen = Screen.Profile },
            onHomeClick = { currentScreen = Screen.Dashboard },
            onFaqClick = { currentScreen = Screen.FAQ },
            onAboutClick = { currentScreen = Screen.AboutApp },
            onCallClick = { 
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:1-800-432-584")
                }
                context.startActivity(intent)
            },
            onEmailClick = {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:support@drugssearch.com")
                    putExtra(Intent.EXTRA_SUBJECT, "Support Request - DrugsSearch")
                }
                context.startActivity(intent)
            }
        )

        Screen.FAQ -> FAQScreen(
            onBackClick = { currentScreen = Screen.HelpSupport },
            onHomeClick = { currentScreen = Screen.Dashboard }
        )
    }
}
