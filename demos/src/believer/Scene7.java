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

public class Scene7 extends Scene {
	
	private Vector<Vec4> boxCoords, boxColors, box2Coords, box2Colors, box3Coords, box3Colors,
		box4Coords, box4Colors, box5Coords, box5Colors, box6Coords, box6Colors, box7Coords, box7Colors,
		numberCoords, numberColors;
	private Vector<Vec2> numberShape;
	private Mesh box, box2, box3, box4, 
		box5, box6, box7, number;
	private ShaderProgram shaders;
	private boolean draw2 = false, draw3 = false, draw4 = false,
			draw5 = false, draw6 = false, draw7 = false;
	private int whichBox = 2;
	
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
			rotated.add(new Vec4 (original.get(i).x * .819, original.get(i).y * .819, original.get(i).z + 0.00001, 1));
			rotated.get(i).mul(rotationMatrix);
		}
		
		return rotated;
	}
	
	public Scene7(float startTime) {
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
		box2Coords = rotateBox(-Math.PI/12, boxCoords);
		for (int i = 0; i < box2Coords.size(); i++) {box2Colors.add(new Vec4(.094f, .274f, .278f,1));}
		
		box3 = new Mesh(shaders);
		box3Colors = new Vector<Vec4>();
		box3Coords = rotateBox(-Math.PI/12, box2Coords);
		for (int i = 0; i < box3Coords.size(); i++) {box3Colors.add(new Vec4(.878f, .890f, .757f,1));}
		
		box4 = new Mesh(shaders);
		box4Colors = new Vector<Vec4>();
		box4Coords = rotateBox(-Math.PI/12, box3Coords);
		for (int i = 0; i < box4Coords.size(); i++) {box4Colors.add(new Vec4(.933f, .776f, .545f,1));}
		
		box5 = new Mesh(shaders);
		box5Colors = new Vector<Vec4>();
		box5Coords = rotateBox(-Math.PI/12, box4Coords);
		for (int i = 0; i < box4Coords.size(); i++) {box5Colors.add(new Vec4(.439f, .608f, .541f,1));}
		
		box6 = new Mesh(shaders);
		box6Colors = new Vector<Vec4>();
		box6Coords = rotateBox(-Math.PI/12, box5Coords);
		for (int i = 0; i < box4Coords.size(); i++) {box6Colors.add(new Vec4(.094f, .274f, .278f,1));}
		
		box7 = new Mesh(shaders);
		box7Colors = new Vector<Vec4>();
		box7Coords = rotateBox(-Math.PI/12, box6Coords);
		for (int i = 0; i < box4Coords.size(); i++) {box7Colors.add(new Vec4(.878f, .890f, .757f,1));}
		
		number = new Mesh(shaders);
		
		Square pixel = new Square();
		
		numberShape.add(new Vec2(0,0));
		numberShape.add(new Vec2(1,0));
		numberShape.add(new Vec2(1,1));
		numberShape.add(new Vec2(1,2));
		numberShape.add(new Vec2(0,2));
		numberShape.add(new Vec2(-1,2));
		numberShape.add(new Vec2(-1,-2));
		numberShape.add(new Vec2(1,-2));
		numberShape.add(new Vec2(0,-2));
		numberShape.add(new Vec2(-1,-1));
		numberShape.add(new Vec2(-1,0));
		
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
		
		if (!draw7) {
			if(currentTime - initTime >= (1.91/2)) {
				if (whichBox > 1) {
					draw2 = true;
				}
				if (whichBox > 2) {
					draw3 = true;
				}
				if (whichBox > 3) {
					draw4 = true;
				}
				if (whichBox > 4) {
					draw5 = true;
				}
				if (whichBox > 5) {
					draw6 = true;
				}
				if (whichBox > 6) {
					draw7 = true;
				}

				whichBox++;
				initTime = currentTime;
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
		
		if (draw5) {
			box5.updateData(box5Coords, box5Colors);
			box5.draw();
		}
		
		if (draw6) {
			box6.updateData(box6Coords, box6Colors);
			box6.draw();
		}
		if (draw7) {
			box7.updateData(box7Coords, box7Colors);
			box7.draw();
		}
		
	}

	@Override
	public void destroy(Window window) {
		box.destroy();
		number.destroy();
	}

}
