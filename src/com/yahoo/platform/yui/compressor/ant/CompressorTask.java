/**
 * 
 */
package com.yahoo.platform.yui.compressor.ant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

/**
 * Abstract class for compressor task.
 * 
 * 
 * @author Damien Lebrun
 * @version 0.1
 */
public abstract class CompressorTask extends Task {
	private File srcFile;
	private Vector fileSets = new Vector();
	private File dstFile;
	private File dstDir;
	private String extension;
	private String charset = "UTF-8";
	private int lineBreak = -1;
	private boolean verbose;
	private boolean failonerror = true;

	public void setSrcFile(File srcfile) {
		this.srcFile = srcfile;
	}

	public void addFileset(FileSet aFileSet) {
		this.fileSets.add(aFileSet);
	}

	public void setDstFile(File dstFile) {
		this.dstFile = dstFile;
	}

	public void setDstDir(File dstDir) {
		this.dstDir = dstDir;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	protected String getCharset() {
		return this.charset;
	}

	public void setLineBreak(int lineBreak) {
		this.lineBreak = lineBreak;
	}

	protected int getLineBreak() {
		return lineBreak;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	protected boolean isVerbose() {
		return verbose;
	}

	public boolean isFailonerror() {
		return failonerror;
	}

	public void setFailonerror(boolean failonerror) {
		this.failonerror = failonerror;
	}

	/**
	 * Validate attribute and nested element combination.
	 * 
	 * 
	 * - At least one source should be defined.
	 * 
	 * - The source should either be defined with the srcfile attribute, or
	 * nested fileset elements, but not both.
	 * 
	 * - When using filesets, dstfile connot be set.
	 * 
	 * Throws a BuildException when the validation fails
	 * 
	 */
	protected void validate() {
		if (this.srcFile == null && this.fileSets.size() == 0) {
			throw new BuildException(
					"No source to compress. "
							+ "Set a file to compress with either the srcFile attribute "
							+ "or a fileset element.");
		} else if (this.srcFile != null && this.fileSets.size() > 0) {
			throw new BuildException("Set a file to compress with "
					+ "either the srcFile attribute "
					+ "or a fileset element, but not with both.");
		}

		if (this.srcFile == null && this.dstFile != null) {
			throw new BuildException("You cannot set a destination file "
					+ "when compressing a set of file.");
		}
	}

	public void execute() {
		this.validate();

		if (this.srcFile != null) {
			this.compress(this.srcFile);
		} else {
			for (int i = 0; i < this.fileSets.size(); i++) {
				this.compress((FileSet) this.fileSets.elementAt(i));
			}
		}
	}

	/**
	 * Return destination file for the compression.
	 * 
	 * The destination can either be: 1. The file defined by the dstfile
	 * attribute 2. A file with the same name than the source file in the the
	 * directory defined by the dstdir attribute. 3. A fle with the same path
	 * than the source file, except for the extension, defined by the extension
	 * attribute. 4. Or the same path than the source file
	 * 
	 * @param srcFile
	 *            Path to the source file
	 * @return Path to destination file
	 */
	protected File getDstFile(File srcFile) {
		File dstFile;

		if (this.dstFile != null) {
			// Destination file is explicit
			return this.dstFile;
		}

		if (this.dstDir != null) {
			// make a copy in an other folder
			String srcFileName = srcFile.getName();
			dstFile = new File(this.dstDir, srcFileName);
		} else {
			// Compress in make
			// or make a compressed copy in the same directory
			dstFile = srcFile;
		}

		if (this.extension != null) {
			dstFile = this.swapExtension(dstFile);
		}

		return dstFile;
	}

	/**
	 * Return destination file for the compression.
	 * 
	 * The destination can either be: 1. The file defined by the dstfile
	 * attribute 2. A file with the same relative path than the source file in
	 * the the directory defined by the dstdir attribute. 3. A fle with the same
	 * path than the source file, except for the extension, defined by the
	 * extension attribute. 4. Or the same path than the source file
	 * 
	 * @param baseDir
	 * @param srcFilePath
	 *            Relative path (from baseDir) of the source file.
	 * @return Path to destination file
	 */
	protected File getDstFile(File baseDir, String srcFilePath) {
		File dstFile;

		if (this.dstFile != null) {
			// Destination file is explicit
			return this.dstFile;
		}

		if (this.dstDir != null) {
			// make a copy in an other folder
			dstFile = new File(this.dstDir, srcFilePath);
		} else {
			// Compress in place
			// or make a compressed copy in the same directory
			dstFile = new File(baseDir, srcFilePath);
		}

		if (this.extension != null) {
			dstFile = this.swapExtension(dstFile);
		}

		return dstFile;
	}

	private File swapExtension(File aFile) {
		String name = aFile.getName();
		String newName;

		int extIndex = name.lastIndexOf('.');
		if (extIndex >= 0) {
			newName = name.substring(0, extIndex) + this.extension;
		} else {
			newName = name + this.extension;
		}

		return new File(aFile.getParentFile(), newName);
	}

	private void compress(File srcFile) {
		File dstFile = this.getDstFile(srcFile);
		this.compress(srcFile, dstFile);

	}

	private void compress(FileSet fileSet) {
		DirectoryScanner ds = fileSet.getDirectoryScanner(getProject());
		File base = ds.getBasedir();
		String[] includedFiles = ds.getIncludedFiles();

		for (int i = 0; i < includedFiles.length; i++) {
			File srcFile = new File(base, includedFiles[i]);
			File dstFile = this.getDstFile(base, includedFiles[i]);

			this.compress(srcFile, dstFile);
		}

	}

	protected void compress(File srcFile, File dstFile) {
		Reader in = null;
		Writer out = null;
		String charset = this.getCharset();

		if (dstFile.equals(srcFile)) {
			log("Compress (in place) " + srcFile);
		} else {
			log("Create a compressed version of " + srcFile + " at " + dstFile);
		}

		try {

			try {
				in = new InputStreamReader(new FileInputStream(srcFile),
						charset);
				this.setCompressor(in);
				in.close();

				out = new OutputStreamWriter(new FileOutputStream(dstFile),
						charset);
				this.compress(out);

			} catch (BuildException e) {
				handle(e);
			} catch (UnsupportedEncodingException e) {
				handle(new BuildException("Unsupported encoding: " + charset));
			} catch (FileNotFoundException e) {
				handle(new BuildException("File not found. " + e));
			} catch (IOException e) {
				handle(new BuildException("Could not read or close " + srcFile
						+ " or " + dstFile + ". " + e));
			}

		} finally {

			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
					throw new BuildException("Cannot close " + srcFile);
				}
			}

			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
					throw new BuildException("Cannot close " + dstFile);
				}
			}

		}
	}

	protected void handle(Exception e) {
		if (failonerror) {
			throw (e instanceof BuildException) ? (BuildException) e
					: new BuildException(e);
		}
		log(e, Project.MSG_ERR);
	}

	/**
	 * Set a compressor
	 * 
	 * @param in
	 *            input to compress
	 * @throws IOException
	 */
	abstract protected void setCompressor(Reader in) throws IOException;

	/**
	 * Compress input.
	 * 
	 * @param out
	 *            Writter for compressed output
	 * @throws IOException
	 */
	abstract protected void compress(Writer out) throws IOException;
}
