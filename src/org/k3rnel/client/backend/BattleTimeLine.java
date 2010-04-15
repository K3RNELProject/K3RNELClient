
package org.k3rnel.client.backend;

import java.util.ArrayList;
import java.util.List;

import org.k3rnel.client.GameClient;
import org.k3rnel.client.ui.BattleCanvas;
import org.k3rnel.client.ui.frames.BattleSpeechFrame;

/**
 * Handles Battle Events and arranges them for visual purposes.
 * 
 * @author ZombieBear
 */
public class BattleTimeLine {
  private final BattleSpeechFrame m_narrator;
  private BattleCanvas            m_canvas;
  List<String>                    m_translator = new ArrayList<String>();
  // Lines for REGEX needed for l10n
  String                          m_monsterName, m_move, m_trainer, m_foundItem;
  int                             m_newHPValue, m_exp, m_dmg, m_earnings,
      m_level, m_expRemaining;
  private boolean                 m_isBattling;

  /**
   * Default constructor
   */
  public BattleTimeLine() {
    m_translator = Translator.translate("_BATTLE");
    try {
      m_canvas = new BattleCanvas();
    } catch (Exception e) {
      e.printStackTrace();
    }
    m_narrator = new BattleSpeechFrame();
  }

  /**
   * Starts the TimeLine's components
   */
  public void startBattle() {
    m_canvas.startBattle();
    m_isBattling = true;
    GameClient.getInstance().getDisplay().add(m_canvas);
    GameClient.getInstance().getDisplay().add(m_narrator);
    GameClient.getInstance().getUi().nullSpeechFrame();
  }

  /**
   * Informs a monster fainted
   * 
   * @param monster
   */
  public void informFaintedMonster(String monster) {
    m_monsterName = monster;
    for (int i = 0; i < GameClient.getInstance().getOurPlayer().getMonsters().length; i++) {
      int counter = 0;
      if (GameClient.getInstance().getOurPlayer().getMonsters()[i] != null &&
    		  GameClient.getInstance().getOurPlayer().getMonsters()[i].getCurHP() <= 0) {
        counter++;
      }
      if (counter < i) {
        BattleManager.getInstance().getBattleWindow().showMonsterPane(true);
        addSpeech(m_translator.get(0));
        break;
      }
    }
  }

  /**
   * Informs a move was used
   * 
   * @param data
   */
  public void informMoveUsed(String[] data) {
    m_monsterName = data[0];
    m_move = data[1];
    addSpeech(m_translator.get(1));
  }

  /**
   * Informs that a move is requested
   */
  public void informMoveRequested() {
    BattleManager.getInstance().requestMoves();
    addSpeech(m_translator.get(2));
  }

  /**
   * Informs that a monster gained experience
   * 
   * @param data
   */
  public void informExperienceGained(String[] data) {
    m_monsterName = data[0];
    m_exp = (int) Double.parseDouble(data[1]);
    m_expRemaining = (int) Double.parseDouble(data[2]);
    addSpeech(m_translator.get(3));
  }

  /**
   * Informs that a monster's status was changed
   * 
   * @param data
   */
  public void informStatusChanged(int trainer, String[] data) {
    m_monsterName = data[0];
    m_canvas.setStatus(trainer, data[1]);
    if (data[1].equalsIgnoreCase("poison")) {
      addSpeech(m_translator.get(14));
    } else if (data[1].equalsIgnoreCase("freeze")) {
      addSpeech(m_translator.get(15));
    } else if (data[1].equalsIgnoreCase("burn")) {
      addSpeech(m_translator.get(16));
    } else if (data[1].equalsIgnoreCase("paralysis")) {
      addSpeech(m_translator.get(17));
    } else if (data[1].equalsIgnoreCase("sleep")) {
      addSpeech(m_translator.get(18));
    }
    if (trainer == 1)
      m_canvas.setMonsterRemainingImage(BattleManager.getInstance().getCurEnemyIndex(),
        "status");
  }

  /**
   * Informs that a monster's status was returned to normal
   * 
   * @param data
   */
  public void informStatusHealed(int trainer, String[] data) {
    m_monsterName = data[0];
    m_canvas.setStatus(trainer, "normal");
    addSpeech(m_translator.get(4));
  }

  /**
   * Informs that a monster was switched out.
   * 
   * @param data
   */
  public void informSwitch(String[] data) {
    m_trainer = data[0];
    m_monsterName = data[1];
    BattleManager.getInstance().switchMonster(Integer.parseInt(data[2]),
      Integer.parseInt(data[3]));
    m_canvas.drawOurMonster();
    m_canvas.drawOurInfo();
    m_canvas.drawEnemyMonster();
    m_canvas.drawEnemyInfo();
    addSpeech(m_translator.get(5));
  }

  /**
   * Informs that a monster switch is required
   */
  public void informSwitchRequested() {
    BattleManager.getInstance().getBattleWindow().showMonsterPane(true);
    addSpeech(m_translator.get(6));
  }

  public void informNoPP(String move) {
    m_move = move;
    BattleManager.getInstance().requestMoves();
    addSpeech(m_translator.get(21));
  }

  /**
   * Informs a change in health
   * 
   * @param data
   * @param i
   */
  public void informHealthChanged(String[] data, int i) {
    m_monsterName = data[0];
    m_dmg = Math.abs(Integer.parseInt(data[1]));
    if (i == 0) {
      m_monsterName = BattleManager.getInstance().getCurMonster().getName();
      m_newHPValue = BattleManager.getInstance().getCurMonster().getCurHP()
        + Integer.parseInt(data[1]);
      if (m_newHPValue < 0) {
        m_newHPValue = 0;
      } else if(m_newHPValue > BattleManager.getInstance().getCurMonster().getMaxHP())
      {
    	  m_newHPValue = BattleManager.getInstance().getCurMonster().getMaxHP();
      }
      BattleManager.getInstance().getCurMonster().setCurHP(m_newHPValue);
      m_canvas.updatePlayerHP(BattleManager.getInstance().getCurMonster()
        .getCurHP());
      data[0] = BattleManager.getInstance().getCurMonster().getName();
    } else {
      m_monsterName = BattleManager.getInstance().getCurEnemyMonster().getName();
      m_newHPValue = BattleManager.getInstance().getCurEnemyMonster().getCurHP()
        + Integer.parseInt(data[1]);
      if (m_newHPValue < 0) {
        m_newHPValue = 0;
      } else if(m_newHPValue > BattleManager.getInstance().getCurEnemyMonster().getMaxHP())
      {
    	  m_newHPValue = BattleManager.getInstance().getCurEnemyMonster().getMaxHP();
      }
      BattleManager.getInstance().getCurEnemyMonster().setCurHP(m_newHPValue);
      m_canvas.updateEnemyHP(BattleManager.getInstance().getCurEnemyMonster()
        .getCurHP());
      data[0] = BattleManager.getInstance().getCurEnemyMonster().getName();
    }

    if (i == 1 && m_newHPValue == 0) {
      m_canvas.setMonsterRemainingImage(BattleManager.getInstance().getCurEnemyIndex(),
        "fainted");
    }

    if (Integer.parseInt(data[1]) <= 0) {
      addSpeech(m_translator.get(7));
      addSpeech(m_translator.get(8));
    } else {
      addSpeech(m_translator.get(9));
    }
  }

  /**
   * Informs a victory on the player's side
   */
  public void informVictory() {
    m_trainer = GameClient.getInstance().getOurPlayer().getUsername();
    addSpeech(m_translator.get(10));
    BattleManager.getInstance().endBattle();
    m_isBattling = false;
  }

  /**
   * Informs a loss on the player's side
   */
  public void informLoss() {
    m_trainer = GameClient.getInstance().getOurPlayer().getUsername();
    addSpeech(m_translator.get(11));
    BattleManager.getInstance().endBattle();
    m_isBattling = false;
  }

  /**
   * Shows a custom message sent by the server
   * 
   * @param msg
   */
  public void showMessage(String msg) {
    addSpeech(msg);
  }

  /**
   * Informs if a run was successful
   * 
   * @param canRun
   */
  public void informRun(boolean canRun) {
    if (canRun) {
      addSpeech(m_translator.get(12));
      m_narrator.advance();
      BattleManager.getInstance().endBattle();
    } else {
      addSpeech(m_translator.get(13));
      m_narrator.advance();
      informMoveRequested();
    }
  }

  /**
   * Informs the player's earnings
   * 
   * @param money
   */
  public void informMoneyGain(int money) {
    m_earnings = money;
    addSpeech(m_translator.get(19));
  }

  /**
   * Informs the player's that the monster dropped an item
   * 
   * @param item
   */
  public void informItemDropped(String item) {
    m_foundItem = item;
    if (BattleManager.getInstance().isWild()) {
      m_monsterName = BattleManager.getInstance().getCurEnemyMonster().getName();
      addSpeech(m_translator.get(22));
    } else
      addSpeech(m_translator.get(23));
  }

  /**
   * Informs the player's earnings
   * 
   * @param money
   */
  public void informLevelUp(String monster, int level) {
    m_monsterName = monster;
    m_level = level;
    addSpeech(m_translator.get(20));
  }

  /**
   * Adds speech to the narrator and waits for it to be read before the next
   * action is taken
   * 
   * @param msg
   */
  public void addSpeech(String msg) {
    String newMsg = parsel10n(msg);
    m_narrator.addSpeech(parsel10n(newMsg));
    while (!m_narrator.getCurrentLine().equalsIgnoreCase(newMsg))
      ;
    while (!m_narrator.getAdvancedLine().equalsIgnoreCase(newMsg))
      ;
  }

  /**
   * Returns the battle speech
   * 
   * @return
   */
  public BattleSpeechFrame getBattleSpeech() {
    return m_narrator;
  }

  /**
   * Returns the battle canvas
   * 
   * @return
   */
  public BattleCanvas getBattleCanvas() {
    return m_canvas;
  }

  /**
   * Stops the timeline
   */
  public void endBattle() {
    m_canvas.stop();
    try {
      GameClient.getInstance().getDisplay().remove(m_canvas);
    } catch (Exception e) {
    }
    ;
    while (GameClient.getInstance().getDisplay().containsChild(m_canvas))
      ;
    try {
      GameClient.getInstance().getDisplay().remove(m_narrator);
    } catch (Exception e) {
    }
    ;
    while (GameClient.getInstance().getDisplay().containsChild(m_narrator))
      ;
  }

  /**
   * Uses regexes to create the appropriate battle messages for battle
   * 
   * @param line
   */
  public String parsel10n(String line) {
    if (line.contains("trainerName")) {
      line = line.replaceAll("trainerName", m_trainer);
    }
    if (line.contains("moveName")) {
      line = line.replaceAll("moveName", m_move);
    }
    if (line.contains("monsterName")) {
      line = line.replace("monsterName", m_monsterName);
    }
    if (line.contains("hpNum")) {
      line = line.replaceAll("hpNum", String.valueOf(m_newHPValue));
    }
    if (line.contains("expNum")) {
      line = line.replaceAll("expNum", String.valueOf(m_exp));
    }
    if (line.contains("damageNum")) {
      line = line.replaceAll("damageNum", String.valueOf(m_dmg));
    }
    if (line.contains("earningsNum")) {
      line = line.replaceAll("earningsNum", String.valueOf(m_earnings));
    }
    if (line.contains("levelNum")) {
      line = line.replaceAll("levelNum", String.valueOf(m_level));
    }
    if (line.contains("rewardItem")) {
      line = line.replaceAll("rewardItem", m_foundItem);
    }
    if (line.contains("expRemaining")) {
      line = line.replaceAll("expRemaining", String.valueOf(m_expRemaining));
    }
    return line;
  }

  public boolean isBattling() {
    return m_isBattling;
  }
}
