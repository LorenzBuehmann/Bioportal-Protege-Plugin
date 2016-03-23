package de.leipzig.imise.bioportal.ui;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.beans.PropertyChangeEvent;

/**
 * @author Lorenz Buehmann
 */
public //http://docs.oracle.com/javase/tutorial/uiswing/misc/jlayer.html
//How to Decorate Components with the JLayer Class
//(The Java? Tutorials > Creating a GUI With JFC/Swing > Using Other Swing Features)
//TapTapTap.java
class WaitLayerUI extends LayerUI<JComponent> implements ActionListener {
	private boolean mIsRunning;
	private boolean mIsFadingOut;
	private Timer mTimer;

	private int mAngle;
	private int mFadeCount;
	private int mFadeLimit = 15;
	private String message;

	public WaitLayerUI(){
		this(null);
	}

	public WaitLayerUI(String message) {
		this.message = message;
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		int w = c.getWidth();
		int h = c.getHeight();

		// Paint the view.
		super.paint(g, c);

		if (!mIsRunning) {
			return;
		}

		Graphics2D g2 = (Graphics2D) g.create();

		float fade = (float) mFadeCount / (float) mFadeLimit;
		// Gray it out.
		Composite urComposite = g2.getComposite();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f * fade));
		g2.fillRect(0, 0, w, h);
		g2.setComposite(urComposite);

		// Paint the wait indicator.
		int s = Math.min(w, h) / 5;
		int cx = w / 2;
		int cy = h / 2;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
							RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setStroke(
				new BasicStroke(s / 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g2.setPaint(Color.white);

		if(message != null) {
			Font font = new Font("Serif", Font.PLAIN, 96);
			g2.setFont(font);
			FontMetrics metrics = g.getFontMetrics(font);
			int stringWidth = metrics.stringWidth(message);
			// Find out how much the font can grow in width.
			double widthRatio = (double)w / (double)stringWidth;
			int newFontSize = (int)(font.getSize() * widthRatio);
			font = new Font(font.getName(), Font.PLAIN, newFontSize);
			g2.setFont(font);
			metrics = g.getFontMetrics(font);
			g2.drawString(message + " ...", (cx - metrics.stringWidth(message)/2), (cy));
		}


		g2.rotate(Math.PI * mAngle / 180, cx, cy);
		for (int i = 0; i < 12; i++) {
			float scale = (11.0f - (float) i) / 11.0f;
			g2.drawLine(cx + s, cy, cx + s * 2, cy);
			g2.rotate(-Math.PI / 6, cx, cy);
			g2.setComposite(AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, scale * fade));
		}

		g2.dispose();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (mIsRunning) {
			firePropertyChange("tick", 0, 1);
			mAngle += 3;
			if (mAngle >= 360) {
				mAngle = 0;
			}
			if (mIsFadingOut) {
				if (mFadeCount == 0 || --mFadeCount == 0) {
					mIsRunning = false;
					mTimer.stop();
				}
			} else if (mFadeCount < mFadeLimit) {
				mFadeCount++;
			}
		}
	}

	public void start() {
		if (mIsRunning) {
			return;
		}
		// Run a thread for animation.
		mIsRunning = true;
		mIsFadingOut = false;
		mFadeCount = 0;
		int fps = 24;
		int tick = 1000 / fps;
		mTimer = new Timer(tick, this);
		mTimer.start();
	}

	public void stop() {
		mIsFadingOut = true;
	}

	@Override
	public void applyPropertyChange(PropertyChangeEvent pce, JLayer l) {
		if ("tick".equals(pce.getPropertyName())) {
			l.repaint();
		}
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		((JLayer) c).setLayerEventMask(
				AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK |
						AWTEvent.MOUSE_WHEEL_EVENT_MASK | AWTEvent.KEY_EVENT_MASK |
						AWTEvent.FOCUS_EVENT_MASK | AWTEvent.COMPONENT_EVENT_MASK);
	}

	@Override
	public void uninstallUI(JComponent c) {
		((JLayer) c).setLayerEventMask(0);
		super.uninstallUI(c);
	}

	@Override
	public void eventDispatched(AWTEvent e, JLayer<? extends JComponent> l) {
		if (mIsRunning && e instanceof InputEvent) {
			((InputEvent) e).consume();
		}
	}
}
