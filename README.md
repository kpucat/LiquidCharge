# Liquid Charge — Plug-In Phone v0.3

The charging animation has been redesigned around a phone being physically plugged in.

Sequence:
1. Minimal phone outline rises into place.
2. Charging cable travels upward and connects to the phone.
3. Screen glow appears.
4. Current battery percentage and "Charging" appear.
5. A slim internal battery fill animates to the current percentage.
6. A tiny lightning mark flashes at connection.
7. The whole overlay fades away after about 2.85 seconds.

Design goals:
- No circular charging ring.
- No particle-heavy effect.
- Programmatic vector drawing instead of video/GIF assets.
- Short-lived overlay rather than a permanently visible service.
- Clean Samsung/One UI-inspired appearance.
