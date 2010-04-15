package org.k3rnel.client.backend;

import java.util.HashMap;
import java.util.Map;

import org.k3rnel.client.GameClient;
import org.k3rnel.client.backend.entity.OurPlayer;
import org.k3rnel.client.backend.entity.OurMonster;
import org.k3rnel.client.backend.entity.Monster;
import org.k3rnel.client.ui.BattleWindow;

/**
 * Handles battle events and controls the battle window
 * 
 * @author ZombieBear
 * 
 */
public class BattleManager {
	private OurPlayer m_player;
	private BattleWindow m_battle;
	private OurMonster[] m_ourMonsters;
	private Monster[] m_enemyMonsters;
	private BattleTimeLine m_timeLine;
	private OurMonster m_curMonster;
	private int m_curMonsterIndex;
	private Monster m_curEnemyMonster;
	private int m_curEnemyIndex;
	private String m_enemy;
	private boolean m_isWild;
	private Map<Integer, String> m_ourStatuses = new HashMap<Integer, String>();
	private static BattleManager m_instance;
	private static boolean m_isBattling = false;
	private String m_curTrack;
	
	/**
	 * Default Constructor
	 */
	public BattleManager() {
		m_instance = this;
		m_battle = new BattleWindow("Battle!");
		m_timeLine = new BattleTimeLine();
		m_battle.setVisible(false);
		m_battle.setAlwaysOnTop(true);
	}

	/**
	 * Returns the instance
	 * @return
	 */
	public static BattleManager getInstance() {
		return m_instance;
	}
	
	/**
	 * Retrieves player data
	 */
	private void getPlayerData() {
		m_player = GameClient.getInstance().getOurPlayer();
		m_ourMonsters = m_player.getMonsters();
		for (int i = 0; i < 6; i++){
			if(m_ourMonsters[i].getCurHP() > 0){
				m_curMonsterIndex = i;
				m_curMonster = m_ourMonsters[i];
				break;
			}
		}
	}

	/**
	 * Sets the enemy's data 
	 */
	private void setEnemyData() {
		m_curEnemyMonster = m_enemyMonsters[0];
		m_curEnemyIndex = 0;
		try{
			m_timeLine.getBattleCanvas().drawEnemyMonster();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try{
			m_timeLine.getBattleCanvas().drawEnemyInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try{
			if(!m_isWild){
				m_timeLine.getBattleCanvas().showMonsterRemainingIndicators();
				m_timeLine.addSpeech(m_enemy + " sent out " + m_curEnemyMonster.getName());
			} else{
				m_timeLine.getBattleCanvas().hideMonsterRemainingIndicators();
				m_timeLine.addSpeech("A wild " + m_curEnemyMonster.getName() + " attacked!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets the enemy's name
	 * @param name
	 */
	public void setEnemyName(String name) {
		m_enemy = name;
	}

	/**
	 * Starts a new BattleWindow and BattleCanvas
	 * @param isWild
	 * @param monsterAmount
	 */
	public void startBattle(char isWild,
			int monsterAmount) {
		m_isBattling = true;

		GameClient.getInstance().getUi().hideHUD(true);
		if (isWild == '0'){
			setWild(false);
		} else {
			setWild(true);
		}
		m_battle.showAttack();
		m_battle.setVisible(true);
		m_enemyMonsters = new Monster[monsterAmount];
		getPlayerData();
		m_battle.disableMoves();
		updateMoves();
		updateMonsterPane();
		m_timeLine.startBattle();
        m_curTrack = GameClient.getSoundPlayer().m_trackName;
        System.out.println("Before Battle Music Name:" + m_curTrack);
		GameClient.getInstance().getDisplay().add(m_battle);
		GameClient.changeTrack("pvnpc");
	}
	
	/**
	 * Ends the battle
	 */
	public void endBattle() {
		GameClient.getInstance().getUi().hideHUD(false);
		m_timeLine.endBattle();
		m_battle.setVisible(false);
		m_isBattling = false;
		if (GameClient.getInstance().getDisplay().containsChild(m_battle.m_bag))
			GameClient.getInstance().getDisplay().remove(m_battle.m_bag);
		GameClient.getInstance().getDisplay().remove(m_battle);
		while (GameClient.getInstance().getDisplay().containsChild(m_battle));
		GameClient.getSoundPlayer().setTrackByLocation(GameClient.getInstance().getMapMatrix().getCurrentMap().getName());
		if (GameClient.getSoundPlayer().m_trackName == "pvnpc") {
			GameClient.getSoundPlayer().setTrack(m_curTrack);
		}
	}

	/**
	 * Returns the TimeLine
	 * @return m_timeLine
	 */
	public BattleTimeLine getTimeLine(){
		return m_timeLine;
	}
	
	/**
	 * Retrieves a monster's moves and updates the BattleWindow
	 * @param int monsterIndex
	 */
	public void updateMoves(int monsterIndex) {
		for (int i = 0; i < 4; i++){
			if (m_ourMonsters[monsterIndex].getMoves()[i] != null) {
				m_battle.m_moveButtons.get(i).setText(m_ourMonsters[monsterIndex].getMoves()[i]);
				m_battle.m_ppLabels.get(i).setText(m_ourMonsters[monsterIndex].getMoveCurPP()[i] + "/"
						+ m_ourMonsters[monsterIndex].getMoveMaxPP()[i]);
			} else {
				m_battle.m_moveButtons.get(i).setText("");
				m_battle.m_ppLabels.get(i).setText("");
			}
		}
	}
	
	/**
	 * Updates moves with the current monster
	 */
	public void updateMoves() {
		for (int i = 0; i < 4; i++){
			if (m_curMonster != null && m_curMonster.getMoves()[i] != null) {
				m_battle.m_moveButtons.get(i).setText(m_curMonster.getMoves()[i]);
				m_battle.m_ppLabels.get(i).setText(m_curMonster.getMoveCurPP()[i] + "/"
						+ m_curMonster.getMoveMaxPP()[i]);
			} else {
				m_battle.m_moveButtons.get(i).setText("");
				m_battle.m_ppLabels.get(i).setText("");
			}
		}
	}

	
	/**
	 * Switch a monster
	 * @param trainer
	 * @param monsterIndex
	 */
	public void switchMonster(int trainer, int monsterIndex){
		if (trainer == 0) {
			m_curMonster = GameClient.getInstance().getOurPlayer().getMonsters()[monsterIndex];
			m_curMonsterIndex = monsterIndex;
			updateMoves();
			updateMonsterPane();
			m_timeLine.getBattleCanvas().drawOurMonster();
			m_timeLine.getBattleCanvas().drawOurInfo();
		} else {
			m_curEnemyMonster = m_enemyMonsters[monsterIndex];
			m_curEnemyIndex = monsterIndex;
		}
	}
	
	/**
	 * Updates the monster pane
	 */
	public void updateMonsterPane() {
		for (int i = 0; i < 6; i++) {
			try{
				m_battle.m_monsterButtons.get(i).setText(m_ourMonsters[i].getName());
				m_battle.m_monsterInfo.get(i).setText("Lv: " + m_ourMonsters[i].getLevel() + " HP:"
						+ m_ourMonsters[i].getCurHP() + "/" + m_ourMonsters[i].getMaxHP());
				try{
					if (m_ourStatuses.containsKey(i) && m_battle.m_statusIcons.containsKey(m_ourStatuses.get(i))){
						m_battle.m_monsterStatus.get(i).setImage(m_battle.m_statusIcons.get(m_ourStatuses.get(i)));
					} else {
						m_battle.m_monsterStatus.get(i).setImage(null);
					}
				} catch (Exception e2){}
				if (m_ourMonsters[i].getCurHP() <= 0 || m_curMonsterIndex == i)
					m_battle.m_monsterButtons.get(i).setEnabled(false);
				else
					m_battle.m_monsterButtons.get(i).setEnabled(true);
			} catch (Exception e) {}
		}
	}

	/**
	 * Requests a move from the player
	 */
	public void requestMoves() {
		m_battle.enableMoves();
		m_battle.showAttack();
	}
	
	/**
	 * Gets the BattleWindow
	 * @return
	 */
	public BattleWindow getBattleWindow(){
		return m_battle;
	}
	
	/**
	 * Returns the player's active monster
	 */
	public OurMonster getCurMonster(){
		return m_curMonster;
	}
	
	/**
	 * Returns the active monster's index in party
	 * @return
	 */
	public int getCurMonsterIndex(){
		return m_curMonsterIndex;
	}

	/**
	 * Returns the active enemy monster's index in party
	 * @return
	 */
	public int getCurEnemyIndex(){
		return m_curEnemyIndex;
	}
	
	/**
	 * Returns the enemy's active monster or the wild monster
	 */
	public Monster getCurEnemyMonster(){
		return m_curEnemyMonster;
	}
	
	/**
	 * Adds an enemy monster
	 * @param index
	 * @param name
	 * @param level
	 * @param gender
	 * @param maxHP
	 * @param curHP
	 * @param spriteNum
	 * @param isShiny
	 */
	public void setEnemyMonster(int index,
			String name,
			int level,
			int gender,
			int maxHP,
			int curHP,
			int spriteNum,
			boolean isShiny){

		if (curHP != 0)
			m_timeLine.getBattleCanvas().setMonsterRemainingImage(index, "normal");
		else
			m_timeLine.getBattleCanvas().setMonsterRemainingImage(index, "fainted");
		
		m_enemyMonsters[index] = new Monster();
		m_enemyMonsters[index].setName(name);
		m_enemyMonsters[index].setLevel(level);
		m_enemyMonsters[index].setGender(gender);
		m_enemyMonsters[index].setMaxHP(maxHP);
		m_enemyMonsters[index].setCurHP(curHP);
		m_enemyMonsters[index].setSpriteNumber(spriteNum + 1);
		m_enemyMonsters[index].setShiny(isShiny);
		
		if ((index + 1) == m_enemyMonsters.length)
			setEnemyData();
	}

	/**
	 * Sets wild battle
	 * @param m_isWild
	 */
	public void setWild(boolean m_isWild) {
		this.m_isWild = m_isWild;
		m_battle.setWild(m_isWild);
	}
	
	/**
	 * Returns a boolean determining whether the monster is wild
	 * @return m_isWild
	 */
	public boolean isWild() {
		return m_isWild;
	}
	
	/**
	 * Returns a list of our monsters who are affected by statuses
	 * @return a list of our monsters who are affected by statuses
	 */
	public Map<Integer, String> getOurStatuses(){
		return m_ourStatuses;
	}
	
	/**
	 * Returns true if a battle is in progress
	 * @return true if a battle is in progress
	 */
	public static boolean isBattling(){
		return m_isBattling;
	}
}
