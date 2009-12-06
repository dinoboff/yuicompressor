package com.yahoo.platform.yui.compressor.ant;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.apache.tools.ant.BuildException;

import com.yahoo.platform.yui.compressor.CssCompressor;

public class CssCompressorTask extends CompressorTask {

	private CssCompressor compressor = null;

	protected void compress(Writer out) throws IOException {
		if (this.compressor != null) {
			this.compressor.compress(out, this.getLineBreak());
		} else {
			throw new BuildException("Compressor not set.");
		}
		this.compressor = null;
	}

	protected void setCompressor(Reader in) throws IOException {
		this.compressor = new CssCompressor(in);
	}

}
