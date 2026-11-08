# Carlinkit AutoKit-TBox (X7): Claude Code on the box itself

<div dir="rtl">

جهاز Carlinkit (AutoKit-TBox، الاسم X7) أندرويد يوصَل بمنفذ CarPlay في السيارة. تطبيق Claude ما يسجّل دخول عليه
لأن الـ Bootloader مفتوح من المصنع وGoogle Play ما يعتمده. هذا شي ما نغيّره.
بدال ذلك نشغّل **Claude Code داخل الجهاز** (في Termux)، ويتحكم في الجهاز عبر ADB على نفسه، مثل ما سوّينا مع التلفزيونات.

## على الجهاز (مرة وحدة)

1. **Termux:** حمّله من F-Droid أو من صفحة Termux على GitHub (Releases). لا تأخذه من Google Play، نسخته قديمة.
2. **خيارات المطوّر:** الإعدادات ← About ← اضغط **Build number** ٧ مرات ← ارجع ← Developer options ←
   شغّل **USB debugging** و **Wireless debugging**. لازم الجهاز يكون على واي فاي (هوتسبوت الجوال يكفي).
3. **في Termux** اكتب:
   ```
   pkg update -y && pkg upgrade -y
   pkg install -y nodejs git android-tools
   npm install -g @anthropic-ai/claude-code
   termux-setup-storage
   ```
4. **اربط ADB بالجهاز نفسه:** افتح Wireless debugging ← **Pair device with pairing code**.
   خلّ Termux والإعدادات جنب بعض (تقسيم الشاشة أو نافذة عائمة) واكتب:
   ```
   adb pair localhost:<منفذ الاقتران> <الكود>
   adb connect localhost:<المنفذ اللي في أعلى صفحة Wireless debugging>
   adb devices
   ```
   لازم يطلع جهاز واحد مكتوب جنبه `device`.
5. **شغّل Claude:**
   ```
   git clone https://github.com/Professor-uae/MYTV ~/MYTV && cd ~/MYTV
   claude
   ```
   سجّل الدخول بحساب claude.ai (اشتراك Pro) من المتصفح، أو بمفتاح API من Claude Console.
6. الصق له هذا السطر:

   > اقرأ `docs/CARLINKIT-SETUP.md` من القسم «For Claude Code» واتبعه. أنت تشتغل داخل جهاز Carlinkit نفسه.

## بديل: من تلفون أندرويد

إذا الكتابة على شاشة السيارة صعبة، شغّل Claude من تلفونك بدل الجهاز:

1. شغّل **الهوتسبوت** في التلفون، وخلّ جهاز Carlinkit يتصل فيه.
2. على **الجهاز**: شغّل Wireless debugging (الخطوة ٢ فوق) وشوف الـ IP والمنفذ في أعلى الصفحة.
3. على **التلفون**: نزّل Termux وسوّ الخطوة ٣ فوق (nodejs, git, android-tools, Claude Code).
4. اربط من التلفون بنفس الخطوة ٤ بس بـ IP الجهاز بدل `localhost`:
   ```
   adb pair <IP الجهاز>:<منفذ الاقتران> <الكود>
   adb connect <IP الجهاز>:<المنفذ>
   adb devices
   ```
5. شغّل Claude (الخطوة ٥) والصق له:

   > اقرأ `docs/CARLINKIT-SETUP.md` من القسم «For Claude Code» واتبعه. أنت تشتغل من تلفوني، والجهاز على `<IP>:<المنفذ>` بدل `localhost`.

كيبل التلفون اللي في الجهاز (لـ CarPlay / Android Auto) ما ينفع لـ adb، لازم الاتصال يكون عن طريق الواي فاي.

</div>

---

## For Claude Code

You run inside Termux **on the Carlinkit box itself** (Android, device name X7, about page «AutoKit-TBox», SIM from du).
You reach the system through `adb` connected to `localhost` (or `<box IP>:<port>` if I said you run on my phone; then use that serial everywhere below instead of `localhost`). Talk to me in Arabic; keep commands in English.
The box sits in a moving car: keep every step short, and never ask me to do something long on the screen while driving.

### Hard rules

- **Never** lock or unlock the bootloader, flash anything, root the box, or install tools that hide root or fake
  Google Play certification (Magisk, Play Integrity fixes, spoofed fingerprints). They can brick the box, and getting
  around Google's checks is not something we do. The Claude app staying signed out is accepted; the browser and
  Claude Code work.
- **Never** disable or uninstall anything that runs CarPlay / Android Auto / the car link: any package with
  `carlinkit`, `autokit`, `carplay`, `zlink`, `carbit`, `android.auto`, `projection`, `usb`, `bluetooth`, `launcher`,
  `systemui`, `settings`, `inputmethod`, `phone`, `telephony`, `wifi`, `gms`, `gsf`, `vending` in its name, and
  nothing whose purpose you can't tell. When in doubt, leave it.
- Only `pm disable-user --user 0` (reversible with `pm enable`), never `pm uninstall` on system apps.
- Before any change, save a backup (step 1). Every change you make must be listed in `~/carlinkit-changes.txt`.

### 0. Check the connection

```
adb devices
adb -s localhost:<port> shell getprop ro.product.model
```
If no device shows, walk me through step 4 above again (pairing ports change each time Wireless debugging restarts).
Use one serial for every command: `export ANDROID_SERIAL=localhost:<port>`.

### 1. Look before touching (read only)

Save, under `~/carlinkit-backup/<date>/`:
- `getprop` → `props.txt` (model, Android version, build, `ro.boot.verifiedbootstate`, `ro.boot.flash.locked`);
- `pm list packages -f` and `pm list packages -d` → `packages.txt`, `disabled-before.txt`;
- `cmd package resolve-activity -a android.intent.action.MAIN -c android.intent.category.HOME` → `home.txt`;
- `settings list global`, `settings list secure`, `settings list system` → three files;
- `dumpsys meminfo | head -40` and `df -h /data` → `health.txt`.

Then tell me in Arabic, in a few lines: Android version, free storage and RAM, the current home screen app, how many
apps are installed, and the heaviest ones that are safe to turn off (with what each one is).

### 2. Ask me what I want

One message, a short list, each option one line:
1. اختصار claude.ai على الشاشة الرئيسية (يفتح مثل التطبيق)
2. تسريع: إطفاء التطبيقات الزائدة (بعد ما أعرض لك القائمة)
3. تقليل الحركات (animations) لتسريع الإحساس
4. واجهة عربية خاصة (لاحقاً، مبنية على UrTV)
5. شي ثاني

Wait for my answer. Do only what I pick.

### 3. The options

**claude.ai shortcut.** A shortcut can't be created by a shell command on a non-rooted box. Walk me through it:
Chrome → claude.ai → sign in → menu (⋮) → **Add to Home screen** (or **Install app**). If Chrome is missing, install it
from Google Play.

**Speed up.** Show me the candidate list first (package, what it is, RAM it uses), then disable only what I approve:
```
pm disable-user --user 0 <package>
```
Append each one to `~/carlinkit-changes.txt`. After that, reboot only if I say the car is parked.

**Fewer animations** (reversible, I can set them back in Developer options):
```
settings put global window_animation_scale 0.5
settings put global transition_animation_scale 0.5
settings put global animator_duration_scale 0.5
```

**Arabic home screen.** Not ready for this box yet: the UrTV home in this repo is built for Android TV (remote,
leanback launcher). Write down what I want for the car (screen size, touch, what tiles) in
`~/carlinkit-wishes.txt` and tell me it will come in a later version.

### 4. Undo

Everything back as it was:
```
while read p; do pm enable "$p"; done < ~/carlinkit-changes.txt
settings put global window_animation_scale 1
settings put global transition_animation_scale 1
settings put global animator_duration_scale 1
```

### 5. Finish

Tell me in Arabic, in a few lines: what changed, what you left alone and why, and the undo command above.
Remind me that the Claude **app** still won't sign in on this box, and that claude.ai in Chrome is the way.
