package eu.over9000.skadi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.MenuItem;
import eu.over9000.skadi.model.Channel;
import eu.over9000.skadi.model.StreamQuality;
import eu.over9000.skadi.remote.StreamQualityRetriever;

public class QualityRetrievalService extends Service<List<MenuItem>> {

	private final Consumer<StreamQuality> consumer;
	private final Channel channel;

	public QualityRetrievalService(final Consumer<StreamQuality> consumer, final Channel channel) {
		this.consumer = consumer;
		this.channel = channel;
	}

	@Override
	protected Task<List<MenuItem>> createTask() {
		return new Task<List<MenuItem>>() {

			@Override
			protected List<MenuItem> call() throws Exception {

				final List<MenuItem> result = new ArrayList<>();
				
				StreamQualityRetriever.getQualities(QualityRetrievalService.this.channel).forEach(quality -> {
					final MenuItem mi = new MenuItem("Stream: " + quality.getQuality());
					mi.setOnAction(event -> QualityRetrievalService.this.consumer.accept(quality));
					result.add(mi);
				});
				
				return result;
			}
		};
	}
}