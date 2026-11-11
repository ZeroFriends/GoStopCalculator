---
name: GoStop Calculator Official Theme
colors:
  surface: '#FFFFFF'
  surface-dim: '#F8F8F8'
  surface-bright: '#FFFFFF'
  surface-container-lowest: '#FFFFFF'
  surface-container-low: '#F8F8F8'
  surface-container: '#FFFFFF'
  surface-container-high: '#F5F5F5'
  surface-container-highest: '#EEEEEE'
  on-surface: '#212121'
  on-surface-variant: '#616161'
  inverse-surface: '#212121'
  inverse-on-surface: '#FFFFFF'
  outline: '#EEEEEE'
  outline-variant: '#E0E0E0'
  surface-tint: '#FF5252'
  primary: '#FF5252'
  on-primary: '#FFFFFF'
  primary-container: '#FF5252'
  on-primary-container: '#FFFFFF'
  inverse-primary: '#FF5252'
  secondary: '#1E88E5'
  on-secondary: '#FFFFFF'
  secondary-container: '#E3F2FD'
  on-secondary-container: '#0D47A1'
  tertiary: '#1E88E5'
  on-tertiary: '#FFFFFF'
  tertiary-container: '#E3F2FD'
  on-tertiary-container: '#0D47A1'
  error: '#FF5252'
  on-error: '#FFFFFF'
  error-container: '#FFEBEE'
  on-error-container: '#C62828'
  primary-fixed: '#FFCDD2'
  primary-fixed-dim: '#EF9A9A'
  on-primary-fixed: '#B71C1C'
  on-primary-fixed-variant: '#C62828'
  secondary-fixed: '#BBDEFB'
  secondary-fixed-dim: '#90CAF9'
  on-secondary-fixed: '#0D47A1'
  on-secondary-fixed-variant: '#1565C0'
  tertiary-fixed: '#BBDEFB'
  tertiary-fixed-dim: '#90CAF9'
  on-tertiary-fixed: '#0D47A1'
  on-tertiary-fixed-variant: '#1565C0'
  background: '#FFFFFF'
  on-background: '#212121'
  surface-variant: '#F8F8F8'
typography:
  display-hero:
    fontFamily: Pretendard
    fontSize: 32px
    fontWeight: '800'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Pretendard
    fontSize: 24px
    fontWeight: '700'
    lineHeight: 32px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Pretendard
    fontSize: 20px
    fontWeight: '700'
    lineHeight: 28px
  title-md:
    fontFamily: Pretendard
    fontSize: 16px
    fontWeight: '700'
    lineHeight: 22px
  body-lg:
    fontFamily: Pretendard
    fontSize: 15px
    fontWeight: '500'
    lineHeight: 22px
  body-md:
    fontFamily: Pretendard
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-lg:
    fontFamily: Pretendard
    fontSize: 14px
    fontWeight: '700'
    lineHeight: 18px
  label-md:
    fontFamily: Pretendard
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
  label-sm:
    fontFamily: Pretendard
    fontSize: 11px
    fontWeight: '500'
    lineHeight: 14px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 1rem
  margin: 1rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
---

## Brand & Style

This design system faithfully replicates the visual identity of the GoStop Calculator (고스톱 금액계산기) app built with Compose Multiplatform for Android and iOS.

The aesthetic is Clean Modern Material Design with vibrant Korean Hwatu (화투) color accents:
- **Canvas & Primary Surfaces:** Crisp, pure white (`#FFFFFF`) backgrounds and cards, delivering maximum readability, transparency, and a friendly, dispute-free game night experience.
- **Brand Identity Red:** Energetic `OrangeyRed` (`#FF5252`) and `GoStopColor` (`#F44336`) used for setup headers, primary action buttons, winning badges, and active state highlights.
- **Profit & Loss Contrast:** Positive profits and receivables are displayed in vivid `OrangeyRed` (`#FF5252` e.g., `+24,000원`), while losses and payables are displayed in `Blue` (`#1E88E5` e.g., `-12,000원`).
- **Pill Geometry:** Distinctive full pill action buttons (`border-radius: 9999px`) and generous 16dp-24dp rounded sheet corners (`AprilBackground`).

## Colors

- **Primary (`#FF5252` / `#F44336`):** Vibrant Orangey Red. Primary action triggers, setup headers, and winner indicators.
- **Positive Accent (`#FF5252`):** Orangey Red (`OrangeyRed`). Exclusively for positive profits, accounts receivable, and net winnings (`+24,000원`).
- **Negative Accent (`#1E88E5`):** Clear Royal Blue (`Blue`). Used for loser accounts, payables, and negative balances (`-12,000원`).
- **Neutral Dark (`#212121` - Nero):** High-contrast dark grey for typography, headers, and navigation icons.
- **Neutral Mid (`#616161` & `#9E9E9E`):** Medium grey for subtitles, descriptions, and helper text.
- **Surface & Background (`#FFFFFF`):** Pure white clean canvas.
- **Secondary Surface (`#F8F8F8`):** Subtle grey for rule sheets, list containers, and background cards.
- **Dividers (`#EEEEEE`):** Crisp 1px structural separation.

## Typography

- **App Font Family:** Platform default sans-serif (`FontFamily.Default`: Roboto on Android / SF Pro on iOS). Showcase/marketing HTML cards use `Pretendard` for display presentation.
- **Amounts:** Clear tabular figures with thousands separator commas (`+24,000원`, `-12,000원`).
- **Title Sizes:** 24px Screen Titles, 18px Card Headers, 14px Body & Labels.

## Components

### 1. Primary Action Button (`GoStopButton`)
- Full pill rounded button (`border-radius: 9999px`).
- Solid `OrangeyRed` (`#FF5252`) background with bold white text.
- Disabled state uses soft neutral grey (`#C7C7C7`).

### 2. Header Container (`AprilBackground`)
- Solid `OrangeyRed` (`#FF5252`) top banner with white text.
- Content surface is a white card with top corners rounded at 24px (`rounded-t-3xl`), seamlessly sliding into the white base.

### 3. Top App Bar (`CenterTextTopBar`)
- Flat white background (`#FFFFFF`) with 0 elevation.
- Centered 14px title in dark Nero (`#212121`).
- Clean back chevron `<` on the left, close `✕` icon on the right.

### 4. Player & Settlement Rows
- Crisp white surface cards with subtle 1px border (`#EEEEEE`) or soft drop shadow.
- Winner highlighted with `OrangeyRed` (`#FF5252`) profit text (`+24,000원`).
- Losers clearly tagged with penalty tags (`피박`, `광박`) and `Blue` (`#1E88E5`) loss amounts (`-12,000원`).
