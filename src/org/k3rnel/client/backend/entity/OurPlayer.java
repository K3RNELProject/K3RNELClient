package org.k3rnel.client.backend.entity;

import java.util.ArrayList;

import org.k3rnel.client.GameClient;
import org.k3rnel.client.backend.entity.Enums.Monstertype;

/**
 * Represents our player
 * @author shadowkanji
 *
 */
public class OurPlayer extends Player {
	private OurMonster [] m_monster;
	private ArrayList<PlayerItem> m_items;
    private int[] m_badges;
	private int m_money;
	private int m_trainerLvl = -1;
	private int m_breedingLvl = -1;
	private int m_fishingLvl = -1;
	private int m_coordinatingLvl = -1;
	
	/**
	 * Default constructor
	 */
	public OurPlayer() {
		m_monster = new OurMonster[6];
		m_items = new ArrayList<PlayerItem>();
		m_badges = new int[42];
		m_money = 0;
	}
	
	/**
	 * Constructor to be used if our player already exists
	 * @param original
	 */
	public OurPlayer(OurPlayer original) {
		m_badges = original.getBadges();
		m_monster = original.getMonsters();
		m_items = original.getItems();
		m_sprite = original.getSprite();
		m_username = original.getUsername();
		m_isAnimating = original.isAnimating();
		m_trainerLvl = original.getTrainerLevel();
		m_breedingLvl = original.getBreedingLevel();
		m_fishingLvl = original.getFishingLevel();
		m_coordinatingLvl = original.getCoordinatingLevel();
	}
	
	public void set(Player p) {
		m_x = p.getX();
		m_y = p.getY();
		m_svrX = p.getServerX();
		m_svrY = p.getServerY();
		m_sprite = p.getSprite();
		m_direction = p.getDirection();
		m_username = p.getUsername();
		m_id = p.getId();
		m_ours = p.isOurPlayer();
	}
	
	/**
	 * Returns the player's trainer level
	 * @return m_trainerLvl
	 */
	public int getTrainerLevel() {
		return m_trainerLvl;
	}
	
	/**
	 * Sets the player's trainer level
	 * @param i
	 */
	public void setTrainerLevel(int i) {
		m_trainerLvl = i;
	}
	
	/**
	 * Returns the player's breeding level
	 * @return m_breedingLvl
	 */
	public int getBreedingLevel() {
		return m_breedingLvl;
	}
	
	/**
	 * Sets the player's breeding level
	 * @param i
	 */
	public void setBreedingLevel(int i) {
		m_breedingLvl = i;
	}
	
	/**
	 * Returns the player's fishing level
	 * @return m_fishingLvl
	 */
	public int getFishingLevel() {
		return m_fishingLvl;
	}
	
	/**
	 * Sets the player's fishing level
	 * @param i
	 */
	public void setFishingLevel(int i) {
		m_fishingLvl = i;
	}
	
	/**
	 * Returns the player's coordinating level
	 * @return m_coordinatingLvl
	 */
	public int getCoordinatingLevel() {
		return m_coordinatingLvl;
	}
	
	/**
	 * Sets the player's coordinating level
	 * @param i
	 */
	public void setCoordinatingLevel(int i) {
		m_coordinatingLvl = i;
	}
	
	/**
	 * Returns our player's party
	 * @return
	 */
	public OurMonster[] getMonsters() {
		return m_monster;
	}
	
	/**
	 * Returns our player's bag
	 * @return
	 */
	public ArrayList<PlayerItem> getItems() {
		return m_items;
	}
	
	/**
	 * Adds an item to this player's bag (automatically handles if its in the bag already)
	 * @param number
	 * @param quantity
	 */
	public void addItem(int number, int quantity) {
		boolean exists = false;
		for(int i = 0; i < m_items.size(); i++) {
			if(m_items.get(i) != null && m_items.get(i).getNumber() == number) {
				m_items.get(i).setQuantity(m_items.get(i).getQuantity() + quantity);
				exists = true;
				if (GameClient.getInstance().getUi().getBag() != null)
					GameClient.getInstance().getUi().getBag().addItem(number, false);
			}
		}
		if(!exists){
			m_items.add(new PlayerItem(number, quantity));
			if (GameClient.getInstance().getUi().getBag() != null)
				GameClient.getInstance().getUi().getBag().addItem(number, true);
		}
	}
	
	/**
	 * Removes an item from this player's bag
	 * @param number
	 * @param quantity
	 */
	public void removeItem(int number, int quantity) {
		for(int i = 0; i < m_items.size(); i++) {
			if(m_items.get(i) != null && m_items.get(i).getNumber() == number) {
				if(m_items.get(i).getQuantity() - quantity > 0) {
					m_items.get(i).setQuantity(m_items.get(i).getQuantity() - quantity);
					if (GameClient.getInstance().getUi().getBag() != null)
						GameClient.getInstance().getUi().getBag().removeItem(number, false);
				} else {
					m_items.remove(i);
					if (GameClient.getInstance().getUi().getBag() != null)
						GameClient.getInstance().getUi().getBag().removeItem(number, true);
				}
				return;
			}
		}
	}
	
	/**
	 * Gets item quantity from bag. 
	 * @param number
	 */
	public int getItemQuantity(int number) {
		int quantity = 0;
		for(int i = 0; i < m_items.size(); i++) {
			if(m_items.get(i) != null && m_items.get(i).getItem().getId() == number) {
				quantity = m_items.get(i).getQuantity(); //Return quantity
				return quantity;
			} else {
				quantity = 0; //Player doesnt own item
			}
		}
		return quantity;
	}
	
	/**
	 * Updates a monster's stats
	 * @param i
	 * @param info
	 */
	public void updateMonster(int i, String [] info) {
		if(m_monster[i] != null) {
			m_monster[i].setCurHP(Integer.parseInt(info[0]));
			m_monster[i].setMaxHP(Integer.parseInt(info[1]));
			m_monster[i].setAtk(Integer.parseInt(info[2]));
			m_monster[i].setDef(Integer.parseInt(info[3]));
			m_monster[i].setSpeed(Integer.parseInt(info[4]));
			m_monster[i].setSpatk(Integer.parseInt(info[5]));
			m_monster[i].setSpdef(Integer.parseInt(info[6]));
		}
	}
	
	/**
	 * Sets a monster in this player's party
	 * @param i
	 * @param information
	 */
	public void setMonster(int i, String [] info) {
		if(info == null) {
			m_monster[i] = null;
		} else {
			/*
			 * Set sprite, name, gender and hp
			 */
			System.out.println(info.length);
			m_monster[i] = new OurMonster();
			m_monster[i].setName(info[1]);
			m_monster[i].setCurHP(Integer.parseInt(info[2]));
			m_monster[i].setGender(Integer.parseInt(info[3]));
			if(info[4].equalsIgnoreCase("0"))
				m_monster[i].setShiny(false);
			else
				m_monster[i].setShiny(true);
			m_monster[i].setSpriteNumber(Integer.parseInt(info[0]) + 1);
			m_monster[i].setMaxHP(Integer.parseInt(info[5]));
			/*
			 * Stats
			 */
			m_monster[i].setAtk(Integer.parseInt(info[6]));
			m_monster[i].setDef(Integer.parseInt(info[7]));
			m_monster[i].setSpeed(Integer.parseInt(info[8]));
			m_monster[i].setSpatk(Integer.parseInt(info[9]));
			m_monster[i].setSpdef(Integer.parseInt(info[10]));
			m_monster[i].setType1(Monstertype.valueOf(info[11]));
			if(info[12] != null && !info[12].equalsIgnoreCase("")) {
				m_monster[i].setType2(Monstertype.valueOf(info[12]));
			}
			m_monster[i].setExp(Integer.parseInt(info[13].substring(0, info[13].indexOf('.'))));
			m_monster[i].setLevel(Integer.parseInt(info[14]));
			m_monster[i].setAbility(info[15]);
			m_monster[i].setNature(info[16]);
			/*
			 * Moves
			 */
			String [] moves = new String[4];
			for(int j = 0; j < 4; j++) {
				if(j < info.length - 17 && info[j + 17] != null)
					moves[j] = info[j + 17];
				else
					moves[j] = "";
			}
			m_monster[i].setMoves(moves);
		}
	}
	
	/**
	 * Returns the player's money
	 * @return
	 */
	public int getMoney(){
		return m_money;
	}
	
	/**
	 * Sets the players money
	 * @param m
	 */
	public void setMoney(int m) {
		m_money = m;
	}
	
	/**
	 * Returns the player's badges
	 */
	public int [] getBadges(){
		return m_badges;
	}
	
	/**
	 * Swaps two monster
	 * @param Monster1
	 * @param Monster2
	 */
	public void swapMonster(int Monster1, int Monster2){
		OurMonster temp1 = m_monster[Monster1];
		m_monster[Monster1] = m_monster[Monster2];
		m_monster[Monster2] = temp1;
		GameClient.getInstance().getUi().refreshParty();
	}
	
	/**
	 * Initializes the player's badges
	 * @param str
	 */
	public void initBadges(String str) {
		m_badges = new int[str.length()];
		for (int i = 0; i < str.length(); i++) {
			try{
				m_badges[i] = Integer.valueOf(String.valueOf(str.charAt(i)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Adds a badge to the player
	 * @param index
	 */
	public void addBadge(int index) {
		m_badges[index] = 1;
	}

	@Override
	public int getType(){
		return 1;
	}
}
