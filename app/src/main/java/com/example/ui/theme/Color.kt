package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Pure Minimalist Monochrome Palette: Gray, White, and Black
val PureBlack = Color(0xFF000000)
val PureWhite = Color(0xFFFFFFFF)

// Dark Theme Surfaces & Backgrounds
val DarkBackground = Color(0xFF09090B)      // Obsidian Black (Zinc 950)
val DarkSurface = Color(0xFF141417)         // Deep Charcoal
val DarkSurfaceVariant = Color(0xFF1C1C21)  // Card Surface
val DarkSurfaceElevated = Color(0xFF26262D) // Elevated Popover/Dialog
val DarkBorder = Color(0xFF2E2E36)          // Crisp Border
val DarkBorderSubtle = Color(0xFF202026)    // Subtle Divider

// Monochromatic Gray Scales
val Gray50 = Color(0xFFFAFAFA)
val Gray100 = Color(0xFFF4F4F5)
val Gray200 = Color(0xFFE4E4E7)
val Gray300 = Color(0xFFD4D4D8)
val Gray400 = Color(0xFFA1A1AA)
val Gray500 = Color(0xFF71717A)
val Gray600 = Color(0xFF52525B)
val Gray700 = Color(0xFF3F3F46)
val Gray800 = Color(0xFF27272A)
val Gray900 = Color(0xFF18181B)
val Gray950 = Color(0xFF09090B)

// Light Theme Surfaces & Backgrounds
val LightBackground = Color(0xFFFFFFFF)
val LightSurface = Color(0xFFFAFAFB)
val LightSurfaceVariant = Color(0xFFF1F1F3)
val LightBorder = Color(0xFFE2E2E6)
val LightBorderSubtle = Color(0xFFECECEF)

// Minimalist Semantic Aliases
val MonoPrimary = PureWhite
val MonoSecondary = Gray300
val MonoMuted = Gray500
val MonoContainer = Gray800
val MonoBorder = DarkBorder

// Monochromatic Backward-Compatibility Tokens (Strictly Grayscale)
val EmeraldPrimary = PureWhite
val EmeraldLight = Gray300
val CyanAccent = Gray300
val AmberAlert = Gray400
val KarachiClockGold = Gray200
val RunningBlue = Gray300
val GroomingPurple = Gray400
val HealthGreen = Gray200
val FocusIndigo = Gray300
