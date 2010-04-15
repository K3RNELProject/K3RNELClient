package org.k3rnel.client.ui.frames;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import mdes.slick.sui.Button;
import mdes.slick.sui.Container;
import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;
import mdes.slick.sui.event.MouseAdapter;
import mdes.slick.sui.event.MouseEvent;
import mdes.slick.sui.skin.simple.SimpleArrowButton;

import org.k3rnel.client.GameClient;
import org.k3rnel.client.backend.FileLoader;
import org.k3rnel.client.backend.Translator;
import org.k3rnel.client.backend.entity.OurMonster;
import org.k3rnel.client.ui.base.ProgressBar;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.loading.LoadingList;

/**
 * Party information frame
 * @author ZombieBear
 *
 */
@SuppressWarnings("deprecation")
public class PartyInfoDialog extends Frame {
	Container[] m_container;
	Label[] m_monsterBackground;
	Label[] m_hpBar;
	Label[] m_monsterIcon;
	Label[] m_monsterName;
	Label[] m_level;
	ProgressBar[] m_hp;
	Button[] m_switchUp;
	Button[] m_switchDown;

	OurMonster[] m_monsters;

	/**
	 * Default constructor
	 * 
	 * @param ourMonsters
	 * @param out
	 */
	public PartyInfoDialog(OurMonster[] ourMonsters) {
		m_monsters = ourMonsters;
		allocateVariables();
		loadImages(ourMonsters);
		/* ContentPane location is moved here instead of in initGUI so that
		 * if/when initGui is recalled the ConentPane doesn't move.
		 */
		getContentPane().setX(getContentPane().getX() - 1);
		getContentPane().setY(getContentPane().getY() + 1);
		initGUI();
	}
	
	private void allocateVariables()
	{
		m_switchDown = new Button[6];
		m_switchUp = new Button[6];
		m_hp = new ProgressBar[6];
		m_level = new Label[6];
		m_monsterName = new Label[6];
		m_monsterIcon = new Label[6];
		m_hpBar = new Label[6];
		m_monsterBackground = new Label[6];
		m_container = new Container[6];
	}

	/**
	 * Initializes interface
	 */
	public void initGUI() {
		InputStream f;
		int y = -8;
		this.getTitleBar().getCloseButton().setVisible(false);
		this.setFont(GameClient.getFontSmall());
		this.setBackground(new Color(0, 0, 0, 85));
		this.setForeground(new Color(255, 255, 255));
		for (int i = 0; i < 6; i++) {
			final int j = i;
			m_container[i] = new Container();
			m_container[i].setSize(170, 42);
			m_container[i].setVisible(true);
			m_container[i].setLocation(2, y+10);
			m_container[i].setBackground(new Color(0, 0, 0, 0));
			System.out.println("Y: "+y);
			getContentPane().add(m_container[i]);
			y += 41;
			m_container[i].setOpaque(true);
			String respath = System.getProperty("res.path");
			if(respath==null)
				respath="";
			try {
				Label tempLabel = new Label(); 
				if (i ==0) {
					f = FileLoader.loadFile(respath+"res/ui/party_info/partyActive.png");
					tempLabel = new Label(new Image(f, respath+"res/ui/party_info/partyActive.png", false));
				} else {
					f = FileLoader.loadFile(respath+"res/ui/party_info/partyInactive.png");
					tempLabel = new Label(new Image(f, respath+"res/ui/party_info/partyInactive.png", false));
				}
				tempLabel.setSize(170, 42);
				tempLabel.setY(-4);
				m_container[i].add(tempLabel);
			} catch (Exception e) {e.printStackTrace();}
			
			try{
				f = FileLoader.loadFile(respath+"res/ui/party_info/HPBar.png");
				m_hpBar[i] = new Label(new Image(f, respath+"res/ui/party_info/HPBar.png", false));
				m_hpBar[i].setSize(98, 11);
				m_hpBar[i].setVisible(false);
				m_container[i].add(m_hpBar[i]);
			} catch (Exception e) {e.printStackTrace();}
			
			try {
				m_container[i].add(m_monsterBackground[i]);
				m_monsterBackground[i].setLocation(4, 4);
				m_monsterName[i].setFont(GameClient.getFontSmall());
				m_monsterName[i].setForeground(new Color(255, 255, 255));
				m_monsterName[i].addMouseListener(new MouseAdapter() {

					@Override
					public void mouseReleased(MouseEvent e) {
						super.mouseReleased(e);
						MonsterInfoDialog info = new MonsterInfoDialog(m_monsters[j]);
						info.setAlwaysOnTop(true);
						info.setLocationRelativeTo(null);
						getDisplay().add(info);
					}

					@Override
					public void mouseEntered(MouseEvent e) {
						super.mouseEntered(e);
						m_monsterName[j].setForeground(new Color(255, 215, 0));
					}

					@Override
					public void mouseExited(MouseEvent e) {
						super.mouseExited(e);
						m_monsterName[j].setForeground(new Color(255, 255, 255));
					}

				});
				m_container[i].add(m_monsterIcon[i]);
				m_monsterIcon[i].setLocation(2, 3);
				m_container[i].add(m_monsterName[i]);
				m_monsterName[i].setLocation(42, 5);
				m_container[i].add(m_level[i]);
				m_level[i].setFont(GameClient.getFontSmall());
				m_level[i].setForeground(new Color(255, 255, 255));
				m_level[i].setLocation(m_monsterName[i].getX()
						+ m_monsterName[i].getWidth() + 10, m_monsterName[i].getY());
				m_container[i].add(m_hp[i]);
				m_hp[i].setSize(72, 5);
				m_hp[i].setLocation(m_hpBar[i].getX() + 23, m_hpBar[i].getY() + 3);
				if (i != 0) {
					m_switchUp[i] = new SimpleArrowButton(
							SimpleArrowButton.FACE_UP);
					m_switchUp[i].addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							GameClient.getInstance().getPacketGenerator().writeTcpMessage("s" + String.valueOf(j)
									+ ","+String.valueOf(j - 1));
							//reinitialize the gui
							getContentPane().removeAll();
							allocateVariables();
							loadImages(m_monsters);
							initGUI();
						}
					});
					m_switchUp[i].setHeight(16);
					m_switchUp[i].setWidth(16);
					m_container[i].add(m_switchUp[i]);
				}
				if (i != 5) {
					m_switchDown[i] = new SimpleArrowButton(
							SimpleArrowButton.FACE_DOWN);
					m_switchDown[i].addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							GameClient.getInstance().getPacketGenerator().writeTcpMessage("s" + String.valueOf(j)
									+ ","+String.valueOf(j + 1));
							//reinitialize the gui
							getContentPane().removeAll();
							allocateVariables();
							loadImages(m_monsters);
							initGUI();
						}
					});

					m_switchDown[i].setHeight(16);
					m_switchDown[i].setWidth(16);
					m_switchDown[i].setX(24);
					m_container[i].add(m_switchDown[i]);
				}
			} catch (NullPointerException e) {
				//e.printStackTrace();
			}
		}
		update(m_monsters);
		this.getTitleBar().setGlassPane(true);
		this.setResizable(false);
		this.setSize(170, 270);
		List<String> translated = Translator.translate("_GUI");
		this.setTitle(translated.get(0));
	}

	/**
	 * Loads necessary images
	 * @param m_monsters
	 */
	public void loadImages(OurMonster[] m_monsters) {
		LoadingList.setDeferredLoading(true);
		InputStream f;
		for (int i = 0; i < 6; i++) {
			m_monsterIcon[i] = new Label();
			m_monsterBackground[i] = new Label();
			m_monsterName[i] = new Label();

			m_level[i] = new Label();
			m_hp[i] = new ProgressBar(0, 0);
			m_hp[i].setForeground(Color.green);

			m_monsterIcon[i].setSize(32, 32);

			m_monsterName[i].pack();
			String respath = System.getProperty("res.path");
			if(respath==null)
				respath="";
			try {
				f = FileLoader.loadFile(respath+"res/ui/Pokeball.gif");
				m_monsterBackground[i].setImage(new Image(f, respath+"res/ui/Pokeball.gif", false));
				m_monsterBackground[i].setSize(30, 30);
			} catch (SlickException e) {
				System.out.println("Couldn't load pokeball");
			} catch (FileNotFoundException e) {
				System.out.println("Couldn't load pokeball");
			}
			try {
				List<String> translated = Translator.translate("_GUI");
				if (m_monsters[i] != null) {
					m_level[i].setText(translated.get(32)
							+ String.valueOf(m_monsters[i].getLevel()));
					m_level[i].pack();
					m_monsterName[i].setText(m_monsters[i].getName());
					m_monsterIcon[i].setImage(m_monsters[i].getIcon());
					m_hp[i].setMaximum(m_monsters[i].getMaxHP());
					m_hp[i].setForeground(Color.green);
					m_hp[i].setValue(m_monsters[i].getCurHP());
					if (m_monsters[i].getCurHP() > m_monsters[i].getMaxHP() / 2) {
						m_hp[i].setForeground(Color.green);
					} else if (m_monsters[i].getCurHP() < m_monsters[i].getMaxHP() / 2
							&& m_monsters[i].getCurHP() > m_monsters[i].getMaxHP() / 3) {
						m_hp[i].setForeground(Color.orange);
					} else if (m_monsters[i].getCurHP() < m_monsters[i].getMaxHP() / 3) {
						m_hp[i].setForeground(Color.red);
					}
					m_monsterIcon[i].setImage(m_monsters[i].getIcon());
					m_monsterIcon[i].setSize(32, 32);
					m_monsterName[i].setText(m_monsters[i].getName());
					m_monsterName[i].pack();
					m_level[i].setText(translated.get(32)
							+ String.valueOf(m_monsters[i].getLevel()));
					m_level[i].pack();
				} else {
					m_hp[i].setVisible(false);
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
		LoadingList.setDeferredLoading(false);
	}

	/**
	 * Updates info
	 * 
	 * @param pokes
	 */
	public void update(OurMonster[] pokes) {
		m_monsters = pokes;
		List<String> translated = Translator.translate("_GUI");
		LoadingList.setDeferredLoading(true);
		for (int i = 0; i < 6; i++) {
			try {
				if (pokes[i] != null) {
					m_hp[i].setMaximum(pokes[i].getMaxHP());
					m_hp[i].setValue(pokes[i].getCurHP());
					if (pokes[i].getCurHP() > pokes[i].getMaxHP() / 2) {
						m_hp[i].setForeground(Color.green);
					} else if (pokes[i].getCurHP() < pokes[i].getMaxHP() / 2
							&& pokes[i].getCurHP() > pokes[i].getMaxHP() / 3) {
						m_hp[i].setForeground(Color.orange);
					} else if (pokes[i].getCurHP() < pokes[i].getMaxHP() / 3) {
						m_hp[i].setForeground(Color.red);
					}
					m_monsterIcon[i].setImage(pokes[i].getIcon());
					m_monsterName[i].setText(pokes[i].getName());
					m_monsterName[i].pack();
					m_level[i].setText(translated.get(32)
							+ String.valueOf(pokes[i].getLevel()));
					m_level[i].pack();
					m_level[i].setLocation(m_monsterName[i].getX()
							+ m_monsterName[i].getWidth() + 10, 5);

					m_monsterBackground[i].setLocation(4, 4);
					m_monsterIcon[i].setLocation(2, 3);
					m_monsterName[i].setLocation(45, 5);
					m_hpBar[i].setLocation(45, m_monsterName[i].getY()
							+ m_monsterName[i].getHeight() + 3);
					m_hp[i].setLocation(m_hpBar[i].getX() + 23, 
							m_hpBar[i].getY() + 3);
					m_hpBar[i].setVisible(true);
					m_hp[i].setVisible(true);
					if (i != 0)
						m_switchUp[i].setVisible(true);
					if (i != 5)
						m_switchDown[i].setVisible(true);
				} else {
					if (i != 0)
						m_switchUp[i].setVisible(false);
					if (i != 5)
						m_switchDown[i].setVisible(false);
					m_hpBar[i].setVisible(false);
					m_hp[i].setVisible(false);
					m_level[i].setText("");
					m_level[i].pack();
					m_monsterIcon[i].setImage(null);
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
		LoadingList.setDeferredLoading(false);
	}

	/**
	 * Sets sprite number
	 * 
	 * @param x
	 * @return
	 */
	public int setSpriteNumber(int x) {
		int i = 0;
		if (x <= 385) {
			i = x + 1;
		} else if (x <= 388) {
			i = 386;
		} else if (x <= 414) {
			i = x - 2;
		} else if (x <= 416) {
			i = 413;
		} else {
			i = x - 4;
		}
		return i;
	}
}
