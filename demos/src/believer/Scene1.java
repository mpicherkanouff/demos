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

public class Scene1 extends Scene {
	
	private Vector<Vec4> boxCoords, boxColors;
	private Mesh box;
	private float alpha = 1;
	private ShaderProgram shaders;
	
	private String vertShaderFile = "src/believer/scene1.vertex.glsl";
	private String fragShaderFile = "src/believer/scene1.fragment.glsl";

	private Window window;
	
	private double initTime, initTime2, currentTime;
	private boolean fadePoint = false;
	private boolean resets = false;
	
	private Mat4 projectionMatrix;
	private Mat4 modelViewMatrix;
	
	public Scene1(float startTime) {
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
		
		glDepthFunc(GL_LESS);
		glEnable(GL_DEPTH_TEST);
		
		box = new Mesh(shaders);
		boxCoords.add(new Vec4(7,7,-10, 1));
		boxCoords.add(new Vec4(-7,7,-10, 1));
		boxCoords.add(new Vec4(-7,-7,-10, 1));
		
		boxCoords.add(new Vec4(7,7,-10, 1));
		boxCoords.add(new Vec4(7,-7,-10, 1));
		boxCoords.add(new Vec4(-7,-7,-10, 1));
		
		for (int i = 0; i < boxCoords.size(); i++) {boxColors.add(new Vec4(.439f, .608f, .541f, 1));}
		
		glUniform1f(shaders.getUniformLocation("alpha"), alpha);
		
		initTime = window.getRuntime();
		initTime2 = initTime;
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
		
		if (!fadePoint) {
			if(currentTime - initTime >= .243) {
				fadePoint = true;
			}
		}
		
		if (fadePoint && !resets) {
			if (alpha > 0) {
				alpha -= 0.01f;
				glUniform1f(shaders.getUniformLocation("alpha"), alpha);
			}
		} else if (fadePoint && resets) {
			if (alpha > 0) {
				alpha -= 0.03f;
				glUniform1f(shaders.getUniformLocation("alpha"), alpha);
			}
		}
		
		if(currentTime - initTime2 >= 1.445) {
			resets = true;
			initTime2 = currentTime;
			alpha = 1f;
		}
		
		if (resets) {
			if(currentTime - initTime2 >= .161) {
				initTime2 = currentTime;
				alpha = 1f;
			}
		}
		
		box.updateData(boxCoords, boxColors);
		box.draw();
	}

	@Override
	public void destroy(Window window) {
		box.destroy();
	}

}
