package finalproject;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.util.Vector;

import glm.vec._4.Vec4;


public class Mesh {
	/* This class represents a 3D object that we can draw with
	 * the GPU. The standard usage for it is:
	 * 	 - create one, passing in the corresponding shader
	 *     program that should be used to draw it
	 *   - call updateData() to load its vertex positions and colors
	 *   - call draw()
	 *   - potentially, call updateData() again should the model change
	 *   - call destroy() once, when the program is closing down
	 */
	
	/*********************************
	 * Mesh state
	 *********************************/
	private ShaderProgram shaders;
	private int vaoID;
	private int[] buffers = new int[2];
	private int positionBufferID, colorBufferID;
	private float[] positionData, colorData;
	
	
	/*********************************
	 * Mesh utility methods
	 *********************************/

	private float[] flattenVector(Vec4[] v) {
		float[] flattenedVector = new float[v.length * 4];
		
		for (int i = 0; i < v.length; i++) {
			flattenedVector[i*4 + 0] = v[i].x;
			flattenedVector[i*4 + 1] = v[i].y;
			flattenedVector[i*4 + 2] = v[i].z;
			flattenedVector[i*4 + 3] = v[i].w;
		}
		
		return flattenedVector;
	}
	
	
	/*********************************
	 * Mesh public methods
	 *********************************/

	public Mesh(ShaderProgram shaders) {
		this.shaders = shaders;
		
		//VBOs for position and color
		glGenBuffers(buffers);
		positionBufferID = buffers[0];
		colorBufferID = buffers[1];
		
		//VAO
		vaoID = glGenVertexArrays();
		glBindVertexArray(vaoID);
		
		//bind position
		glEnableVertexAttribArray(shaders.getAttribLocation("position"));
		glBindBuffer(GL_ARRAY_BUFFER, positionBufferID);
		glVertexAttribPointer(shaders.getAttribLocation("position"), 4, GL_FLOAT, false, 0, 0);
		
		//bind color
		glEnableVertexAttribArray(shaders.getAttribLocation("color"));
		glBindBuffer(GL_ARRAY_BUFFER, colorBufferID);
		glVertexAttribPointer(shaders.getAttribLocation("color"), 4, GL_FLOAT, false, 0, 0);
	}
	
	public void updateData(Vector<Vec4> positions, Vector<Vec4> colors) {
		updateData(positions.toArray(new Vec4[positions.size()]),
					colors.toArray(new Vec4[positions.size()]));
	}
	
	public void updateData(Vec4[] positions, Vec4[] colors) {
		positionData = flattenVector(positions);
		colorData = flattenVector(colors);
		
		glBindVertexArray(vaoID);
		
		//position
		glBindBuffer(GL_ARRAY_BUFFER, positionBufferID);
		glBufferData(GL_ARRAY_BUFFER, positionData, GL_STATIC_DRAW);
		
		//color
		glBindBuffer(GL_ARRAY_BUFFER, colorBufferID);
		glBufferData(GL_ARRAY_BUFFER, colorData, GL_STATIC_DRAW);
		
	}
	
	public void draw() {
		shaders.bind();
		
		glBindVertexArray(vaoID);
		glDrawArrays(GL_TRIANGLES, 0, positionData.length/4);
	}
	
	public void destroy() {
		glDeleteBuffers(colorBufferID);
		glDeleteBuffers(positionBufferID);
		glDeleteBuffers(buffers);
		glDeleteVertexArrays(vaoID);
	}
	
}
