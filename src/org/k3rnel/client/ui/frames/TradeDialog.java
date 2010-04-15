package org.k3rnel.client.ui.frames;

import mdes.slick.sui.Button;
import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;
import mdes.slick.sui.TextField;
import mdes.slick.sui.ToggleButton;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;
import mdes.slick.sui.event.MouseAdapter;
import mdes.slick.sui.event.MouseEvent;

import org.k3rnel.client.GameClient;
import org.k3rnel.client.backend.entity.OurMonster;
import org.k3rnel.client.backend.entity.Monster;
import org.k3rnel.client.ui.base.ConfirmationDialog;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.loading.LoadingList;

/**
 * The trade interface
 * @author ZombieBear
 *
 */
public class TradeDialog extends Frame {
	private ToggleButton[] m_ourMonsters;
	private ToggleButton[] m_theirMonsters;
	private MonsterInfoDialog[] m_theirMonsterInfo;
	private Button m_makeOfferBtn;
	private Button m_tradeBtn;
	private Button m_cancelBtn;
	private Label m_ourCashLabel;
	private Label m_theirMoneyOffer;
	private TextField m_ourMoneyOffer;
	private ActionListener m_offerListener;
	private ConfirmationDialog m_confirm;
	private int m_offerNum = 6;
	private boolean	m_madeOffer = false;
	private boolean	m_receivedOffer = false;

	
	/**
	 * Default constructor
	 */
	public TradeDialog(String trainerName){
		getContentPane().setX(getContentPane().getX() - 1);
		getContentPane().setY(getContentPane().getY() + 1);
		initGUI();
		setVisible(true);
		setTitle("Trade with " + trainerName);
		setCenter();
		GameClient.getInstance().getDisplay().add(this);
	}

	/**
	 * Sends the offer to the server
	 */
	private void makeOffer(){
		if(m_ourMoneyOffer.getText().equals("")) m_ourMoneyOffer.setText("0");
		
		if (!m_ourMoneyOffer.getText().equals("")){
			GameClient.getInstance().getPacketGenerator().writeTcpMessage("To" + m_offerNum + "," + 
					m_ourMoneyOffer.getText());
		} else {
			GameClient.getInstance().getPacketGenerator().writeTcpMessage("To" + m_offerNum + ",0");
		}
			
		m_makeOfferBtn.setText("Cancel Offer");
		for (int i = 0; i < 6; i++){
			m_ourMonsters[i].setGlassPane(true);
		}
		
		m_madeOffer = true;
		if(m_receivedOffer) m_tradeBtn.setEnabled(true);
	}
	
	/**
	 * Cancels a sent offer
	 */
	private void cancelOffer(){
		GameClient.getInstance().getPacketGenerator().writeTcpMessage("Tc");
		m_makeOfferBtn.setText("Make Offer");
		for (int i = 0; i < 6; i++){
			m_ourMonsters[i].setGlassPane(false);
		}
		m_tradeBtn.setEnabled(false);
	}
	
	/**
	 * Allows only one pokemon to be toggled
	 * @param btnIndex
	 */
	private void untoggleOthers(int btnIndex){		
		for (int i = 0; i < 6; i++){
			if (i != btnIndex){
				m_ourMonsters[i].setSelected(false);
				m_ourMonsters[i].setBorderRendered(false);
			} else {
				m_ourMonsters[btnIndex].setBorderRendered(true);
				m_ourMonsters[btnIndex].setSelected(true);
			}
		}
	}
	
	/**
	 * Performs the trade
	 */
	private void performTrade(){
		GameClient.getInstance().getPacketGenerator().writeTcpMessage("Tt");
		System.out.println("Trade complete");
		this.setVisible(false);
	}
	
	/**
	 * Cancels the trade
	 */
	private void cancelTrade(){
		ActionListener yes = new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				GameClient.getInstance().getPacketGenerator().writeTcpMessage("TC");
				m_confirm.setVisible(false);
				getDisplay().remove(m_confirm);
				m_confirm = null;
				setVisible(false);
				GameClient.getInstance().getUi().stopTrade();
				System.out.println("Trade Cancelled");
			}
		
		};
		ActionListener no = new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				getDisplay().remove(m_confirm);
				m_confirm = null;
			}
		};
		m_confirm = new ConfirmationDialog("Are you sure you want to cancel the trade?", yes, no);
	}
	
	/**
	 * Receives an offer
	 * @param index
	 * @param cash
	 */
	public void getOffer(int index, int cash){
		for (int i = 0; i < 6; i++){
			m_theirMonsters[i].setBorderRendered(false);
			m_theirMonsters[i].setSelected(false);
		}
		if (index < 6)
		{
			m_theirMonsters[index].setSelected(true);
			m_theirMonsters[index].setBorderRendered(true);
		}
		m_theirMoneyOffer.setText("$" + cash);
		m_receivedOffer  = true;
		if(m_madeOffer) m_tradeBtn.setEnabled(true);
	}
	
	/**
	 * Updates the UI when the other player cancels his/her offer
	 */
	public void cancelTheirOffer(){
		for (int i = 0; i < 6; i++){
			m_theirMonsters[i].setSelected(false);
		}
		m_theirMoneyOffer.setText("$0");
		m_tradeBtn.setEnabled(false);
	}
	
	/**
	 * Initializes the interface
	 */
	private void initGUI(){
		m_ourMonsters = new ToggleButton[6];
		m_theirMonsters = new ToggleButton[6];
		m_theirMonsterInfo = new MonsterInfoDialog[6];
		m_ourMoneyOffer = new TextField();
		m_makeOfferBtn = new Button();
		m_tradeBtn = new Button();
		m_cancelBtn = new Button();
		
		//Action Listener for the offer button
		m_offerListener = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (m_makeOfferBtn.getText().equalsIgnoreCase("Make Offer")){
					if(m_ourMoneyOffer.getText().equals("") || m_ourMoneyOffer.getText() == null){
						m_ourMoneyOffer.setText("0");
					}
					makeOffer();
				}
				else {
					cancelOffer();
				}
			}
		};
		
		int x = 10, y = 10;
		for (int i = 0; i < 6; i++){
			//Show Our Monstermon for Trade
			m_ourMonsters[i] = new ToggleButton();
			m_ourMonsters[i].setSize(32, 32);
			m_ourMonsters[i].setVisible(true);
			try {
				m_ourMonsters[i].setImage(GameClient.getInstance().getOurPlayer()
						.getMonsters()[i].getIcon());
			} catch (NullPointerException e){
				m_ourMonsters[i].setGlassPane(true);
			}
			
			getContentPane().add(m_ourMonsters[i]);
			if (i < 3)
				m_ourMonsters[i].setLocation(x, y);
			else
				m_ourMonsters[i].setLocation(x + 40, y);
			
			//Show the Other Character's Monstermon for Trade
			m_theirMonsters[i] = new ToggleButton();
			m_theirMonsters[i].setSize(32, 32);
			m_theirMonsters[i].setVisible(true);
			m_theirMonsters[i].setGlassPane(true);
			getContentPane().add(m_theirMonsters[i]);

			//Item Location Algorithms
			if (i < 3)
				m_theirMonsters[i].setLocation(x + 178, y);
			else
				m_theirMonsters[i].setLocation(x + 218, y);
		
			if (i == 2)
				y = 10;
			else
				y += 40;
		}
		m_ourMonsters[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (m_offerNum == 0){
					m_offerNum = 6;
					untoggleOthers(6);
				} else {
					m_offerNum = 0;
					untoggleOthers(0);
				}
				m_makeOfferBtn.setEnabled(true);

			};
		});
		m_ourMonsters[1].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (m_offerNum == 1){
					m_offerNum = 6;
					untoggleOthers(6);
				} else {
					m_offerNum = 1;
					untoggleOthers(1);
				}
				m_makeOfferBtn.setEnabled(true);

			};
		});
		m_ourMonsters[2].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (m_offerNum == 2){
					m_offerNum = 6;
					untoggleOthers(6);
				} else {
					m_offerNum = 2;
					untoggleOthers(2);
				}
				m_makeOfferBtn.setEnabled(true);

			};
		});
		m_ourMonsters[3].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (m_offerNum == 3){
					m_offerNum = 6;
					untoggleOthers(6);
				} else {
					m_offerNum = 3;
					untoggleOthers(3);
				}
				m_makeOfferBtn.setEnabled(true);

			};
		});
		m_ourMonsters[4].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (m_offerNum == 4){
					m_offerNum = 6;
					untoggleOthers(6);
				} else {
					m_offerNum = 4;
					untoggleOthers(4);
				}
				
				m_makeOfferBtn.setEnabled(true);
			};
		});
		m_ourMonsters[5].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (m_offerNum == 5){
					m_offerNum = 6;
					untoggleOthers(6);
				} else {
					m_offerNum = 5;
					untoggleOthers(5);
				}
				
				m_makeOfferBtn.setEnabled(true);
			};
		});
		
		//UI Buttons
		m_makeOfferBtn.setText("Make Offer");
		m_makeOfferBtn.setSize(90, 30);
		m_makeOfferBtn.setLocation(90, 10);
		m_makeOfferBtn.setEnabled(false);
		m_makeOfferBtn.addActionListener(m_offerListener);
		getContentPane().add(m_makeOfferBtn);
		
		m_tradeBtn.setText("Trade");
		m_tradeBtn.setEnabled(false);
		m_tradeBtn.setSize(90, 30);
		m_tradeBtn.setLocation(90, 50);
		m_tradeBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				ActionListener yes = new ActionListener(){
					public void actionPerformed(ActionEvent evt) {
						performTrade();
					 	getDisplay().remove(m_confirm);
						m_confirm = null;
						setVisible(false);
					}
				
				};
				ActionListener no = new ActionListener(){
					public void actionPerformed(ActionEvent evt) {
						m_confirm.setVisible(false);
						getDisplay().remove(m_confirm);
						m_confirm = null;
						setVisible(true);
					}
				
				};
				m_confirm = new ConfirmationDialog("Are you sure you want to trade?", yes, no);
				setVisible(false);
			}
		});
		getContentPane().add(m_tradeBtn);
		
		m_cancelBtn.setText("Cancel Trade");
		m_cancelBtn.setSize(90, 30);
		m_cancelBtn.setLocation(90, 90);
		m_cancelBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				cancelTrade();
			};
		});
		getContentPane().add(m_cancelBtn);
		
		//Our money trade info
		m_ourCashLabel = new Label("$");
		m_ourCashLabel.pack();
		m_ourCashLabel.setLocation(10, 130);
		getContentPane().add(m_ourCashLabel);
		m_ourMoneyOffer = new TextField();
		m_ourMoneyOffer.setSize(60, 20);
		m_ourMoneyOffer.setLocation(20, 128);
		getContentPane().add(m_ourMoneyOffer);
		//Their money trade info
		m_theirMoneyOffer = new Label("$0");
		m_theirMoneyOffer.pack();
		m_theirMoneyOffer.setLocation(188, 130);
		getContentPane().add(m_theirMoneyOffer);
		
		//Window Settings
		getTitleBar().remove(getCloseButton());
		setSize(270,178);
		setResizable(false);
	}
	
	/**
	 * Centers the frame
	 */
	public void setCenter() {
		int height = (int) GameClient.getInstance().getDisplay().getHeight();
		int width = (int) GameClient.getInstance().getDisplay().getWidth();
		int x = (width / 2) - ((int)getWidth()/2);
		int y = (height / 2) - ((int)getHeight()/2);
		this.setLocation(x, y);
	}
	
	/**
	 * Adds a pokemon to the other player's side
	 * @param data
	 */
	public void addMonster(int index, String[] data) {
        final int j = index;
		LoadingList.setDeferredLoading(true);
		int ic = Integer.parseInt(data[0]);
		if(ic > 389) {
			ic -= 2;
		} else {
			ic ++;
		}
        try {
        	m_theirMonsters[index].setImage(new Image(Monster.getIconPathByIndex(ic)));
        } catch (SlickException e){}
        LoadingList.setDeferredLoading(false);
        
        // Load pokemon data
        OurMonster tempMonster = new OurMonster().initTradeMonster(data);
        
        // Create a pokemon information panel with stats
        // for informed decisions during trade
        m_theirMonsterInfo[index] = new MonsterInfoDialog(tempMonster);
        m_theirMonsterInfo[index].setVisible(false);
        m_theirMonsterInfo[index].setAlwaysOnTop(true);
        m_theirMonsterInfo[index].setLocation(m_theirMonsters[index].getX(),
        		m_theirMonsters[index].getY() + 32);
        GameClient.getInstance().getDisplay().add(m_theirMonsterInfo[index]);
        m_theirMonsters[index].addMouseListener(new MouseAdapter() {
        	@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				m_theirMonsterInfo[j].setVisible(true);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				m_theirMonsterInfo[j].setVisible(false);
			}
        });
	}
}
