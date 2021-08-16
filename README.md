# Mahasiswa Wali Screen Saver

Mahasiswa Wali Screen Saver (MWSS) is a Windows Screen Saver, that when running
shows randomly picked mahasiswa that has you as their guardian. A user name
and password of you is needed to access the students' information

Please note that this program is in the alpha version. It is not user friendly
but more or less developer friendly.

## Installation Guide

This screen saver works best if you install it in UNPAR's computer. Otherwise,
you need to connect to VPN to get access to SIAKAD data.

1. **Install JDK 1.8** if you don't have one. You need exactly this version. The screen saver was not tested in < 1.8, while in > 1.8 JavaFX is no longer bundled, making the setup much more complicated.
2. **Get `MWSS.scr` from release page**
3. **Copy this file into `C:\Windows\System32`**
4. **Create a file named `login-dosen.properties** that contains your username and password. Put this file into `C:\Windows\System32` as well. The format of the file is given below.
5. **Go to Control Panel and select this screen saver**. Please note that when you select this screen saver, it will launch one. Also, when you click "Settings" it will again launch the screen saver instead of settings dialog. This is a known issue, due to the screen saver does not fully handle the protocol of a Windows screen saver.

Example of `login-dosen.properties`

```properties
# replace below with your UNPAR SSO e-mail
username = pascal@unpar.ac.id
# replace below with your UNPAR SSO password
user.password = UNP4R123
```

## Development Guide

You also need the same JDK requirements as running one.

1. Open the project in NetBeans
2. Clean and Build
3. Using [launch4j](http://launch4j.sourceforge.net/), wrap `ScreenSaver-1.0-jar-with-dependencies.jar` into an exe file.
4. Rename that exe file to `MWSS.scr`
