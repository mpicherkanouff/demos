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

public class Scene5 extends Scene {
	
	private Vector<Vec4> boxCoords, boxColors, box2Coords, box2Colors, box3Coords, box3Colors,
		box4Coords, box4Colors, numberCoords, numberColors;
	private Vector<Vec2> numberShape;
	private Mesh box, box2, box3, box4, number;
	private ShaderProgram shaders;
	private boolean draw2 = false, draw3 = false, draw4 = false;
	
	private String vertShaderFile = "src/believer/scene1.vertex.glsl";
	private String fragShaderFile = "src/believer/scene5.fragment.glsl";

	private Window window;
	
	private double initTime, draw2Time, draw3Time, draw4Time, currentTime;
	
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
	
	public Scene5(float startTime) {
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
	
		number.updateData(numberCoords, numberColors);
		number.draw();
		
		box.updateData(boxCoords, boxColors);
		box.draw();
		
		if (!draw4) {
			if(currentTime - initTime >= 1.91) {
				if (!draw2) {
					draw2 = true;
					initTime = currentTime;
				} else if (draw2 && !draw3) {
					draw3 = true;
					initTime = currentTime;
				} else if (draw2 && draw3 && !draw4) {
					draw4 = true;
					initTime = currentTime;
				}
			}
		}
		
		if (draw2) {
			box2.updateData(box2Coords, box2Colors);
			box2.draw();
		}
		
		if (draw3) {
			box3.updateData(box3Coords, box3Colors);
			box3.draw();
		}
		
		if (draw4) {
			box4.updateData(box4Coords, box4Colors);
			box4.draw();
		}
		
	}

	@Override
	public void destroy(Window window) {
		box.destroy();
		number.destroy();
	}

}
