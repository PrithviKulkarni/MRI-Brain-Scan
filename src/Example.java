import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.*;

// OK this is not best practice - maybe you'd like to create
// a volume data class?
// I won't give extra marks for that though.

public class Example extends Application {
	// store the 3D volume data set
	short cthead[][][]; 
	// min/max value in the 3D volume data set
	short min, max; 
	static final int IMAGE_WIDTH = 256;
	static final int IMAGE_HEIGHT = 256;
	static final int IMAGE_SLICES = 113;
	static final int WINDOW_WIDTH = 800;
	static final int WINDOW_HEIGHT = 999;
	static int SCALE_FACTOR = 1;

	//STATIC TO KEEP IMAGE FROM RESIZING TO DEFAULT
	WritableImage medical_imageX = new WritableImage(IMAGE_WIDTH*SCALE_FACTOR, IMAGE_HEIGHT*SCALE_FACTOR);
	WritableImage medical_imageY = new WritableImage(IMAGE_WIDTH*SCALE_FACTOR, IMAGE_HEIGHT*SCALE_FACTOR);
	WritableImage medical_imageZ = new WritableImage(IMAGE_WIDTH*SCALE_FACTOR, IMAGE_HEIGHT*SCALE_FACTOR);
	
	ImageView imageViewX = new ImageView(medical_imageX);
	ImageView imageViewY = new ImageView(medical_imageY);
	ImageView imageViewZ = new ImageView(medical_imageZ);
	
	
	@Override
	public void start(Stage stage) throws FileNotFoundException, IOException {
		
		WritableImage medical_imageThumbNailX = new WritableImage(IMAGE_WIDTH, IMAGE_HEIGHT);
		WritableImage medical_imageThumbNailY = new WritableImage(IMAGE_WIDTH, IMAGE_HEIGHT);
		WritableImage medical_imageThumbNailZ = new WritableImage(IMAGE_WIDTH, IMAGE_HEIGHT);

		WritableImage medical_imageMIPX = new WritableImage(IMAGE_WIDTH, IMAGE_HEIGHT);
		WritableImage medical_imageMIPY = new WritableImage(IMAGE_WIDTH, IMAGE_HEIGHT);
		WritableImage medical_imageMIPZ = new WritableImage(IMAGE_WIDTH, IMAGE_HEIGHT);

		ImageView thumbnailX = new ImageView(medical_imageThumbNailX);
		ImageView thumbnailY = new ImageView(medical_imageThumbNailY);
		ImageView thumbnailZ = new ImageView(medical_imageThumbNailZ);

		ImageView imageViewMIPX = new ImageView(medical_imageMIPX);
		ImageView imageViewMIPY = new ImageView(medical_imageMIPY);
		ImageView imageViewMIPZ = new ImageView(medical_imageMIPZ);

		Button MIPX = new Button("MIP");
		Button MIPY = new Button("MIP");
		Button MIPZ = new Button("MIP");

		Label forSliderX = new Label("Left to right");
		Label forSliderY = new Label("   Front to back");
		Label forSliderZ = new Label("Top to bottom");

		Label ResizeSliderX = new Label("Resize");
		Label ResizeSliderY = new Label("Resize");
		Label ResizeSliderZ = new Label("Resize");

		Slider sliderX = new Slider(0, 255, 0);
		Slider sliderY = new Slider(0, 255, 0);
		Slider sliderZ = new Slider(0, 112, 0);
		
		Slider sliderImageX = new Slider(0, 112, 0);
		Slider sliderImageY = new Slider(0, 112, 0);
		Slider sliderImageZ = new Slider(0, 112, 0);

		VBox centreBox = new VBox();
		HBox imageSliderLabels = new HBox();
		HBox resizeSliderLabels = new HBox();
		HBox sliderImageBox = new HBox();
		HBox sliderResizeBox = new HBox();
		HBox thumbnailBox = new HBox();
		HBox imageBox = new HBox();
		HBox MIPBox = new HBox();
		HBox buttonBox = new HBox();
		BorderPane root = new BorderPane();

		stage.setTitle("CThead Viewer");

		ReadData();

		sliderX.setPadding(new Insets(0, 10, 0, 10));
		sliderY.setPadding(new Insets(0, 10, 0, 10));
		sliderZ.setPadding(new Insets(0, 10, 0, 10));

		sliderImageX.setPadding(new Insets(0, 10, 0, 10));
		sliderImageY.setPadding(new Insets(0, 10, 0, 10));
		sliderImageZ.setPadding(new Insets(0, 10, 0, 10));

		imageSliderLabels.getChildren().addAll(forSliderX, forSliderY, forSliderZ);
		imageSliderLabels.setAlignment(Pos.CENTER);
		imageSliderLabels.setPadding(new Insets(10, 40, 10, 40));
		imageSliderLabels.setSpacing(165);

		resizeSliderLabels.getChildren().addAll(ResizeSliderX, ResizeSliderY, ResizeSliderZ);
		resizeSliderLabels.setAlignment(Pos.CENTER);
		resizeSliderLabels.setPadding(new Insets(10, 40, 10, 40));
		resizeSliderLabels.setSpacing(205);

		sliderImageBox.getChildren().addAll(sliderX, sliderY, sliderZ);
		sliderImageBox.setAlignment(Pos.CENTER);
		sliderImageBox.setPadding(new Insets(10, 40, 10, 40));
		sliderImageBox.setSpacing(100);

		sliderResizeBox.getChildren().addAll(sliderImageX, sliderImageY, sliderImageZ);
		sliderResizeBox.setAlignment(Pos.CENTER);
		sliderResizeBox.setPadding(new Insets(10, 40, 10, 40));
		sliderResizeBox.setSpacing(100);

		buttonBox.getChildren().addAll(MIPX, MIPY, MIPZ);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setPadding(new Insets(10, 40, 10, 40));
		buttonBox.setSpacing(200);

		imageBox.getChildren().addAll(imageViewX, imageViewY, imageViewZ);
		imageBox.setPadding(new Insets(10, 0, 10, 0));
		imageBox.setSpacing(10);
		imageBox.setAlignment(Pos.CENTER);

		MIPBox.getChildren().addAll(imageViewMIPX, imageViewMIPY, imageViewMIPZ);
		MIPBox.setPadding(new Insets(10, 0, 10, 0));
		MIPBox.setSpacing(10);
		MIPBox.setAlignment(Pos.CENTER);

		thumbnailBox.getChildren().addAll(thumbnailX, thumbnailY, thumbnailZ);
		thumbnailBox.setPadding(new Insets(10, 0, 10, 0));
		thumbnailBox.setSpacing(10);
		thumbnailBox.setAlignment(Pos.CENTER);
		
		centreBox.getChildren().addAll(imageSliderLabels, sliderImageBox, buttonBox, sliderResizeBox,
				resizeSliderLabels, thumbnailBox);

		root.setCenter(centreBox);
		root.setTop(imageBox);
		root.setBottom(MIPBox);

		Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
		stage.setScene(scene);
		stage.show();

		MIPX.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				mipX(medical_imageMIPX);
			}
		});

		MIPY.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				mipY(medical_imageMIPY);
			}
		});

		MIPZ.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				mipZ(medical_imageMIPZ);
			}
		});
		
		sliderImageX.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				imageBox.getChildren().remove(imageViewX);
				resizeX(medical_imageX, sliderX.getValue(), newValue.intValue());
				imageBox.getChildren().add(0, imageViewX);
			}
		});
		
		sliderImageY.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				resizeY(medical_imageY, sliderY.getValue(), newValue.intValue());
			}
		});
		
		sliderImageZ.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {			
				resizeZ(medical_imageZ, sliderZ.getValue(), newValue.intValue());
			}
		});

		sliderX.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				imageX(medical_imageX, newValue.intValue());
			}
		});

		sliderY.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				imageY(medical_imageY, newValue.intValue());
			}
		});

		sliderZ.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				imageZ(medical_imageZ, newValue.intValue());
			}
		});
		
		sliderX.setValue(100);
		sliderY.setValue(70);
		sliderZ.setValue(50);
		thumbnailX(medical_imageThumbNailX);
		
	}

	// Function to read in the cthead data set
	public void ReadData() throws IOException {
		// File name is hardcoded here - much nicer to have a dialog to select
		// it and capture the size from the user
		File file = new File("CThead");
		// Read the data quickly via a buffer (in C++ you can just do a single
		// fread - I couldn't find if there is an equivalent in Java)
		DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));

		int i, j, k; // loop through the 3D data set

		min = Short.MAX_VALUE;
		max = Short.MIN_VALUE; // set to extreme values
		short read; // value read in
		int b1, b2; // data is wrong Endian (check wikipedia) for Java so we
					// need to swap the bytes around

		cthead = new short[113][256][256]; // allocate the memory - note this is
											// fixed for this data set
		// loop through the data reading it in
		for (k = 0; k < 113; k++) {
			for (j = 0; j < 256; j++) {
				for (i = 0; i < 256; i++) {
					// because the Endianess is wrong, it needs to be read byte
					// at a time and swapped
					b1 = ((int) in.readByte()) & 0xff; // the 0xff is because
														// Java does not have
														// unsigned types
					b2 = ((int) in.readByte()) & 0xff; // the 0xff is because
														// Java does not have
														// unsigned types
					read = (short) ((b2 << 8) | b1); // and swizzle the bytes
														// around
					if (read < min)
						min = read; // update the minimum
					if (read > max)
						max = read; // update the maximum
					cthead[k][j][i] = read; // put the short into memory (in C++
											// you can replace all this code
											// with one fread)
				}
			}
		}
		System.out.println(min + " " + max); // diagnostic - for CThead this
												// should be -1117, 2248
		// (i.e. there are 3366 levels of grey (we are trying to display on 256
		// levels of grey)
		// therefore histogram equalization would be a good thing
	}

	public void thumbnailX(WritableImage image) {
		PixelWriter image_writer = image.getPixelWriter();
		float col;
		int Yb = IMAGE_SLICES-112;
		int Xb = IMAGE_WIDTH-112;
		int y = -2 ^ -31;
		int x = -2 ^ -31;
		short datum;
		for (int j = 0; j < Yb - 1; j++) {
			for (int i = 0; i < Xb - 1; i++) {
				for (int c = 0; c < 3; c++) {		
					for (int k = 0; k < IMAGE_SLICES; k++) {
					y = (int) (j * IMAGE_SLICES / Yb);
					x = (int) (i * IMAGE_WIDTH / Xb);
					datum = cthead[y][x][k];
					col = (((float) datum - (float) min) / ((float) (max - min)));
					image_writer.setColor(i, j, Color.color(col, col, col, 1.0));
					}
				}
			}
		}
	}
	
	public void resizeX(WritableImage image, double slice, int sliderVal) {
		PixelWriter image_writer = image.getPixelWriter();
		float col;
		int Yb = IMAGE_SLICES - (sliderVal/2);
		int Xb = IMAGE_WIDTH - sliderVal;
		int y = -2 ^ -31;
		int x = -2 ^ -31;
		short datum;
		for (int j = 0; j < Yb - 1; j++) {
			for (int i = 0; i < Xb - 1; i++) {				
					y = (int) (j * IMAGE_SLICES / Yb);
					x = (int) (i * IMAGE_WIDTH / Xb);
					datum = cthead[y][x][(int) slice];
					col = (((float) datum - (float) min) / ((float) (max - min)));
					image_writer.setColor(i, j, Color.color(col, col, col, 1.0));
			}
		}
	}
	
	public void resizeY(WritableImage image, double slice, int sliderVal) {
		PixelWriter image_writer = image.getPixelWriter();
		float col;
		int Yb = IMAGE_HEIGHT - sliderVal;
		int Xb = IMAGE_SLICES - (sliderVal/2);
		int y = -2 ^ -31;
		int x = -2 ^ -31;
		short datum;
		for (int j = 0; j < Yb - 1; j++) {
			for (int i = 0; i < Xb - 1; i++) {		
					y = (int) (j * IMAGE_HEIGHT / Yb);
					x = (int) (i * IMAGE_SLICES / Xb);
					datum = cthead[x][(int) slice][y];
					col = (((float) datum - (float) min) / ((float) (max - min)));
					image_writer.setColor(j, i, Color.color(col, col, col, 1.0));
			}
		}
	}
	
	public void resizeZ(WritableImage image, double slice, int sliderVal) {
		PixelWriter image_writer = image.getPixelWriter();
		float col;
		int Yb = IMAGE_HEIGHT - sliderVal;
		int Xb = IMAGE_WIDTH - sliderVal;
		int y = -2 ^ -31;
		int x = -2 ^ -31;
		short datum;
		for (int j = 0; j < Yb - 1; j++) {
			for (int i = 0; i < Xb - 1; i++) {			
					y = (int) (j * IMAGE_HEIGHT / Yb);
					x = (int) (i * IMAGE_WIDTH / Xb);
					datum = cthead[(int) slice][x][y];
					col = (((float) datum - (float) min) / ((float) (max - min)));
					image_writer.setColor(j, i, Color.color(col, col, col, 1.0));
			}
		}
	}

	public void mipX(WritableImage image) {
		PixelWriter image_writer = image.getPixelWriter();
		float col;
		int maximum;
		for (int k = 0; k < IMAGE_SLICES; k++) {
			for (int j = 0; j < IMAGE_HEIGHT; j++) {
				maximum = -2 ^ -31;
				for (int i = 0; i < IMAGE_WIDTH; i++) {
					if (cthead[k][j][i] > maximum) {
						maximum = cthead[k][j][i];
					}
				}
				col = (((float) maximum - (float) min) / ((float) (max - min)));
				for (int c = 0; c < 3; c++) {
					image_writer.setColor(j, k, Color.color(col, col, col, 1.0));
				}

			}
		}

	}

	public void mipY(WritableImage image) {
		PixelWriter image_writer = image.getPixelWriter();
		float col;
		int maximum;
		for (int k = 0; k < IMAGE_SLICES; k++) {
			for (int i = 0; i < IMAGE_WIDTH; i++) {
				maximum = -2 ^ -31;
				for (int j = 0; j < IMAGE_HEIGHT; j++) {
					if (cthead[k][j][i] > maximum) {
						maximum = cthead[k][j][i];
					}
				}
				col = (((float) maximum - (float) min) / ((float) (max - min)));
				for (int c = 0; c < 3; c++) {
					image_writer.setColor(i, k, Color.color(col, col, col, 1.0));
				}
			}
		}
	}

	public void mipZ(WritableImage image) {
		PixelWriter image_writer = image.getPixelWriter();
		float col;
		int maximum;
		for (int j = 0; j < IMAGE_HEIGHT; j++) {
			for (int i = 0; i < IMAGE_WIDTH; i++) {
				maximum = -2 ^ -31;
				for (int k = 0; k < IMAGE_SLICES; k++) {
					if (cthead[k][j][i] > maximum) {
						maximum = cthead[k][j][i];
					}
				}
				col = (((float) maximum - (float) min) / ((float) (max - min)));
				for (int c = 0; c < 3; c++) {
					image_writer.setColor(i, j, Color.color(col, col, col, 1.0));
				}
			}
		}
	}

	public void imageX(WritableImage image, int newValx) {
		PixelWriter image_writer = image.getPixelWriter();
		float col;
		short datum;
		for (int z = 0; z < IMAGE_SLICES; z++) {
			for (int j = 0; j < IMAGE_HEIGHT; j++) {
				datum = cthead[z][j][newValx];
				col = (((float) datum - (float) min) / ((float) (max - min)));
					image_writer.setColor(j, z, Color.color(col, col, col, 1.0));
			}
		}
	}

	public void imageY(WritableImage image, int newValy) {
		PixelWriter image_writer = image.getPixelWriter();
		float col;
		short datum;
		for (int z = 0; z < IMAGE_SLICES; z++) {
			for (int i = 0; i < IMAGE_WIDTH; i++) {
				datum = cthead[z][newValy][i];
				col = (((float) datum - (float) min) / ((float) (max - min)));
				for (int c = 0; c < 3; c++) {
					image_writer.setColor(i, z, Color.color(col, col, col, 1.0));
				}
			}
		}
	}

	public void imageZ(WritableImage image, int newValz) {
		PixelWriter image_writer = image.getPixelWriter();
		float col;
		short datum;
		for (int j = 0; j < IMAGE_HEIGHT; j++) {
			for (int i = 0; i < IMAGE_WIDTH; i++) {
				datum = cthead[newValz][j][i];
				col = (((float) datum - (float) min) / ((float) (max - min)));
				for (int c = 0; c < 3; c++) {
					image_writer.setColor(i, j, Color.color(col, col, col, 1.0));
				}
			}
		}
	}

	public static void main(String[] args) {
		launch();
	}

}