/*
 * Copyright (c) 2014-2016 Jan Strauß <jan[at]over9000.eu>
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
 */

package eu.over9000.skadi.ui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import org.controlsfx.control.StatusBar;

public class StatusBarWrapper {
	private final StatusBar statusBar;

	public StatusBarWrapper() {
		statusBar = new StatusBar();
		statusBar.setBorder(null);
	}

	public StatusBar getStatusBar() {
		return statusBar;
	}

	public void updateStatusText(final String text) {
		if (!statusBar.textProperty().isBound()) {
			statusBar.setText(text);
		}
	}

	public void updateProgress(final double percent) {
		if (!statusBar.progressProperty().isBound()) {
			progressProperty().set(percent);
		}
	}

	public StringProperty textProperty() {
		return statusBar.textProperty();
	}

	public DoubleProperty progressProperty() {
		return statusBar.progressProperty();
	}

	public void bindToService(final Service service) {
		statusBar.textProperty().bind(service.messageProperty());
		statusBar.progressProperty().bind(service.progressProperty());
	}

	public void unbindFromService() {
		statusBar.textProperty().unbind();
		statusBar.progressProperty().unbind();
		updateProgress(0);
	}


}