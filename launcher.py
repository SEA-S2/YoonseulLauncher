"""launcher.py — 게임 직접 실행 + 동글랜드 서버 자동 접속 (v3 Phase 3).

preflight.launch_minecraft()(공식 런처 열기)를 대체한다.

커맨드 조립은 mll 의 get_minecraft_command 에 위임:
  classpath + Fabric KnotClient 메인클래스 + JVM 인자 +
  게임 인자(username/uuid/accessToken/version/assets/gameDir) 를
  버전 JSON 규칙대로 생성. 여기에 우리가 얹는 것:
    - executablePath: preflight 가 찾은 Java 21+ (javaw 우선)
    - quickPlayMultiplayer: "dongleland.com" → 실행 즉시 서버 입장
      (모던 클라이언트가 SRV 자동 해석 → mvp2.dongleland.com:25565)
    - -Xmx 메모리 인자

Java 는 preflight.find_valid_java() 재사용 (HANDOFF §5.4 — 핵심 자산).
"""

import os
import shutil
import subprocess
import sys

import app_meta
import auth
import instance
import preflight

from minecraft_launcher_lib import command as _command


class LaunchError(Exception):
    def __init__(self, message: str, code: str = "launch_failed"):
        super().__init__(message)
        self.message = message
        self.code = code


DEFAULT_MAX_MEM_MB = 4096

def _java_executable() -> str:
    """실행에 쓸 java 경로.

    우선순위:
      1) 인스턴스에 설치된 Mojang 공식 런타임 (버전 JSON 의 javaVersion)
         → Mojang 이 그 버전으로 테스트한 정확한 JVM. 격리돼 있어 시스템 영향 없음.
      2) 시스템 Java (구버전처럼 javaVersion 이 없는 경우의 폴백)

    ⚠️ '더 최신 Java' 를 쓰지 않는다. Java 가 요구보다 높으면 Mixin/ASM 이
       새 클래스 파일을 읽지 못해(Unsupported class file major version) 깨진다.
    Windows 에서는 콘솔창이 뜨지 않도록 javaw 를 우선한다.
    """
    # 1) 인스턴스 런타임
    try:
        import game_installer
        java = game_installer.java_executable()
        if java:
            return java
    except Exception:
        pass

    # 2) 시스템 Java 폴백
    info = preflight.find_valid_java()
    if not info:
        raise LaunchError(
            "게임 실행에 필요한 Java 를 찾을 수 없습니다.\n"
            "게임을 다시 실행하면 자동으로 내려받습니다.",
            code="no_java")
    java = info["path"] if isinstance(info, dict) else info

    if java == "java":
        # PATH 에서 찾은 경우. java.exe 는 있어도 javaw.exe 는 없을 수 있다
        # (일부 JRE, Scoop/Chocolatey 처럼 java 만 PATH 에 노출하는 배포).
        # 존재를 확인하지 않고 "javaw" 를 넘기면 Popen 이 WinError 2 로 죽는다.
        if sys.platform == "win32":
            javaw = shutil.which("javaw")
            if javaw:
                return javaw
            java_exe = shutil.which("java")
            if java_exe:
                return java_exe
            raise LaunchError(
                "게임 실행에 필요한 Java 를 찾을 수 없습니다.\n"
                "게임을 다시 실행하면 자동으로 내려받습니다.",
                code="no_java")
        return "java"
    if sys.platform == "win32" and os.path.basename(java).lower().startswith("java"):
        javaw = os.path.join(os.path.dirname(java), "javaw.exe")
        if os.path.isfile(javaw):
            return javaw
    return java


def _set_fullscreen_option(on: bool):
    """인스턴스 options.txt 의 `fullscreen:` 한 줄만 고친다 (런처 인자로는 전체화면을 못 끈다).

    파일이 없으면 창모드는 이미 바닐라 기본값이라 만들지 않는다. 전체화면일 때만 한 줄짜리로 만든다
    (나머지 옵션은 게임이 기본값으로 채운다).
    """
    path = os.path.join(instance.instance_dir(), "options.txt")
    try:
        with open(path, encoding="utf-8") as f:
            lines = f.read().splitlines()
    except FileNotFoundError:
        if not on:
            return
        lines = []
    line = "fullscreen:" + ("true" if on else "false")
    idx = next((i for i, l in enumerate(lines) if l.startswith("fullscreen:")), None)
    if idx is None:
        lines.append(line)
    elif lines[idx] == line:
        return
    else:
        lines[idx] = line
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        f.write("\n".join(lines) + "\n")


def build_command(account: dict, *, quick_connect: bool = True,
                  max_mem_mb: int = DEFAULT_MAX_MEM_MB,
                  window: dict | None = None) -> list[str]:
    """실행 커맨드 조립 (실행은 안 함 — 테스트 가능하도록 분리)."""
    # Fabric 프로필로 실행한다. 윤슬 기능은 공개 소스로 빌드한 일반 Fabric 모드가 담당한다.
    version_id = instance.installed_version_id()
    if not version_id or not instance.is_version_ready(version_id):
        raise LaunchError("게임이 설치되지 않았습니다.\n먼저 설치를 진행해주세요.",
                          code="not_installed")

    options = {
        "username": account["mc_username"],
        "uuid": account["mc_uuid"],
        "token": account["mc_access_token"],
        "executablePath": _java_executable(),
        "jvmArguments": [f"-Xmx{max_mem_mb}M"],
        "launcherName": "YoonseulLauncher",
        "launcherVersion": app_meta.APP_VERSION,
        "gameDirectory": instance.instance_dir(),
    }
    if quick_connect and app_meta.SERVER_HOST:
        # 도메인만 넘기면 클라이언트가 SRV 해석 (HANDOFF §5.1)
        options["quickPlayMultiplayer"] = app_meta.SERVER_HOST
    if window and window.get("mode") in ("windowed", "fullscreen"):
        # 창 크기(전체화면에서 빠져나올 때의 크기이기도 하다). 전체화면 여부는 options.txt 쪽.
        options["customResolution"] = True
        options["resolutionWidth"] = str(int(window["width"]))
        options["resolutionHeight"] = str(int(window["height"]))

    return _command.get_minecraft_command(
        version_id, instance.instance_dir(), options)


_running_proc = None  # 마지막으로 실행한 게임 프로세스 (실행중 여부 확인용)
_exit_logged = False  # 종료 로그 중복 방지 (프론트가 5초마다 폴링하므로)


def launch(*, quick_connect: bool = True,
           max_mem_mb: int = DEFAULT_MAX_MEM_MB,
           window: dict | None = None) -> dict:
    """로그인 계정 확보(자동 갱신 포함) → 커맨드 조립 → 게임 프로세스 실행.

    반환: {"ok":True, "pid":int, "version_id":str, "username":str}
    실패: LaunchError / auth.AuthError
    """
    global _running_proc, _exit_logged
    account = auth.get_account()  # 만료 시 자동 리프레시, 불가 시 AuthError(relogin)
    cmd = build_command(account, quick_connect=quick_connect,
                        max_mem_mb=max_mem_mb, window=window)
    if window and window.get("mode") in ("windowed", "fullscreen"):
        try:
            _set_fullscreen_option(window["mode"] == "fullscreen")
        except OSError as e:  # 못 고쳐도 실행은 한다 — 창 크기 인자는 이미 들어갔다
            preflight.write_log(f"[실행] options.txt 전체화면 설정 실패: {e}")

    # PyInstaller one-file 런처에서 자식 프로세스가 런처의 임시 Python
    # 환경을 상속하면, 게임을 오래 실행한 뒤 임시 폴더가 정리되는 시점에
    # "Failed to load Python DLL" 창이 나타날 수 있다. Java 게임 프로세스에는
    # 해당 환경이 필요 없으므로 PyInstaller가 권장하는 초기화 값을 넘긴다.
    child_env = os.environ.copy()
    child_env["PYINSTALLER_RESET_ENVIRONMENT"] = "1"
    kwargs: dict = {"cwd": instance.instance_dir(), "env": child_env}
    if sys.platform == "win32":
        # 런처와 분리된 독립 프로세스 + 콘솔창 없이
        kwargs["creationflags"] = (
            subprocess.DETACHED_PROCESS | subprocess.CREATE_NEW_PROCESS_GROUP)

    # WinError 2("지정된 파일을 찾을 수 없습니다")는 실행 파일이 없을 때와
    # cwd 가 없을 때 '똑같이' 난다. Windows 는 어느 쪽인지 알려주지 않으므로
    # 미리 구분해 두어야 사용자도 우리도 원인을 안다.
    exe = cmd[0] if cmd else ""
    if not exe:
        raise LaunchError("실행 커맨드를 만들지 못했습니다.", code="bad_command")

    if os.path.sep in exe or (sys.platform == "win32" and ":" in exe):
        if not os.path.isfile(exe):
            preflight.write_log(f"[실행실패] Java 실행 파일 없음: {exe}")
            raise LaunchError(
                "Java 실행 파일을 찾을 수 없습니다.\n"
                "설정에서 게임 파일을 복구하거나, 게임을 다시 설치해주세요.",
                code="no_java")
    elif not shutil.which(exe):
        preflight.write_log(f"[실행실패] PATH 에 {exe} 없음")
        raise LaunchError(
            "게임 실행에 필요한 Java 를 찾을 수 없습니다.\n"
            "게임을 다시 실행하면 자동으로 내려받습니다.",
            code="no_java")

    if not os.path.isdir(kwargs["cwd"]):
        preflight.write_log(f"[실행실패] 인스턴스 폴더 없음: {kwargs['cwd']}")
        raise LaunchError(
            "게임 폴더를 찾을 수 없습니다.\n설정에서 게임을 다시 설치해주세요.",
            code="no_instance")

    try:
        proc = subprocess.Popen(cmd, **kwargs)
    except FileNotFoundError as e:
        preflight.write_log(f"[실행실패] FileNotFoundError: exe={exe} cwd={kwargs['cwd']} ({e})")
        raise LaunchError(
            "게임 실행 파일을 찾을 수 없습니다.\n설정에서 게임 파일을 복구해주세요.",
            code="no_java")
    except Exception as e:
        preflight.write_log(f"[실행실패] {type(e).__name__}: {e}")
        raise LaunchError(f"게임 프로세스 실행에 실패했습니다:\n{e}")

    _running_proc = proc
    _exit_logged = False
    preflight.write_log(
        f"[실행] java={exe} version={instance.installed_version_id()} "
        f"mem={max_mem_mb}MB pid={proc.pid}")
    return {"ok": True, "pid": proc.pid,
            "version_id": instance.installed_version_id(),
            "username": account["mc_username"]}


def is_game_running() -> bool:
    """마지막으로 실행한 게임 프로세스가 아직 살아있는지."""
    global _running_proc, _exit_logged
    if _running_proc is None:
        return False
    # poll() 이 None 이면 아직 실행 중, 아니면 종료됨(종료코드 반환)
    rc = _running_proc.poll()
    if rc is None:
        return True
    if not _exit_logged:
        _exit_logged = True
        preflight.write_log(f"[종료] exit_code={rc}")
    _running_proc = None
    return False
