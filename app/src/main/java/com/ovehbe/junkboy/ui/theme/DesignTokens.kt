package com.ovehbe.junkboy.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Colors
object DesignColors {
    // Base colors
    val Primary: Color = Color(0xFF000000)
    val Secondary: Color = Color(0xFF6B6B6B)
    val Accent: Color = Color(0xFFFF914D)
    val Background: Color = Color(0xFFFFFFFF)
    val Surface: Color = Color(0xFFF9F9F9)
    val OnPrimary: Color = Color(0xFFFFFFFF)
    val OnSecondary: Color = Color(0xFFFFFFFF)
    val OnSurface: Color = Color(0xFF000000)
    val OnBackground: Color = Color(0xFF000000)
    
    // Component specific colors
    val ButtonBackground: Color = Color(0xFF000000)
    val ButtonText: Color = Color(0xFFFFFFFF)
    val InputBackground: Color = Color(0xFFF5F5F5)
    val InputBorder: Color = Color(0xFFE0E0E0)
    val Divider: Color = Color(0xFFE0E0E0)
    
    // Navigation colors
    val NavigationBackground: Color = Color(0xFFFFFFFF)
    val NavigationActive: Color = Color(0xFF000000)
    val NavigationInactive: Color = Color(0xFF6B6B6B)
    
    // Message category colors
    val GeneralMessage: Color = Color(0xFF4CAF50)
    val PromotionMessage: Color = Color(0xFF2196F3)
    val NotificationMessage: Color = Color(0xFFF57C00)
    val TransactionMessage: Color = Color(0xFF9C27B0)
    val JunkMessage: Color = Color(0xFFF44336)
    
    // Search and filtering
    val SearchBackground: Color = Color(0xFFF0F0F0)
    
    // Badge colors
    val BadgeBackground: Color = Color(0xFFFF914D)
    val BadgeText: Color = Color(0xFFFFFFFF)
    
    // Active state
    val ActiveBackground: Color = Color(0xFFEFEFEF)
}

// Typography
object DesignTypography {
    val FontSizes = object {
        val XS = 12.sp
        val SM = 14.sp
        val Base = 16.sp
        val LG = 18.sp
        val XL = 20.sp
        val XXL = 24.sp
        val DisplayLarge = 32.sp
        val DisplayMedium = 28.sp
    }
    
    val FontWeights = object {
        val Regular = FontWeight.Normal
        val Medium = FontWeight.Medium
        val Bold = FontWeight.Bold
    }
    
    val LineHeights = object {
        val Base = 1.4f
        val Heading = 1.2f
    }
}

// Spacing
object DesignSpacing {
    val None = 0.dp
    val XS = 4.dp
    val SM = 8.dp
    val MD = 16.dp
    val LG = 24.dp
    val XL = 32.dp
}

// Border Radius
object DesignBorderRadius {
    val SM = 6.dp
    val MD = 12.dp
    val LG = 20.dp
    val Full = 999.dp
}

// Layout
object DesignLayout {
    val ContainerPadding = 16.dp
    val ListItemSpacing = 16.dp
    val AvatarSize = 40.dp
    val IconSize = 20.dp
    val BottomNavHeight = 56.dp
    val MessageBubbleSpacing = 8.dp
}

// Component Specifications
object DesignComponents {
    object Button {
        val Height: androidx.compose.ui.unit.Dp = 48.dp
        val BorderRadius: androidx.compose.ui.unit.Dp = DesignBorderRadius.MD
        val FontWeight: androidx.compose.ui.text.font.FontWeight = androidx.compose.ui.text.font.FontWeight.Medium
    }
    
    object Input {
        val BorderRadius: androidx.compose.ui.unit.Dp = DesignBorderRadius.MD
        val Padding: androidx.compose.ui.unit.Dp = DesignSpacing.SM
        val Background: Color = DesignColors.InputBackground
        val Border: Color = DesignColors.InputBorder
    }
    
    object ChatBubble {
        val BorderRadius: androidx.compose.ui.unit.Dp = DesignBorderRadius.LG
        val Padding: androidx.compose.ui.unit.Dp = DesignSpacing.SM
    }
    
    object Badge {
        val BorderRadius: androidx.compose.ui.unit.Dp = DesignBorderRadius.Full
        val FontSize: androidx.compose.ui.unit.TextUnit = 12.sp
        val PaddingHorizontal: androidx.compose.ui.unit.Dp = 8.dp
        val PaddingVertical: androidx.compose.ui.unit.Dp = 4.dp
    }
    
    object Divider {
        val Color: androidx.compose.ui.graphics.Color = DesignColors.Divider
        val Thickness: androidx.compose.ui.unit.Dp = 1.dp
    }
}

// Iconography
object DesignIcons {
    val StrokeWidth = 2.dp
    val Size = 20.dp
} 