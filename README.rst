
This fork adds Ant tasks to compress css and JavaScript files.
Using these tasks is easier and faster than using the <java> task equivalent 
and spawning a new process for each file to process.


Usage:
======

You first need to define the new task inside a target element or directly
in the project element of your Ant build script with::

	<taskdef
		resource="com/yahoo/platform/yui/compressor/ant/antlib.xml"
		classpath="/path/to/yuicompressor.jar"/>


Or::

	<taskdef
		name="jscompressor" 
	    classname="com.yahoo.platform.yui.compressor.JavaScriptCompressorTask" 
	    classpath="/path/to/yuicompressor.jar"/>
	<taskdef
		name="csscompressor" 
	    classname="com.yahoo.platform.yui.compressor.CssCompressorTask" 
	    classpath="/path/to/yuicompressor.jar"/>


The tasks are then available with all the command line options and
the same default (the following shows their default values)::

	<jscompressor srcfile="script.js" dstfile="script.min.js"
		linebreak="-1" charset="UTF-8"
		nomunge="no" preservesemicolons="no" disableoptimizations="no"/>

	<csscompressor srcfile="style.css" dstfile="style.min.js"
		linebreak="-1" charset="UTF-8"/>


You can use these tasks to compress file in place::

	<!-- build/js/script.js content is now compress.
		 Make sure to keep a copy of the source! -->
	<jscompressor srcfile="build/js/script.js"/>
	
	<!-- all files in build/ and its subfolders are compress.
		 Make sure to keep a copy of the source! -->
	<jscompressor>
	    <fileset dir="build/">
   			<include name="**/*.js"/>
   		</fileset>
   	</jscompressor>


To creating a compressed copies with a different extension::

	<!-- Create a compress copy of build/js/script.js
		 in build/js/script.min.js. -->
	<jscompressor srcfile="build/js/script.js" extension="min.js"/>
	
	<!-- The same but for all the js files in build/
	 	 and its subfolder. -->
	<jscompressor extension="min.js">
	    <fileset dir="build/">
			<include name="**/*.js"/>
		</fileset>
	</jscompressor>


Or to creating compressed copies in a different directory::

	<!-- Create a compress copy of src/js/script.js
		 in build/js/script.js. -->
	<jscompressor srcfile="src/js/script.js" dstdir="build/js/"/>
	
	<!-- Create a compress copy from all the js files in src/ 
		and its subfolder, to the build/ folder. -->
	<jscompressor dstdir="build/">
	    <fileset dir="src/">
			<include name="**/*.js"/>
		</fileset>
	</jscompressor>


To do:
======

- Add support for file list and concatenation.