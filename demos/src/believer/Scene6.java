package believer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.opengl.GL;

import glm.mat._4.Mat4;
import glm.vec._2.Vec2;
import glm.vec._3.Vec3;
import glm.vec._4.Vec4;

import java.util.Stack;
import java.util.Vector;

public class Scene6 extends Scene {
	
	private Vector<Vec4> boxCoords, boxColors, box2Coords, box2Colors, box3Coords, box3Colors,
		box4Coords, box4Colors, numberCoords, numberColors;
	private Vector<Vec2> numberShape;
	private Mesh box, box2, box3, box4, number;
	private ShaderProgram shaders;
	private float decreaseNum;
	
	private String vertShaderFile = "src/believer/scene1.vertex.glsl";
	private String fragShaderFile = "src/believer/scene5.fragment.glsl";

	private Window window;
	
	private double initTime, currentTime;
	
	private Mat4 projectionMatrix;
	private Mat4 modelViewMatrix;
	
	private class Square { 
		public Vector<Vec2> shape;
		
		public Square() {
			shape = new Vector<Vec2>();
			
			shape.add(new Vec2(-0.5, -0.5));
			shape.add(new Vec2(-0.5, 0.5));
			shape.add(new Vec2(0.5, 0.5));
			
			shape.add(new Vec2(-0.5, -0.5));
			shape.add(new Vec2(0.5, -0.5));
			shape.add(new Vec2(0.5, 0.5));
		}
	}
	
	private Vector<Vec4> rotateBox(double degree, Vector<Vec4> original) {
		Vector<Vec4> rotated = new Vector<Vec4>();
		Mat4 rotationMatrix = new Mat4(Math.cos(degree), -Math.sin(degree), 	0, 0, 
								  Math.sin(degree), 	  Math.cos(degree), 	0, 0, 
								  0, 					  0, 					1, 0,
								  0, 					  0, 					0, 1);
		for(int i = 0; i < original.size(); i++) {
			rotated.add(new Vec4 (original.get(i).x * .735, original.get(i).y * .735, original.get(i).z + 0.00001, 1));
			rotated.get(i).mul(rotationMatrix);
		}
		
		return rotated;
	}
	
	public Scene6(float startTime) {
		super(startTime);
		
	}

	@Override
	public void init(Window window) {
		GL.createCapabilities();
		this.window = window;
		shaders = new ShaderProgram();
		shaders.attachVertexShaderFile(vertShaderFile);
		shaders.attachFragmentShaderFile(fragShaderFile);
		shaders.link();
		shaders.bind();
		
		projectionMatrix = new Mat4().perspective(45, 1, 1, 1000);
		modelViewMatrix = new Mat4().identity();
		updateMatrixUniforms();
		
		boxCoords = new Vector<Vec4>();
		boxColors = new Vector<Vec4>();
		numberCoords = new Vector<Vec4>();
		numberColors = new Vector<Vec4>();
		numberShape = new Vector<Vec2>();
		
		glDepthFunc(GL_LESS);
		glEnable(GL_DEPTH_TEST);
		
		box = new Mesh(shaders);
		boxCoords.add(new Vec4(5.55,5.55,-10, 1));
		boxCoords.add(new Vec4(-5.55,5.55,-10, 1));
		boxCoords.add(new Vec4(-5.55,-5.55,-10, 1));
		
		boxCoords.add(new Vec4(5.55,5.55,-10, 1));
		boxCoords.add(new Vec4(5.55,-5.55,-10, 1));
		boxCoords.add(new Vec4(-5.55,-5.55,-10, 1));
		
		for (int i = 0; i < boxCoords.size(); i++) {boxColors.add(new Vec4(.439f, .608f, .541f,1));}
		
		box2 = new Mesh(shaders);
		box2Colors = new Vector<Vec4>();
		box2Coords = rotateBox(Math.PI/6, boxCoords);
		for (int i = 0; i < box2Coords.size(); i++) {box2Colors.add(new Vec4(.094f, .274f, .278f,1));}
		
		box3 = new Mesh(shaders);
		box3Colors = new Vector<Vec4>();
		box3Coords = rotateBox(Math.PI/6, box2Coords);
		for (int i = 0; i < box3Coords.size(); i++) {box3Colors.add(new Vec4(.878f, .890f, .757f,1));}
		
		box4 = new Mesh(shaders);
		box4Colors = new Vector<Vec4>();
		box4Coords = rotateBox(Math.PI/6, box3Coords);
		for (int i = 0; i < box4Coords.size(); i++) {box4Colors.add(new Vec4(.933f, .776f, .545f,1));}
		
		number = new Mesh(shaders);
		
		Square pixel = new Square();
		
		numberShape.add(new Vec2(0,0));
		numberShape.add(new Vec2(0,1));
		numberShape.add(new Vec2(0,2));
		numberShape.add(new Vec2(-1,2));
		numberShape.add(new Vec2(0,-1));
		numberShape.add(new Vec2(0,-2));
		numberShape.add(new Vec2(-1,-2));
		numberShape.add(new Vec2(1,-2));
		
		for(int i =0; i < numberShape.size(); i++) {
			for(int j = 0; j < pixel.shape.size(); j++) {
				numberCoords.add(new Vec4(pixel.shape.get(j).x + numberShape.get(i).x, pixel.shape.get(j).y + numberShape.get(i).y, -9.99, 1));
				numberColors.add(new Vec4(.717f, .149f, .216f,1));
			}
		}
		
		
		decreaseNum = 0.1f;
		initTime = window.getRuntime();
	}
	
	private void updateMatrixUniforms() {
		glUniformMatrix4fv(shaders.getUniformLocation("projectionMatrix"), false, projectionMatrix.toDfb_());
		glUniformMatrix4fv(shaders.getUniformLocation("modelviewMatrix"), false, modelViewMatrix.toDfb_());
	}

	@Override
	public void renderFrame(Window window, float musicTime) {
		currentTime = window.getRuntime();
		
		modelViewMatrix.identity();
		//modelViewMatrix.rotate(0,0,0,1);
		updateMatrixUniforms();
		
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		if (numberColors.get(0).w > 0) {
			number.updateData(numberCoords, numberColors);
			number.draw();
		}
		
		box.updateData(boxCoords, boxColors);
		box.draw();
		box2.updateData(box2Coords, box2Colors);
		box2.draw();
		box3.updateData(box3Coords, box3Colors);
		box3.draw();
		
		for (int i = 0; i < box4Coords.size(); i++) {
			Vec4 temp = new Vec4(box4Coords.get(i).x, box4Coords.get(i).y - decreaseNum, box4Coords.get(i).z, box4Coords.get(i).w);
			box4Coords.setElementAt(temp, i);
			if (box4Colors.lastElement().w > 0) {
				temp = new Vec4(box4Colors.get(i).x, box4Colors.get(i).y, box4Colors.get(i).z, box4Colors.get(i).w - 0.01f);
				box4Colors.setElementAt(temp, i);
			}
		}
		
		if (box4Colors.get(0).w > 0) {
			box4.updateData(box4Coords, box4Colors);
			box4.draw();
		}
		
		if (currentTime - initTime >= .3) {
			for (int i = 0; i < box3Coords.size(); i++) {
				Vec4 temp = new Vec4(box3Coords.get(i).x, box3Coords.get(i).y - decreaseNum, box3Coords.get(i).z, box3Coords.get(i).w);
				box3Coords.setElementAt(temp, i);
				if (box3Colors.lastElement().w > 0) {
					temp = new Vec4(box3Colors.get(i).x, box3Colors.get(i).y, box3Colors.get(i).z, box3Colors.get(i).w - 0.01f);
					box3Colors.setElementAt(temp, i);
				}
			}
			
			if (box3Colors.get(0).w > 0) {
				box3.updateData(box3Coords, box3Colors);
				box3.draw();
			}
		}
		
		if (currentTime - initTime >= .6) {
			for (int i = 0; i < box2Coords.size(); i++) {
				Vec4 temp = new Vec4(box2Coords.get(i).x, box2Coords.get(i).y - decreaseNum, box2Coords.get(i).z, box2Coords.get(i).w);
				box2Coords.setElementAt(temp, i);
				if (box2Colors.lastElement().w >0) {
					temp = new Vec4(box2Colors.get(i).x, box2Colors.get(i).y, box2Colors.get(i).z, box2Colors.get(i).w - 0.01f);
					box2Colors.setElementAt(temp, i);
				}
			}
			
			if (box2Colors.get(0).w > 0) {
				box2.updateData(box2Coords, box2Colors);
				box2.draw();
			}
		}
		
		if (currentTime - initTime >= .9) {
			for (int i = 0; i < boxCoords.size(); i++) {
				Vec4 temp = new Vec4(boxCoords.get(i).x, boxCoords.get(i).y - decreaseNum, boxCoords.get(i).z, boxCoords.get(i).w);
				boxCoords.setElementAt(temp, i);
				if (boxColors.lastElement().w > 0) {
					temp = new Vec4(boxColors.get(i).x, boxColors.get(i).y, boxColors.get(i).z, boxColors.get(i).w - 0.01f);
					boxColors.setElementAt(temp, i);
				}
			}
			
			if (boxColors.get(0).w > 0) {
				box2.updateData(boxCoords, boxColors);
				box2.draw();
			}
		}
		
		if (currentTime - initTime >= 3.84 && numberColors.lastElement().w > 0) {
			for (int i = 0; i < numberColors.size(); i++) {
				Vec4 temp = new Vec4(numberColors.get(i).x, numberColors.get(i).y, numberColors.get(i).z, numberColors.get(i).w - 0.01f);
				numberColors.setElementAt(temp, i);
			}
		}
		
	}

	@Override
	public void destroy(Window window) {
		box.destroy();
		number.destroy();
	}

}
