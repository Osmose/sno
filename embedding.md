---
layout: default
title: Embedding SNO
---
<div class="icon"><img src="images/icons/32/brick.png" /></div>
# Embedding SNO

SNO is a Java applet, and needs to be embedded in a web page to run (barring things like [AppletViewer](http://en.wikipedia.org/wiki/AppletViewer)). This page explains how you can embed SNO into a web page and what configuration options are available.

## The Basics

There are a few things you need to do to get SNO up and running on your website:

1. [Download SNO](getting_sno.html)
2. Upload SNO to your webserver into a publicly-accessible directory

Once `sno.jar` is on your web server, you simply embed it like any other Java applet:

{% highlight html %}
<applet code="edu.fit.cs.sno.applet.SNOApplet.class" 
        archive="sno.jar" width="512" height="496">
    <param name="sno.applet.width" value="512">
    <param name="sno.applet.height" value="496">
</applet>
{% endhighlight %}

## Customize

There's several different ways to customize your embedded instance of SNO.

### Dimensions

The width of the applet should be a multiple of 256, and the height a multiple of 248. The example above uses 512 by 496, which will double the size of the game being displayed. 

Changing the dimensions involves changing the `width` and `height` attributes of the applet tag, as well as the `sno.applet.width` and `sno.applet.height` parameters. See the code excerpt above for an example.

### Loading ROM / Save from Server

You can specify a ROM file and save file for SNO to automatically load on startup. SNO will download the file and begin executing it without any interaction from the user.

Due to security restrictions, SNO can only load a ROM file or a save file from the same server that is hosting the `sno.jar` file. If you try to point to a ROM or save file on a different server, the program will fail to load it.

To specify a file to download, use the `sno.rom.file` and `sno.save.file` parameters as shown below.

### Parameters

SNO draws most configuration options from applet parameters. For example, the following embeds SNO and sets the parameter `sno.ppu.autoFrameSkip` to `true`:

{% highlight html %}
<applet code="edu.fit.cs.sno.applet.SNOApplet.class" 
        archive="sno.jar" width="512" height="496">
    <param name="sno.ppu.autoFrameSkip" value="true">
</applet>
{% endhighlight %}

The following code lists the main parameters that can be altered:

{% highlight html %}
<applet code="edu.fit.cs.sno.applet.SNOApplet.class" 
        archive="sno.jar" width="512" height="496">
    
    <!-- Width and height of applet -->
    <param name="sno.applet.width" value="512">
    <param name="sno.applet.height" value="496">
    
    <!-- Enables auto frame skip. "true" or "false" -->
    <param name="sno.ppu.autoFrameSkip" value="true">
    
    <!-- Number of frames to skip during frameskip -->
    <param name="sno.ppu.framesToSkip" value="5">
    
    <!-- Enables / disbales limit speed option -->
    <param name="sno.cpu.limitSpeed" value="true">
    
    <!-- Points to ROM file for SNO to download and run -->
    <param name="sno.rom.url" value="/path/to/rom/file.smc">
    
    <!-- Points to a save file for SNO to download and use -->
    <param name="sno.save.file" value="/path/to/save.srm">
    
    <!-- Sets the default controls. The values are Java
         virtual key codes. -->
    <param name="sno.input.emulator.pause" value="112">
    <param name="sno.input.emulator.resetAudio" value="121">
    <param name="sno.input.emulator.frameskip" value="192">

    <param name="sno.input.player1.up" value="38">
    <param name="sno.input.player1.down" value="40">
    <param name="sno.input.player1.left" value="37">
    <param name="sno.input.player1.right" value="39">
    <param name="sno.input.player1.a" value="68">
    <param name="sno.input.player1.b" value="70">
    <param name="sno.input.player1.x" value="65">
    <param name="sno.input.player1.y" value="83">
    <param name="sno.input.player1.l" value="87">
    <param name="sno.input.player1.r" value="69">
    <param name="sno.input.player1.select" value="16">
    <param name="sno.input.player1.start" value="10">
</applet>
{% endhighlight %}