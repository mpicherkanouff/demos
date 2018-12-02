package finalproject;

import glm.vec._2.Vec2;
import glm.vec._4.Vec4;

import java.util.Random;
import java.util.Vector;

public class CantorSet {
	
	private Mesh set;
	private Vector<Vec4> path;
	private Vector<Vec4> colors;
	
	private class Square { 
		public Vector<Vec2> shape;
		
		public Square(Vec2[] vertices) {
			shape = new Vector<Vec2>();
			
			shape.add(vertices[0]);
			shape.add(vertices[1]);
			shape.add(vertices[2]);
			
			shape.add(vertices[0]);
			shape.add(vertices[3]);
			shape.add(vertices[2]);
		}
	}
	
	public CantorSet(ShaderProgram shaders) {
		set = new Mesh(shaders);
		path = new Vector<Vec4>();
		colors = new Vector<Vec4>();
	}
	
	public void generate(int n, Vec2[] corners, float zIndex) {
		if (n <= 0) {
			//create square based on corners
			Vector<Vec4> vertices = makeSquare(n, corners, zIndex);
			
			//add to path
			path.addAll(vertices);
		} else {
			
			
			//create square based on corners
			Vector<Vec4> vertices = makeSquare(n, corners, zIndex);
			
			//add to path
			path.addAll(vertices);
			
			//slice corners
			Vector<Vec2[]> grid = new Vector<Vec2[]>();
			populateGrid(grid, corners);
			
			//generate for each square
			for (int i = 0; i < grid.size(); i++) {
				generate(n-1, grid.elementAt(i), zIndex+0.01f);
			}
		}
	}
	
	private void populateGrid(Vector<Vec2[]> grid, Vec2[] corners) {
		double totalLength = Math.sqrt( Math.pow(corners[1].x - corners[0].x, 2) 
				+ Math.pow(corners[1].y - corners[0].y, 2) );
		double thirdLength = totalLength/3;
		
		Vec2[] temp = new Vec2[4];
		/*
		 * Specified: [(1,1), (-1,1), (-1,-1), (1, -1)]
		 * [ top left, top right, bottom right, bottom left ]
		 */
		
		//top right
		temp[0] = new Vec2(corners[0].x, corners[0].y);
		temp[3] = new Vec2(corners[0].x, corners[0].y - thirdLength);
		temp[2]	= new Vec2(corners[0].x - thirdLength, corners[0].y - thirdLength);
		temp[1] = new Vec2(corners[0].x - thirdLength, corners[0].y);
		grid.add(temp);
		
		//top left
		temp = new Vec2[4];
		temp[1] = new Vec2(corners[1].x, corners[1].y);
		temp[0] = new Vec2(corners[1].x + thirdLength, corners[1].y);
		temp[3]	= new Vec2(corners[1].x + thirdLength, corners[1].y - thirdLength);
		temp[2] = new Vec2(corners[1].x, corners[1].y - thirdLength);
		grid.add(temp);
		
		//bottom left
		temp = new Vec2[4];
		temp[2] = new Vec2(corners[2].x, corners[2].y);
		temp[3] = new Vec2(corners[2].x + thirdLength, corners[2].y);
		temp[0]	= new Vec2(corners[2].x + thirdLength, corners[2].y + thirdLength);
		temp[1] = new Vec2(corners[2].x, corners[2].y + thirdLength);
		grid.add(temp);
		
		//bottom right
		temp = new Vec2[4];
		temp[3] = new Vec2(corners[3].x, corners[3].y);
		temp[2] = new Vec2(corners[3].x - thirdLength, corners[3].y);
		temp[1]	= new Vec2(corners[3].x - thirdLength, corners[3].y + thirdLength);
		temp[0] = new Vec2(corners[3].x, corners[3].y + thirdLength);
		grid.add(temp);
	}
	
	
	private Vector<Vec4> makeSquare(int n, Vec2[] corners, float zIndex) {
		Square s = new Square(corners);
		Vector<Vec4> vertices = new Vector<Vec4>();
		float x, y;
		
		for(int i = 0; i < s.shape.size(); i++) {
			x = s.shape.get(i).x;
			y = s.shape.get(i).y;
			vertices.add(new Vec4(x,y,zIndex,1));
			if (n%3 == 1) {
				colors.add(new Vec4(.255f, .094f, .125f,1));
			} else if (n%3 == 2) {
				colors.add(new Vec4(.408f, .702f, .525f,1));
			} else {
				colors.add(new Vec4(.772f, .208f, .055f,1));
			}
		}
		
		return vertices;
	}
	
	public void updateData() {
		set.updateData(path, colors);
	}
	
	public void draw() {
		set.draw();
	}
	
	public void destroy() {
		set.destroy();
	}
}
