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
<applet code="edu.fit.cs.sno.applet.SNOApplet.class" archive="sno.jar" width="512" height="496">
    <param name="sno.applet.width" value="512">
    <param name="sno.applet.height" value="496">
</applet>
{% endhighlight %}