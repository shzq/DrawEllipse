package com.drawellipse.main;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JApplet;
import javax.swing.JFrame;

public class PaintIt extends JFrame {

	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private int windowWidth = screenSize.width/2;
	private int windowHeight = screenSize.height - 30;
	
	private int originX = windowWidth/2;
	private int originY = windowHeight/2;
	
	public PaintIt() {
		super("Draw Ellipse");
		
		setSize(windowWidth, windowHeight);
		setVisible(true);
	}
	
	public void paint(Graphics g) {
		g.drawString("Draw Ellipse", 10, 40);
	}
	
}
