/*******************************************************************************
 * The MIT License (MIT)
 * 
 * Copyright (c) 2014-2015 s1mpl3x <jan[at]over9000.eu>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package eu.over9000.skadi.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.over9000.skadi.model.StateContainer;

public final class PersistenceHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceHandler.class);

	private static final String PERSISTENCE_DIRECTORY = System.getProperty("user.home") + File.separator + ".skadi"
			+ File.separator;
	private static final String PERSISTENCE_FILE = "skadi_state.xml";

	private JAXBContext context;
	private Marshaller marshaller;
	private Unmarshaller unmarshaller;
	
	private final Object fileLock = new Object();
	
	public PersistenceHandler() {
		try {
			this.context = JAXBContext.newInstance(StateContainer.class);
			this.marshaller = this.context.createMarshaller();
			this.unmarshaller = this.context.createUnmarshaller();
			this.marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		} catch (final JAXBException e) {
			PersistenceHandler.LOGGER.error("exception construction persistence handler", e);
		}
	}

	public StateContainer loadState() {
		StateContainer result = null;
		
		try {
			if (Files.exists(this.getStateFilePath())) {
				result = this.readFromFile();
			} else {
				this.checkDir();
				result = StateContainer.fromDefault();
				this.writeToFile(result);
			}
		} catch (IOException | JAXBException e) {
			PersistenceHandler.LOGGER.error("exception loading state", e);
		}
		return result;
	}
	
	public void saveState(final StateContainer state) {
		try {
			this.checkDir();
			this.writeToFile(state);
		} catch (IOException | JAXBException e) {
			PersistenceHandler.LOGGER.error("exception saving state", e);
		}
	}

	private Path getStateFilePath() {
		return Paths.get(PersistenceHandler.PERSISTENCE_DIRECTORY, PersistenceHandler.PERSISTENCE_FILE);
	}

	private void writeToFile(final StateContainer state) throws IOException, JAXBException {
		final Path stateFile = this.getStateFilePath();
		synchronized (this.fileLock) {
			final OutputStream stream = Files.newOutputStream(stateFile, StandardOpenOption.CREATE);
			this.marshaller.marshal(state, stream);
			stream.close();
		}
		PersistenceHandler.LOGGER.debug("wrote state to file");
	}
	
	private StateContainer readFromFile() throws IOException, JAXBException {
		final Path stateFile = this.getStateFilePath();
		StateContainer state;
		synchronized (this.fileLock) {
			final InputStream stream = Files.newInputStream(stateFile);
			state = (StateContainer) this.unmarshaller.unmarshal(stream);
			stream.close();
		}
		PersistenceHandler.LOGGER.debug("load state from file");
		return state;
	}
	
	private void checkDir() throws IOException {
		Files.createDirectories(Paths.get(PersistenceHandler.PERSISTENCE_DIRECTORY));
	}
}
