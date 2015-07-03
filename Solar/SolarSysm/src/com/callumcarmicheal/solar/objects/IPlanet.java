package com.callumcarmicheal.solar.objects;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.AsyncBoxView.ChildLocator;

import org.lwjgl.opengl.GL11;
import org.objectweb.asm.tree.IntInsnNode;
import org.pushingpixels.substance.internal.contrib.randelshofer.quaqua.colorchooser.PaletteListModel;

import com.callumcarmicheal.OpenGL.GLUT;
import com.callumcarmicheal.solar.exceptions.ExCause;
import com.callumcarmicheal.solar.exceptions.PlanetException;
import com.callumcarmicheal.solar.maths.Vector3f;

public abstract class IPlanet {

	// REQUIRED
	protected String planetName;
	protected int orbitIndex;
	protected float size;
	protected float offset;
	protected Vector3f Color;

	// ** OPTIONAL
	protected IPlanet BasePlanet = null; // IF NULL THEN SPIN AROUND THE SUN,
											// Maybe?
	protected List<IPlanet> subplanets = new ArrayList<IPlanet>();;
	protected float dayMultiplier = 1; // THIS IS THE SPEED MULTIPLIER -.-
	protected float subplanets_Multiplier = 4;
	protected float subplanets_offset = 0.7f;
	protected float DaysInAYear = 365;
	
	protected float xOffset = 0;

	public IPlanet() {
		init();

		calculateOffset();
	}

	public IPlanet(String PlanetName, int OrbitIndex, float Size,
			float DayMultiplier, Vector3f planetColor) {
		this(PlanetName, OrbitIndex, Size, DayMultiplier, planetColor, null,
				null, 0.7f);
	}

	public IPlanet(String PlanetName, int OrbitIndex, float Size,
			float DayMultiplier, Vector3f planetColor,
			List<IPlanet> Subplanets, float Subplanets_offset) {
		this(PlanetName, OrbitIndex, Size, DayMultiplier, planetColor, null,
				Subplanets, Subplanets_offset);
	}

	public IPlanet(String PlanetName, int OrbitIndex, float Size,
			float DayMultiplier, Vector3f planetColor, IPlanet BasePlanet,
			List<IPlanet> Subplanets, float Subplanets_Multiplier) {
		this.planetName = PlanetName;
		this.orbitIndex = OrbitIndex;
		this.size = Size;
		this.dayMultiplier = DayMultiplier;
		this.Color = planetColor;
		this.BasePlanet = BasePlanet;
		this.subplanets = Subplanets;
		this.subplanets_Multiplier = Subplanets_Multiplier;

		init();

		calculateOffset();
	}
	
	private void calculateOffset() {
		if (BasePlanet != null) {
			this.offset = 100.0f;
		} else {
			this.offset = subplanets_offset;
		}
	}
	
	public float getDay(float DayOfYear) {
		return (360.0f * DayOfYear / 365.0f);
	}
	
	public float getHour(float HourOfDay) {
		return (360.0f * HourOfDay / 24.0f);
	}
	
	

	/**
	 * 
	 * Add a sub Planet
	 * 
	 * @param subPlanet
	 *            The planet to add
	 * @throws PlanetException
	 *             If Planet is already created, this will be thrown
	 */
	public void addChildPlanet(IPlanet subPlanet) throws PlanetException {
		boolean valid = true;

		for (IPlanet moon : this.subplanets) {
			if (moon.planetName.equals(subPlanet.planetName)) {
				valid = false;
			}
		}

		if (valid) {
			this.subplanets.add(subPlanet);
		} else {
			throw new PlanetException(subPlanet, ExCause.PlanetAlreadyTaken,
					"The planet's name was already created and therefore was not added");
		}
	}

	/**
	 * Get Planet by planet's name.
	 * 
	 * @param name
	 * @return
	 * @throws PlanetException
	 *             If planet is not in array, this will be thrown
	 */

	public IPlanet getChildPlanet(String name) throws PlanetException {
		if (!subplanets.isEmpty()) {
			for (IPlanet moon : this.subplanets) {
				if (moon.planetName.equals(name)) {
					return moon;
				}
			}
		}

		throw new PlanetException(ExCause.PlanetDoesnotExist,
				"The planet specified does not exist in child array.");
	}

	/**
	 * Any extra code needed add it in here
	 */
	public abstract void init();

	/**
	 * Called before render
	 */
	public void update(float HourOfDay, float DayOfYear, int NumberOfYear) {
		// Render Self
		render(HourOfDay, DayOfYear, NumberOfYear);

		// Render Sub-planets
		if (subplanets != null) {
			if (!subplanets.isEmpty()) {
				for (IPlanet moon : subplanets) {
					moon.update(HourOfDay, DayOfYear, NumberOfYear);
				}
			}
		}
	}

	/**
	 * DO NOT OVERWRITE UNLESS NEEDED TO, AFTER CALL super.render(HourOfDay,
	 * DayOfYear);
	 */
	private void render(float HourOfDay, float DayOfYear, int NumberOfYear) {
		float angle1;
		float angle2;

		if (orbitIndex == 0) {
			GL11.glLoadIdentity();
			GL11.glTranslatef(0.0f, 0.0f, -8.0f);
			GL11.glRotatef(15.0f, 1.0f, 0.0f, 0.0f);
			
			if (Color != null) {
				GL11.glColor3f(Color.R, Color.G, Color.B);
			}
			GLUT.WireSphere3D((this.size / 10), 15, 15);
		} else if (BasePlanet == null) {
			GL11.glLoadIdentity();
			GL11.glTranslatef(0.0f, 0.0f, -8.0f);
			GL11.glRotatef(15.0f, 1.0f, 0.0f, 0.0f);
			
			// Render planet as its own
			{
				// Get Planets time of Day (DEFAULT : EARTH)
				GL11.glRotatef(getDay(DayOfYear), 0.0f, 1.0f, 0.0f);
				GL11.glRotatef(getHour(HourOfDay), 0.0f, 1.0f, 0.0f);
				
				GL11.glTranslatef((2 * orbitIndex - 1 + offset), 0.0f, 0.0f);

				GL11.glPushMatrix(); // Save Matrix State
				{
					// Third, we draw the Planet as a Sphere
					if (Color != null) {
						GL11.glColor3f(Color.R, Color.G, Color.B);
					}
					GLUT.WireSphere3F((this.size / 10), 10, 10);
				}
				GL11.glPopMatrix(); // Restore Matrix State
			}
		} else {
			GL11.glRotatef(
				(float) 360.0 * DayOfYear / 365.0f,
				0.0f,
				1.0f,
				0.0f
			);
			GL11.glTranslatef(
				0.7f,
				0.0f, 0.0f
			);
			
			if (Color != null) {
				GL11.glColor3f(Color.R, Color.G, Color.B);
			}
			
			GLUT.WireSphere3F(
				( (BasePlanet.size / 10) / BasePlanet.subplanets_Multiplier ) 
				/ BasePlanet.subplanets.size(),
				5,
				5
			);
		}

	}

}
