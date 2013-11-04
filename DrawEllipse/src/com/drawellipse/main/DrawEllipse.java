package com.drawellipse.main;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
//import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.JApplet;
import javax.swing.JFrame;

import math.geom2d.Vector2D;

public class DrawEllipse extends JApplet {
	
	// Hard coded foci
	private final static Focus a = new Focus(0,1);
	private final static Focus b = new Focus(2,0);
	private final static Focus c = new Focus(3,5);
	private final static Focus d = new Focus(5,5);
	private final static Focus e = new Focus(5,7);
	
	// Store focus in one vector array, foci
	private static Vector<Focus> foci = new Vector<Focus>();
	
	// Hard coded Xo
	private static Point2D X = new Point2D.Double(5,10);
	
	// For drawing
	private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private static int windowWidth = screenSize.width/2;
	private static int windowHeight = screenSize.height/2;
	// Keep arcs in a Vector to be able to check what points lies in our ellipse
	private static Vector<Arc2D> arcs = new Vector<Arc2D>();
	// Parameters for drawing arcs
	private static double R; // Radius of curvature
    private static Arc2D arc = new Arc2D.Double();
	private static Line2D line;
    
	// Draw axis
	private static Point2D origin;
	
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;		
        final BasicStroke stroke = new BasicStroke(2.0f); 

        // Draw axis
        g2.draw(new Line2D.Double(origin.getX(), 0, origin.getX(), windowHeight));
        g2.draw(new Line2D.Double(0, origin.getY(), windowWidth, origin.getY()));
        
        //
	    g2.setStroke(stroke);
	    g2.draw(arc);
	    if (line != null)
	    	g2.draw(line);
	}
	
	public static void main(String arg[]) {
		
		foci.add(a); 
		foci.add(b); // foci.add(c); foci.add(d); foci.add(e);
		
		JFrame f = new JFrame("DrawEllipse");
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) { System.exit(0); }
		});
		JApplet applet = new DrawEllipse();
		f.getContentPane().add("Center", applet);
		applet.init();
		// sizes the frame so that all its contents are at or above their preferred sizes
		f.pack();
		windowWidth = screenSize.width/2;
		windowHeight = screenSize.height - 40;
		f.setSize(new Dimension(windowWidth,windowHeight));
        f.setVisible(true);
        
        origin = new Point2D.Double(windowWidth/2, windowHeight/2);
        
        // get C. C must be constant. Rounded to two decimal places
		final double C = Calculate.roundToTwoD( Calculate.C(Calculate.XFi(X, foci), foci) );
        System.out.println("final C = " + C);
        
        double totalAngle = 0;
		do {	
			// Get XFi.
			// Note: It is important to let focus in foci is in order with their vector from X in XFi
			Vector<Vector2D> XFi = Calculate.XFi(X, foci);
			
			/** Formula (9) **/
			// Calculate sum of weighted unit vectors from XFi	
			Vector2D u = Calculate.sumOfWeightedUnitVector(XFi);
			System.out.println("u = " + u);
			
			/** Formula (10) **/
			// Get R		
			R = Calculate.radiusOfCurvature(XFi, foci);
		    if (R < 0) {
		    	R = R*-1;
		    }
			System.out.println("R = " + R);      
	        
	     // Get the center of curvature
	        Vector2D normalizedU = u.normalize();
		    Point2D centerOfCircleOfCurvature = new Point2D.Double(X.getX() + normalizedU.x()*R, 
		    		X.getY() + normalizedU.y()*R );
		    System.out.println("centerOfCircleOfCurvature = " + centerOfCircleOfCurvature);
		    
		    // Get the start angle of the curve
		    Vector2D reversedU = normalizedU.times(-1);
		    double startAngle = reversedU.angle()*180/Math.PI;
		    System.out.println("Start angle = " + startAngle);
		    
		    // We draw the curve incrementally as long as it produces the same C
		    // CXo is the sum of weighted distance at end of arc
		    double CXo;
		    double curveAngle = 0.0;
		    Point2D X1;
		    
		    do {
		    	curveAngle += 1;
		    	CXo = 0;
		    	arc.setArcByCenter(centerOfCircleOfCurvature.getX(), centerOfCircleOfCurvature.getY(), R, 
		    			-startAngle, curveAngle, Arc2D.OPEN);
		    	X1 = arc.getEndPoint();
		    	Vector<Vector2D> X1Fi = Calculate.XFi(X1, foci);
		    	CXo = Calculate.roundToTwoD( Calculate.C(X1Fi, foci) );
		    } while (CXo == C);
		   
		    System.out.println("C = " + C);
		    System.out.println("CXo = " + CXo );
		    
		    // Our arc is of curveAngle - 1.
		    curveAngle -= 1;
		    // But will there be a case where curveAngle = 0. Has to extrapolate new point
		    if (curveAngle == 0) {
		    	// We set this new point to be collinear with the previous end of arc and radius.
		    	X1 = Calculate.newStartOfArc(X, normalizedU, C);
		    	System.out.println("new End of arc = " + X1);
		    	// Draw a line between previous end of arc to this new point
		    	line = new Line2D.Double(X.getX(), X.getY(), X1.getX(), X1.getY());
		    	X = X1;
		    } else {		    
		    	arc.setArcByCenter(centerOfCircleOfCurvature.getX(), centerOfCircleOfCurvature.getY(), R, 
	    			-startAngle, curveAngle, Arc2D.OPEN);
		    	arcs.add(arc);
			    totalAngle += curveAngle;
			    System.out.println("Total angle = " + totalAngle);
			    X1 = arc.getEndPoint();
			    X = X1;
			    
			    System.out.println("StartofArc = " + arc.getStartPoint());
			    System.out.println("EndofArc = " + X1);
			    System.out.println("curveAngle = " + curveAngle);
		    }
		    
		    f.repaint();
		} while (totalAngle < 360);
        
	} // end of Main
	
	
	
private static class Calculate {
		
		// Calculate vector between two points 
		private static Vector2D vectorBetweenTwoPoints(Point2D firstPoint, Point2D secondPoint) {
			Vector2D v = new Vector2D(secondPoint.getX() - firstPoint.getX(),secondPoint.getY() - firstPoint.getY());
			return v;
		}
		
		// Calculate XFi
		private static Vector<Vector2D> XFi(Point2D X, Vector<Focus> foci) {
			Vector<Vector2D> XFi = new Vector<Vector2D>();
			for (int i = 0; i < foci.size(); i++) {
				XFi.add(Calculate.vectorBetweenTwoPoints(X,	 foci.get(i).getPoint()));
			}
			return XFi;
		}
		
		// Sum of weighted unit vector
		private static Vector2D sumOfWeightedUnitVector(Vector<Vector2D> XFi) {
			Vector2D u = new Vector2D(0,0); // Init. with (0,0). o/w it will be init. with (1,0)
			for (int i = 0; i < XFi.size(); i++) {
				u = u.plus( XFi.get(i).normalize().times(foci.get(i).getWeight()) );			
			}
			return u;
		}
		
		/** Formula (10) **/
		
		// First, calculate the sum of weighted sin theta
		private static double sumOfWeightedSinTheta(Vector<Vector2D> XFi, Vector<Focus> foci) {
			
			double weightedSinTheta = 0;		
			
			for (int i = 0; i < XFi.size(); i++) {
				// Get sin theta
				weightedSinTheta += Math.sin( XFi.get(i).angle() )*foci.get(i).getWeight();		
			}			
			return weightedSinTheta;
		}
		
		// Second, calculate the sum of weighted sin square theta over distance XFi.
		private static double sumOfSinSquareThetaOverDistance(Vector<Vector2D> XFi, Vector<Focus> foci) {
			
			double weightedSinSquareTheta = 0;
			double sumOfWeightedSinSquareThetaOverDistanceXFi = 0;
			
			for (int i = 0; i < XFi.size(); i++) {
				weightedSinSquareTheta = Math.sin( XFi.get(i).angle() )*Math.sin( XFi.get(i).angle() )*foci.get(i).getWeight();
				sumOfWeightedSinSquareThetaOverDistanceXFi += weightedSinSquareTheta/XFi.get(i).norm();
			}
			return sumOfWeightedSinSquareThetaOverDistanceXFi;
		}
		
		// Lastly, calculate R
		private static double radiusOfCurvature(Vector<Vector2D> XFi, Vector<Focus> foci) {
			return sumOfWeightedSinTheta(XFi, foci)/sumOfSinSquareThetaOverDistance(XFi, foci);
		}
	/** End of Formula (10) **/	
		
		private static double C(Vector<Vector2D> XFi, Vector<Focus> foci) {
	        double C = 0;
			for (int i = 0; i < XFi.size(); i++) {
	        	C += XFi.get(i).norm()*foci.get(i).getWeight();
	        }
	        return C;
		}
		
		public static double roundToTwoD(double d) {
			DecimalFormat twoDForm = new DecimalFormat("#.##");
			return Double.valueOf(twoDForm.format(d));
		}
		
		public static double roundToZeroD(double d) {
			DecimalFormat oneDForm = new DecimalFormat("#");
			return Double.valueOf(oneDForm.format(d));
		}
		
		public static Point2D getP2(Point2D startOfArc, Point2D endOfArc, Point2D centerOfCurve) {
			double l1 = Calculate.vectorBetweenTwoPoints(startOfArc, endOfArc).norm()/2;
			double l2 = Calculate.vectorBetweenTwoPoints(centerOfCurve, endOfArc).norm();
			double anglel1l3 = Math.atan(l1/l2); // in radians
			double l3Length = l1/Math.cos(anglel1l3*180/Math.PI);
			Vector2D v = Calculate.vectorBetweenTwoPoints(centerOfCurve, endOfArc);
			v.rotate(anglel1l3); // v now must be collinear with line centerOfCurve and p2
			Point2D p2 = new Point2D.Double(centerOfCurve.getX() + v.x()*l3Length, centerOfCurve.getY() + v.y()*l3Length);
			return p2;
		}
		
		// Extrapolate points around X that gives the same C (to zero decimal places only)
		public static Point2D newStartOfArc(Point2D X, Vector2D normalizedU, double C) {
			
			int id = 0;
			Point2D newArcStartPoint;
			Vector<Vector2D> newXFi;
			double newC;
			
			do {
				switch (id) {
				case 0:
					newArcStartPoint = new Point2D.Double( X.getX() + normalizedU.x()/4, X.getY() + normalizedU.y()/4);
					newXFi = Calculate.XFi(newArcStartPoint, foci);
					newC = Calculate.roundToZeroD( Calculate.C(newXFi, foci) );
					System.out.println("newC = " + newC);
					if (newC == Calculate.roundToZeroD(C)) {
						return newArcStartPoint;
					} else {
						System.out.println("newC = " + newC);
						id = 1;
					}
				case 1:
					newArcStartPoint = new Point2D.Double( X.getX() - normalizedU.x()/4, X.getY() - normalizedU.y()/4);
					newXFi = Calculate.XFi(newArcStartPoint, foci);
					newC = Calculate.roundToZeroD( Calculate.C(newXFi, foci) );
					System.out.println("newC = " + newC);
					if (newC == Calculate.roundToZeroD(C)) {
						return newArcStartPoint;
					} else {
						id = 2;
					}
				case 2:
					newArcStartPoint = new Point2D.Double( X.getX() + normalizedU.x()/3, X.getY() + normalizedU.y()/3);
					newXFi = Calculate.XFi(newArcStartPoint, foci);
					newC = Calculate.roundToZeroD( Calculate.C(newXFi, foci) );
					System.out.println("newC = " + newC);
					if (newC == Calculate.roundToZeroD(C)) {
						return newArcStartPoint;
					} else {
						id = 3;
					}
				case 3:
					newArcStartPoint = new Point2D.Double( X.getX() - normalizedU.x()/3, X.getY() - normalizedU.y()/3);
					newXFi = Calculate.XFi(newArcStartPoint, foci);
					newC = Calculate.roundToZeroD( Calculate.C(newXFi, foci) );
					System.out.println("newC = " + newC);
					if (newC == Calculate.roundToZeroD(C)) {
						return newArcStartPoint;
					} else {
						id = 4;
					}			
				case 4:
					newArcStartPoint = new Point2D.Double( X.getX() + normalizedU.x()/2, X.getY() + normalizedU.y()/2);
					newXFi = Calculate.XFi(newArcStartPoint, foci);
					newC = Calculate.roundToZeroD( Calculate.C(newXFi, foci) );
					System.out.println("newC = " + newC);
					if (newC == Calculate.roundToZeroD(C)) {
						return newArcStartPoint;
					} else {
						id = 5;
					}
				case 5:
					newArcStartPoint = new Point2D.Double( X.getX() - normalizedU.x()/2, X.getY() - normalizedU.y()/2);
					newXFi = Calculate.XFi(newArcStartPoint, foci);
					newC = Calculate.roundToZeroD( Calculate.C(newXFi, foci) );
					System.out.println("newC = " + newC);
					if (newC == Calculate.roundToZeroD(C)) {
						return newArcStartPoint;
					} else {
						System.out.println("Failed to find new arc start point. System exiting");
						System.exit(ERROR);
					}
				}
			} while (true);

			
			
	
		}
	}
}
