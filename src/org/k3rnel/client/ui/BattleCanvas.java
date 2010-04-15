package org.k3rnel.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mdes.slick.sui.Container;
import mdes.slick.sui.Label;

import org.k3rnel.client.GameClient;
import org.k3rnel.client.backend.BattleManager;
import org.k3rnel.client.ui.base.ProgressBar;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.loading.LoadingList;

/**
 * Canvas for drawing the battle and it's animations
 * 
 * @author ZombieBear
 * 
 */
public class BattleCanvas extends Container {
	private ProgressBar playerHP;
	private ProgressBar enemyHP;
	private Label bgPic;
	private Label playerMonster;
	private Label enemyMonster;
	private Label playerNameLabel;
	private Label enemyNameLabel;
	private Label playerDataBG;
	private Label enemyDataBG;
	private Label playerHPBar;
	private Label enemyHPBar;
	private Label playerLv;
	private Label enemyLv;
	private Label playerStatus;
	private Label enemyStatus;
	private List<Label> m_enemyMonstersRemaining = new ArrayList<Label>();
	private HashMap<String, Image> m_statusIcons = new HashMap<String, Image>();
	private HashMap<String, Image> m_monsterRemainingIcons = new HashMap<String, Image>();
	// Image Loading stuff
	private String m_path = "res/battle/";
	
	/**
	 * Default constructor
	 */
	public BattleCanvas(){
		String respath = System.getProperty("res.path");
		if(respath==null)
			respath="";
		m_path = respath+m_path;
		setSize(257, 144);
		setVisible(true);
		loadImages();
		startRemainingMonsterIndicators();
		loadStatusIcons();
	}
	
	/**
	 * Draws our monstermon
	 */
	public void drawOurMonster(){
		//TODO: Animate!
		try {
			remove(playerMonster);
		} catch (Exception e) {}
		playerMonster = new Label();
		playerMonster = new Label(BattleManager.getInstance().getCurMonster().getBackSprite());
		playerMonster.setSize(80, 80);
		playerMonster.setLocation(20, 76);
		add(playerMonster);
	}

	/**
	 * Starts a battle
	 */
	public void startBattle() {
		initComponents();
		positionCanvas();
		drawBackground();
		drawOurMonster();
		drawOurInfo();
	}
	
	/**
	 * Initializes the monster indicator icons for trainer battles
	 */
	public void startRemainingMonsterIndicators(){
		m_enemyMonstersRemaining.clear();
		int x = 1;
		for (int i = 0; i < 6; i++){
			m_enemyMonstersRemaining.add(new Label());
			m_enemyMonstersRemaining.get(i).setSize(14, 14);
			m_enemyMonstersRemaining.get(i).setImage(m_monsterRemainingIcons.get("empty"));
			m_enemyMonstersRemaining.get(i).setLocation(125 + 14 * x + x * 5, 3);
			x++;
		}
	}
	
	/**
	 * Loads images that can't be loading on startBattle()
	 */
	public void loadImages(){
        LoadingList.setDeferredLoading(true);
		try {
			enemyHPBar = new Label(new Image( m_path + "HPBar.png", false));
			playerHPBar = new Label(new Image( m_path + "HPBar.png", false));
		} catch (SlickException e) {}
		try{
			m_monsterRemainingIcons.put("empty", new Image(m_path + "ballempty" + ".png", false));
			m_monsterRemainingIcons.put("normal", new Image(m_path + "ballnormal" + ".png", false));
			m_monsterRemainingIcons.put("status", new Image(m_path + "ballstatus" + ".png", false));
			m_monsterRemainingIcons.put("fainted", new Image(m_path + "ballfainted" + ".png", false));
		} catch (SlickException e) {e.printStackTrace();}
		LoadingList.setDeferredLoading(false);
		enemyHPBar.setSize(98, 11);
		playerHPBar.setSize(98, 11);
	}
	
	/**
	 * Draws the enemy's Monster
	 */
	public void drawEnemyMonster(){
		//TODO: Animate!
		try{
			try {
				remove(enemyMonster);
			} catch (Exception e) {}
			enemyMonster = new Label (BattleManager.getInstance()
					.getCurEnemyMonster().getSprite());
			enemyMonster.setSize(80, 80);
			enemyMonster.setLocation(150, 21);
			add(enemyMonster);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initComponents() {
		playerHP = new ProgressBar(0,0);
		enemyHP = new ProgressBar(0,0);
		bgPic = new Label();
		playerMonster = new Label();
		enemyMonster = new Label();
		playerNameLabel = new Label();
		enemyNameLabel = new Label();
		playerDataBG = new Label();
		enemyDataBG = new Label();
		playerLv = new Label();
		enemyLv = new Label();
		playerStatus = new Label();
		enemyStatus = new Label();
	}
	
	/**
	 * Draw our poke's information
	 */
	public void drawOurInfo(){
		// display player's data
		playerNameLabel.setFont(GameClient.getFontSmall());
		playerNameLabel.setForeground(Color.white);
		playerNameLabel.setText(BattleManager.getInstance()
				.getCurMonster().getName());
        playerNameLabel.setSize(GameClient.getFontSmall().getWidth(playerNameLabel
        		.getText()), GameClient.getFontSmall().getHeight(playerNameLabel
        				.getText()));
        playerNameLabel.setLocation(playerDataBG.getX() + 30, playerDataBG.getY() + 7);
        
        playerLv.setText("Lv:" + BattleManager.getInstance()
        		.getCurMonster().getLevel());
        playerLv.setFont(GameClient.getFontSmall());
        playerLv.setForeground(Color.white);
        playerLv.setSize(GameClient.getFontSmall().getWidth(playerLv.getText()),
        		GameClient.getFontSmall().getHeight(playerLv.getText()));
        playerLv.setLocation(playerDataBG.getX() + playerDataBG.getWidth() 
        		- playerLv.getWidth() - 5, playerDataBG.getY() + 7);
        
        playerStatus.setSize(30, 12);
        playerStatus.setLocation(playerNameLabel.getX(), 125);
        
        add(playerNameLabel);
        add(playerLv);
        add(playerStatus);
        initPlayerHPBar();
	}
	
	/**
	 * Draw our enemy poke's information
	 */
	public void drawEnemyInfo(){
        //display enemy's data
		enemyNameLabel.setText(BattleManager.getInstance()
				.getCurEnemyMonster().getName());
		enemyNameLabel.setFont(GameClient.getFontSmall());
		enemyNameLabel.setForeground(Color.white);
		enemyNameLabel.setSize(GameClient.getFontSmall().getWidth(enemyNameLabel.getText()),
				GameClient.getFontSmall().getHeight(enemyNameLabel.getText()));
        enemyNameLabel.setLocation(enemyDataBG.getX() + 15, enemyDataBG.getY() + 7);

        enemyLv.setText("Lv: " + BattleManager.getInstance()
        		.getCurEnemyMonster().getLevel());
        enemyLv.setFont(GameClient.getFontSmall());
        enemyLv.setForeground(Color.white);
        enemyLv.setSize(GameClient.getFontSmall().getWidth(enemyLv.getText()),
        		GameClient.getFontSmall().getHeight(enemyLv.getText()));
        enemyLv.setLocation(enemyDataBG.getX() + enemyDataBG.getWidth() - enemyLv.getWidth()
        		- 25, enemyDataBG.getY() + 7);

        enemyStatus.setSize(30, 12);
        enemyStatus.setLocation(105, 40);
        
        add(enemyNameLabel);
        add(enemyLv);
        add(enemyStatus);
        initEnemyHPBar();
	}
	
	/**
	 * Updates the HP bar for the player's poke
	 * @param newValue
	 */
	public void updatePlayerHP(int newValue) {              
        playerHP.setValue(newValue);
        
        if(BattleManager.getInstance().getCurMonster().getCurHP() 
        		> BattleManager.getInstance().getCurMonster().getMaxHP() / 2){
                playerHP.setForeground(Color.green);
        }
        else if(BattleManager.getInstance().getCurMonster().getCurHP() 
        		< BattleManager.getInstance().getCurMonster().getMaxHP() / 2 
        		&& BattleManager.getInstance().getCurMonster().getCurHP() 
        		> BattleManager.getInstance().getCurMonster().getMaxHP() / 3){
                playerHP.setForeground(Color.orange);
        }
        else if(BattleManager.getInstance().getCurMonster().getCurHP() 
        		< BattleManager.getInstance().getCurMonster().getMaxHP() / 3){
                playerHP.setForeground(Color.red);
        }
	}

	/**
	 * Updates the HP bar for the opponent's poke
	 * @param newValue
	 */
	public void updateEnemyHP(int newValue) {
		enemyHP.setValue(newValue);

		if(BattleManager.getInstance().getCurEnemyMonster().getCurHP() 
				> BattleManager.getInstance().getCurEnemyMonster().getMaxHP() / 2){
			enemyHP.setForeground(Color.green);
		}
		else if(BattleManager.getInstance().getCurEnemyMonster().getCurHP() 
				< BattleManager.getInstance().getCurEnemyMonster().getMaxHP() / 2 
				&& BattleManager.getInstance().getCurEnemyMonster().getCurHP() 
				> BattleManager.getInstance().getCurEnemyMonster().getMaxHP() / 3){
			enemyHP.setForeground(Color.orange);
		}
		else if(BattleManager.getInstance().getCurEnemyMonster().getCurHP() 
				< BattleManager.getInstance().getCurEnemyMonster().getMaxHP() / 3){
			enemyHP.setForeground(Color.red);
		}
	}

	
	/**
	 * Draws the background
	 */
	public void drawBackground(){
		LoadingList.setDeferredLoading(true);
		String respath = System.getProperty("res.path");
		if(respath == null || respath.equals("null"))
			respath="";
		try {
			bgPic = new Label(new Image(respath+"res/ui/DP_darkgrass.png", false));
		} catch (SlickException e) {
			e.printStackTrace();
		} try {
			playerDataBG = new Label(new Image(respath+"res/battle/singlePlayerBox3.png", false));
		} catch (SlickException e) {
			e.printStackTrace();
		} try {
			enemyDataBG = new Label(new Image(respath+"res/battle/singleEnemyBox3.png", false));
		} catch (SlickException e) {
			e.printStackTrace();
		}
		LoadingList.setDeferredLoading(false);
		add(bgPic);
		add(playerDataBG);
		add(enemyDataBG);
		bgPic.setBounds(0, 0, 256, 144);
		playerDataBG.setBounds(82, 96, 170, 48);
		enemyDataBG.setBounds(-10, 10, 170, 48);
	}
	
	/**
	 * Starts the enemy HP Bar
	 */
	public void initEnemyHPBar(){
		// show enemy hp bar
        enemyHP = new ProgressBar(0, (int)BattleManager.getInstance()
        		.getCurEnemyMonster().getMaxHP());
        enemyHP.setSize(72, 5);
        
        if(BattleManager.getInstance().getCurEnemyMonster().getCurHP() 
        		> BattleManager.getInstance().getCurEnemyMonster().getMaxHP() / 2){
                enemyHP.setForeground(Color.green);
        }
        else if(BattleManager.getInstance().getCurEnemyMonster().getCurHP() 
        		< BattleManager.getInstance().getCurEnemyMonster().getMaxHP() / 2 
        		&& BattleManager.getInstance().getCurEnemyMonster().getCurHP() 
        		> BattleManager.getInstance().getCurEnemyMonster().getMaxHP() / 3){
                enemyHP.setForeground(Color.orange);
        }
        else if(BattleManager.getInstance().getCurEnemyMonster().getCurHP() 
        		< BattleManager.getInstance().getCurEnemyMonster().getMaxHP() / 3){
                enemyHP.setForeground(Color.red);
        }
        updateEnemyHP(BattleManager.getInstance().getCurEnemyMonster().getCurHP());
        
        enemyHPBar.setLocation(enemyNameLabel.getX(), 40); 
		enemyHP.setLocation(enemyHPBar.getX() + 23, enemyHPBar.getY() + 3);
		
		add(enemyHPBar);
		add(enemyHP);
	}
	
	/**
	 * Starts the player's HP bar
	 */
	public void initPlayerHPBar(){
		// show hp bar
        playerHP = new ProgressBar(0, (int)BattleManager.getInstance()
        		.getCurMonster().getMaxHP());
        playerHP.setSize(72, 5);

        if(BattleManager.getInstance().getCurMonster().getCurHP() 
        		> BattleManager.getInstance().getCurMonster().getMaxHP() / 2){
                playerHP.setForeground(Color.green);
        }
        else if(BattleManager.getInstance().getCurMonster().getCurHP() 
        		< BattleManager.getInstance().getCurMonster().getMaxHP() / 2 
        		&& BattleManager.getInstance().getCurMonster().getCurHP() 
        		> BattleManager.getInstance().getCurMonster().getMaxHP() / 3){
                playerHP.setForeground(Color.orange);
        }
        else if(BattleManager.getInstance().getCurMonster().getCurHP() 
        		< BattleManager.getInstance().getCurMonster().getMaxHP() / 3){
                playerHP.setForeground(Color.red);
        }

        updatePlayerHP(BattleManager.getInstance().getCurMonster().getCurHP());
        
        playerHPBar.setLocation(playerLv.getX() + playerLv.getWidth() - 98, 125); 
		playerHP.setLocation(playerHPBar.getX() + 23, playerHPBar.getY() + 3);
		
		add(playerHPBar);
		add(playerHP);
	}
	
	/**
	 * Sets the status image
	 * @param trainer
	 * @param status
	 */
	public void setStatus(int trainer, String status){
		if (trainer == 0){
			// The player's monster
			if (status != "normal") {
				BattleManager.getInstance().getOurStatuses().put(
						BattleManager.getInstance().getCurMonsterIndex(), status);
				playerStatus.setImage(m_statusIcons.get(status));
			} else {
				BattleManager.getInstance().getOurStatuses().remove(
						BattleManager.getInstance().getCurMonsterIndex());
				playerStatus.setImage(null);
			}
		} else {
			// The enemy's monster
			if (status != "normal") {
				enemyStatus.setImage(m_statusIcons.get(status));
			} else {
				enemyStatus.setImage(null);
			}
		}
	}
	
	/**
	 * Loads the status icons
	 */
	public void loadStatusIcons(){
		LoadingList.setDeferredLoading(true);
		try{
			m_statusIcons.put("Poison", new Image( m_path + "PSN" + ".png", false));
		} catch (SlickException e) {e.printStackTrace();} try{
			m_statusIcons.put("Sleep", new Image( m_path + "SLP" + ".png", false));
		} catch (SlickException e) {e.printStackTrace();} try{
			m_statusIcons.put("Freze", new Image( m_path + "FRZ" + ".png", false));
		} catch (SlickException e) {e.printStackTrace();} try{
			m_statusIcons.put("Burn", new Image( m_path + "BRN" + ".png", false));
		} catch (SlickException e) {e.printStackTrace();} try{
			m_statusIcons.put("Paralysis", new Image(m_path + "PAR" + ".png", false));
		} catch (SlickException e) {e.printStackTrace();}
		LoadingList.setDeferredLoading(false);
	}
	
	/**
	 * Shows monster remaining indicators
	 */
	public void showMonsterRemainingIndicators(){
		for (Label l : m_enemyMonstersRemaining){
			if (!containsChild(l))
				add(l);
		}
	}
	
	/**
	 * Hides monster remaining indicators
	 */
	public void hideMonsterRemainingIndicators(){
		for (Label l : m_enemyMonstersRemaining){
			l.setImage(m_monsterRemainingIcons.get("empty"));
			try{
				remove(l);
			} catch (Exception e){}
		}
	}
	
	/**
	 * Sets the image for the monster remaining indicators
	 * @param i
	 * @param key
	 */
	public void setMonsterRemainingImage(int i, String key){
		m_enemyMonstersRemaining.get(i).setImage(m_monsterRemainingIcons.get(key));
	}
	
	/**
	 * Centers the battle window
	 */
	public void positionCanvas() {
		float y = BattleManager.getInstance().getBattleWindow().getY() 
		 	+ BattleManager.getInstance().getBattleWindow().getTitleBar().getHeight();
		float x = BattleManager.getInstance().getBattleWindow().getX() + 1;
		setLocation(x, y);
	}
	
	/**
	 * Stops the canvas
	 */
	public void stop() {
		this.removeAll();
		playerHP = null;
		enemyHP = null;
		bgPic = null;
		playerMonster = null;
		enemyMonster = null;
		playerNameLabel = null;
		enemyNameLabel = null;
		playerDataBG = null;
		enemyDataBG = null;
		playerLv = null;
		enemyLv = null;
		playerStatus = null;
		enemyStatus = null;
		hideMonsterRemainingIndicators();
	}
}
