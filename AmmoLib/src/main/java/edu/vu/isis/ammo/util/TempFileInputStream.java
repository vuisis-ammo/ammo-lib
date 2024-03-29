/* Copyright (c) 2010-2015 Vanderbilt University
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package edu.vu.isis.ammo.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link FileInputStream} which (optionally) deletes the underlying file when
 * the stream is closed.
 * 
 */
public class TempFileInputStream extends FileInputStream {

	private static final Logger logger = LoggerFactory.getLogger("util.tempfile");

	/**
	 * Underlying file object
	 */
	private File mFile;

	/**
	 * Indicates intention to delete on close.
	 */
	private final boolean mWillDeleteOnClose;

	/**
	 * Was underlying file stream closed.
	 * Indicates closed was called.
	 */
	private boolean mIsClosed;

	/**
	 * Was underlying File object deleted.
	 * Indicates closed was called and file was deleted successfully.
	 */
	private boolean mIsDeleted;

	/**
	 * Creates a fileInputStream wrapped around the given file.
	 * This file will be deleted when close.
	 * 
	 * @param file
	 * 
	 * @throws FileNotFoundException
	 */
	static public TempFileInputStream getInstance(File file) 
	throws FileNotFoundException {
		return new TempFileInputStream(file, true);
	}
	
	static public TempFileInputStream getInstance(File file, final boolean aDeleteOnClose) 
	throws FileNotFoundException {
		return new TempFileInputStream(file, aDeleteOnClose);
	}

	/**
	 * Creates a fileInputStream wrapped around the given file.
	 * 
	 * @param file
	 * @param mWillDeleteOnClose
	 * 
	 * @throws FileNotFoundException
	 */
	private TempFileInputStream(final File aFile, final boolean aDeleteOnClose) 
	throws FileNotFoundException {
		super(aFile);
		this.mFile = aFile;
		this.mWillDeleteOnClose = aDeleteOnClose;
		this.mIsClosed = false;
		this.mIsDeleted = false;
	}

	/**
	 * @return should the file should be deleted on close()?
	 */
	public final boolean willDeleteOnClose() {
		return mWillDeleteOnClose;
	}

	/**
	 * @return has file been deleted?
	 */
	public boolean mIsDeleted() {
		return mIsDeleted;
	}

	/**
	 * Closes the underlying FileInputStream. Normally also deletes the file.
	 * 
	 * @see java.io.FileInputStream#close()
	 */
	@Override
	public void close() {

		if (mIsClosed) {
			logger.debug("already closed: {}", this);
			return;
		}

		logger.debug("closing {}", this);
		mIsClosed = true;

		try {
			super.close();
			if (willDeleteOnClose()) {
				mIsDeleted = mFile.delete();
			}
		} catch (IOException ex) {
			logger.warn("Failed to close() stream: {}", this, ex);
		} catch (RuntimeException ex) {
			logger.warn("Failed to delete(): {}", mFile, ex);
		}

		logger.info("close {}", this);
		mFile = null;
	}

	@Override
	public String toString() {

		String defaultStr = super.toString();

		try {
			StringBuilder sb = new StringBuilder();
			sb.append(defaultStr).append("File: ").append("name[")
					.append(mFile).append("], ").append("size[")
					.append(mFile == null ? 0L : mFile.length()).append("], ")
					.append("delete?[")
					.append(mWillDeleteOnClose ? "yes" : "no").append("], ")
					.append("is closed?[").append(mIsClosed ? "yes" : "no")
					.append("], ").append("is deleted?[")
					.append(mIsDeleted ? "yes" : "no").append("]").append("");
			return sb.toString();
		} catch (RuntimeException ex) {
			logger.info("could not make string", ex);
			return defaultStr;
		}
	}

	// testing
	public static void main(String[] args) throws IOException {
		File file = File.createTempFile("temp_file", ".tmp");
		FileInputStream fis = TempFileInputStream.getInstance(file);

		System.out.println("*** Before close: " + fis);
		System.out.println("Temp file exists: " + file.exists());
		fis.close();
		System.out.println("*** After close: " + fis);
		System.out.println("Temp file exists: " + file.exists());
	}
}