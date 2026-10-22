
package com.example.languagelearningapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import org.json.JSONArray


private val Mint = Color(0xFF8CE6C0)
private val Background = Color(0xFF10131F)
private val SurfaceDark = Color(0xFF1B2030)
private val MainText = Color(0xFFF4F5FA)
private val MutedText = Color(0xFF9DA6B9)
private val Gold = Color(0xFFFFD580)

private val languages = listOf(
    "English", "Sindhi", "Urdu", "Arabic", "Spanish",
    "French", "German", "Chinese", "Japanese", "Korean",
    "Turkish", "Persian", "Hindi", "Italian", "Portuguese",
    "Russian", "Bengali", "Punjabi", "Pashto", "Malay",
    "Indonesian", "Thai", "Dutch", "Swahili", "Greek"
)

private data class Lesson(
    val englishMeaning: String,
    val targetLanguageWord: String,
    val nativeLanguageTranslation: String,
    val pronunciation: String,
    val category: String
) {
    // Compatibility aliases used by the quiz and history features.
    val word: String get() = targetLanguageWord
    val translation: String get() = nativeLanguageTranslation
}

// Expanded starter deck. The current verified target-language content is Sindhi;
// additional language pairs should be added as curated translations, not guessed.
private val lessons = listOf(
    Lesson("Hello", "سلام", "Hello", "Salaam", "Greetings"),
    Lesson("Thank you", "مهرباني", "Thank you", "Mehrbani", "Greetings"),
    Lesson("How are you?", "توهان ڪيئن آهيو؟", "How are you?", "Tawhan keean aahiyo?", "Phrases"),
    Lesson("Water", "پاڻي", "Water", "Paani", "Vocabulary"),
    Lesson("Friend", "دوست", "Friend", "Dost", "Vocabulary"),
    Lesson("Good morning", "صبح جو سلام", "Good morning", "Subuh jo salaam", "Phrases"),
    Lesson("Book", "ڪتاب", "Book", "Kitaab", "Vocabulary"),
    Lesson("Please", "مهرباني ڪري", "Please", "Mehrbani kare", "Phrases"),
    Lesson("House", "گهر", "House", "Ghar", "Vocabulary"),
    Lesson("Goodbye", "خدا حافظ", "Goodbye", "Khuda hafiz", "Greetings"),
    Lesson("Yes", "ها", "Yes", "Haa", "Vocabulary"),
    Lesson("No", "نه", "No", "Na", "Vocabulary"),
    Lesson("Sorry", "معاف ڪجو", "Sorry", "Maaf kajo", "Phrases"),
    Lesson("Welcome", "ڀلي ڪري آيا", "Welcome", "Bhali kare aaya", "Greetings"),
    Lesson("Good night", "رات جو سلام", "Good night", "Raat jo salaam", "Phrases"),
    Lesson("Food", "کاڌو", "Food", "Khaadho", "Vocabulary"),
    Lesson("Milk", "کير", "Milk", "Kheer", "Vocabulary"),
    Lesson("Tea", "چانهه", "Tea", "Chaah", "Vocabulary"),
    Lesson("Coffee", "ڪافي", "Coffee", "Kaafi", "Vocabulary"),
    Lesson("Bread", "ماني", "Bread", "Maanee", "Vocabulary"),
    Lesson("Apple", "صوف", "Apple", "Soof", "Food"),
    Lesson("School", "اسڪول", "School", "School", "Places"),
    Lesson("Teacher", "استاد", "Teacher", "Ustaad", "People"),
    Lesson("Student", "شاگرد", "Student", "Shaagird", "People"),
    Lesson("Family", "خاندان", "Family", "Khandaan", "People"),
    Lesson("Mother", "امڙ", "Mother", "Amar", "Family"),
    Lesson("Father", "پيءُ", "Father", "Peeu", "Family"),
    Lesson("Brother", "ڀاءُ", "Brother", "Bhaau", "Family"),
    Lesson("Sister", "ڀيڻ", "Sister", "Bhen", "Family"),
    Lesson("Love", "محبت", "Love", "Muhabbat", "Feelings"),
    Lesson("Happy", "خوش", "Happy", "Khush", "Feelings"),
    Lesson("Beautiful", "سهڻو", "Beautiful", "Suhno", "Adjectives"),
    Lesson("Big", "وڏو", "Big", "Wado", "Adjectives"),
    Lesson("Small", "ننڍو", "Small", "Nindho", "Adjectives"),
    Lesson("Today", "اڄ", "Today", "Aj", "Time"),
    Lesson("Tomorrow", "سڀاڻي", "Tomorrow", "Subhaani", "Time"),
    Lesson("Yesterday", "ڪالهه", "Yesterday", "Kaalh", "Time"),
    Lesson("One", "هڪ", "One", "Hik", "Numbers"),
    Lesson("Two", "ٻه", "Two", "Ba", "Numbers"),
    Lesson("Three", "ٽي", "Three", "Tee", "Numbers"),
    Lesson("Where is the bathroom?", "باٿ روم ڪٿي آهي؟", "Where is the bathroom?", "Bathroom kithay aahe?", "Travel"),
    Lesson("I don't understand", "مان نٿو سمجهان", "I don't understand", "Maan natho samjhaan", "Travel"),
    Lesson("Help me, please", "مهرباني ڪري منهنجي مدد ڪريو", "Help me, please", "Mehrbani kare munhinji madad kayo", "Travel"),
    Lesson("How much does it cost?", "هي ڪيتري جو آهي؟", "How much does it cost?", "He ketri jo aahe?", "Shopping"),
    Lesson("I am learning Sindhi", "مان سنڌي سکي رهيو آهيان", "I am learning Sindhi", "Maan Sindhi sikhi rahyo aahiyan", "Phrases")
)

// Curated starter translations for the first 15 everyday lessons in all 25 supported languages.
private val starterTranslations: Map<String, Map<String, String>> = mapOf(
    "English" to mapOf("Hello" to "Hello", "Thank you" to "Thank you", "How are you?" to "How are you?", "Water" to "Water", "Friend" to "Friend", "Good morning" to "Good morning", "Book" to "Book", "Please" to "Please", "House" to "House", "Goodbye" to "Goodbye", "Yes" to "Yes", "No" to "No", "Sorry" to "Sorry", "Welcome" to "Welcome", "Good night" to "Good night"),
    "Sindhi" to mapOf("Hello" to "سلام", "Thank you" to "مهرباني", "How are you?" to "توهان ڪيئن آهيو؟", "Water" to "پاڻي", "Friend" to "دوست", "Good morning" to "صبح جو سلام", "Book" to "ڪتاب", "Please" to "مهرباني ڪري", "House" to "گهر", "Goodbye" to "خدا حافظ", "Yes" to "ها", "No" to "نه", "Sorry" to "معاف ڪجو", "Welcome" to "ڀلي ڪري آيا", "Good night" to "رات جو سلام"),
    "Urdu" to mapOf("Hello" to "سلام", "Thank you" to "شکریہ", "How are you?" to "آپ کیسے ہیں؟", "Water" to "پانی", "Friend" to "دوست", "Good morning" to "صبح بخیر", "Book" to "کتاب", "Please" to "براہ کرم", "House" to "گھر", "Goodbye" to "خدا حافظ", "Yes" to "ہاں", "No" to "نہیں", "Sorry" to "معاف کیجیے", "Welcome" to "خوش آمدید", "Good night" to "شب بخیر"),
    "Arabic" to mapOf("Hello" to "مرحبًا", "Thank you" to "شكرًا", "How are you?" to "كيف حالك؟", "Water" to "ماء", "Friend" to "صديق", "Good morning" to "صباح الخير", "Book" to "كتاب", "Please" to "من فضلك", "House" to "منزل", "Goodbye" to "مع السلامة", "Yes" to "نعم", "No" to "لا", "Sorry" to "آسف", "Welcome" to "أهلًا وسهلًا", "Good night" to "تصبح على خير"),
    "Spanish" to mapOf("Hello" to "Hola", "Thank you" to "Gracias", "How are you?" to "¿Cómo estás?", "Water" to "Agua", "Friend" to "Amigo", "Good morning" to "Buenos días", "Book" to "Libro", "Please" to "Por favor", "House" to "Casa", "Goodbye" to "Adiós", "Yes" to "Sí", "No" to "No", "Sorry" to "Lo siento", "Welcome" to "Bienvenido", "Good night" to "Buenas noches"),
    "French" to mapOf("Hello" to "Bonjour", "Thank you" to "Merci", "How are you?" to "Comment allez-vous ?", "Water" to "Eau", "Friend" to "Ami", "Good morning" to "Bonjour", "Book" to "Livre", "Please" to "S’il vous plaît", "House" to "Maison", "Goodbye" to "Au revoir", "Yes" to "Oui", "No" to "Non", "Sorry" to "Désolé", "Welcome" to "Bienvenue", "Good night" to "Bonne nuit"),
    "German" to mapOf("Hello" to "Hallo", "Thank you" to "Danke", "How are you?" to "Wie geht es dir?", "Water" to "Wasser", "Friend" to "Freund", "Good morning" to "Guten Morgen", "Book" to "Buch", "Please" to "Bitte", "House" to "Haus", "Goodbye" to "Auf Wiedersehen", "Yes" to "Ja", "No" to "Nein", "Sorry" to "Entschuldigung", "Welcome" to "Willkommen", "Good night" to "Gute Nacht"),
    "Chinese" to mapOf("Hello" to "你好", "Thank you" to "谢谢", "How are you?" to "你好吗？", "Water" to "水", "Friend" to "朋友", "Good morning" to "早上好", "Book" to "书", "Please" to "请", "House" to "房子", "Goodbye" to "再见", "Yes" to "是", "No" to "不", "Sorry" to "对不起", "Welcome" to "欢迎", "Good night" to "晚安"),
    "Japanese" to mapOf("Hello" to "こんにちは", "Thank you" to "ありがとう", "How are you?" to "お元気ですか？", "Water" to "水", "Friend" to "友達", "Good morning" to "おはようございます", "Book" to "本", "Please" to "お願いします", "House" to "家", "Goodbye" to "さようなら", "Yes" to "はい", "No" to "いいえ", "Sorry" to "ごめんなさい", "Welcome" to "ようこそ", "Good night" to "おやすみなさい"),
    "Korean" to mapOf("Hello" to "안녕하세요", "Thank you" to "감사합니다", "How are you?" to "어떻게 지내세요?", "Water" to "물", "Friend" to "친구", "Good morning" to "좋은 아침입니다", "Book" to "책", "Please" to "부탁합니다", "House" to "집", "Goodbye" to "안녕히 가세요", "Yes" to "네", "No" to "아니요", "Sorry" to "죄송합니다", "Welcome" to "환영합니다", "Good night" to "안녕히 주무세요"),
    "Turkish" to mapOf("Hello" to "Merhaba", "Thank you" to "Teşekkür ederim", "How are you?" to "Nasılsın?", "Water" to "Su", "Friend" to "Arkadaş", "Good morning" to "Günaydın", "Book" to "Kitap", "Please" to "Lütfen", "House" to "Ev", "Goodbye" to "Hoşça kal", "Yes" to "Evet", "No" to "Hayır", "Sorry" to "Özür dilerim", "Welcome" to "Hoş geldiniz", "Good night" to "İyi geceler"),
    "Persian" to mapOf("Hello" to "سلام", "Thank you" to "ممنون", "How are you?" to "حال شما چطور است؟", "Water" to "آب", "Friend" to "دوست", "Good morning" to "صبح بخیر", "Book" to "کتاب", "Please" to "لطفاً", "House" to "خانه", "Goodbye" to "خداحافظ", "Yes" to "بله", "No" to "نه", "Sorry" to "ببخشید", "Welcome" to "خوش آمدید", "Good night" to "شب بخیر"),
    "Hindi" to mapOf("Hello" to "नमस्ते", "Thank you" to "धन्यवाद", "How are you?" to "आप कैसे हैं?", "Water" to "पानी", "Friend" to "दोस्त", "Good morning" to "सुप्रभात", "Book" to "किताब", "Please" to "कृपया", "House" to "घर", "Goodbye" to "अलविदा", "Yes" to "हाँ", "No" to "नहीं", "Sorry" to "माफ़ कीजिए", "Welcome" to "स्वागत है", "Good night" to "शुभ रात्रि"),
    "Italian" to mapOf("Hello" to "Ciao", "Thank you" to "Grazie", "How are you?" to "Come stai?", "Water" to "Acqua", "Friend" to "Amico", "Good morning" to "Buongiorno", "Book" to "Libro", "Please" to "Per favore", "House" to "Casa", "Goodbye" to "Arrivederci", "Yes" to "Sì", "No" to "No", "Sorry" to "Mi dispiace", "Welcome" to "Benvenuto", "Good night" to "Buonanotte"),
    "Portuguese" to mapOf("Hello" to "Olá", "Thank you" to "Obrigado/Obrigada", "How are you?" to "Como você está?", "Water" to "Água", "Friend" to "Amigo", "Good morning" to "Bom dia", "Book" to "Livro", "Please" to "Por favor", "House" to "Casa", "Goodbye" to "Adeus", "Yes" to "Sim", "No" to "Não", "Sorry" to "Desculpe", "Welcome" to "Bem-vindo", "Good night" to "Boa noite"),
    "Russian" to mapOf("Hello" to "Здравствуйте", "Thank you" to "Спасибо", "How are you?" to "Как дела?", "Water" to "Вода", "Friend" to "Друг", "Good morning" to "Доброе утро", "Book" to "Книга", "Please" to "Пожалуйста", "House" to "Дом", "Goodbye" to "До свидания", "Yes" to "Да", "No" to "Нет", "Sorry" to "Извините", "Welcome" to "Добро пожаловать", "Good night" to "Спокойной ночи"),
    "Bengali" to mapOf("Hello" to "হ্যালো", "Thank you" to "ধন্যবাদ", "How are you?" to "আপনি কেমন আছেন?", "Water" to "পানি", "Friend" to "বন্ধু", "Good morning" to "সুপ্রভাত", "Book" to "বই", "Please" to "দয়া করে", "House" to "বাড়ি", "Goodbye" to "বিদায়", "Yes" to "হ্যাঁ", "No" to "না", "Sorry" to "দুঃখিত", "Welcome" to "স্বাগতম", "Good night" to "শুভ রাত্রি"),
    "Punjabi" to mapOf("Hello" to "ਸਤ ਸ੍ਰੀ ਅਕਾਲ", "Thank you" to "ਧੰਨਵਾਦ", "How are you?" to "ਤੁਸੀਂ ਕਿਵੇਂ ਹੋ?", "Water" to "ਪਾਣੀ", "Friend" to "ਦੋਸਤ", "Good morning" to "ਸ਼ੁਭ ਸਵੇਰ", "Book" to "ਕਿਤਾਬ", "Please" to "ਕਿਰਪਾ ਕਰਕੇ", "House" to "ਘਰ", "Goodbye" to "ਅਲਵਿਦਾ", "Yes" to "ਹਾਂ", "No" to "ਨਹੀਂ", "Sorry" to "ਮਾਫ਼ ਕਰਨਾ", "Welcome" to "ਜੀ ਆਇਆਂ ਨੂੰ", "Good night" to "ਸ਼ੁਭ ਰਾਤਰੀ"),
    "Pashto" to mapOf("Hello" to "سلام", "Thank you" to "مننه", "How are you?" to "تاسو څنګه یاست؟", "Water" to "اوبه", "Friend" to "ملګری", "Good morning" to "سهار مو پخیر", "Book" to "کتاب", "Please" to "مهرباني وکړئ", "House" to "کور", "Goodbye" to "خدای پامان", "Yes" to "هو", "No" to "نه", "Sorry" to "بخښنه غواړم", "Welcome" to "ښه راغلاست", "Good night" to "ښه شپه"),
    "Malay" to mapOf("Hello" to "Helo", "Thank you" to "Terima kasih", "How are you?" to "Apa khabar?", "Water" to "Air", "Friend" to "Kawan", "Good morning" to "Selamat pagi", "Book" to "Buku", "Please" to "Sila", "House" to "Rumah", "Goodbye" to "Selamat tinggal", "Yes" to "Ya", "No" to "Tidak", "Sorry" to "Maaf", "Welcome" to "Selamat datang", "Good night" to "Selamat malam"),
    "Indonesian" to mapOf("Hello" to "Halo", "Thank you" to "Terima kasih", "How are you?" to "Apa kabar?", "Water" to "Air", "Friend" to "Teman", "Good morning" to "Selamat pagi", "Book" to "Buku", "Please" to "Tolong", "House" to "Rumah", "Goodbye" to "Sampai jumpa", "Yes" to "Ya", "No" to "Tidak", "Sorry" to "Maaf", "Welcome" to "Selamat datang", "Good night" to "Selamat malam"),
    "Thai" to mapOf("Hello" to "สวัสดี", "Thank you" to "ขอบคุณ", "How are you?" to "สบายดีไหม?", "Water" to "น้ำ", "Friend" to "เพื่อน", "Good morning" to "อรุณสวัสดิ์", "Book" to "หนังสือ", "Please" to "กรุณา", "House" to "บ้าน", "Goodbye" to "ลาก่อน", "Yes" to "ใช่", "No" to "ไม่", "Sorry" to "ขอโทษ", "Welcome" to "ยินดีต้อนรับ", "Good night" to "ราตรีสวัสดิ์"),
    "Dutch" to mapOf("Hello" to "Hallo", "Thank you" to "Dank je", "How are you?" to "Hoe gaat het?", "Water" to "Water", "Friend" to "Vriend", "Good morning" to "Goedemorgen", "Book" to "Boek", "Please" to "Alsjeblieft", "House" to "Huis", "Goodbye" to "Tot ziens", "Yes" to "Ja", "No" to "Nee", "Sorry" to "Sorry", "Welcome" to "Welkom", "Good night" to "Goedenacht"),
    "Swahili" to mapOf("Hello" to "Habari", "Thank you" to "Asante", "How are you?" to "Habari yako?", "Water" to "Maji", "Friend" to "Rafiki", "Good morning" to "Habari za asubuhi", "Book" to "Kitabu", "Please" to "Tafadhali", "House" to "Nyumba", "Goodbye" to "Kwaheri", "Yes" to "Ndiyo", "No" to "Hapana", "Sorry" to "Samahani", "Welcome" to "Karibu", "Good night" to "Usiku mwema"),
    "Greek" to mapOf("Hello" to "Γεια σας", "Thank you" to "Ευχαριστώ", "How are you?" to "Τι κάνεις;", "Water" to "Νερό", "Friend" to "Φίλος", "Good morning" to "Καλημέρα", "Book" to "Βιβλίο", "Please" to "Παρακαλώ", "House" to "Σπίτι", "Goodbye" to "Αντίο", "Yes" to "Ναι", "No" to "Όχι", "Sorry" to "Συγγνώμη", "Welcome" to "Καλώς ήρθατε", "Good night" to "Καληνύχτα"),
)

private fun translatedWord(meaning: String, language: String, sindhiFallback: String): String =
    starterTranslations[language]?.get(meaning) ?: if (language == "Sindhi") sindhiFallback else meaning

private fun historyKey(email: String) =
    "history_" + email.trim().lowercase()

private fun prefs(context: Context) =
    context.getSharedPreferences("language_learning", Context.MODE_PRIVATE)

private fun loadHistory(context: Context, email: String): List<String> {
    val json = prefs(context).getString(historyKey(email), "[]")
        ?: "[]"
    val array = JSONArray(json)
    return (0 until array.length()).map { array.getString(it) }
}

private fun saveHistory(context: Context, email: String, item: String) {
    val array = JSONArray()
    loadHistory(context, email).takeLast(49).forEach {
        array.put(it)
    }
    array.put(item)
    prefs(context).edit()
        .putString(historyKey(email), array.toString())
        .apply()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Mint,
                    background = Background,
                    surface = SurfaceDark,
                    onPrimary = Background,
                    onBackground = MainText,
                    onSurface = MainText
                )
            ) {
                LanguageLearningApp(this)
            }
        }
    }
}

@Composable
private fun LanguageLearningApp(context: Context) {
    val preferences = remember {
        prefs(context)
    }

    var email by remember {
        mutableStateOf(preferences.getString("email", "") ?: "")
    }
    var loggedIn by remember {
        mutableStateOf(
            preferences.getBoolean("logged_in", false)
        )
    }
    var guest by remember {
        mutableStateOf(false)
    }

    var nativeLanguage by remember {
        mutableStateOf(
            preferences.getString("native_language", "English")
                ?: "English"
        )
    }
    var learningLanguage by remember {
        mutableStateOf(
            preferences.getString("learning_language", "Sindhi")
                ?: "Sindhi"
        )
    }

    var page by remember { mutableStateOf("splash") }
    var lastBackPress by remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
        delay(1800)
        page = if (loggedIn) "home" else "welcome"
    }

    BackHandler(enabled = page != "splash") {
        when (page) {
            "signin", "signup" -> page = "welcome"
            "lesson", "quiz", "languages", "history" -> page = "home"
            "welcome" -> {
                val now = System.currentTimeMillis()
                if (now - lastBackPress < 2000L) {
                    (context as? ComponentActivity)?.finish()
                } else {
                    lastBackPress = now
                    Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show()
                }
            }
            "home" -> {
                val now = System.currentTimeMillis()
                if (now - lastBackPress < 2000L) {
                    (context as? ComponentActivity)?.finish()
                } else {
                    lastBackPress = now
                    Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show()
                }
            }
            else -> page = "home"
        }
    }
    var selectedTab by remember { mutableStateOf("Home") }
    var selectedCategory by remember { mutableStateOf("All") }
    var cardIndex by remember { mutableIntStateOf(0) }
    var showAnswer by remember { mutableStateOf(false) }
    var quizIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var quizFinished by remember { mutableStateOf(false) }
    var lastMessage by remember { mutableStateOf("") }
    var googleSignInError by remember { mutableStateOf("") }

    val history = remember(email, page, lastMessage) {
        if (email.isBlank()) emptyList()
        else loadHistory(context, email)
    }

    fun record(message: String) {
        if (loggedIn && email.isNotBlank()) {
            saveHistory(context, email, message)
            lastMessage = message
        }
    }

    fun startGuest() {
        guest = true
        page = "home"
        selectedTab = "Home"
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
        preferences.edit().putBoolean("logged_in", false).remove("email").apply()
        email = ""
        loggedIn = false
        guest = false
        page = "welcome"
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {
        when (page) {
            "splash" -> SplashScreen()

            "welcome" -> WelcomeScreen(
                googleError = googleSignInError,
                onSignIn = {
                    googleSignInError = ""
                    page = "signin"
                },
                onSignUp = {
                    googleSignInError = ""
                    page = "signup"
                },
                onGuest = { startGuest() },
                onGoogleSignedIn = { user ->
                    email = user.email ?: ""
                    loggedIn = true
                    guest = false
                    googleSignInError = ""
                    page = "home"
                    selectedTab = "Home"

                    preferences.edit()
                        .putString("email", email)
                        .putBoolean("logged_in", true)
                        .apply()
                },
                onGoogleSignInError = { message ->
                    googleSignInError = message
                }
            )


            "signin", "signup" -> AuthScreen(
                isSignUp = page == "signup",
                onBack = { page = "welcome" },
                onSubmit = { userEmail, userPassword, fullName, signingUp, onResult ->

                    val auth = FirebaseAuth.getInstance()

                    val task = if (signingUp) {
                        auth.createUserWithEmailAndPassword(
                            userEmail,
                            userPassword
                        )
                    } else {
                        auth.signInWithEmailAndPassword(
                            userEmail,
                            userPassword
                        )
                    }

                    task.addOnCompleteListener { result ->
                        if (result.isSuccessful) {
                            val firebaseUser = auth.currentUser

                            if (signingUp && firebaseUser != null &&
                                fullName.isNotBlank()
                            ) {
                                val profileUpdate =
                                    com.google.firebase.auth.UserProfileChangeRequest
                                        .Builder()
                                        .setDisplayName(fullName)
                                        .build()

                                firebaseUser.updateProfile(profileUpdate)
                                    .addOnCompleteListener {
                                        email = firebaseUser.email ?: userEmail
                                        loggedIn = true
                                        guest = false
                                        page = "home"
                                        selectedTab = "Home"

                                        preferences.edit()
                                            .putString("email", email)
                                            .putBoolean("logged_in", true)
                                            .apply()

                                        onResult(true, "")
                                    }
                            } else {
                                email = firebaseUser?.email ?: userEmail
                                loggedIn = true
                                guest = false
                                page = "home"
                                selectedTab = "Home"

                                preferences.edit()
                                    .putString("email", email)
                                    .putBoolean("logged_in", true)
                                    .apply()

                                onResult(true, "")
                            }
                        } else {
                            onResult(
                                false,
                                result.exception?.localizedMessage
                                    ?: "Authentication failed. Please try again."
                            )
                        }
                    }
                }
            )


            "languages" -> LanguageSettingsScreen(
                nativeLanguage = nativeLanguage,
                learningLanguage = learningLanguage,
                onNativeChange = {
                    nativeLanguage = it
                    preferences.edit()
                        .putString("native_language", it).apply()
                },
                onLearningChange = {
                    learningLanguage = it
                    preferences.edit()
                        .putString("learning_language", it).apply()
                },
                onBack = { page = "home" }
            )

            "lesson" -> LessonScreen(
                language = learningLanguage,
                nativeLanguage = nativeLanguage,
                index = cardIndex,
                showAnswer = showAnswer,
                onReveal = { showAnswer = true },
                onNext = {
                    cardIndex = (cardIndex + 1) % lessons.size
                    showAnswer = false
                },
                onPrevious = {
                    cardIndex =
                        (cardIndex - 1 + lessons.size) % lessons.size
                    showAnswer = false
                },
                onKnown = {
                    record("Learned: ${lessons[cardIndex].word}")
                    cardIndex = (cardIndex + 1) % lessons.size
                    showAnswer = false
                },
                onBack = { page = "home" }
            )

            "quiz" -> QuizScreen(
                language = learningLanguage,
                index = quizIndex,
                score = score,
                finished = quizFinished,
                onAnswer = { correct ->
                    if (correct) score++
                    if (quizIndex == lessons.size - 1) {
                        quizFinished = true
                        record(
                            "Quiz completed: ${score + if (correct) 0 else 0}/10"
                        )
                    } else {
                        quizIndex++
                    }
                },
                onRestart = {
                    quizIndex = 0
                    score = 0
                    quizFinished = false
                },
                onBack = { page = "home" }
            )

            "history" -> HistoryScreen(
                email = email,
                guest = guest,
                history = history,
                onBack = { page = "home" }
            )

            else -> {
                if (selectedTab == "Profile") {
                    ProfileScreen(
                        email = email,
                        guest = guest,
                        nativeLanguage = nativeLanguage,
                        learningLanguage = learningLanguage,
                        historyCount = history.size,
                        onLanguages = { page = "languages" },
                        onHistory = { page = "history" },
                        onLogout = { logout() },
                        onBack = {
                            selectedTab = "Home"
                        }
                    )
                } else {
                    HomeScreen(
                        learningLanguage = learningLanguage,
                        nativeLanguage = nativeLanguage,
                        guest = guest,
                        onLesson = {
                            cardIndex = 0
                            showAnswer = false
                            page = "lesson"
                        },
                        onQuiz = {
                            quizIndex = 0
                            score = 0
                            quizFinished = false
                            page = "quiz"
                        },
                        onLanguages = { page = "languages" },
                        onHistory = { page = "history" },
                        selectedTab = selectedTab,
                        onTabChange = { selectedTab = it }
                    )
                }
            }
        }
    }
}

@Composable
private fun SplashScreen() {
    Box(
        modifier = Modifier.fillMaxSize().background(Background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(112.dp).background(Mint, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Translate,
                    contentDescription = "LinGua logo",
                    tint = Background,
                    modifier = Modifier.size(62.dp)
                )
            }
            Spacer(Modifier.height(22.dp))
            Text("LinGua", color = MainText, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(8.dp))
            Text("Learn languages, one word at a time", color = MutedText, fontSize = 14.sp)
            Spacer(Modifier.height(28.dp))
            CircularProgressIndicator(color = Mint, strokeWidth = 3.dp, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(18.dp))
            Text("Developed by Muhammad Faraz", color = Gold, fontSize = 12.sp)
        }
    }
}

@Composable
private fun WelcomeScreen(
    onSignIn: () -> Unit,
    onSignUp: () -> Unit,
    onGuest: () -> Unit,
    onGoogleSignedIn: (FirebaseUser) -> Unit,
    onGoogleSignInError: (String) -> Unit,
    googleError: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Mint, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Translate,
                contentDescription = "App logo",
                tint = Background,
                modifier = Modifier.size(52.dp)
            )
        }

        Spacer(Modifier.height(28.dp))

        Text(
            text = "LinGua",
            fontSize = 38.sp,
            fontWeight = FontWeight.Bold,
            color = MainText
        )

        Text(
            text = "Learn a language. Open a new world.",
            color = MutedText,
            textAlign = TextAlign.Center,
            fontSize = 15.sp
        )

        Spacer(Modifier.height(36.dp))

        PrimaryButton("Create Account", onSignUp)

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onSignIn,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Sign In", color = Mint)
        }

        Spacer(Modifier.height(18.dp))

        // Google Sign-In button
        GoogleSignInButton(
            onSuccess = onGoogleSignedIn,
            onError = onGoogleSignInError
        )

        if (googleError.isNotBlank()) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = googleError,
                color = Color(0xFFFF9B9B),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = "or",
            color = MutedText,
            fontSize = 13.sp
        )

        TextButton(onClick = onGuest) {
            Text("Continue as Guest →", color = Mint)
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Start Learning With LinGua",
            color = MutedText,
            textAlign = TextAlign.Center,
            fontSize = 12.sp
        )
    }
}


@Composable
private fun GoogleSignInButton(
    onSuccess: (FirebaseUser) -> Unit,
    onError: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    OutlinedButton(
        onClick = {
            if (isLoading) return@OutlinedButton

            scope.launch {
                isLoading = true

                try {
                    val credentialManager = CredentialManager.create(context)

                    val googleIdOption = GetGoogleIdOption.Builder()
                        .setServerClientId(
                            context.getString(R.string.default_web_client_id)
                        )
                        // Show accounts even if they have not authorized this app before.
                        .setFilterByAuthorizedAccounts(false)
                        .setAutoSelectEnabled(false)
                        .build()

                    val request = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()

                    val result = credentialManager.getCredential(
                        context = context,
                        request = request
                    )

                    val credential = result.credential

                    if (
                        credential is CustomCredential &&
                        credential.type ==
                        GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                    ) {
                        val googleCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)

                        val firebaseCredential = GoogleAuthProvider.getCredential(
                            googleCredential.idToken,
                            null
                        )

                        FirebaseAuth.getInstance()
                            .signInWithCredential(firebaseCredential)
                            .addOnCompleteListener { task ->
                                isLoading = false

                                if (task.isSuccessful) {
                                    val user = FirebaseAuth.getInstance().currentUser
                                    if (user != null) {
                                        onSuccess(user)
                                    } else {
                                        onError(
                                            "Google sign-in completed, but Firebase did not return a user."
                                        )
                                    }
                                } else {
                                    Log.e(
                                        "LinGuaGoogleSignIn",
                                        "Firebase sign-in failed",
                                        task.exception
                                    )
                                    onError(
                                        task.exception?.localizedMessage
                                            ?: "Firebase Google sign-in failed. Check Firebase Authentication settings."
                                    )
                                }
                            }
                    } else {
                        isLoading = false
                        onError(
                            "Google returned an unsupported credential. Please try another Google account."
                        )
                    }
                } catch (e: Exception) {
                    isLoading = false
                    Log.e("LinGuaGoogleSignIn", "Credential Manager failed", e)

                    val details = e.localizedMessage
                        ?: e.javaClass.simpleName
                        ?: "Unknown error"

                    onError(
                        "Google sign-in failed: $details. Check your Google account, OAuth client, and SHA-1 configuration."
                    )
                }
            }
        },
        enabled = !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MainText
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Mint,
                strokeWidth = 2.dp
            )
            Spacer(Modifier.width(10.dp))
            Text("Signing in...")
        } else {
            Text("G  Continue with Google")
        }
    }
}

@Composable
private fun AuthScreen(
    isSignUp: Boolean,
    onBack: () -> Unit,
    onSubmit: (
        email: String,
        password: String,
        fullName: String,
        isSignUp: Boolean,
        onResult: (Boolean, String) -> Unit
    ) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextButton(onClick = onBack, enabled = !isLoading) {
            Text("← Back", color = Mint)
        }

        Text(
            text = if (isSignUp) "Create your account" else "Welcome back",
            fontSize = 29.sp,
            fontWeight = FontWeight.Bold,
            color = MainText
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = if (isSignUp)
                "Your language journey starts here."
            else
                "Continue your learning journey.",
            color = MutedText
        )

        Spacer(Modifier.height(26.dp))

        if (isSignUp) {
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(Modifier.height(12.dp))
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email address") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password (minimum 6 characters)") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(Modifier.height(20.dp))

        PrimaryButton(
            if (isSignUp) "Sign Up" else "Sign In"
        ) {
            if (isLoading) return@PrimaryButton

            errorMessage = ""

            if (isSignUp && fullName.isBlank()) {
                errorMessage = "Please enter your full name."
            } else if (!android.util.Patterns.EMAIL_ADDRESS
                    .matcher(email.trim()).matches()
            ) {
                errorMessage = "Please enter a valid email address."
            } else if (password.length < 6) {
                errorMessage = "Password must contain at least 6 characters."
            } else {
                isLoading = true

                onSubmit(
                    email.trim(),
                    password,
                    fullName.trim(),
                    isSignUp
                ) { success, message ->
                    isLoading = false
                    errorMessage = if (success) "" else message
                }
            }
        }

        if (isLoading) {
            Spacer(Modifier.height(12.dp))
            CircularProgressIndicator(color = Mint)
        }

        if (errorMessage.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = errorMessage,
                color = Color(0xFFFF9B9B)
            )
        }
    }
}


@Composable
private fun HomeScreen(
    learningLanguage: String,
    nativeLanguage: String,
    guest: Boolean,
    onLesson: () -> Unit,
    onQuiz: () -> Unit,
    onLanguages: () -> Unit,
    onHistory: () -> Unit,
    selectedTab: String,
    onTabChange: (String) -> Unit
) {
    Scaffold(
        containerColor = Background,
        bottomBar = {
            NavigationBar(containerColor = SurfaceDark) {
                listOf("Home", "History", "Profile").forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = {
                            onTabChange(tab)
                            if (tab == "History") onHistory()
                        },
                        icon = {
                            Icon(
                                when (tab) {
                                    "Home" -> Icons.Default.Home
                                    "History" -> Icons.Default.History
                                    else -> Icons.Default.Person
                                },
                                contentDescription = tab
                            )
                        },
                        label = { Text(tab) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Mint,
                            selectedTextColor = Mint,
                            indicatorColor = Background
                        )
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(22.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Text("LINGUA", color = Mint, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("Your next word\nopens a new world.", fontSize = 30.sp,
                    fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(
                    if (guest) "Guest learner • progress is not synced"
                    else "Welcome back • keep your streak alive",
                    color = MutedText
                )
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Mint
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(22.dp)) {
                        Text("YOUR DAILY GOAL", color = Background)
                        Spacer(Modifier.height(10.dp))
                        Text(
                            "$nativeLanguage → $learningLanguage",
                            color = Background,
                            fontSize = 23.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Learn 10 words today",
                            color = Background
                        )
                        Spacer(Modifier.height(18.dp))
                        Button(
                            onClick = onLesson,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Background,
                                contentColor = Mint
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Start today's lesson")
                        }
                    }
                }
            }

            item {
                Text(
                    "Your learning space",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FeatureCard(
                        "Vocabulary",
                        "Learn new words",
                        Icons.AutoMirrored.Filled.MenuBook,
                        Modifier.weight(1f),
                        onLesson
                    )
                    FeatureCard(
                        "Practice quiz",
                        "Test your skills",
                        Icons.Default.Quiz,
                        Modifier.weight(1f),
                        onQuiz
                    )
                }
            }

            item {
                FeatureCard(
                    "Language preferences",
                    "Choose your native and learning languages",
                    Icons.Default.Language,
                    Modifier.fillMaxWidth(),
                    onLanguages
                )
            }

            item {
                FeatureCard(
                    "Learning history",
                    "Review your completed activities",
                    Icons.Default.History,
                    Modifier.fillMaxWidth(),
                    onHistory
                )
            }

            item {
                Text(
                    "Explore languages",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            items(languages.take(12)) { language ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .background(
                            SurfaceDark,
                            RoundedCornerShape(14.dp)
                        )
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🌍", fontSize = 23.sp)
                    Spacer(Modifier.width(12.dp))
                    Text(language, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.weight(1f))
                    if (language == learningLanguage) {
                        Text("Selected", color = Mint, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun FeatureCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(Modifier.padding(18.dp)) {
            Icon(icon, contentDescription = title, tint = Mint,
                modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(12.dp))
            Text(title, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(5.dp))
            Text(subtitle, color = MutedText, fontSize = 12.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageSettingsScreen(
    nativeLanguage: String,
    learningLanguage: String,
    onNativeChange: (String) -> Unit,
    onLearningChange: (String) -> Unit,
    onBack: () -> Unit
) {
    var nativeExpanded by remember { mutableStateOf(false) }
    var targetExpanded by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().padding(22.dp)
    ) {
        TextButton(onClick = onBack) {
            Text("← Back to home", color = Mint)
        }
        Text(
            "Language preferences",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Choose the language you speak and the language you want to learn.",
            color = MutedText
        )
        Spacer(Modifier.height(28.dp))

        Text("MY NATIVE LANGUAGE", color = Mint, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = nativeExpanded,
            onExpandedChange = { nativeExpanded = it }
        ) {
            OutlinedTextField(
                value = nativeLanguage,
                onValueChange = {},
                readOnly = true,
                label = { Text("I speak") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(nativeExpanded)
                },
                modifier = Modifier.fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
            )
            ExposedDropdownMenu(
                expanded = nativeExpanded,
                onDismissRequest = { nativeExpanded = false }
            ) {
                languages.forEach { language ->
                    DropdownMenuItem(
                        text = { Text(language) },
                        onClick = {
                            onNativeChange(language)
                            nativeExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Text("LANGUAGE I WANT TO LEARN", color = Mint, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = targetExpanded,
            onExpandedChange = { targetExpanded = it }
        ) {
            OutlinedTextField(
                value = learningLanguage,
                onValueChange = {},
                readOnly = true,
                label = { Text("I'm learning") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(targetExpanded)
                },
                modifier = Modifier.fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
            )
            ExposedDropdownMenu(
                expanded = targetExpanded,
                onDismissRequest = { targetExpanded = false }
            ) {
                languages.filter { it != nativeLanguage }.forEach { language ->
                    DropdownMenuItem(
                        text = { Text(language) },
                        onClick = {
                            onLearningChange(language)
                            targetExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(22.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Your language pair", color = MutedText)
                Spacer(Modifier.height(8.dp))
                Text(
                    "$nativeLanguage  →  $learningLanguage",
                    color = Mint,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(18.dp))
        Text(
            "Sindhi is included. Lesson translations and audio need suitable content for each language pair.",
            color = MutedText,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun LessonScreen(
    language: String,
    nativeLanguage: String,
    index: Int,
    showAnswer: Boolean,
    onReveal: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onKnown: () -> Unit,
    onBack: () -> Unit
) {
    val lesson = lessons[index]
    val targetWord = translatedWord(lesson.englishMeaning, language, lesson.targetLanguageWord)
    val nativeTranslation = starterTranslations[nativeLanguage]?.get(lesson.englishMeaning)
        ?: if (nativeLanguage == "Sindhi") lesson.nativeLanguageTranslation else lesson.englishMeaning
    val pronunciation = if (language == "Sindhi") lesson.pronunciation else "Pronunciation guide coming soon"

    Column(
        Modifier.fillMaxSize().padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) {
                Text("← Back", color = Mint)
            }
            Spacer(Modifier.weight(1f))
            Text("${index + 1} / ${lessons.size}", color = MutedText)
        }

        Spacer(Modifier.height(24.dp))
        Text("DAILY FLASHCARDS", color = Mint, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("$nativeLanguage → $language", color = MutedText)
        Spacer(Modifier.height(25.dp))

        Card(
            modifier = Modifier.fillMaxWidth().weight(1f),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(28.dp)
        ) {
            Column(
                Modifier.fillMaxSize().padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(lesson.category.uppercase(), color = Mint, fontSize = 12.sp)
                Spacer(Modifier.height(25.dp))
                Text(
                    targetWord,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                if (showAnswer) {
                    Spacer(Modifier.height(25.dp))
                    HorizontalDivider(color = MutedText.copy(alpha = 0.3f))
                    Spacer(Modifier.height(25.dp))
                    Text("Target-language word", color = MutedText, fontSize = 12.sp)
                    Text(
                        targetWord,
                        fontSize = 29.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(10.dp))
                    Text("English meaning", color = MutedText, fontSize = 12.sp)
                    Text(
                        lesson.englishMeaning,
                        fontSize = 19.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(10.dp))
                    Text("Native-language translation ($nativeLanguage)", color = MutedText, fontSize = 12.sp)
                    Text(
                        nativeTranslation,
                        fontSize = 19.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(pronunciation, color = Mint, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        if (starterTranslations[language]?.containsKey(lesson.englishMeaning) == true || language == "Sindhi")
                            "Starter translation"
                        else "English fallback: this lesson's translation is not in the starter pack yet.",
                        color = MutedText,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Spacer(Modifier.height(24.dp))
                    Text(
                        "Think about the meaning, then reveal the answer.",
                        color = MutedText,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        if (!showAnswer) {
            PrimaryButton("Reveal translation", onReveal)
        } else {
            PrimaryButton("I learned this ✓", onKnown)
        }

        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onPrevious,
                modifier = Modifier.weight(1f)
            ) { Text("Previous") }
            OutlinedButton(
                onClick = onNext,
                modifier = Modifier.weight(1f)
            ) { Text("Next") }
        }
    }
}

@Composable
private fun QuizScreen(
    language: String,
    index: Int,
    score: Int,
    finished: Boolean,
    onAnswer: (Boolean) -> Unit,
    onRestart: () -> Unit,
    onBack: () -> Unit
) {
    val lesson = lessons[index]
    val quizWord = translatedWord(lesson.englishMeaning, language, lesson.targetLanguageWord)
    var selected by remember(index) { mutableStateOf("") }

    Column(
        Modifier.fillMaxSize().padding(22.dp)
    ) {
        TextButton(onClick = onBack) {
            Text("← Back to home", color = Mint)
        }

        if (finished) {
            Spacer(Modifier.weight(1f))
            Text(
                "Quiz completed!",
                fontSize = 29.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(15.dp))
            Text(
                "$score / ${lessons.size}",
                fontSize = 44.sp,
                color = Mint,
                fontWeight = FontWeight.Bold
            )
            Text("Your final score", color = MutedText)
            Spacer(Modifier.height(25.dp))
            PrimaryButton("Try again", onRestart)
            Spacer(Modifier.weight(1f))
        } else {
            Spacer(Modifier.height(18.dp))
            Text("PRACTICE QUIZ", color = Mint, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { (index + 1).toFloat() / lessons.size },
                modifier = Modifier.fillMaxWidth(),
                color = Mint
            )
            Spacer(Modifier.height(20.dp))
            Text("Question ${index + 1} of ${lessons.size}", color = MutedText)
            Spacer(Modifier.height(12.dp))
            Text(
                "What does \"${quizWord}\" mean in $language?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(20.dp))

            val options = remember(index) {
                (listOf(lesson.translation) +
                        lessons.filter { it != lesson }
                            .shuffled()
                            .take(3)
                            .map { it.translation })
                    .shuffled()
            }

            options.forEach { option ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clickable { selected = option },
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected == option)
                            Mint.copy(alpha = 0.18f) else SurfaceDark
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selected == option,
                            onClick = { selected = option }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(option, fontSize = 17.sp)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            PrimaryButton("Submit answer") {
                if (selected.isNotBlank()) {
                    onAnswer(selected == lesson.translation)
                }
            }
        }
    }
}

@Composable
private fun HistoryScreen(
    email: String,
    guest: Boolean,
    history: List<String>,
    onBack: () -> Unit
) {
    Column(Modifier.fillMaxSize().padding(22.dp)) {
        TextButton(onClick = onBack) {
            Text("← Back to home", color = Mint)
        }
        Text(
            "Learning history",
            fontSize = 29.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            if (guest) "Guest learning activity"
            else email,
            color = MutedText
        )
        Spacer(Modifier.height(20.dp))

        if (guest) {
            Text(
                "Guest history is not saved to a personal account. Sign in to keep a local learning history.",
                color = MutedText
            )
        } else if (history.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(25.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.History,
                        contentDescription = null,
                        tint = Mint,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text("Your journey starts today")
                    Text(
                        "Complete lessons and quizzes to build your history.",
                        color = MutedText,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(history.reversed()) { entry ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = SurfaceDark
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Mint
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(entry)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileScreen(
    email: String,
    guest: Boolean,
    nativeLanguage: String,
    learningLanguage: String,
    historyCount: Int,
    onBack: () -> Unit,
    onLanguages: () -> Unit,
    onHistory: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MainText
                )
            }

            Text(
                text = "My profile",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = MainText
            )
        }

        Box(
            Modifier
                .size(76.dp)
                .background(Mint, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = "Profile",
                tint = Background,
                modifier = Modifier.size(42.dp)
            )
        }

        Text(
            if (guest) "Guest learner" else email,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MainText
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = SurfaceDark
            )
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Text("Native language", color = MutedText)
                Text(
                    nativeLanguage,
                    color = Mint,
                    fontSize = 19.sp
                )

                Spacer(Modifier.height(12.dp))

                Text("Learning language", color = MutedText)
                Text(
                    learningLanguage,
                    color = Mint,
                    fontSize = 19.sp
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    "Recorded activities: $historyCount",
                    color = MainText
                )
            }
        }

        OutlinedButton(
            onClick = onLanguages,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Change languages")
        }

        OutlinedButton(
            onClick = onHistory,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("View learning history")
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF442A36)
            )
        ) {
            Text(
                if (guest) "Exit guest session" else "Sign out",
                color = MainText
            )
        }
    }
}

@Composable
private fun PrimaryButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(54.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Mint,
            contentColor = Background
        )
    ) {
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}
