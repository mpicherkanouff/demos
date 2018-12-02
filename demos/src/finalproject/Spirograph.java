package finalproject;

import java.util.Vector;

import glm.vec._4.Vec4;
import glm.vec._2.Vec2;
import glm.mat._4.Mat4;


public class Spirograph {
	
	private Vector<Vec4> graph;
	private Mesh spirograph;
	//texture data eventually
	//temporary vector of white color
	private Vector<Vec4> colors;
	private Mat4 rotationMatrix;
	
	private class Square { 
		public Vector<Vec2> shape;
		
		public Square() {
			shape = new Vector<Vec2>();
			
			shape.add(new Vec2(-0.02, -0.02));
			shape.add(new Vec2(-0.02, 0.02));
			shape.add(new Vec2(0.02, 0.02));
			
			shape.add(new Vec2(-0.02, -0.02));
			shape.add(new Vec2(0.02, -0.02));
			shape.add(new Vec2(0.02, 0.02));
		}
	}

	public Spirograph(ShaderProgram shaders) {
		spirograph = new Mesh(shaders);
		graph = new Vector<Vec4>();
		colors = new Vector<Vec4>();
	}
	
	public void populateSpirograph(float zCoordinate, float bigRadius, float smallRadius, float notch, float tStep) {
		Vector<Vec2> path = new Vector<Vec2>();
		Square pixel = new Square();
		for(float t = 0; t <= 360; t += tStep) {
			path.add(new Vec2((bigRadius - smallRadius)*Math.cos(t) + notch*Math.cos((bigRadius - smallRadius)*t/smallRadius),
					(bigRadius - smallRadius)*Math.sin(t) - notch*Math.sin((bigRadius - smallRadius)*t/smallRadius)));
		}
		
		for(int i = 0; i < path.size(); i++) {
			
			for(int j = 0; j < pixel.shape.size(); j++) {
				graph.add(new Vec4(pixel.shape.get(j).x + path.get(i).x, pixel.shape.get(j).y + path.get(i).y, zCoordinate, 1));
				colors.add(new Vec4(.980f, .969f, .718f,1));
			}
			
		}
		spirograph.updateData(graph, colors);
	}
	
	public void rotate(double degree) {
		rotationMatrix = new Mat4(Math.cos(degree), -Math.sin(degree), 	0, 0, 
								  Math.sin(degree), Math.cos(degree), 	0, 0, 
								  0, 				0, 					1, 0,
								  0, 				0, 					0, 1);
		for(int i = 0; i < graph.size(); i++) {
			graph.get(i).mul(rotationMatrix);
		}
		spirograph.updateData(graph, colors);
	}
	
	public void draw() {
		spirograph.draw();
	}
	
	public void destroy() {
		spirograph.destroy();
	}
	
}
