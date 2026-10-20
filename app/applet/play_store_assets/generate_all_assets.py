#!/usr/bin/env python3
import os
import subprocess

OUT_DIR = "play_store_assets"
os.makedirs(OUT_DIR, exist_ok=True)

def run(cmd):
    print("Running:", cmd[:80] + "..." if len(cmd) > 80 else cmd)
    res = subprocess.run(cmd, shell=True, capture_output=True, text=True)
    if res.returncode != 0:
        print("Error output:", res.stderr)
        raise RuntimeError(f"Command failed: {cmd[:60]}")

print("Generating 1. App Icon (512x512)...")
# 512x512 App Icon: Deep midnight background, glowing warm sun, layered cloud, vibrant rain drops
cmd_icon = f"""
convert -size 512x512 gradient:'#090D16-#1E293B' \\
  -stroke none \\
  \\( -size 512x512 radial-gradient:'#FDE04744-none' -geometry +30-30 -composite \\) \\
  -fill '#F59E0B' -draw "circle 310,190 310,285" \\
  -fill '#FBBF24' -draw "circle 310,190 310,270" \\
  -fill '#FEF08A' -draw "circle 305,185 305,250" \\
  -fill '#FFFFFF' -draw "circle 220,290 220,360" \\
  -fill '#FFFFFF' -draw "circle 290,260 290,340" \\
  -fill '#FFFFFF' -draw "circle 350,300 350,355" \\
  -fill '#FFFFFF' -draw "roundrectangle 160,300 380,360 30,30" \\
  -fill '#E0F2FE' -draw "roundrectangle 165,310 375,355 25,25" \\
  -fill '#38BDF8' -draw "path 'M 210,380 C 210,395 195,405 195,405 C 195,405 180,395 180,380 C 180,370 195,355 195,355 C 195,355 210,370 210,380 Z'" \\
  -fill '#38BDF8' -draw "path 'M 270,390 C 270,405 255,415 255,415 C 255,415 240,405 240,390 C 240,380 255,365 255,365 C 255,365 270,380 270,390 Z'" \\
  -fill '#38BDF8' -draw "path 'M 330,380 C 330,395 315,405 315,405 C 315,405 300,395 300,380 C 300,370 315,355 315,355 C 315,355 330,370 330,380 Z'" \\
  PNG32:{OUT_DIR}/store_app_icon_512x512.png
"""
run(cmd_icon)

print("Generating 2. Feature Graphic (1024x500)...")
# 1024x500 Feature Graphic: Play Store Header Banner
cmd_feature = f"""
convert -size 1024x500 gradient:'#070B14-#131F38' \\
  \\( -size 1024x500 radial-gradient:'#38BDF822-none' -geometry +350-100 -composite \\) \\
  \\( -size 1024x500 radial-gradient:'#F59E0B22-none' -geometry -200+150 -composite \\) \\
  -fill '#FFFFFF' -font DejaVu-Sans-Bold -pointsize 54 -draw "text 70,140 'ATMOSPHERE'" \\
  -fill '#38BDF8' -font DejaVu-Sans-Bold -pointsize 22 -draw "text 72,185 'REAL-TIME WEATHER & SEVERE ALERTS'" \\
  -fill '#94A3B8' -font DejaVu-Sans -pointsize 18 -draw "text 72,225 'Precision hourly forecasts, customizable sync intervals & storm telemetry.'" \\
  -fill '#1E293B' -stroke '#38BDF888' -strokewidth 1.5 -draw "roundrectangle 70,265 285,310 20,20" \\
  -fill '#38BDF8' -stroke none -font DejaVu-Sans-Bold -pointsize 14 -draw "text 90,294 '⚡ 2-Min Emergency Mode'" \\
  -fill '#1E293B' -stroke '#F59E0B88' -strokewidth 1.5 -draw "roundrectangle 300,265 520,310 20,20" \\
  -fill '#FBBF24' -stroke none -font DejaVu-Sans-Bold -pointsize 14 -draw "text 320,294 '📈 Hourly Curve Graphs'" \\
  -fill '#1E293B' -stroke '#10B98188' -strokewidth 1.5 -draw "roundrectangle 70,325 310,370 20,20" \\
  -fill '#34D399' -stroke none -font DejaVu-Sans-Bold -pointsize 14 -draw "text 90,354 '🔒 100% Private (No Ads)'" \\
  -fill '#1E293B' -stroke '#818CF888' -strokewidth 1.5 -draw "roundrectangle 325,325 540,370 20,20" \\
  -fill '#A5B4FC' -stroke none -font DejaVu-Sans-Bold -pointsize 14 -draw "text 345,354 '⏱️ Smart WorkManager'" \\
  -fill '#182238DD' -stroke '#475569' -strokewidth 2 -draw "roundrectangle 600,60 960,440 28,28" \\
  -fill '#38BDF8' -stroke none -font DejaVu-Sans-Bold -pointsize 16 -draw "text 630,105 'NEW YORK  •  LIVE'" \\
  -fill '#FFFFFF' -font DejaVu-Sans-Bold -pointsize 64 -draw "text 630,180 '21°C'" \\
  -fill '#F59E0B' -font DejaVu-Sans-Bold -pointsize 20 -draw "text 790,145 'Partly Cloudy'" \\
  -fill '#94A3B8' -font DejaVu-Sans -pointsize 15 -draw "text 790,175 'Feels like 22°C'" \\
  -fill '#334155' -draw "roundrectangle 630,215 930,220 2,2" \\
  -fill '#E2E8F0' -font DejaVu-Sans-Bold -pointsize 14 -draw "text 630,250 'Wind: 14 km/h  •  Humidity: 62%  •  UV: 3.2'" \\
  -fill '#0F172ACC' -stroke '#EF4444' -strokewidth 1.5 -draw "roundrectangle 630,280 930,360 16,16" \\
  -fill '#EF4444' -stroke none -font DejaVu-Sans-Bold -pointsize 14 -draw "text 650,312 '🚨 SEVERE THUNDERSTORM ALERT'" \\
  -fill '#CBD5E1' -font DejaVu-Sans -pointsize 12 -draw "text 650,338 'Active Emergency Mode engaged with 2-min sync.'" \\
  -fill '#38BDF8' -font DejaVu-Sans-Bold -pointsize 13 -draw "text 630,405 'Google Play Ready • Version 3.0 Production'" \\
  PNG32:{OUT_DIR}/store_feature_graphic_1024x500.png
"""
run(cmd_feature)

print("Generating 3. Screenshot 1: Real-Time Local Forecast (1080x1920)...")
cmd_s1 = f"""
convert -size 1080x1920 gradient:'#070B14-#111827' \\
  \\( -size 1080x1920 radial-gradient:'#38BDF820-none' -geometry +100-300 -composite \\) \\
  \\( -size 1080x1920 radial-gradient:'#F59E0B18-none' -geometry +600+600 -composite \\) \\
  -fill '#38BDF8' -font DejaVu-Sans-Bold -pointsize 32 -draw "text 80,130 'REAL-TIME LOCAL FORECAST'" \\
  -fill '#FFFFFF' -font DejaVu-Sans-Bold -pointsize 54 -draw "text 80,200 'Accurate Atmospheric Telemetry'" \\
  -fill '#94A3B8' -font DejaVu-Sans -pointsize 26 -draw "text 80,250 'Live GPS weather, dynamic conditions & status bar indicators'" \\
  -fill '#0F172AEE' -stroke '#334155' -strokewidth 4 -draw "roundrectangle 120,320 960,1860 56,56" \\
  -fill '#1E293B' -stroke none -draw "roundrectangle 380,345 700,375 15,15" \\
  -fill '#94A3B8' -font DejaVu-Sans-Bold -pointsize 20 -draw "text 160,420 '09:41' text 870,420 '100%'" \\
  -fill '#FFFFFF' -font DejaVu-Sans-Bold -pointsize 42 -draw "text 170,510 'New York, United States'" \\
  -fill '#38BDF8' -font DejaVu-Sans -pointsize 22 -draw "text 170,555 'Mon, Oct 19 • Auto-synced 2m ago'" \\
  -fill '#F59E0B' -draw "circle 340,730 340,820" \\
  -fill '#FFFFFF' -draw "circle 450,780 450,850" \\
  -fill '#FFFFFF' -draw "circle 530,750 530,830" \\
  -fill '#FFFFFF' -draw "circle 610,790 610,850" \\
  -fill '#FFFFFF' -draw "roundrectangle 380,780 650,850 30,30" \\
  -fill '#FFFFFF' -font DejaVu-Sans-Bold -pointsize 130 -draw "text 370,1020 '21°'" \\
  -fill '#38BDF8' -font DejaVu-Sans-Bold -pointsize 36 -draw "text 390,1080 'Partly Cloudy'" \\
  -fill '#94A3B8' -font DejaVu-Sans -pointsize 26 -draw "text 330,1130 'Feels like 22°C  •  H: 24°  L: 14°'" \\
  -fill '#1E293B88' -stroke '#38BDF844' -strokewidth 2 -draw "roundrectangle 160,1180 520,1320 28,28" \\
  -fill '#38BDF8' -stroke none -font DejaVu-Sans-Bold -pointsize 22 -draw "text 190,1230 'WIND SPEED'" \\
  -fill '#FFFFFF' -font DejaVu-Sans-Bold -pointsize 38 -draw "text 190,1285 '14 km/h'" \\
  -fill '#1E293B88' -stroke '#38BDF844' -strokewidth 2 -draw "roundrectangle 560,1180 920,1320 28,28" \\
  -fill '#38BDF8' -stroke none -font DejaVu-Sans-Bold -pointsize 22 -draw "text 590,1230 'HUMIDITY'" \\
  -fill '#FFFFFF' -font DejaVu-Sans-Bold -pointsize 38 -draw "text 590,1285 '62%'" \\
  -fill '#1E293B88' -stroke '#38BDF844' -strokewidth 2 -draw "roundrectangle 160,1350 520,1490 28,28" \\
  -fill '#38BDF8' -stroke none -font DejaVu-Sans-Bold -pointsize 22 -draw "text 190,1400 'UV INDEX'" \\
  -fill '#FFFFFF' -font DejaVu-Sans-Bold -pointsize 38 -draw "text 190,1455 '3.2 Moderate'" \\
  -fill '#1E293B88' -stroke '#38BDF844' -strokewidth 2 -draw "roundrectangle 560,1350 920,1490 28,28" \\
  -fill '#38BDF8' -stroke none -font DejaVu-Sans-Bold -pointsize 22 -draw "text 590,1400 'AIR PRESSURE'" \\
  -fill '#FFFFFF' -font DejaVu-Sans-Bold -pointsize 38 -draw "text 590,1455 '1014 hPa'" \\
  -fill '#1E293BCC' -stroke '#38BDF888' -strokewidth 2 -draw "roundrectangle 160,1530 920,1720 32,32" \\
  -fill '#38BDF8' -stroke none -font DejaVu-Sans-Bold -pointsize 22 -draw "text 200,1585 'LIVE NOTIFICATION BAR WIDGET'" \\
  -fill '#FFFFFF' -font DejaVu-Sans-Bold -pointsize 28 -draw "text 200,1635 'New York  21°C  •  Partly Cloudy'" \\
  -fill '#94A3B8' -font DejaVu-Sans -pointsize 20 -draw "text 200,1680 'Ongoing live status • Single timestamp clean header'" \\
  PNG32:{OUT_DIR}/screenshot_1_live_forecast.png
"""
run(cmd_s1)

print("Generating 4. Screenshot 2: Hourly Trends & 7-Day Forecast (1080x1920)...")
cmd_s2 = f"""
convert -size 1080x1920 gradient:'#070B14-#111827' \\
  \\( -size 1080x1920 radial-gradient:'#F59E0B20-none' -geometry +100-300 -composite \\) \\
  \\( -size 1080x1920 radial-gradient:'#38BDF820-none' -geometry +600+600 -composite \\) \\
  -fill '#F59E0B' -font DejaVu-Sans-Bold -pointsize 32 -draw "text 80,130 'HOURLY TRENDS & 7-DAY FORECAST'" \\
  -fill '#FFFFFF' -font DejaVu-Sans-Bold -pointsize 54 -draw "text 80,200 'Smooth Temperature Curves'" \\
  -fill '#94A3B8' -font DejaVu-Sans -pointsize 26 -draw "text 80,250 'Interactive cubic Bézier graphs with 12h / 24h clock toggle'" \\
  -fill '#0F172AEE' -stroke '#334155' -strokewidth 4 -draw "roundrectangle 120,320 960,1860 56,56" \\
  -fill '#1E293B' -stroke none -draw "roundrectangle 380,345 700,375 15,15" \\
  -fill '#94A3B8' -font DejaVu-Sans-Bold -pointsize 20 -draw "text 160,420 '09:41' text 870,420 '100%'" \\
  -fill '#FFFFFF' -font DejaVu-Sans-Bold -pointsize 36 -draw "text 170,500 '24-Hour Temperature Curve'" \\
  -fill '#38BDF8' -font DejaVu-Sans -pointsize 22 -draw "text 170,540 'Hourly variations and rain probability'" \\
  -fill '#1E293B88' -stroke '#38BDF844' -strokewidth 2 -draw "roundrectangle 160,570 920,950 32,32" \\
  -stroke '#38BDF8' -strokewidth 5 -fill none -draw "path 'M 190,820 C 300,750 380,680 480,660 C 580,640 680,720 780,790 C 840,830 870,850 890,870'" \\
  -fill '#F59E0B' -stroke none -draw "circle 190,820 190,830" -draw "circle 480,660 480,670" -draw "circle 780,790 780,800" \\
  -fill '#FFFFFF' -font DejaVu-Sans-Bold -pointsize 20 -draw "text 175,800 '18°' text 465,640 '24°' text 765,770 '19°'" \\
  -fill '#94A3B8' -font DejaVu-Sans -pointsize 18 -draw "text 170,880 'Now' text 310,880 '12:00' text 460,880 '15:00' text 610,880 '18:00' text 760,880 '21:00'" \\
  -fill '#FFFFFF' -font DejaVu-Sans-Bold -pointsize 36 -draw "text 170,1020 '7-Day Extended Forecast'" \\
  -fill '#1E293B99' -stroke '#334155' -strokewidth 1.5 -draw "roundrectangle 160,1060 920,1160 22,22" \\
  -fill '#FFFFFF' -stroke none -font DejaVu-Sans-Bold -pointsize 24 -draw "text 200,1120 'Today' text 400,1120 'Partly Cloudy' text 760,1120 '24° / 14°'" \\
  -fill '#1E293B99' -stroke '#334155' -strokewidth 1.5 -draw "roundrectangle 160,1180 920,1280 22,22" \\
  -fill '#FFFFFF' -stroke none -font DejaVu-Sans-Bold -pointsize 24 -draw "text 200,1240 'Tue' text 400,1240 'Sunny & Warm' text 760,1240 '26° / 15°'" \\
  -fill '#1E293B99' -stroke '#334155' -strokewidth 1.5 -draw "roundrectangle 160,1300 920,1400 22,22" \\
  -fill '#FFFFFF' -stroke none -font DejaVu-Sans-Bold -pointsize 24 -draw "text 200,1360 'Wed' text 400,1360 'Showers' text 760,1360 '19° / 12°'" \\
  -fill '#1E293B99' -stroke '#334155' -strokewidth 1.5 -draw "roundrectangle 160,1420 920,1520 22,22" \\
  -fill '#FFFFFF' -stroke none -font DejaVu-Sans-Bold -pointsize 24 -draw "text 200,1480 'Thu' text 400,1480 'Thunderstorm' text 760,1480 '18° / 11°'" \\
  -fill '#1E293B99' -stroke '#334155' -strokewidth 1.5 -draw "roundrectangle 160,1540 920,1640 22,22" \\
  -fill '#FFFFFF' -stroke none -font DejaVu-Sans-Bold -pointsize 24 -draw "text 200,1600 'Fri' text 400,1600 'Clear Skies' text 760,1600 '22° / 13°'" \\
  -fill '#1E293B99' -stroke '#334155' -strokewidth 1.5 -draw "roundrectangle 160,1660 920,1760 22,22" \\
  -fill '#FFFFFF' -stroke none -font DejaVu-Sans-Bold -pointsize 24 -draw "text 200,1720 'Sat' text 400,1720 'Pleasant' text 760,1720 '23° / 14°'" \\
  PNG32:{OUT_DIR}/screenshot_2_hourly_forecast.png
"""
run(cmd_s2)

print("Generating 5. Screenshot 3: Severe Alerts & Emergency Mode (1080x1920)...")
cmd_s3 = f"""
convert -size 1080x1920 gradient:'#160B0B-#111827' \\
  \\( -size 1080x1920 radial-gradient:'#EF444425-none' -geometry +100-300 -composite \\) \\
  \\( -size 1080x1920 radial-gradient:'#F59E0B20-none' -geometry +600+600 -composite \\) \\
  -fill '#EF4444' -font DejaVu-Sans-Bold -pointsize 32 -draw "text 80,130 'CRITICAL STORM ALERTS & EMERGENCY MODE'" \\
  -fill '#FFFFFF' -font DejaVu-Sans-Bold -pointsize 54 -draw "text 80,200 'Instant Life-Safety Warnings'" \\
  -fill '#94A3B8' -font DejaVu-Sans -pointsize 26 -draw "text 80,250 'Rapid 2-minute foreground sync during severe thunderstorms & tornadoes'" \\
  -fill '#0F172AEE' -stroke '#450A0A' -strokewidth 4 -draw "roundrectangle 120,320 960,1860 56,56" \\
  -fill '#1E293B' -stroke none -draw "roundrectangle 380,345 700,375 15,15" \\
  -fill '#94A3B8' -font DejaVu-Sans-Bold -pointsize 20 -draw "text 160,420 '09:41' text 870,420 '100%'" \\
  -fill '#450A0ACC' -stroke '#EF4444' -strokewidth 3 -draw "roundrectangle 160,480 920,830 36,36" \\
  -fill '#EF4444' -stroke none -font DejaVu-Sans-Bold -pointsize 36 -draw "text 200,550 '🚨 SEVERE WEATHER WARNING'" \\
  -fill '#FFFFFF' -font DejaVu-Sans-Bold -pointsize 26 -draw "text 200,610 'Severe Thunderstorm & Lightning Warning Detected'" \\
  -fill '#FECACA' -font DejaVu-Sans -pointsize 20 -draw "text 200,660 'Damaging wind gusts up to 75 km/h, intense cloud-to-ground lightning'" \\
  -fill '#FECACA' -font DejaVu-Sans -pointsize 20 -draw "text 200,700 'and hail possible in your local area over the next 2 hours.'" \\
  -fill '#EF4444' -draw "roundrectangle 200,735 520,795 20,20" \\
  -fill '#FFFFFF' -font DejaVu-Sans-Bold -pointsize 20 -draw "text 220,772 '⚡ 2-MIN SYNC ACTIVE'" \\
  -fill '#FFFFFF' -font DejaVu-Sans-Bold -pointsize 32 -draw "text 170,890 'Recommended Precautions'" \\
  -fill '#1E293B99' -stroke '#334155' -strokewidth 1.5 -draw "roundrectangle 160,930 920,1030 22,22" \\
  -fill '#34D399' -stroke none -font DejaVu-Sans-Bold -pointsize 24 -draw "text 200,990 '✓'" \\
  -fill '#FFFFFF' -font DejaVu-Sans -pointsize 22 -draw "text 240,990 'Seek shelter indoors immediately away from windows'" \\
  -fill '#1E293B99' -stroke '#334155' -strokewidth 1.5 -draw "roundrectangle 160,1050 920,1150 22,22" \\
  -fill '#34D399' -stroke none -font DejaVu-Sans-Bold -pointsize 24 -draw "text 200,1110 '✓'" \\
  -fill '#FFFFFF' -font DejaVu-Sans -pointsize 22 -draw "text 240,1110 'Unplug sensitive electronics and stay off landlines'" \\
  -fill '#1E293B99' -stroke '#334155' -strokewidth 1.5 -draw "roundrectangle 160,1170 920,1270 22,22" \\
  -fill '#34D399' -stroke none -font DejaVu-Sans-Bold -pointsize 24 -draw "text 200,1230 '✓'" \\
  -fill '#FFFFFF' -font DejaVu-Sans -pointsize 22 -draw "text 240,1230 'Monitor local civil protection broadcast updates'" \\
  -fill '#182238DD' -stroke '#EF4444' -strokewidth 2 -draw "roundrectangle 160,1320 920,1540 32,32" \\
  -fill '#EF4444' -stroke none -font DejaVu-Sans-Bold -pointsize 24 -draw "text 200,1375 'FOREGROUND SERVICE STATUS'" \\
  -fill '#FFFFFF' -font DejaVu-Sans-Bold -pointsize 26 -draw "text 200,1430 'High-Priority Alert Notification'" \\
  -fill '#94A3B8' -font DejaVu-Sans -pointsize 20 -draw "text 200,1475 'Persistent notification bar monitoring ensures instant notification'" \\
  -fill '#94A3B8' -font DejaVu-Sans -pointsize 20 -draw "text 200,1505 'when storm conditions clear and automatically returns to WorkManager.'" \\
  -fill '#1E293B' -stroke '#38BDF8' -strokewidth 2 -draw "roundrectangle 160,1580 920,1720 28,28" \\
  -fill '#38BDF8' -stroke none -font DejaVu-Sans-Bold -pointsize 24 -draw "text 200,1640 '🛡️ Civil Defense Disclaimer'" \\
  -fill '#CBD5E1' -font DejaVu-Sans -pointsize 18 -draw "text 200,1680 'Complies with Google Play emergency and life-safety guidelines.'" \\
  PNG32:{OUT_DIR}/screenshot_3_severe_alerts.png
"""
run(cmd_s3)

print("Generating 6. Screenshot 4: Smart Background Sync & Privacy (1080x1920)...")
cmd_s4 = f"""
convert -size 1080x1920 gradient:'#070B14-#111827' \\
  \\( -size 1080x1920 radial-gradient:'#10B98120-none' -geometry +100-300 -composite \\) \\
  \\( -size 1080x1920 radial-gradient:'#38BDF820-none' -geometry +600+600 -composite \\) \\
  -fill '#34D399' -font DejaVu-Sans-Bold -pointsize 32 -draw "text 80,130 'CUSTOM SYNC & ZERO-TRACKING PRIVACY'" \\
  -fill '#FFFFFF' -font DejaVu-Sans-Bold -pointsize 54 -draw "text 80,200 'Battery Friendly & 100% Private'" \\
  -fill '#94A3B8' -font DejaVu-Sans -pointsize 26 -draw "text 80,250 'Strict 30-min minimum battery rule & Google Play compliant privacy policy'" \\
  -fill '#0F172AEE' -stroke '#334155' -strokewidth 4 -draw "roundrectangle 120,320 960,1860 56,56" \\
  -fill '#1E293B' -stroke none -draw "roundrectangle 380,345 700,375 15,15" \\
  -fill '#94A3B8' -font DejaVu-Sans-Bold -pointsize 20 -draw "text 160,420 '09:41' text 870,420 '100%'" \\
  -fill '#FFFFFF' -font DejaVu-Sans-Bold -pointsize 36 -draw "text 170,500 'Time Format'" \\
  -fill '#38BDF8' -draw "roundrectangle 170,530 400,600 24,24" \\
  -fill '#FFFFFF' -font DejaVu-Sans-Bold -pointsize 22 -draw "text 205,575 '12h (AM/PM)'" \\
  -fill '#1E293B' -stroke '#475569' -strokewidth 1.5 -draw "roundrectangle 420,530 650,600 24,24" \\
  -fill '#94A3B8' -stroke none -font DejaVu-Sans -pointsize 22 -draw "text 475,575 '24 Hours'" \\
  -fill '#FFFFFF' -font DejaVu-Sans-Bold -pointsize 36 -draw "text 170,680 'Background Sync Interval'" \\
  -fill '#94A3B8' -font DejaVu-Sans -pointsize 20 -draw "text 170,720 'Periodic background weather update frequency (WorkManager)'" \\
  -fill '#1E293B' -stroke '#475569' -strokewidth 1.5 -draw "roundrectangle 170,760 300,830 20,20" \\
  -fill '#94A3B8' -stroke none -font DejaVu-Sans -pointsize 22 -draw "text 215,805 '30m'" \\
  -fill '#38BDF8' -draw "roundrectangle 320,760 450,830 20,20" \\
  -fill '#FFFFFF' -font DejaVu-Sans-Bold -pointsize 22 -draw "text 370,805 '1h'" \\
  -fill '#1E293B' -stroke '#475569' -strokewidth 1.5 -draw "roundrectangle 470,760 600,830 20,20" \\
  -fill '#94A3B8' -stroke none -font DejaVu-Sans -pointsize 22 -draw "text 520,805 '2h'" \\
  -fill '#1E293B' -stroke '#475569' -strokewidth 1.5 -draw "roundrectangle 620,760 750,830 20,20" \\
  -fill '#94A3B8' -stroke none -font DejaVu-Sans -pointsize 22 -draw "text 670,805 '6h'" \\
  -fill '#1E293B' -stroke '#38BDF8' -strokewidth 1.5 -draw "roundrectangle 770,760 920,830 20,20" \\
  -fill '#38BDF8' -stroke none -font DejaVu-Sans-Bold -pointsize 20 -draw "text 795,805 'Custom...'" \\
  -fill '#1E293B88' -stroke '#F59E0B66' -strokewidth 2 -draw "roundrectangle 170,860 920,950 20,20" \\
  -fill '#F59E0B' -stroke none -font DejaVu-Sans-Bold -pointsize 18 -draw "text 200,900 'Strict Battery Rule: minimum 30 minutes enforced in normal mode'" \\
  -fill '#94A3B8' -font DejaVu-Sans -pointsize 16 -draw "text 200,930 'Preserves battery health & adheres to Android background execution limits.'" \\
  -fill '#182238DD' -stroke '#34D399' -strokewidth 2 -draw "roundrectangle 170,1000 920,1440 32,32" \\
  -fill '#34D399' -stroke none -font DejaVu-Sans-Bold -pointsize 26 -draw "text 210,1060 'PRIVACY & GOOGLE PLAY COMPLIANCE'" \\
  -fill '#94A3B8' -font DejaVu-Sans -pointsize 20 -draw "text 210,1100 'Google Play Developer Program Compliant • Zero Tracking'" \\
  -fill '#1E293B' -stroke '#334155' -strokewidth 1.5 -draw "roundrectangle 210,1130 880,1210 18,18" \\
  -fill '#FFFFFF' -stroke none -font DejaVu-Sans-Bold -pointsize 22 -draw "text 240,1180 '🔒 In-App Privacy Policy & Data Safety'" \\
  -fill '#1E293B' -stroke '#334155' -strokewidth 1.5 -draw "roundrectangle 210,1230 880,1310 18,18" \\
  -fill '#FFFFFF' -stroke none -font DejaVu-Sans-Bold -pointsize 22 -draw "text 240,1280 '⚖️ Terms of Service & Weather Disclaimer'" \\
  -fill '#1E293B' -stroke '#334155' -strokewidth 1.5 -draw "roundrectangle 210,1330 880,1410 18,18" \\
  -fill '#FFFFFF' -stroke none -font DejaVu-Sans-Bold -pointsize 22 -draw "text 240,1380 '🌐 Open-Meteo CC BY 4.0 Open Data Attribution'" \\
  -fill '#1E293BCC' -stroke '#334155' -strokewidth 2 -draw "roundrectangle 170,1490 920,1720 28,28" \\
  -fill '#38BDF8' -stroke none -font DejaVu-Sans-Bold -pointsize 24 -draw "text 210,1550 'Developer Support & Inquiries'" \\
  -fill '#FFFFFF' -font DejaVu-Sans-Bold -pointsize 22 -draw "text 210,1600 'muzamilofficially@gmail.com'" \\
  -fill '#94A3B8' -font DejaVu-Sans -pointsize 18 -draw "text 210,1645 'Tap to compose email directly from app'" \\
  -fill '#34D399' -font DejaVu-Sans-Bold -pointsize 18 -draw "text 210,1690 'Clear Local Cache & Reset Data button provided'" \\
  PNG32:{OUT_DIR}/screenshot_4_sync_privacy.png
"""
run(cmd_s4)

print("All 6 store listing visual assets generated successfully!")
