package com.yahoo.platform.yui.compressor.ant;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.apache.tools.ant.BuildException;

import com.yahoo.platform.yui.compressor.CssCompressor;

/**
 * JavaScript compression class
 * 
 * Uses com.yahoo.platform.yui.compressor.CssCompressor for compression.
 * 
 * @author Damien Lebrun
 * @version 0.1
 * 
 */
public class CssCompressorTask extends CompressorTask {

	private CssCompressor compressor = null;

	protected void compress(Writer out) throws IOException {
		if (this.compressor == null) {
			throw new BuildException("Compressor not set.");
		}

		this.compressor.compress(out, this.getLineBreak());
		this.compressor = null;
	}

	protected void setCompressor(Reader in) throws IOException {
		this.compressor = new CssCompressor(in);
	}

}
