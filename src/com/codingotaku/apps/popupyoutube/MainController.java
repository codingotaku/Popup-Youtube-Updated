package com.codingotaku.apps.popupyoutube;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class MainController<T> {
	@FXML private WebView browser;
	@FXML private VBox root;
	@FXML private HBox searchBar;
	// Title bar
	@FXML private Label resize;
	@FXML private Label pin;
	@FXML private HBox title;
	@FXML private HBox titleBar;
	@FXML private TextField search;

	private WebEngine webEngine;
	private Stage stage;
	private Delta dragDelta = new Delta();
	private double dx;
	private double dy;

	private final String youtube = "https://www.youtube-nocookie.com";
	private final String regExp = "^.*(youtu.be\\/|v\\/|u\\/\\w\\/|embed\\/|playlist\\?|\\/watch\\?v=|\\&v=|\\?v=)([^#\\&\\?]*)(.*)"; // untested
																																		// regex

	@FXML
	private void initialize() {
		webEngine = browser.getEngine();
		webEngine.setUserStyleSheetLocation(getClass().getResource("web.css").toString());
		browser.setOnMousePressed(event -> {
			if (stage == null) stage = (Stage) title.getScene().getWindow();
			dx = stage.getWidth() - event.getX();
			dy = stage.getHeight() - event.getY();
		});

		browser.setOnMouseDragged(event -> {
			if (!stage.isMaximized()) {
				if (event.getX() + dx >= stage.getMinWidth()) {
					stage.setWidth(event.getX() + dx);
				}
				if (event.getY() + dy >= stage.getMinHeight()) {
					stage.setHeight(event.getY() + dy);
				}
			}
		});
		searchBar.setOnMouseEntered(e -> search.setVisible(true));

		searchBar.setOnMouseExited(e -> {
			if (webEngine.getTitle() != null) {
				search.setVisible(false);
			}
		});
		search.setOnKeyPressed(this::search);
	}

	@FXML
	private void resize() {
		if (stage == null) stage = (Stage) title.getScene().getWindow(); // an ugly way of initializing stage
		if (stage.isMaximized()) {
			stage.setMaximized(false);
			resize.setText("⬜");
		} else {
			stage.setY(0);
			stage.setMaximized(true);
			resize.setText("⧉");
		}
		browser.requestFocus();
	}

	@FXML
	private void titleSelected(MouseEvent event) {
		if (stage == null) stage = (Stage) title.getScene().getWindow();
		dragDelta.x = event.getScreenX() - stage.getX();
		dragDelta.y = event.getScreenY() - stage.getY();
	}

	@FXML
	private void titleDragged(MouseEvent event) {
		if (stage.isMaximized()) {
			double pw = stage.getWidth();
			resize();
			double nw = stage.getWidth();
			dragDelta.x /= (pw / nw);
		}
		stage.setX(event.getScreenX() - dragDelta.x);
		stage.setY(event.getScreenY() - dragDelta.y);
	}

	@FXML
	private void titleReleased(MouseEvent event) {
		if (event.getScreenY() == 0 && !stage.isMaximized()) {
			resize();
		}
	}

	@FXML
	private void minimize() {
		if (stage == null) stage = (Stage) title.getScene().getWindow();
		stage.setIconified(true);
		browser.requestFocus();

	}

	@FXML
	private void close() {
		if (stage == null) stage = (Stage) title.getScene().getWindow();
		stage.close();
		System.exit(0);// I shouldn't do this but for now I'll force close the app.
	}

	@FXML
	private void pin() {
		if (stage == null) stage = (Stage) title.getScene().getWindow();
		if (stage.isAlwaysOnTop()) {
			pin.setText("Pin");
			stage.setAlwaysOnTop(false);
		} else {
			pin.setText("Unpin");
			stage.setAlwaysOnTop(true);
		}
		browser.requestFocus();
	}

	ChangeListener<Boolean> listener = (observable, oldValue, newVal) -> {
		if (newVal) {
			root.getChildren().remove(titleBar);
		} else {
			root.getChildren().add(0,titleBar);
		}
	};

	@FXML
	private void fullScreen() {
		if (stage == null) stage = (Stage) title.getScene().getWindow();
		if (!stage.isFullScreen()) {
			stage.fullScreenProperty().removeListener(listener);
			stage.fullScreenProperty().addListener(listener);
			stage.setFullScreen(true);
		}
	}

	private void search(KeyEvent e) {
		if (e.getCode() == KeyCode.ENTER) {
			String url = search.getText();
			Matcher matcher;
			Pattern pattern = Pattern.compile(regExp);
			matcher = pattern.matcher(url);

			if (matcher.find()) {
				String src = String.format("%s/embed/%s", youtube, matcher.group(2));
				if (matcher.group(2).length() == 11 && matcher.groupCount() > 2) {
					src = String.format("%s/embed/videoseries?v=%s&%s", youtube, matcher.group(2), matcher.group(3));
				} else if (matcher.group(2).startsWith("list")) {
					src = String.format("%s/embed/videoseries?%s", youtube, matcher.group(2));
				}

				// No related video on ending screen, autoplay videos, and remove annotations.
				src += "&rel=0&autoplay=1&iv_load_policy=3&fs=0&modestbranding=1";
				webEngine.load(src);
				webEngine.setUserStyleSheetLocation(getClass().getResource("web.css").toString());
				search.setVisible(false);
			} else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Popup YouTube");
				alert.setContentText("Please provide a video/playlist URL");
				alert.showAndWait();
			}
		}
	}

	private class Delta {
		private double x;
		private double y;
	}
}
