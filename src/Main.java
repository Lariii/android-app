import static com.googlecode.javacv.cpp.opencv_core.cvCreateMemStorage;
import static com.googlecode.javacv.cpp.opencv_highgui.CV_LOAD_IMAGE_COLOR;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.thoughtworks.lariii.model.BasicImageSource;
import com.thoughtworks.lariii.model.Point;
import com.thoughtworks.lariii.model.Square;
import com.thoughtworks.lariii.service.ColorDensityProcessor;
import com.thoughtworks.lariii.service.NumberDetector;
import com.thoughtworks.lariii.service.SquareDetector;

public class Main {
	private static final String BASE_LOCATION = "/Users/chandrasekar/Desktop/testData";

	public static void main(String[] args) throws IOException, InterruptedException {
		String sourceFileName = BASE_LOCATION + "/testImage.png";
		IplImage iplImage = cvLoadImage(sourceFileName, CV_LOAD_IMAGE_COLOR);
		SquareDetector squareDetectorService = new SquareDetector(2);
		List<Square> squares = squareDetectorService.findSquares(iplImage, cvCreateMemStorage(0));
		ColorDensityProcessor imageDensityProcessor = new ColorDensityProcessor();
		int basicLineLength = 15;
		int basicLineWidth = 3;
		Point baseOffset = new Point(16, 7);
		double threshold = 0.4;
		NumberDetector numberDetector = new NumberDetector(imageDensityProcessor, basicLineLength,
				basicLineWidth, threshold);
		String fileName = BASE_LOCATION + "/partImage";
		String extension = ".png";
		String outputFileName = "";
		int width = 0;
		int height = 0;
		System.out.println(squares.size());
		Collections.sort(squares);
		for (Square square : squares) {
			outputFileName = fileName + squares.indexOf(square) + extension;

			width = square.getBottomRight().x() - square.getTopLeft().x();
			height = square.getBottomRight().y() - square.getTopLeft().y();

			cropAndSaveImage(sourceFileName, outputFileName, square.getTopLeft(), width, height);

			BufferedImage blackAndWhiteImage = getBlackAndWhiteImage(new File(outputFileName));

			int number = numberDetector
					.detectNumber(new BasicImageSource(blackAndWhiteImage), baseOffset);
			System.out.println(outputFileName + " Number detected: " + number);

		}
	}
	private static void cropAndSaveImage(String sourceFileName, String outputFileName, Point topLeft,
			int width, int height) throws IOException {
		BufferedImage sourceImage = ImageIO.read(new File(sourceFileName));
		BufferedImage subImage = sourceImage.getSubimage(topLeft.x(), topLeft.y(), width, height);
		ImageIO.write(subImage, "png", new File(outputFileName));
	}

	private static BufferedImage getBlackAndWhiteImage(File sourceImage) throws IOException {
		BufferedImage originalImage = ImageIO.read(sourceImage);
		return originalImage;
		// BufferedImage blackAndWhiteImg = new
		// BufferedImage(originalImage.getWidth(),
		// originalImage.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
		// Graphics2D graphics = blackAndWhiteImg.createGraphics();
		// graphics.drawImage(originalImage, 0, 0, null);
		// ImageIO.write(blackAndWhiteImg, ".png", new
		// File(sourceImage.getParentFile().getAbsolutePath()
		// + "/" + sourceImage.getName() + "_bw" + ".png"));
		// return blackAndWhiteImg;
	}

}
