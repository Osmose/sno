---
layout: default
title: Embedding SNO
---
<div class="icon"><img src="images/brick.png" /></div>
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
</applet>
{% endhighlight %}