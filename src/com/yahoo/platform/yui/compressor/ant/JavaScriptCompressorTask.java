package com.yahoo.platform.yui.compressor.ant;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

/**
 * JavaScript compression class
 * 
 * Add support for nomunge, preservesemicolons and disableoptimizations attribute.
 * Uses com.yahoo.platform.yui.compressor.JavaScriptCompressor for compression.
 * 
 * @author dinoboff
 * @version 0.1
 *
 */
public class JavaScriptCompressorTask extends CompressorTask {
	
	private boolean noMunge;
	private boolean preserveSemicolons;
	private boolean disableOptimizations;
	private JavaScriptCompressor compressor;

	protected boolean doesMunge() {
		return !noMunge;
	}

	public void setNoMunge(boolean noMunge) {
		this.noMunge = noMunge;
	}

	protected boolean doesPreserveSemicolons() {
		return preserveSemicolons;
	}

	public void setPreserveSemicolons(boolean preserveSemicolons) {
		this.preserveSemicolons = preserveSemicolons;
	}

	protected boolean areOptimizationsDisabled() {
		return disableOptimizations;
	}

	public void setDisableOptimizations(boolean disableOptimizations) {
		this.disableOptimizations = disableOptimizations;
	}

	@Override
	protected void compress(Writer out) throws IOException {
		compressor.compress(out, this.getLineBreak(), this.doesMunge(),
				this.isVerbose(), this.doesPreserveSemicolons(),
				this.areOptimizationsDisabled());
	}

	@Override
	protected void setCompressor(Reader in) throws IOException {
		try {
			this.compressor = new JavaScriptCompressor(in,
					new ParsingErrorReporter());
		} catch (EvaluatorException e) {
			throw new BuildException(
					"There is a problem with the JavaScript source: " + e.toString());
		}
	}
	
	private class ParsingErrorReporter implements ErrorReporter {
		
		private void log(String msg, int line, int lineOffset, int msgLevel) {
			if (line < 0) {
                JavaScriptCompressorTask.this.log(msg, Project.MSG_WARN);
            } else {
            	JavaScriptCompressorTask.this.log(line + ':' + lineOffset + ':' + msg, Project.MSG_WARN);
            }
		}
		
        public void warning(String message, String sourceName,
                int line, String lineSource, int lineOffset) {
            this.log(message, line, lineOffset, Project.MSG_WARN);
        }

        public void error(String message, String sourceName,
                int line, String lineSource, int lineOffset) {
        	this.log(message, line, lineOffset, Project.MSG_ERR);
        }

        public EvaluatorException runtimeError(String message, String sourceName,
                int line, String lineSource, int lineOffset) {
            this.error(message, sourceName, line, lineSource, lineOffset);
            return new EvaluatorException(message);
        }
	}
}
