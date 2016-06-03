package cma.images;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

public class Images {
	public static File file;
	public static BufferedImage image;
	public static File dir;
	private static ImageFrame frame;

	public static void main(String[] args) {
		if (args.length == 0) {
			dir = new File("");
		} else {
			dir = new File(args[0]);
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				frame = new ImageFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				randomImage();
				frame.setVisible(true);
			}
		});
	}

	public static void randomImage() {
		File[] files = dir.listFiles((FileFilter) FileFilterUtils.fileFileFilter());
		file = files[new Random().nextInt(files.length)];
		frame.setTitle(file.getAbsolutePath() + " " + files.length);
		try {
			image = ImageIO.read(Images.file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class ImageFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	public ImageFrame() {
		setTitle("ImageTest");
		Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		setSize((int) screenSize.getWidth(), (int) screenSize.getHeight());

		final ImageComponent component = new ImageComponent();
		add(component);
		component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "next");
		component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "keep");
		component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "delete");
		component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "again");
		final Action nextAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				Images.randomImage();
				component.repaint();
			}
		};
		final Action againAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				try {
					FileUtils.moveFileToDirectory(Images.file, new File(Images.dir, "again"), true);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				nextAction.actionPerformed(e);
			}
		};
		final Action keepAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				try {
					FileUtils.moveFileToDirectory(Images.file, new File(Images.dir, "viewed"), true);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				nextAction.actionPerformed(e);
			}
		};
		component.getActionMap().put("keep", keepAction);
		component.getActionMap().put("next", nextAction);
		component.getActionMap().put("again", againAction);
		component.getActionMap().put("delete", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				try {
					FileUtils.moveFileToDirectory(Images.file, new File(Images.dir, "delete"), true);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				nextAction.actionPerformed(e);
			}
		});

	}
}

class ImageComponent extends JComponent {
	private static final long serialVersionUID = 1L;

	public void paintComponent(Graphics g) {
		if (Images.image == null)
			return;
		int imageWidth = Images.image.getWidth(this);
		int imageHeight = Images.image.getHeight(this);

		int screenWidth = this.getWidth();
		int screenHeight = this.getHeight();

		int padding = 5;
		int maxImageWidth = screenWidth - padding * 2;
		int maxImageHight = screenHeight - padding * 2;

		int drawWidth = imageWidth;
		int drawHeight = imageHeight;
		if (imageHeight > imageWidth) {
			if (imageHeight < maxImageHight) {
				drawHeight = imageHeight;
			} else {
				drawHeight = maxImageHight;
				drawWidth = (int) (((double) drawHeight) / imageHeight * imageWidth);
			}
		} else {
			if (imageWidth < maxImageWidth) {
				drawWidth = imageWidth;
			} else {
				drawWidth = maxImageWidth;
				drawHeight = (int) (((double) drawWidth) / imageWidth * imageHeight);
			}
		}

		g.drawImage(Images.image, screenWidth / 2 - drawWidth / 2, screenHeight / 2 - drawHeight / 2, drawWidth,
				drawHeight, this);
	}
}
