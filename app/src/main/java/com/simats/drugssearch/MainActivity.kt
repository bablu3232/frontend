package com.simats.drugssearch

import android.os.Bundle
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

class MainActivity : ComponentActivity() {
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
    ReportAnalysis,
    ReportHistory,
    SearchDrugInformation,
    SearchResults,
    DrugDetails,
    Profile,
    ChangePassword,
    ReportDetail,
    CompareReports,
    DrugRecommendations
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf(Screen.Splash) }
    var previousScreen by remember { mutableStateOf<Screen?>(null) }
    
    // User Session
    var userEmail by remember { mutableStateOf("") }
    var loggedInUserId by remember { mutableStateOf<Int?>(null) }
    var loggedInUserName by remember { mutableStateOf("") }
    
    // Upload & Report State
    var selectedCategory by remember { mutableStateOf("") }
    var reviewValuesMap by remember { mutableStateOf(mapOf<String, String>()) }
    
    // Category Specific State Values
    var bloodCountValues by remember { mutableStateOf(BloodCountValues()) }
    var metabolicPanelValues by remember { mutableStateOf(MetabolicPanelValues()) }
    var lipidProfileValues by remember { mutableStateOf(LipidProfileValues()) }
    var kidneyFunctionValues by remember { mutableStateOf(KidneyFunctionValues()) }
    
    // Report History State
    var selectedReport by remember { mutableStateOf<HealthReport?>(null) }
    var selectedReport1 by remember { mutableStateOf<HealthReport?>(null) }
    var selectedReport2 by remember { mutableStateOf<HealthReport?>(null) }

    // Search State
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Drug>>(emptyList()) }
    var selectedDrug by remember { mutableStateOf<Drug?>(null) }

    val scope = rememberCoroutineScope()

    // Helper to safely get value from map
    fun getVal(map: Map<String, String>, key: String): String = map[key] ?: ""

    when (currentScreen) {
        Screen.Splash -> SplashScreen(onSplashComplete = { currentScreen = Screen.Welcome })
        
        Screen.Welcome -> WelcomeScreen(
            onLoginClick = { currentScreen = Screen.Login },
            onCreateAccountClick = { currentScreen = Screen.Register }
        )
        
        Screen.Login -> LoginScreen(
            onLoginSuccess = { userId, fullName, email -> 
                loggedInUserId = userId
                loggedInUserName = fullName
                currentScreen = Screen.Dashboard 
            },
            onRegisterClick = { currentScreen = Screen.Register },
            onForgotPasswordClick = { /* Navigate to Forgot Password */ }
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
            onUploadClick = { currentScreen = Screen.Upload },
            onSearchClick = { currentScreen = Screen.SearchDrugInformation },
            onHistoryClick = { currentScreen = Screen.ReportHistory },
            onProfileClick = { currentScreen = Screen.Profile },
            onHomeClick = { /* Already on dashboard */ }
        )
        
        Screen.Upload -> com.simats.drugssearch.ui.FileSelectedScreen(
            userId = loggedInUserId,
            onBackClick = { currentScreen = Screen.Dashboard },
            onHomeClick = { currentScreen = Screen.Dashboard },
            onChooseDifferentFileClick = { /* Trigger file picker again */ },
            onUploadSuccess = { values, category ->
                selectedCategory = category
                when (category) {
                    "Blood Count" -> {
                        bloodCountValues = BloodCountValues(
                            hemoglobin = getVal(values, "Hemoglobin"),
                            wbcCount = getVal(values, "WBC"),
                            rbcCount = getVal(values, "RBC"),
                            plateletCount = getVal(values, "Platelets"),
                            hematocrit = getVal(values, "Hematocrit")
                        )
                    }
                    "Metabolic Panel" -> {
                        metabolicPanelValues = MetabolicPanelValues(
                            bloodGlucose = getVal(values, "Glucose"),
                            sodium = getVal(values, "Sodium"),
                            potassium = getVal(values, "Potassium"),
                            calcium = getVal(values, "Calcium"),
                            bicarbonate = getVal(values, "Bicarbonate")
                        )
                    }
                     "Lipid Profile" -> {
                          lipidProfileValues = LipidProfileValues(
                              totalCholesterol = getVal(values, "Cholesterol"),
                              hdlCholesterol = getVal(values, "HDL"),
                              ldlCholesterol = getVal(values, "LDL"),
                              triglycerides = getVal(values, "Triglycerides")
                          )
                      }
                    "Kidney Function" -> {
                         kidneyFunctionValues = KidneyFunctionValues(
                             creatinine = getVal(values, "Creatinine"),
                             bun = getVal(values, "BUN"),
                             egfr = getVal(values, "eGFR"),
                             uricAcid = getVal(values, "Uric Acid")
                         )
                     }
                    else -> {
                        reviewValuesMap = values
                    }
                }
                currentScreen = Screen.ReviewValues
            },
            onSearchClick = { currentScreen = Screen.SearchDrugInformation },
            onHistoryClick = { currentScreen = Screen.ReportHistory },
            onProfileClick = { currentScreen = Screen.Profile }
        )
        
        Screen.FileSelected -> { currentScreen = Screen.Upload }
        
        Screen.ReviewValues -> {
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
                     "Cholesterol" to lipidProfileValues.totalCholesterol,
                     "HDL" to lipidProfileValues.hdlCholesterol,
                     "LDL" to lipidProfileValues.ldlCholesterol,
                     "Triglycerides" to lipidProfileValues.triglycerides
                 )
                 "Kidney Function" -> mapOf(
                     "Creatinine" to kidneyFunctionValues.creatinine,
                     "BUN" to kidneyFunctionValues.bun,
                     "eGFR" to kidneyFunctionValues.egfr,
                     "Uric Acid" to kidneyFunctionValues.uricAcid
                 )
                 else -> reviewValuesMap
             }

            val editTargetScreen = when (selectedCategory) {
                 "Blood Count" -> Screen.BloodCountEntry
                 "Metabolic Panel" -> Screen.MetabolicPanelEntry
                 "Lipid Profile" -> Screen.LipidProfileEntry
                 "Kidney Function" -> Screen.KidneyFunctionEntry
                 else -> Screen.ManualEntry 
            }

            ReviewValuesScreen(
                userId = loggedInUserId ?: 0,
                categoryName = selectedCategory,
                values = valuesForReview,
                onBackClick = { currentScreen = Screen.Upload },
                onHomeClick = { currentScreen = Screen.Dashboard },
                onEditClick = { 
                    previousScreen = Screen.ReviewValues
                    currentScreen = editTargetScreen 
                },
                onSubmitForAnalysisClick = { 
                    scope.launch {
                        try {
                            val parameters = valuesForReview.map { (k, v) ->
                                ReportParameter(name = k, value = v, unit = "", isNormal = false) 
                            }
                            
                            val req = SaveReportRequest(
                                userId = loggedInUserId ?: 0,
                                category = selectedCategory,
                                parameters = parameters
                            )
                            val response = com.simats.drugssearch.network.RetrofitClient.instance.saveReportData(req)
                            if (response.isSuccessful) {
                                currentScreen = Screen.ReportAnalysis
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        currentScreen = Screen.DrugRecommendations
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
                selectedCategory = category
                currentScreen = when (category) {
                    "Blood Count" -> Screen.BloodCountEntry
                    "Metabolic Panel" -> Screen.MetabolicPanelEntry
                    "Lipid Profile" -> Screen.LipidProfileEntry
                    "Kidney Function" -> Screen.KidneyFunctionEntry
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
        
        Screen.ReportAnalysis -> {
             Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) { Text("Analyzing Report...") }
             LaunchedEffect(Unit) {
                 currentScreen = Screen.DrugRecommendations
             }
        }
        
        Screen.DrugRecommendations -> DrugRecommendationsScreen(
            categoryName = selectedCategory,
            onBackClick = { currentScreen = Screen.ReviewValues },
            onHomeClick = { currentScreen = Screen.Dashboard },
            onDrugDetailsClick = { recommendation -> 
                 // Mock Drug Details navigation
            },
            onSearchClick = { currentScreen = Screen.SearchDrugInformation },
            onHistoryClick = { currentScreen = Screen.ReportHistory },
            onProfileClick = { currentScreen = Screen.Profile },
            onUploadClick = { currentScreen = Screen.Upload }
        )
        
        Screen.ReportHistory -> ReportHistoryScreen(
            userId = loggedInUserId ?: 0,
            onBackClick = { currentScreen = Screen.Dashboard },
            onHomeClick = { currentScreen = Screen.Dashboard },
            onReportClick = { report -> 
                selectedReport = report
                currentScreen = Screen.ReportDetail
            },
            onCompareClick = { 
                 if (selectedReport1 != null && selectedReport2 != null) {
                    currentScreen = Screen.CompareReports
                 }
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
                scope.launch {
                    try {
                        val response = com.simats.drugssearch.network.RetrofitClient.instance.searchDrugs(query)
                        if (response.isSuccessful) {
                            searchResults = response.body() ?: emptyList()
                            currentScreen = Screen.SearchResults
                        } else { searchResults = emptyList() }
                    } catch (e: Exception) { e.printStackTrace() }
                    currentScreen = Screen.SearchResults
                }
            },
            onPopularDrugClick = { drugName ->
                  searchQuery = drugName
                  scope.launch {
                    try {
                       val response = com.simats.drugssearch.network.RetrofitClient.instance.searchDrugs(drugName)
                       if (response.isSuccessful) {
                           searchResults = response.body() ?: emptyList()
                           currentScreen = Screen.SearchResults
                       }
                    } catch (e: Exception) { e.printStackTrace() }
                    currentScreen = Screen.SearchResults
                }
            },
            onBrowseCategoryClick = { /* Navigate to category browse */ },
            onNavigationHomeClick = { currentScreen = Screen.Dashboard },
            onUploadClick = { currentScreen = Screen.Upload },
            onHistoryClick = { currentScreen = Screen.ReportHistory },
            onProfileClick = { currentScreen = Screen.Profile }
        )
        
        Screen.SearchResults -> SearchResultsScreen(
            searchQuery = searchQuery,
            drugs = searchResults,
            onBackClick = { currentScreen = Screen.SearchDrugInformation },
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
                    name = selectedDrug!!.name,
                    condition = selectedDrug!!.condition ?: "",
                    commonUses = selectedDrug!!.description ?: "",
                    typicalDosage = selectedDrug!!.dosages.joinToString(", "),
                    sideEffects = selectedDrug!!.sideEffects ?: "",
                    precautions = selectedDrug!!.warnings ?: ""
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
        
        Screen.Profile -> ProfileScreen(
             onBackClick = { currentScreen = Screen.Dashboard },
             onLogoutClick = {
                  loggedInUserId = null
                  currentScreen = Screen.Welcome
             },
             onHomeClick = { currentScreen = Screen.Dashboard },
             onNavigationHomeClick = { currentScreen = Screen.Dashboard }, // Added distinct handler if needed
             onSearchClick = { currentScreen = Screen.SearchDrugInformation },
             onHistoryClick = { currentScreen = Screen.ReportHistory },
             onUploadClick = { currentScreen = Screen.Upload }
        )
        
        Screen.ChangePassword -> ChangePasswordScreen(
            userId = loggedInUserId ?: 0,
            onBackClick = { currentScreen = Screen.Profile }
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
        
        Screen.CompareReports -> {
             if (selectedReport1 != null && selectedReport2 != null) {
                 CompareReportsScreen(
                     report1 = selectedReport1!!,
                     report2 = selectedReport2!!,
                     onBackClick = { currentScreen = Screen.ReportHistory },
                     onHomeClick = { currentScreen = Screen.Dashboard },
                     onNavigationHomeClick = { currentScreen = Screen.Dashboard },
                    onUploadClick = { currentScreen = Screen.Upload },
                    onSearchClick = { currentScreen = Screen.SearchDrugInformation },
                    onProfileClick = { currentScreen = Screen.Profile }
                 )
             } else {
                 Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) { Text("Select two reports to compare") }
             }
        }
        else -> {
             Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                 Text("Screen under construction")
             }
        }
    }
}
