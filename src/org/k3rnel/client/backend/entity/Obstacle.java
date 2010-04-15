package org.k3rnel.client.backend.entity;


/**
 * obstacles
 * @author ZombieBear
 *
 */
public class Obstacle extends Player {
	private int m_neededTrainerLvl;
	private String m_objectName;

	public static enum HMObjectType {
		TREE,
		ROCK,
		BOULDER,
		WHIRLPOOL,
		HEADBUTT_TREE
	}
	
	public static HMObjectType parseHMObject(String s) throws Exception {
		for (HMObjectType HMObj : HMObjectType.values()) {
			if (s.equalsIgnoreCase(HMObj.name()))
				return HMObj;
		}
		throw new Exception("This is not an obstacle");
	}
	
	/**
	 * Returns the objeect's name (for internal use only)
	 * @param e
	 * @return
	 */
	private static String getObjectName(HMObjectType e){
		switch (e) {
		case TREE:
			return "Headbutt Tree";
		case ROCK:
			return "Rocksmash Rock";
		case BOULDER:
			return "Strength Boulder";
		case WHIRLPOOL:
			return "Whirlpool"; 
		case HEADBUTT_TREE:
			return "Headbutt Tree";
		}
		return "";
	}
	
	/**
	 * Returns the necessary trainer level to work the object (for internal use only)
	 * @param e
	 * @return
	 */
	private int getNeededTrainerLvl(HMObjectType e){
		switch (e) {
		case TREE:
			return 15;
		case ROCK:
			return 30;
		case BOULDER:
			return 35;
		case WHIRLPOOL:
			return 40; 
		case HEADBUTT_TREE:
			return 0;
		}
		return 0;
	}
	
	/**
	 * Default constructor
	 * @param e HMObjectType enum
	 */
	public Obstacle(HMObjectType e){
		super();
		m_objectName = getObjectName(e);
		m_neededTrainerLvl = getNeededTrainerLvl(e);
		setUsername("");
	}
	
	
	
	/**
	 * Returns the necessary trainer level to use the object
	 * @return
	 */
	public int getRequiredTrainerLevel() {
		return m_neededTrainerLvl;
	}
	
	/**
	 * Returns the object's name
	 * @return
	 */
	public String getName(){
		return m_objectName;
	}
	
	@Override
	public int getType(){
		return 2;
	}
}
