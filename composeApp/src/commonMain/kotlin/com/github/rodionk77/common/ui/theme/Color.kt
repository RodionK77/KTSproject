package com.github.rodionk77.common.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color


// Blue (accent / primary)
val GitHubBlueLight = Color(0xFF0969DA)
val GitHubBlueDark  = Color(0xFF58A6FF)

val OnBlueLight = Color(0xFFFFFFFF)
val OnBlueDark  = Color(0xFF0D1117)

val BlueTonalLight = Color(0xFFDEECFD)
val BlueTonalDark  = Color(0xFF1C3A5C)

val OnBlueTonalLight = Color(0xFF0550AE)
val OnBlueTonalDark  = Color(0xFF58A6FF)

// Green (secondary / success)
val GitHubGreenLight = Color(0xFF2DA44E)
val GitHubGreenDark  = Color(0xFF3FB950)

val OnGreenLight = Color(0xFFFFFFFF)
val OnGreenDark  = Color(0xFF0D1117)

val GreenTonalLight = Color(0xFFDCFCE7)
val GreenTonalDark  = Color(0xFF112D22)

val OnGreenTonalLight = Color(0xFF1A7F37)
val OnGreenTonalDark  = Color(0xFF3FB950)

// Background / canvas
val BackgroundLight = Color(0xFFFFFFFF)
val BackgroundDark  = Color(0xFF0D1117)

// Surface / card
val SurfaceLight = Color(0xFFF6F8FA)
val SurfaceDark  = Color(0xFF161B22)

val SurfaceVariantLight = Color(0xFFEAEEF2)
val SurfaceVariantDark  = Color(0xFF21262D)

// Text
val OnBackgroundLight        = Color(0xFF24292F)
val OnBackgroundDark         = Color(0xFFE6EDF3)

val OnSurfaceLight           = Color(0xFF24292F)
val OnSurfaceDark            = Color(0xFFE6EDF3)

val OnSurfaceVariantLight    = Color(0xFF57606A)
val OnSurfaceVariantDark     = Color(0xFF8B949E)

// Error / danger
val DangerLight    = Color(0xFFCF222E)
val DangerDark     = Color(0xFFF85149)

val OnDangerLight  = Color(0xFFFFFFFF)
val OnDangerDark   = Color(0xFF0D1117)

val DangerTonalLight = Color(0xFFFFEBE9)
val DangerTonalDark  = Color(0xFF3D0D0D)

val OnDangerTonalLight = Color(0xFF82071E)
val OnDangerTonalDark  = Color(0xFFF85149)

// Outline / border
val OutlineLight         = Color(0xFFD0D7DE)
val OutlineDark          = Color(0xFF30363D)

val OutlineVariantLight  = Color(0xFFEAEEF2)
val OutlineVariantDark   = Color(0xFF21262D)

// Surface containers (NavigationBar, AlertDialog, BottomSheet, etc.)
// M3 uses these instead of plain surface — must be overridden to avoid default purple tint
val SurfaceContainerLowestLight  = Color(0xFFFFFFFF)
val SurfaceContainerLowLight     = Color(0xFFF6F8FA)
val SurfaceContainerLight        = Color(0xFFEAEEF2)  // NavigationBar background
val SurfaceContainerHighLight    = Color(0xFFD8DEE4)  // AlertDialog background
val SurfaceContainerHighestLight = Color(0xFFCDD5DC)

val SurfaceContainerLowestDark   = Color(0xFF0D1117)
val SurfaceContainerLowDark      = Color(0xFF161B22)
val SurfaceContainerDark         = Color(0xFF1C2128)  // NavigationBar background
val SurfaceContainerHighDark     = Color(0xFF21262D)  // AlertDialog background
val SurfaceContainerHighestDark  = Color(0xFF2D333B)

// surfaceTint — убираем фиолетовый оттенок на поверхностях с elevation
val SurfaceTintLight = Color(0xFF0969DA)  // наш primary синий, а не дефолтный пурпурный
val SurfaceTintDark  = Color(0xFF58A6FF)


val GitHubLightColorScheme = lightColorScheme(
    primary              = GitHubBlueLight,
    onPrimary            = OnBlueLight,
    primaryContainer     = BlueTonalLight,
    onPrimaryContainer   = OnBlueTonalLight,

    secondary            = GitHubGreenLight,
    onSecondary          = OnGreenLight,
    secondaryContainer   = GreenTonalLight,
    onSecondaryContainer = OnGreenTonalLight,

    background           = BackgroundLight,
    onBackground         = OnBackgroundLight,

    surface              = SurfaceLight,
    onSurface            = OnSurfaceLight,
    surfaceVariant       = SurfaceVariantLight,
    onSurfaceVariant     = OnSurfaceVariantLight,
    surfaceTint          = SurfaceTintLight,

    surfaceContainer        = SurfaceContainerLight,
    surfaceContainerLow     = SurfaceContainerLowLight,
    surfaceContainerHigh    = SurfaceContainerHighLight,
    surfaceContainerLowest  = SurfaceContainerLowestLight,
    surfaceContainerHighest = SurfaceContainerHighestLight,

    error                = DangerLight,
    onError              = OnDangerLight,
    errorContainer       = DangerTonalLight,
    onErrorContainer     = OnDangerTonalLight,

    outline              = OutlineLight,
    outlineVariant       = OutlineVariantLight,
)

val GitHubDarkColorScheme = darkColorScheme(
    primary              = GitHubBlueDark,
    onPrimary            = OnBlueDark,
    primaryContainer     = BlueTonalDark,
    onPrimaryContainer   = OnBlueTonalDark,

    secondary            = GitHubGreenDark,
    onSecondary          = OnGreenDark,
    secondaryContainer   = GreenTonalDark,
    onSecondaryContainer = OnGreenTonalDark,

    background           = BackgroundDark,
    onBackground         = OnBackgroundDark,

    surface              = SurfaceDark,
    onSurface            = OnSurfaceDark,
    surfaceVariant       = SurfaceVariantDark,
    onSurfaceVariant     = OnSurfaceVariantDark,
    surfaceTint          = SurfaceTintDark,

    surfaceContainer        = SurfaceContainerDark,
    surfaceContainerLow     = SurfaceContainerLowDark,
    surfaceContainerHigh    = SurfaceContainerHighDark,
    surfaceContainerLowest  = SurfaceContainerLowestDark,
    surfaceContainerHighest = SurfaceContainerHighestDark,

    error                = DangerDark,
    onError              = OnDangerDark,
    errorContainer       = DangerTonalDark,
    onErrorContainer     = OnDangerTonalDark,

    outline              = OutlineDark,
    outlineVariant       = OutlineVariantDark,
)
