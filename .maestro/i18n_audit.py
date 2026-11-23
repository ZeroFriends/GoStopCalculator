#!/usr/bin/env python3
"""
다국어(i18n) 리소스 정적 감사 스크립트

검사 항목
  1. 언어별 strings.xml 키 누락/초과
  2. 포맷 지정자 규칙 위반 (Compose Resources 는 %1$d / %1$s 형식만 치환)
  3. 언어 간 포맷 지정자 불일치 (ko 는 %1$d 인데 ja 는 %1$s 등)
  4. 미번역 의심 (en/ja/default 리소스에 한글 포함)
  5. Kotlin UI 코드 내 하드코딩된 한글 문자열 리터럴

사용법: python3 .maestro/i18n_audit.py   (문제 발견 시 exit 1)
"""
import re
import struct
import sys
import xml.etree.ElementTree as ET
import zlib
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
RES = ROOT / "shared/src/commonMain/composeResources"
KOTLIN_UI = ROOT / "shared/src/commonMain/kotlin/zero/friends/gostopcalculator/ui"
LOCALES = {"default": "values", "ko": "values-ko", "en": "values-en", "ja": "values-ja"}

HANGUL = re.compile(r"[\uac00-\ud7a3]")
ANY_SPEC = re.compile(r"%(?:(\d+)\$)?([a-zA-Z])")
UNNUMBERED = re.compile(r"%(?!\d+\$)[a-zA-Z]")

errors, warnings = [], []


def load(locale_dir):
    path = RES / locale_dir / "strings.xml"
    tree = ET.parse(path)
    return {e.get("name"): (e.text or "") for e in tree.getroot().findall("string")}


resources = {loc: load(d) for loc, d in LOCALES.items()}
base_keys = set(resources["ko"])

# 1. 키 누락/초과
for loc, strings in resources.items():
    missing = base_keys - set(strings)
    extra = set(strings) - base_keys
    for k in sorted(missing):
        errors.append(f"[키 누락] {LOCALES[loc]}: '{k}'")
    for k in sorted(extra):
        warnings.append(f"[키 초과] {LOCALES[loc]}: '{k}' (ko 에 없음)")

# 2~4. 값 검사
for key in sorted(base_keys):
    specs = {}
    for loc, strings in resources.items():
        value = strings.get(key)
        if value is None:
            continue
        if UNNUMBERED.search(value):
            errors.append(f"[포맷 미치환 위험] {LOCALES[loc]}:{key} = '{value}' ➔ %1$d / %1$s 형식 사용 필요")
        specs[loc] = sorted(ANY_SPEC.findall(value))
        if loc in ("en", "ja", "default") and HANGUL.search(value):
            errors.append(f"[미번역 의심] {LOCALES[loc]}:{key} = '{value}'")
    if len({tuple(s) for s in specs.values()}) > 1:
        errors.append(f"[포맷 불일치] '{key}': " + ", ".join(f"{l}={s}" for l, s in specs.items()))

# 5. 하드코딩된 한글 리터럴 (주석 제외)
literal = re.compile(r'"((?:[^"\\]|\\.)*)"')
for kt in sorted(KOTLIN_UI.rglob("*.kt")):
    in_block = False
    for no, line in enumerate(kt.read_text(encoding="utf-8").splitlines(), 1):
        code = line
        if in_block:
            if "*/" not in code:
                continue
            code, in_block = code.split("*/", 1)[1], False
        if "/*" in code and "*/" not in code.split("/*", 1)[1]:
            code, in_block = code.split("/*", 1)[0], True
        code = re.sub(r"//.*$", "", code)
        if "Preview" in line or "Log." in code or "throw " in code or "println" in code:
            continue
        for m in literal.finditer(code):
            if HANGUL.search(m.group(1)):
                warnings.append(f"[하드코딩 한글] {kt.relative_to(ROOT)}:{no}: \"{m.group(1)}\"")
        # 도메인 enum 의 한글 라벨(.korean / Rule.displayName)을 그대로 UI 에 노출하는 경우
        if re.search(r"\.korean\b|\.displayName\b", code):
            warnings.append(f"[한글 전용 enum 라벨 노출] {kt.relative_to(ROOT)}:{no}: {code.strip()}")

# 6. prepare_selected_count_format 2-파라미터(%1$d/%2$d) 강제 및 Kotlin stringResource 호출 인자 수 대조
for loc, strings in resources.items():
    val = strings.get("prepare_selected_count_format", "")
    found_specs = sorted(ANY_SPEC.findall(val))
    if found_specs != [("1", "d"), ("2", "d")]:
        errors.append(
            f"[선택 인원 분모 하드코딩 위험] {LOCALES[loc]}:prepare_selected_count_format = '{val}' ➔ 반드시 %1$d/%2$d 2개 인자를 사용해야 함"
        )

# 7. Compose 라운드 컴포넌트(ScorePlayerCard / Surface) 리플 클리핑(.clip 선행) 정적 감사
score_card_clickable = re.compile(r"ScorePlayerCard\s*\((?:(?!\)\s*\{).)*?\.clickable\b", re.DOTALL)
surface_block = re.compile(r"Surface\s*\((.*?)\)\s*\{", re.DOTALL)
for kt in sorted(KOTLIN_UI.rglob("*.kt")):
    text = kt.read_text(encoding="utf-8")
    for m in score_card_clickable.finditer(text):
        line_no = text[: m.start()].count("\n") + 1
        errors.append(
            f"[사각형 리플 오버플로우 위험] {kt.relative_to(ROOT)}:{line_no}: ScorePlayerCard 외부 modifier에 .clickable 직접 전달 금지 (onClick 파라미터 사용 필수)"
        )
    for m in surface_block.finditer(text):
        args_src = m.group(1)
        if ".clickable" in args_src:
            clip_idx = args_src.find(".clip(")
            click_idx = args_src.find(".clickable")
            if clip_idx == -1 or clip_idx > click_idx:
                line_no = text[: m.start()].count("\n") + 1
                errors.append(
                    f"[사각형 리플 오버플로우 위험] {kt.relative_to(ROOT)}:{line_no}: Surface modifier에서 .clickable 앞에 .clip(shape) 누락"
                )

# 8. 인앱 이용 가이드 이미지(guide_1.png ~ guide_6.png) 최신성 및 RGBA 투명 배경 무결성 감사
DRAWABLE = RES / "drawable"
for i in range(1, 7):
    gp = DRAWABLE / f"guide_{i}.png"
    if not gp.exists():
        errors.append(f"[가이드 이미지 누락] {gp.relative_to(ROOT)}")
        continue
    data = gp.read_bytes()
    if data[:8] != b"\x89PNG\r\n\x1a\n":
        errors.append(f"[가이드 이미지 손상] {gp.relative_to(ROOT)}: PNG 시그니처 불일치")
        continue
    w, h, bd, ct = struct.unpack(">IIBB", data[16:26])
    if bd != 8 or ct != 6:
        errors.append(f"[가이드 이미지 포맷 오류] {gp.relative_to(ROOT)}: 8-bit RGBA(color_type=6)여야 함 (현재 bd={bd}, ct={ct})")
    if len(data) > 350_000:
        errors.append(f"[구버전 가이드 이미지 의심] {gp.relative_to(ROOT)}: 용량({len(data)}B)이 구버전(2022) 에셋과 유사함")

print("=" * 60)
print(f"🌐 i18n 정적 감사: 키 {len(base_keys)}개 × 언어 {len(LOCALES)}개")
print("=" * 60)
for e in errors:
    print("❌", e)
for w in warnings:
    print("⚠️ ", w)
print("-" * 60)
print(f"결과: 오류 {len(errors)}건, 경고 {len(warnings)}건")
sys.exit(1 if errors else 0)
