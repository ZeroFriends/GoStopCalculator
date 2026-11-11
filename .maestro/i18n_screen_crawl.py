#!/usr/bin/env python3
"""
다국어(i18n) 런타임 화면 크롤러

에뮬레이터에서 앱 언어를 바꿔가며 핵심 화면을 순서대로 이동하고,
각 화면의 View Hierarchy 텍스트를 수집해 아래 문제를 자동 감지한다.

  - 포맷 지정자 미치환 (%d, %1$s ...)
  - null / undefined / NaN / 리터럴 "\\n" 노출
  - (ko 이외 언어) 한글이 그대로 남아 있는 미번역 문구

사용법:
  python3 .maestro/i18n_screen_crawl.py                       # 연결된 기기에 언어 자동 분배, 병렬 실행
  python3 .maestro/i18n_screen_crawl.py --device emulator-5554 --locales en-US
결과: .maestro/reports/i18n_runtime_report.md (문제 발견 시 exit 1)
"""
import argparse
import json
import re
import subprocess
import sys
import tempfile
import threading
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
RES = ROOT / "shared/src/commonMain/composeResources"
REPORT = ROOT / ".maestro/reports/i18n_runtime_report.md"
PKG = "zero.friends.gostopcalculator"
LOCALE_DIRS = {"ko-KR": "values-ko", "en-US": "values-en", "ja-JP": "values-ja"}

HANGUL = re.compile(r"[\uac00-\ud7a3]")
BROKEN = [
    ("포맷 지정자 미치환", re.compile(r"%(\d+\$)?[dsf]")),
    ("null 노출", re.compile(r"\bnull\b")),
    ("undefined 노출", re.compile(r"\bundefined\b")),
    ("NaN 노출", re.compile(r"\bNaN\b")),
    ("리터럴 \\n 노출", re.compile(r"\\n")),
]


def strings_for(locale):
    tree = ET.parse(RES / LOCALE_DIRS[locale] / "strings.xml")
    return {e.get("name"): (e.text or "").replace("\\n", "\n") for e in tree.getroot().findall("string")}


def rx(text):
    """Maestro 텍스트 셀렉터는 정규식이므로 특수문자를 이스케이프한다 (YAML 큰따옴표 내부용 이중 백슬래시)."""
    return re.sub(r"([\\.^$|?*+()\[\]{}])", r"\\\\\1", text)


def build_steps(s):
    """(체크포인트 이름, Maestro 커맨드 YAML) 목록"""
    tap = lambda t: f'- tapOn: "{rx(t)}"'
    point = lambda p: f'- tapOn:\n    point: "{p}"'
    return [
        ("01 홈", f'- launchApp:\n    clearState: false\n    stopApp: true\n- extendedWaitUntil:\n    visible: "{rx(s["start"])}"\n    timeout: 60000'),
        ("02 플레이어 설정", "\n".join([tap(s["start"])] + [tap(s["add_new_player"])] * 3)),
        ("03 게임 규칙", tap(s["next"])),
        ("04 게임 보드(빈 상태)", "\n".join([point("80%,39%"), '- inputText: "100"', "- hideKeyboard", tap(s["complete"])])),
        ("05 라운드 준비", tap(s["start_game"])),
        ("06 점수기록", "\n".join([point("50%,47%"), point("50%,53%"), point("50%,60%"), tap(s["complete"])])),
        ("07 고스톱 설명서", tap(s["manual"])),
        ("08 승자 점수기록", "\n".join(["- back", tap(s["next_step_1"])])),
        ("09 패자 점수기록", "\n".join([point("55%,45%"), '- inputText: "7"', "- hideKeyboard", tap(s["next_step_2"])])),
        ("10 계산 확인 다이얼로그", tap(s["next_step_3"])),
        ("11 라운드 결과(상세)", tap(s["complete"])),
        ("12 게임 보드(1라운드)", "- back"),
        ("13 정산내역", f'- tapOn: "{rx(s["calculate_history_text"])}.*"'),
    ]


def sh(cmd, **kw):
    return subprocess.run(cmd, capture_output=True, text=True, **kw)


def collect_texts(node, out, system=False):
    attrs = node.get("attributes", {})
    rid = attrs.get("resource-id", "")
    system = system or rid.startswith("com.android.systemui")
    if not system:
        for key in ("text", "accessibilityText", "hintText"):
            v = (attrs.get(key) or "").strip()
            if v and v not in out:
                out.append(v)
    for child in node.get("children", []):
        collect_texts(child, out, system)


def hierarchy_texts(device):
    res = sh(["maestro", "--device", device, "hierarchy", "--no-reinstall-driver"])
    raw = res.stdout
    start = raw.find("{")
    if start < 0:
        raise RuntimeError(f"hierarchy 파싱 실패: {res.stderr[-500:]}")
    texts = []
    collect_texts(json.loads(raw[start:]), texts)
    return texts


def ensure_emulator_serial(device):
    if not device.startswith("emulator-"):
        raise ValueError(f"안전 가드: 물리 기기({device})에는 pm clear를 실행할 수 없습니다. emulator-*만 허용됩니다.")


def crawl(device, locale, results):
    ensure_emulator_serial(device)
    s = strings_for(locale)
    sh(["adb", "-s", device, "shell", "pm", "clear", PKG])
    sh(["adb", "-s", device, "shell", f"cmd locale set-app-locales {PKG} --locales {locale}"])
    screens = []
    with tempfile.TemporaryDirectory() as tmp:
        for i, (name, commands) in enumerate(build_steps(s)):
            flow = Path(tmp) / f"step_{i:02d}.yaml"
            flow.write_text(f"appId: {PKG}\n---\n{commands}\n", encoding="utf-8")
            run = sh(["maestro", "--device", device, "test", "--no-reinstall-driver", str(flow)])
            if run.returncode != 0:
                screens.append({"name": name, "error": "화면 이동 실패 (셀렉터 미발견 등)", "texts": [], "issues": []})
                print(f"   ❌ [{locale}] {name}: 이동 실패 ➔ 크롤링 중단")
                break
            texts = hierarchy_texts(device)
            issues = []
            for t in texts:
                for label, pattern in BROKEN:
                    if pattern.search(t):
                        issues.append((label, t))
                if locale != "ko-KR" and HANGUL.search(t):
                    issues.append(("미번역(한글 노출)", t))
            screens.append({"name": name, "texts": texts, "issues": issues})
            print(f"   {'⚠️ ' if issues else '✅'} [{locale}] {name}: 텍스트 {len(texts)}개, 문제 {len(issues)}건")
    sh(["adb", "-s", device, "shell", f"cmd locale set-app-locales {PKG} --locales ko-KR"])
    results[locale] = screens


def write_report(results):
    REPORT.parent.mkdir(parents=True, exist_ok=True)
    lines = ["# 다국어 런타임 화면 크롤링 리포트", ""]
    lines += ["| 언어 | 크롤링 화면 | 문제 화면 | 문제 건수 |", "| :--- | :---: | :---: | :---: |"]
    for loc, screens in results.items():
        bad = [sc for sc in screens if sc["issues"] or sc.get("error")]
        lines.append(f"| `{loc}` | {len(screens)} | {len(bad)} | {sum(len(sc['issues']) for sc in screens)} |")
    for loc, screens in results.items():
        lines += ["", f"## {loc}", ""]
        for sc in screens:
            if sc.get("error"):
                lines.append(f"- ❌ **{sc['name']}**: {sc['error']}")
            elif sc["issues"]:
                lines.append(f"- ⚠️ **{sc['name']}**")
                for label, text in sc["issues"]:
                    lines.append(f"  - {label}: `{text.replace(chr(10), ' ⏎ ')}`")
            else:
                lines.append(f"- ✅ {sc['name']}")
    REPORT.write_text("\n".join(lines) + "\n", encoding="utf-8")


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--device", action="append")
    ap.add_argument("--locales", default="en-US,ja-JP,ko-KR")
    args = ap.parse_args()
    locales = args.locales.split(",")
    discovered = [
        l.split()[0]
        for l in sh(["adb", "devices"]).stdout.splitlines()[1:]
        if l.strip().endswith("device") and l.split()[0].startswith("emulator-")
    ]
    devices = args.device or discovered
    if not devices:
        sys.exit("❌ 실행 중인 Android 에뮬레이터(emulator-*)가 없습니다.")
    invalid = [d for d in devices if not d.startswith("emulator-")]
    if invalid:
        sys.exit(f"❌ 안전 가드: 물리 기기({', '.join(invalid)})에는 pm clear를 실행할 수 없습니다. emulator-*만 허용됩니다.")

    # 기기별로 언어를 나눠 병렬 실행 (기기 1대당 언어는 순차)
    shards = {d: locales[i::len(devices)] for i, d in enumerate(devices)}
    results = {}
    results_lock = threading.Lock()

    def worker(device, locs):
        for loc in locs:
            print(f"▶️  {device} ➔ {loc} 크롤링 시작")
            try:
                crawl(device, loc, results)
            except Exception as exc:
                with results_lock:
                    results[loc] = [{"step": "worker", "issues": [], "error": str(exc)}]

    threads = [threading.Thread(target=worker, args=(d, l)) for d, l in shards.items() if l]
    for t in threads:
        t.start()
    for t in threads:
        t.join()

    # 크롤링 종료 후 에뮬레이터 앱 로케일을 시스템 기본(빈 값)으로 복원해 후속 테스트 간섭 방지
    for d in devices:
        sh(["adb", "-s", d, "shell", f"cmd locale set-app-locales {PKG} --locales ''"])

    ordered = {loc: results[loc] for loc in locales if loc in results}
    write_report(ordered)
    missing = [loc for loc in locales if loc not in ordered]
    total = sum(len(sc["issues"]) + (1 if sc.get("error") else 0) for scs in ordered.values() for sc in scs) + len(missing)
    print(f"\n📄 리포트: {REPORT}\n결과: 문제 {total}건")
    sys.exit(1 if total else 0)


if __name__ == "__main__":
    main()
