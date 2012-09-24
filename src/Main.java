import static com.googlecode.javacv.cpp.opencv_core.cvCreateMemStorage;
import static com.googlecode.javacv.cpp.opencv_highgui.CV_LOAD_IMAGE_ANYCOLOR;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
		IplImage iplImage = cvLoadImage(sourceFileName, CV_LOAD_IMAGE_ANYCOLOR);
		SquareDetector squareDetectorService = new SquareDetector(2);
		List<Square> squares = squareDetectorService.findSquares(iplImage, cvCreateMemStorage(0));
		ColorDensityProcessor imageDensityProcessor = new ColorDensityProcessor();
		int basicLineLength = 15;
		int basicLineWidth = 3;
		Point baseOffset = new Point(16, 7);
		double threshold = 0.5;
		NumberDetector numberDetector = new NumberDetector(imageDensityProcessor, basicLineLength,
				basicLineWidth, threshold);
		String fileName = "partImage";
		System.out.println("Number of squares detected: " + squares.size());
		int width = 0;
		int height = 0;
		for (Square square : squares) {
			width = square.getBottomRight().x() - square.getTopLeft().x();
			height = square.getBottomRight().y() - square.getTopLeft().y();

			cropAndSaveImage(sourceFileName, BASE_LOCATION + "/" + fileName + squares.indexOf(square)
					+ ".png", square.getTopLeft(), width, height);
			System.out.println("Square Number: "
					+ squares.indexOf(square)
					+ "  Number detected: "
					+ numberDetector.detectNumber(
							new BasicImageSource(ImageIO.read(new File(BASE_LOCATION + "/" + fileName
									+ squares.indexOf(square) + ".png"))), baseOffset));
		}
	}
	private static void cropAndSaveImage(String sourceFileName, String outputFileName, Point topLeft,
			int width, int height) throws IOException {
		BufferedImage sourceImage = ImageIO.read(new File(sourceFileName));
		BufferedImage subImage = sourceImage.getSubimage(topLeft.x(), topLeft.y(), width, height);
		ImageIO.write(subImage, "png", new File(outputFileName));
	}
}
