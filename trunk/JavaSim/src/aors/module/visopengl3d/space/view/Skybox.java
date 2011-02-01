package aors.module.visopengl3d.space.view;


import com.sun.opengl.util.texture.Texture;

import java.util.EnumMap;

/**
 * Skybox of a three dimensional space view.
 * 
 * @author Susanne Schölzel
 * @since November 26th, 2010
 * 
 */
public class Skybox {

	// String constant for the "Skybox" node
	public static final String SKYBOX = "Skybox";

	// String constants for skybox attributes
	public static final String TOP = "top";
	public static final String BOTTOM = "bottom";
	public static final String LEFT = "left";
	public static final String RIGHT = "right";
	public static final String FRONT = "front";
	public static final String BACK = "back";
	
	// EnumMap for the texture filenames of the skybox
	protected EnumMap<Face, String> textureFilenames = new EnumMap<Face, String>(Face.class);
	
	// EnumMap for the textures of the skybox
	protected EnumMap<Face, Texture> textures = new EnumMap<Face, Texture>(Face.class);
	
	public Texture getTexture(Face face) {
		return textures.get(face);
	}

	public void setTexture(Face face, Texture texture) {
		this.textures.put(face, texture);
	}
	
	public String getTextureFilename(Face face) {
		return textureFilenames.get(face);
	}

	public void setTextureFilename(Face face, String textureFilename) {
		this.textureFilenames.put(face, textureFilename);
	}
}
