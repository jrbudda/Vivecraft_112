[Minecrift 1.7.10 R2b Vive]

- Forked repo
- Integrated JOpenVR
- Added tracked controller aiming and UI
- Added teleport movement scheme


[Minecrift 1.7.10 R2b]

Bug fixes
---------
- Ensure correct version of launcherwrapper is used by Forge versions. Prevents crash at startup
  when some mods are loaded.
  

[Minecrift 1.7.10 R2a]

- Supports Forge 1.7.10 #1614
- Updated to Optifine 1.7.10 HD U D1
- Now allows user to select mirror mode, single or dual viewport, 1/3 or full framerate. See 
  the VRSettings->Stereo Rendering option dialog.
  
Bug fixes
---------
- On a failure to initialise Oculus SDK, will switch to 'mono' mode. However will now attempt
  to use the Oculus SDK again after Minecrift is restarted (if the mode is not changed manually).
- Made the Forge in-game crosshair more visible in most scenarios.
- Ensure the installer installs the VS2012 redists. This should prevent issues loading the 
  JRiftLibrary.
  

[Minecrift 1.7.10 R2]

- Oculus SDK - now support SDK 0.8 on Windows ***KUDOS to darkTemp for all his work on this!!***

  NOTE: Currently the number of rendering configuration options for the Rift has been vastly
        reduced - for now. Positional track timewarp has been implemented but does not seem to
	    be working with the current SDK. Also, world scale is not working with the current SDK.
  NOTE: R2 does not support Linux or OSX because of these changes. Use 1.7.10 R1c for now on
        these platforms. 
	
- Controller map defaults have been added. These should match the Xbox controller defaults (layout 
  one). Controller mappings will now be saved to the profile. 
- Press RCtrl-t in game to test timewarp.
- Added the ability to use a key to trigger a comfort mode yaw transition, instead of moving the 
  mouse cursor to the edge of the view.
- Set better defaults on clean install.

Bug fixes
---------

- Fixed Optifine / vanilla Minecraft settings being reset to defaults on start-up.
- Added better error reporting on failure to initialise Oculus SDK. Will switch to Mono mode if an error occurs.
  NOTE: To switch back to Oculus rendering if the issue has been rectified, go to 'Options->Vr Settings->Stereo 
        Rendering' and click Mode until you get to 'Oculus Rift'.
- Better defaults on fresh install.
- Fixed a framebuffer memory leak that occurred whenever the rendering configuration was changed.
        
         
[Minecrift 1.7.10 R1c]
 
Bug fixes
-------------
 
 - Forge - Fix right click block interactions
 - Forge - Fix getBreakSpeed issue
 - Vanilla - Fix right click block crash
 
 [Minecrift 1.7.10 R1b]
 
Bug fixes
-------------
 
- Forge - Fix further extendedProperties issues
- Forge - Fix no tooltips on inventory items
- Installer - Supports Java 6 again

[Minecrift 1.7.10 R1a]

Bug fixes
-------------

- Forge - Fix #151 
- Forge - Fix no player held items visible
- Forge - Fix player held event issue


[Minecrift 1.7.10 R1]

New
------

- Added support for the Oculus 0.5.0.1 SDK (tested on Win 8.1, OSX 10.10, Ubuntu 14.10). 
- [With thanks to Zach Jaggi for work towards the linux port, and Jherico for his Oculus SDK repo]
- Now use Oculus best practice for sensor polling to reduce judder in some scenarios
- Added initial *experimental* support for Forge 1.7.10 10.13.4.1448. This is a WIP.
- Ported to Optifine 1.7.10 HD U B7
- Added settings profiles in-game. Different settings configurations may be created, duplicated or deleted. 
  You can switch between profiles in-game via the VR options GUI (VR Options, profile button)
- Added support for optionally adjusting player movement inertia. 
- Added support for optionally allowing player pitch input to affect up/down direction while flying.
- Streamlined the installer. 
- Can (optionally) add / update Minecrift launcher profiles. 
- Downloads Windows redists automatically if necessary.
- Added support for FOV changes in mono mode


Bug fixes
-------------

- Fixed crosshair pitch issues with arrows [with thanks to Zach Jaggi for the fix]
- FSAA now working correctly in mono mode
- Positional tracking now *generally* works in mono mode, some issues remain
- JMumbleLib rebuilt for Linux in an attempt to avoid librt issues on startup


Known Issues
--------------------

- Hydra *still* not working, disabled in installer for now
- Some rendering issues - some fancy water / lightmap effects seem to not take account of player head 
  orientation / position
- The Forge build will most likely not play nicely with other Forge coremods
- Controller button map is not yet stored in the settings profile
- No default button map for a controller on first install


Roadmap
-------------

General
- Fix known issues
- Add support for Oculus SDK 0.6 on Windows (will continue to support 0.5 on OSX and Linux)
- Add initial support for SteamVR / Vive (if a Vive dev kit is forthcoming!)
- Add support for selected Forge coremods - kayronix shaders mod, FTB?
- Add rudimentary IK to player avatar animation so that body follows head position (within reason!)
- Add debug console as secondary UI screen element
- Investigate OSVR support
- Investigate room-space movement
- Investigate pos-track of arm/hand position with tracked controllers
- Fix crosshair weirdness at extreme angles (due to current Euler implementation)

warning: [options] bootstrap class path not set in conjunction with -source 1.61.8
- Port 1.7.10 features / fixes to Minecrift 1.8.1
- Port to 1.8.7 when MCP release allows
- Port to Forge 1.8.x