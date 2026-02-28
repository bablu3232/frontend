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

    var showPrivacyPolicy by remember { mutableStateOf(false) }
    var showTermsOfService by remember { mutableStateOf(false) }

    var currentScreen by remember { mutableStateOf(Screen.Splash) }
    var previousScreen by remember { mutableStateOf<Screen?>(null) }
    
    // Custom Back Stack Management
    var screenStack by remember { mutableStateOf(listOf<Screen>()) }
    
    fun navigateTo(newScreen: Screen) {
        // Prevent adding duplicate screens to the top of the stack
        if (currentScreen != newScreen) {
             // If navigating to Dashboard or Login, clear the history stack
            if (newScreen == Screen.Dashboard || newScreen == Screen.Welcome || newScreen == Screen.Login) {
                screenStack = emptyList()
            } else {
                 screenStack = screenStack + currentScreen
            }
            currentScreen = newScreen
        }
    }

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
                    onSuccess = { navigateTo(Screen.Dashboard) },
                    onError = { navigateTo(Screen.Login) }
                )
            } else {
                navigateTo(Screen.Dashboard)
            }
        } else if (sessionManager.isLoggedIn()) {
            navigateTo(Screen.Dashboard)
        } else {
            navigateTo(Screen.Welcome)
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

    // Fetch dashboard stats when user logs in or returns to key screens
    LaunchedEffect(loggedInUserId, currentScreen) {
        if (currentScreen == Screen.Dashboard || currentScreen == Screen.Profile || currentScreen == Screen.ReportHistory) {
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

    // Global Back Handler
    androidx.activity.compose.BackHandler(enabled = true) {
        if (screenStack.isNotEmpty()) {
            // Pop the last screen from the stack
            val previousScreen = screenStack.last()
            screenStack = screenStack.dropLast(1)
            currentScreen = previousScreen
        } else {
            // If the stack is empty (e.g. on Dashboard), let the system handle closing the app
            if (currentScreen == Screen.Dashboard || currentScreen == Screen.Welcome) {
               activity?.finish()
            } else {
                // Failsafe: if we somehow got stuck on a sub-screen with an empty stack, go to Dashboard
                navigateTo(Screen.Dashboard)
            }
        }
    }

    // Helper to safely get value from map
    fun getVal(map: Map<String, String>, key: String): String = map[key] ?: ""

    when (currentScreen) {
        Screen.Splash -> SplashScreen(onSplashComplete = {
            handleSplashComplete()
        })
        
        Screen.Welcome -> WelcomeScreen(
            onLoginClick = { navigateTo(Screen.Login) },
            onCreateAccountClick = { navigateTo(Screen.Register) }
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
                navigateTo(Screen.Dashboard) 
            },
            onRegisterClick = { navigateTo(Screen.Register) },
            onForgotPasswordClick = { navigateTo(Screen.ForgotPassword) },
            onPrivacyPolicyClick = { showPrivacyPolicy = true },
            onTermsOfServiceClick = { showTermsOfService = true }
        )
        
        Screen.Register -> RegisterScreen(
            onCreateAccountClick = { _, email, _, _ -> 
                userEmail = email
                navigateTo(Screen.VerifyEmail) 
            },
            onLoginClick = { navigateTo(Screen.Login) },
            onPrivacyPolicyClick = { showPrivacyPolicy = true },
            onTermsOfServiceClick = { showTermsOfService = true }
        )
        
        Screen.VerifyEmail -> VerifyEmailScreen(
            email = userEmail,
            onVerifyClick = { _ -> navigateTo(Screen.Login) },
            onBackClick = { navigateTo(Screen.Register) }
        )
        
        Screen.Dashboard -> DashboardScreen(
            userName = loggedInUserName,
            totalReports = dashboardTotalReports,
            normalReports = dashboardNormalReports,
            abnormalReports = dashboardAbnormalReports,
            onUploadClick = { navigateTo(Screen.Upload) },
            onSearchClick = { navigateTo(Screen.SearchDrugInformation) },
            onHistoryClick = { navigateTo(Screen.ReportHistory) },
            onProfileClick = { navigateTo(Screen.Profile) },
            onHomeClick = { /* Already on dashboard */ },
            onViewAllClick = { navigateTo(Screen.ReportHistory) }
        )
        
        Screen.Upload -> UploadScreen(
            onBackClick = { navigateTo(Screen.Dashboard) },
            onHomeClick = { navigateTo(Screen.Dashboard) },
            onUploadReportClick = { navigateTo(Screen.FileSelected) },
            onManualEntryClick = { 
                currentReportId = null // Reset for manual entry
                navigateTo(Screen.ManualEntry) 
            },
            onSearchClick = { navigateTo(Screen.SearchDrugInformation) },
            onHistoryClick = { navigateTo(Screen.ReportHistory) },
            onProfileClick = { navigateTo(Screen.Profile) }
        )
        
        Screen.FileSelected -> FileSelectedScreen(
            userId = loggedInUserId,
            onBackClick = { navigateTo(Screen.Upload) },
            onHomeClick = { navigateTo(Screen.Dashboard) },
            onChooseDifferentFileClick = { /* Handled in screen */ },
            onUploadSuccess = { values, category, recommendations, patientDetails, reportId ->
                selectedCategory = category
                drugRecommendations = recommendations
                currentPatientDetails = patientDetails
                currentReportId = reportId
                // For OCR uploads, pass ALL extracted parameters to review/analysis
                // Don't filter through category-specific models — those are for manual entry only
                reviewValuesMap = values
                navigateTo(Screen.ReviewValues)
            },
            onSearchClick = { navigateTo(Screen.SearchDrugInformation) },
            onHistoryClick = { navigateTo(Screen.ReportHistory) },
            onProfileClick = { navigateTo(Screen.Profile) }
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
                onBackClick = { navigateTo(Screen.Upload) },
                onHomeClick = { navigateTo(Screen.Dashboard) },
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
                                
                                navigateTo(Screen.ReportAnalysis)
                            } else {
                                // Request failed
                                navigateTo(Screen.ReportAnalysis) 
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            navigateTo(Screen.ReportAnalysis)
                        }
                    }
                },
                onSearchClick = { navigateTo(Screen.SearchDrugInformation) },
                onHistoryClick = { navigateTo(Screen.ReportHistory) },
                onProfileClick = { navigateTo(Screen.Profile) }
            )
        }
        
        Screen.ManualEntry -> ManualEntryScreen(
            onBackClick = { navigateTo(Screen.Dashboard) },
            onHomeClick = { navigateTo(Screen.Dashboard) },
            onCategorySelected = { category -> 
                // Reset state for new entry
                drugRecommendations = emptyList()
                currentAnalysis = null
                currentReportId = null
                currentPatientDetails = null
                currentRemarks = ""
                
                selectedCategory = category
                navigateTo(when (category) {
                    "Blood Count" -> Screen.BloodCountEntry
                    "Metabolic Panel" -> Screen.MetabolicPanelEntry
                    "Lipid Profile" -> Screen.LipidProfileEntry
                    "Kidney Function" -> Screen.KidneyFunctionEntry
                    "Liver Function" -> Screen.LiverFunctionEntry
                    "Thyroid Panel" -> Screen.ThyroidPanelEntry
                    else -> Screen.ManualEntry
                })
            },
            onSearchClick = { navigateTo(Screen.SearchDrugInformation) },
            onHistoryClick = { navigateTo(Screen.ReportHistory) },
            onProfileClick = { navigateTo(Screen.Profile) }
        )
        
        Screen.BloodCountEntry -> BloodCountEntryScreen(
            initialValues = bloodCountValues, 
            onBackClick = { navigateTo(if (previousScreen == Screen.ReviewValues) Screen.ReviewValues else Screen.ManualEntry) },
            onHomeClick = { navigateTo(Screen.Dashboard) },
            onSubmitClick = { values -> 
                bloodCountValues = values
                selectedCategory = "Blood Count"
                navigateTo(Screen.ReviewValues) 
            },
            onSearchClick = { navigateTo(Screen.SearchDrugInformation) },
            onHistoryClick = { navigateTo(Screen.ReportHistory) },
            onProfileClick = { navigateTo(Screen.Profile) }
        )
        
        Screen.MetabolicPanelEntry -> MetabolicPanelEntryScreen(
            initialValues = metabolicPanelValues,
            onSubmitClick = { values ->
                metabolicPanelValues = values
                selectedCategory = "Metabolic Panel"
                navigateTo(Screen.ReviewValues)
            },
             onBackClick = { navigateTo(if (previousScreen == Screen.ReviewValues) Screen.ReviewValues else Screen.ManualEntry) },
             onHomeClick = { navigateTo(Screen.Dashboard) },
             onSearchClick = { navigateTo(Screen.SearchDrugInformation) },
            onHistoryClick = { navigateTo(Screen.ReportHistory) },
            onProfileClick = { navigateTo(Screen.Profile) }
        )
        
        Screen.LipidProfileEntry -> LipidProfileEntryScreen(
             initialValues = lipidProfileValues,
             onSubmitClick = { values ->
                lipidProfileValues = values
                selectedCategory = "Lipid Profile"
                navigateTo(Screen.ReviewValues)
            },
             onBackClick = { navigateTo(if (previousScreen == Screen.ReviewValues) Screen.ReviewValues else Screen.ManualEntry) },
             onHomeClick = { navigateTo(Screen.Dashboard) },
             onSearchClick = { navigateTo(Screen.SearchDrugInformation) },
            onHistoryClick = { navigateTo(Screen.ReportHistory) },
            onProfileClick = { navigateTo(Screen.Profile) }
        )
        
        Screen.KidneyFunctionEntry -> KidneyFunctionEntryScreen(
             initialValues = kidneyFunctionValues,
             onSubmitClick = { values ->
                kidneyFunctionValues = values
                selectedCategory = "Kidney Function"
                navigateTo(Screen.ReviewValues)
            },
             onBackClick = { navigateTo(if (previousScreen == Screen.ReviewValues) Screen.ReviewValues else Screen.ManualEntry) },
             onHomeClick = { navigateTo(Screen.Dashboard) },
             onSearchClick = { navigateTo(Screen.SearchDrugInformation) },
            onHistoryClick = { navigateTo(Screen.ReportHistory) },
            onProfileClick = { navigateTo(Screen.Profile) }
        )
        
        Screen.LiverFunctionEntry -> LiverFunctionEntryScreen(
             initialValues = liverFunctionValues,
             onSubmitClick = { values ->
                liverFunctionValues = values
                selectedCategory = "Liver Function"
                navigateTo(Screen.ReviewValues)
            },
             onBackClick = { navigateTo(if (previousScreen == Screen.ReviewValues) Screen.ReviewValues else Screen.ManualEntry) },
             onHomeClick = { navigateTo(Screen.Dashboard) },
             onSearchClick = { navigateTo(Screen.SearchDrugInformation) },
            onHistoryClick = { navigateTo(Screen.ReportHistory) },
            onProfileClick = { navigateTo(Screen.Profile) }
        )
        
        Screen.ThyroidPanelEntry -> ThyroidPanelEntryScreen(
             initialValues = thyroidPanelValues,
             onSubmitClick = { values ->
                thyroidPanelValues = values
                selectedCategory = "Thyroid Panel"
                navigateTo(Screen.ReviewValues)
            },
             onBackClick = { navigateTo(if (previousScreen == Screen.ReviewValues) Screen.ReviewValues else Screen.ManualEntry) },
             onHomeClick = { navigateTo(Screen.Dashboard) },
             onSearchClick = { navigateTo(Screen.SearchDrugInformation) },
            onHistoryClick = { navigateTo(Screen.ReportHistory) },
            onProfileClick = { navigateTo(Screen.Profile) }
        )
        
        Screen.ReportAnalysis -> ReportAnalysisScreen(
             categoryName = selectedCategory,
             analysis = currentAnalysis,
             onBackClick = { navigateTo(Screen.ReviewValues) },
             onHomeClick = { navigateTo(Screen.Dashboard) },
             onViewNormalResultsClick = { navigateTo(Screen.NormalResults) },
             onViewAbnormalResultsClick = { navigateTo(Screen.AbnormalResults) },
             onViewDrugRecommendationsClick = { navigateTo(Screen.DrugRecommendations) },
             onViewRiskSummaryClick = { navigateTo(Screen.RiskSummary) },
             onSearchClick = { navigateTo(Screen.SearchDrugInformation) },
             onHistoryClick = { navigateTo(Screen.ReportHistory) },
             onProfileClick = { navigateTo(Screen.Profile) }
        )

        Screen.NormalResults -> NormalResultsScreen(
            categoryName = selectedCategory,
            analysis = currentAnalysis,
            onBackClick = { navigateTo(Screen.ReportAnalysis) },
            onHomeClick = { navigateTo(Screen.Dashboard) },
            onBackToAnalysisClick = { navigateTo(Screen.ReportAnalysis) },
            onSearchClick = { navigateTo(Screen.SearchDrugInformation) },
            onHistoryClick = { navigateTo(Screen.ReportHistory) },
            onProfileClick = { navigateTo(Screen.Profile) }
        )

        Screen.AbnormalResults -> AbnormalResultsScreen(
            categoryName = selectedCategory,
            analysis = currentAnalysis,
            onBackClick = { navigateTo(Screen.ReportAnalysis) },
            onHomeClick = { navigateTo(Screen.Dashboard) },
            onBackToAnalysisClick = { navigateTo(Screen.ReportAnalysis) },
            onViewRecommendationsClick = { navigateTo(Screen.DrugRecommendations) },
            onSearchClick = { navigateTo(Screen.SearchDrugInformation) },
            onHistoryClick = { navigateTo(Screen.ReportHistory) },
            onProfileClick = { navigateTo(Screen.Profile) }
        )

        Screen.RiskSummary -> RiskSummaryScreen(
            categoryName = selectedCategory,
            analysis = currentAnalysis,
            onBackClick = { navigateTo(Screen.ReportAnalysis) },
            onHomeClick = { navigateTo(Screen.Dashboard) },
            onBackToAnalysisClick = { navigateTo(Screen.ReportAnalysis) },
            onViewRecommendationsClick = { navigateTo(Screen.DrugRecommendations) },
            onSearchClick = { navigateTo(Screen.SearchDrugInformation) },
            onHistoryClick = { navigateTo(Screen.ReportHistory) },
            onProfileClick = { navigateTo(Screen.Profile) }
        )
        
        Screen.DrugRecommendations -> DrugRecommendationsScreen(
            categoryName = selectedCategory,
            recommendations = drugRecommendations,
            onBackClick = { navigateTo(Screen.ReportAnalysis) },
            onHomeClick = { navigateTo(Screen.Dashboard) },
            onSafetyWarningsClick = { navigateTo(Screen.SafetyWarnings) },
            onCounsellingNotesClick = { navigateTo(Screen.CounsellingNotes) },
            onDrugDetailsClick = { /* recommendation -> Mock Drug Details navigation */ },
            onSearchClick = { navigateTo(Screen.SearchDrugInformation) },
            onHistoryClick = { navigateTo(Screen.ReportHistory) },
            onProfileClick = { navigateTo(Screen.Profile) },
            onUploadClick = { navigateTo(Screen.Upload) }
        )

        Screen.SafetyWarnings -> SafetyWarningsScreen(
            onBackClick = { navigateTo(Screen.DrugRecommendations) },
            onHomeClick = { navigateTo(Screen.Dashboard) },
            onSearchClick = { navigateTo(Screen.SearchDrugInformation) },
            onHistoryClick = { navigateTo(Screen.ReportHistory) },
            onProfileClick = { navigateTo(Screen.Profile) }
        )

        Screen.CounsellingNotes -> CounsellingNotesScreen(
            onBackClick = { navigateTo(Screen.DrugRecommendations) },
            onHomeClick = { navigateTo(Screen.Dashboard) },
            onSearchClick = { navigateTo(Screen.SearchDrugInformation) },
            onHistoryClick = { navigateTo(Screen.ReportHistory) },
            onProfileClick = { navigateTo(Screen.Profile) }
        )

        Screen.ForgotPassword -> ForgotPasswordScreen(
            onBackClick = { navigateTo(Screen.Login) },
            onBackToLoginClick = { navigateTo(Screen.Login) },
            onSendOtpClick = { email ->
                userEmail = email
                navigateTo(Screen.CheckEmail)
            }
        )

        Screen.CheckEmail -> CheckEmailScreen(
            email = userEmail,
            onBackToLoginClick = { navigateTo(Screen.Login) },
            onVerifyOtpClick = { _ ->
                navigateTo(Screen.ResetPassword)
            },
            onResendOtpClick = { /* Already handled in screen */ }
        )

        Screen.ResetPassword -> ResetPasswordScreen(
            email = userEmail,
            onBackClick = { navigateTo(Screen.Login) },
            onBackToLoginClick = { navigateTo(Screen.Login) },
            onResetPasswordClick = {
                navigateTo(Screen.Login)
            }
        )
        
        Screen.ReportHistory -> ReportHistoryScreen(
            userId = loggedInUserId ?: 0,
            onBackClick = { navigateTo(Screen.Dashboard) },
            onHomeClick = { navigateTo(Screen.Dashboard) },
            onReportClick = { report -> 
                selectedReport = report
                navigateTo(Screen.ReportDetail)
            },
            onNavigationHomeClick = { navigateTo(Screen.Dashboard) },
            onUploadClick = { navigateTo(Screen.Upload) },
            onSearchClick = { navigateTo(Screen.SearchDrugInformation) },
            onProfileClick = { navigateTo(Screen.Profile) }
        )
        
        Screen.SearchDrugInformation -> SearchDrugInformationScreen(
            onBackClick = { navigateTo(Screen.Dashboard) },
            onHomeClick = { navigateTo(Screen.Dashboard) },
            onSearchClick = { query -> 
                searchQuery = query
                previousScreen = Screen.SearchDrugInformation
                scope.launch {
                    try {
                        val response = com.simats.drugssearch.network.RetrofitClient.instance.searchDrugs(query)
                        if (response.isSuccessful) {
                            searchResults = response.body() ?: emptyList()
                            navigateTo(Screen.SearchResults)
                        } else { 
                            searchResults = emptyList() 
                            navigateTo(Screen.SearchResults)
                        }
                    } catch (e: Exception) { 
                        e.printStackTrace() 
                        navigateTo(Screen.SearchResults)
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
                           navigateTo(Screen.SearchResults)
                       } else {
                           searchResults = emptyList()
                           navigateTo(Screen.SearchResults)
                       }
                    } catch (e: Exception) { 
                        e.printStackTrace() 
                        navigateTo(Screen.SearchResults)
                    }
                }
            },
            onBrowseCategoryClick = { navigateTo(Screen.DrugCategories) },
            onNavigationHomeClick = { navigateTo(Screen.Dashboard) },
            onUploadClick = { navigateTo(Screen.Upload) },
            onHistoryClick = { navigateTo(Screen.ReportHistory) },
            onProfileClick = { navigateTo(Screen.Profile) }
        )
        
        Screen.SearchResults -> SearchResultsScreen(
            searchQuery = searchQuery,
            drugs = searchResults,
            onBackClick = { currentScreen = previousScreen ?: Screen.SearchDrugInformation },
            onHomeClick = { navigateTo(Screen.Dashboard) },
            onDrugClick = { drug -> 
                selectedDrug = drug
                navigateTo(Screen.DrugDetails)
            },
            onNavigationHomeClick = { navigateTo(Screen.Dashboard) },
            onUploadClick = { navigateTo(Screen.Upload) },
            onHistoryClick = { navigateTo(Screen.ReportHistory) },
            onProfileClick = { navigateTo(Screen.Profile) }
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
                    onBackClick = { navigateTo(Screen.SearchResults) },
                    onHomeClick = { navigateTo(Screen.Dashboard) },
                    onNavigationHomeClick = { navigateTo(Screen.Dashboard) },
                    onUploadClick = { navigateTo(Screen.Upload) },
                    onHistoryClick = { navigateTo(Screen.ReportHistory) },
                    onProfileClick = { navigateTo(Screen.Profile) }
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
                     totalReports = dashboardTotalReports,
                     normalReports = dashboardNormalReports,
                     abnormalReports = dashboardAbnormalReports,
                     onBackClick = { navigateTo(Screen.Dashboard) },
                     onPersonalInfoClick = { navigateTo(Screen.PersonalInformation) },
                     onPrivacySecurityClick = { navigateTo(Screen.PrivacySecurity) },
                     onLogoutClick = {
                          sessionManager.clearSession()
                          loggedInUserId = null
                          loggedInUserName = ""
                          userEmail = ""
                          userPhone = ""
                          userDob = ""
                          userGender = ""
                          navigateTo(Screen.Welcome)
                     },
                     onAboutAppClick = { navigateTo(Screen.AboutApp) },
                     onHelpSupportClick = { navigateTo(Screen.HelpSupport) },
                     onHomeClick = { navigateTo(Screen.Dashboard) },
                     onNavigationHomeClick = { navigateTo(Screen.Dashboard) },
                     onSearchClick = { navigateTo(Screen.SearchDrugInformation) },
                     onHistoryClick = { navigateTo(Screen.ReportHistory) },
                     onUploadClick = { navigateTo(Screen.Upload) }
                )
            }
        }
        
        Screen.AboutApp -> AboutAppScreen(
            onBackClick = { navigateTo(Screen.Profile) },
            onHomeClick = { navigateTo(Screen.Dashboard) }
        )

        Screen.PersonalInformation -> PersonalInformationScreen(
            userId = loggedInUserId ?: 0,
            initialName = loggedInUserName,
            initialEmail = userEmail,
            initialPhone = userPhone,
            initialDob = userDob,
            initialGender = userGender,
            onBackClick = { navigateTo(Screen.Profile) },
            onHomeClick = { navigateTo(Screen.Dashboard) },
            onSaveClick = { name, email, phone, dob, gender ->
                loggedInUserName = name
                userEmail = email
                userPhone = phone
                userDob = dob
                userGender = gender
                sessionManager.saveSession(loggedInUserId ?: 0, name, email, phone, dob, gender)
                navigateTo(Screen.Profile)
            },
            onCancelClick = { navigateTo(Screen.Profile) }
        )

        Screen.PrivacySecurity -> PrivacySecurityScreen(
            onBackClick = { navigateTo(Screen.Profile) },
            onHomeClick = { navigateTo(Screen.Dashboard) },
            onChangePasswordClick = { navigateTo(Screen.ChangePassword) },
            onProfileClick = { navigateTo(Screen.Profile) },
            onUploadClick = { navigateTo(Screen.Upload) },
            onSearchClick = { navigateTo(Screen.SearchDrugInformation) },
            onHistoryClick = { navigateTo(Screen.ReportHistory) },
            onLogoutClick = {
                sessionManager.clearSession()
                loggedInUserId = null
                loggedInUserName = ""
                userEmail = ""
                userPhone = ""
                userDob = ""
                userGender = ""
                navigateTo(Screen.Welcome)
            }
        )

        Screen.DrugCategories -> DrugCategoriesScreen(
            onBackClick = { navigateTo(Screen.SearchDrugInformation) },
            onHomeClick = { navigateTo(Screen.Dashboard) },
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
                            navigateTo(Screen.SearchResults)
                        } else {
                            searchResults = emptyList()
                            navigateTo(Screen.SearchResults)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        navigateTo(Screen.SearchResults)
                    }
                }
            },
            onNavigationHomeClick = { navigateTo(Screen.Dashboard) },
            onUploadClick = { navigateTo(Screen.Upload) },
            onHistoryClick = { navigateTo(Screen.ReportHistory) },
            onProfileClick = { navigateTo(Screen.Profile) }
        )

        Screen.ChangePassword -> ChangePasswordScreen(
            userId = loggedInUserId ?: 0,
            onBackClick = { navigateTo(Screen.PrivacySecurity) }
        )
        
        Screen.ReportDetail -> {
             if (selectedReport != null) {
                 ReportDetailScreen(
                     report = selectedReport!!,
                     onBackClick = { navigateTo(Screen.ReportHistory) },
                     onHomeClick = { navigateTo(Screen.Dashboard) },
                     onNavigationHomeClick = { navigateTo(Screen.Dashboard) },
                    onUploadClick = { navigateTo(Screen.Upload) },
                    onSearchClick = { navigateTo(Screen.SearchDrugInformation) },
                    onProfileClick = { navigateTo(Screen.Profile) }
                 )
             }
        }
        
        Screen.HelpSupport -> HelpSupportScreen(
            onBackClick = { navigateTo(Screen.Profile) },
            onHomeClick = { navigateTo(Screen.Dashboard) },
            onFaqClick = { navigateTo(Screen.FAQ) },
            onAboutClick = { navigateTo(Screen.AboutApp) },
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
            onBackClick = { navigateTo(Screen.HelpSupport) },
            onHomeClick = { navigateTo(Screen.Dashboard) }
        )
    }

    if (showPrivacyPolicy) {
        PrivacyPolicyScreen(
            onBackClick = { showPrivacyPolicy = false }
        )
    }

    if (showTermsOfService) {
        TermsOfServiceScreen(
            onBackClick = { showTermsOfService = false }
        )
    }
}
