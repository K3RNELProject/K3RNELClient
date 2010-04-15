package org.k3rnel.client.ui.frames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mdes.slick.sui.Button;
import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;
import mdes.slick.sui.event.MouseEvent;
import mdes.slick.sui.event.MouseListener;

import org.k3rnel.client.GameClient;
import org.k3rnel.client.backend.FileLoader;
import org.k3rnel.client.backend.ItemDatabase;
import org.k3rnel.client.backend.entity.Item;
import org.k3rnel.client.backend.entity.PlayerItem;
import org.k3rnel.client.ui.base.ConfirmationDialog;
import org.k3rnel.client.ui.base.ListBox;
import org.lwjgl.util.Timer;
import org.newdawn.slick.Image;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.loading.LoadingList;

/**
 * The shop dialog
 * @author Nushio
 * @author ZombieBear
 */
public class ShopDialog extends Frame {
	private Button[] m_categoryButtons;
	private Label[] m_categoryLabels;
	private Button[] m_itemButtons;
	private Button[] m_sellButton;
	private Label[] m_itemPics;
	private Label[] m_itemLabels;
	private Label[] m_itemStockPics;
	public Timer m_timer;
	private ListBox m_sellList;
	List<Item> m_items;
	private Button m_cancel;
	private Button m_buy;
	private Button m_sell;
	private List<Integer> m_merch = new ArrayList<Integer>();
	private HashMap<Integer, Integer> m_stock;

	/**
	 * Constructor
	 * @param stock
	 */
	public ShopDialog(HashMap<Integer, Integer> stock) {
		for (Integer i : stock.keySet()){
			m_merch.add(i);
		}
		m_stock = stock;
		m_timer = new Timer();
		m_timer.pause();
		setCenter();
		getContentPane().setX(getContentPane().getX() - 1);
		getContentPane().setY(getContentPane().getY() + 1);
		initGUI();
	}
	
	/**
	 * Updates the stock
	 * @param stock
	 */
	public void updateStock(HashMap<Integer, Integer> stock) {
		m_stock = stock;
		initGUI();
	}
	
	/**
	 * Called when a category for item to buy is selected
	 * @param name
	 */
	public void categoryClicked(int name) {
		m_items = new ArrayList<Item>();
		switch(name){
		case 0:
			for (int i : m_merch){
				if (PlayerItem.getItem(i).getCategory().equals("Capture"))
					m_items.add(PlayerItem.getItem(i));
			}
			initItems();
			break;
		case 1:
			for (int i : m_merch){
				if (PlayerItem.getItem(i).getCategory().equals("Potions"))
					m_items.add(PlayerItem.getItem(i));
			}
			initItems();
			break;
		case 2:
			for (int i : m_merch){
				if (PlayerItem.getItem(i).getCategory().equals("Medicine"))
					m_items.add(PlayerItem.getItem(i));
			}
			initItems();
			break;
		case 3:
			for (int i : m_merch){
				if (PlayerItem.getItem(i).getCategory().equals("Field") ||
						PlayerItem.getItem(i).getCategory().equals("Move"))
					m_items.add(PlayerItem.getItem(i));
			}
			initItems();
			break;
		}
	}
	
	/**
	 * Initialises the gui when first opened
	 */
	public void initGUI(){
		m_buy = new Button("Buy");
		m_buy.setLocation(0,0);
		m_buy.setSize(150,320);
		m_buy.setFont(GameClient.getFontLarge());
		m_buy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buyGUI();
			}
		});
		getContentPane().add(m_buy);
		
		m_sell = new Button("Sell");
		m_sell.setLocation(151,0);
		m_sell.setSize(150,320);
		m_sell.setFont(GameClient.getFontLarge());
		m_sell.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sellGUI();
			}
		});
		getContentPane().add(m_sell);
		
		m_cancel = new Button("Cancel");
		m_cancel.setSize(300,56);
		m_cancel.setLocation(0,321);
		m_cancel.setFont(GameClient.getFontLarge());
		m_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelled();
			}
		});
		getContentPane().add(m_cancel);
		this.getResizer().setVisible(false);
		getCloseButton().addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						cancelled();
					}
				}
		);
		setTitle("PokeShop");
		setResizable(false);
		setHeight(400);
		setWidth(301);
		pack();
		setVisible(true);
	}
	
	/**
	 * Displays the selling item gui
	 */
	public void sellGUI() {
		m_cancel.setVisible(false);
		String[] m_items = new String[GameClient.getInstance().getOurPlayer().getItems().size()];
		for (int i = 0; i < m_items.length; i++) {
			m_items[i] = GameClient.getInstance().getOurPlayer().getItems().get(i).getItem().getName();
		}
		
		m_sellList = new ListBox(m_items);
		
		m_sellButton = new Button[1];
		m_sellButton[0] = new Button("Sell");
		m_sellButton[0].setFont(GameClient.getFontLarge());
		m_sellButton[0].setSize(getWidth(), 35);
		m_sellButton[0].setLocation(0, m_cancel.getY() - 35);
		m_sellButton[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				try{
				final ConfirmationDialog m_confirm = new ConfirmationDialog("Are you sure you want to sell " 
						+ m_sellList.getSelectedName() + " for $" + (ItemDatabase.getInstance().getItem(
								m_sellList.getSelectedName()).getPrice() / 2) + "?");
				m_confirm.addYesListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						GameClient.getInstance().getPacketGenerator().writeTcpMessage("Ss" + ItemDatabase.getInstance()
								.getItem(m_sellList.getSelectedName()).getId() + ",");
						GameClient.getInstance().getDisplay().remove(m_confirm);
					}
				});
				m_confirm.addNoListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						GameClient.getInstance().getDisplay().remove(m_confirm);
					}
				});
				} catch (Exception e2) {e2.printStackTrace();}
			}
		});
		
		m_sellList.setSize(getWidth(), m_sellButton[0].getY());
		// Start the UI
		m_buy.setVisible(false);
		m_sell.setVisible(false);
		
		getContentPane().add(m_sellList);
		getContentPane().add(m_sellButton[0]);
	}
	
	/**
	 * Displays the item buying GUI
	 */
	public void buyGUI() {
		m_buy.setVisible(false);
		m_sell.setVisible(false);
		m_cancel.setVisible(false);
		m_categoryButtons = new Button[4];
		m_categoryLabels = new Label[4];
		
		m_categoryButtons[0] = new Button(" ");
		LoadingList.setDeferredLoading(true);
		String respath = System.getProperty("res.path");
		if(respath==null)
			respath="";
		try{
			//TODO: Add filepath
			m_categoryButtons[0].setImage(new Image(FileLoader.loadFile(respath+"res/ui/shop/pokeball.png"), 
					respath+"", false));
		}catch(Exception e){
			e.printStackTrace();
		}
		LoadingList.setDeferredLoading(false);
		m_categoryButtons[0].setSize(150, 160);
		m_categoryButtons[0].setLocation(0,0);
		m_categoryButtons[0].setFont(GameClient.getFontLarge());
		m_categoryButtons[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				categoryClicked(0);
			}
		});
		getContentPane().add(m_categoryButtons[0]);
		
		m_categoryLabels[0] = new Label("Capture");
		m_categoryLabels[0].setLocation(0,0);
		m_categoryLabels[0].setGlassPane(true);
		m_categoryLabels[0].setZIndex(1000);
		m_categoryLabels[0].setSize(150,10);
		m_categoryLabels[0].setFont(GameClient.getFontLarge());
		getContentPane().add(m_categoryLabels[0]);
		
		m_categoryButtons[1] = new Button(" ");
		LoadingList.setDeferredLoading(true);
		try{
			//TODO: Add filepath
			m_categoryButtons[1].setImage(new Image(FileLoader.loadFile(respath+"res/ui/shop/potion.png"),
					respath+"res/ui/shop/potion.png", false));
		}catch(Exception e){
			e.printStackTrace();
		}
		LoadingList.setDeferredLoading(false);
		m_categoryButtons[1].setSize(150, 160);
		m_categoryButtons[1].setLocation(151, 0);
		m_categoryButtons[1].setFont(GameClient.getFontLarge());
		m_categoryButtons[1].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				categoryClicked(1);
			}
		});
		getContentPane().add(m_categoryButtons[1]);
		
		m_categoryLabels[1] = new Label("Potions");
		m_categoryLabels[1].setLocation(151,0);
		m_categoryLabels[1].setGlassPane(true);
		m_categoryLabels[1].setFont(GameClient.getFontLarge());
		m_categoryLabels[1].setZIndex(1000);
		m_categoryLabels[1].setSize(150,10);
		getContentPane().add(m_categoryLabels[1]);
		
		m_categoryButtons[2] = new Button(" ");
		LoadingList.setDeferredLoading(true);
		try{
			//TODO: Add filepath
			m_categoryButtons[2].setImage(new Image(FileLoader.loadFile(respath+"res/ui/shop/status.png"),
					respath+"res/ui/shop/status.png", false));
		}catch(Exception e){
			e.printStackTrace();
		}
		LoadingList.setDeferredLoading(false);
		m_categoryButtons[2].setSize(150, 160);
		m_categoryButtons[2].setLocation(0,161);
		m_categoryButtons[2].setFont(GameClient.getFontLarge());
		m_categoryButtons[2].setEnabled(true);
		m_categoryButtons[2].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				categoryClicked(2);
			}
		});
		getContentPane().add(m_categoryButtons[2]);
		
		m_categoryLabels[2] = new Label("Status Healers");
		m_categoryLabels[2].setLocation(0,161);
		m_categoryLabels[2].setGlassPane(true);
		m_categoryLabels[2].setFont(GameClient.getFontLarge());
		m_categoryLabels[2].setZIndex(1000);
		m_categoryLabels[2].setSize(150,10);
		getContentPane().add(m_categoryLabels[2]);
		
		m_categoryButtons[3] = new Button(" ");
		LoadingList.setDeferredLoading(true);
		try{
			//TODO: Add filepath
			m_categoryButtons[3].setImage(new Image(FileLoader.loadFile(respath+"res/ui/shop/field.png"),
					respath+"res/ui/shop/field.png", false));
		}catch(Exception e){
			e.printStackTrace();
		}
		LoadingList.setDeferredLoading(false);
		m_categoryButtons[3].setSize(150, 160);
		m_categoryButtons[3].setLocation(151,161);
		m_categoryButtons[3].setFont(GameClient.getFontLarge());
		m_categoryButtons[3].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				categoryClicked(3);
			}
		});
		getContentPane().add(m_categoryButtons[3]);
		
		m_categoryLabels[3] = new Label("Field Tools");
		m_categoryLabels[3].setLocation(151,161);
		m_categoryLabels[3].setGlassPane(true);
		m_categoryLabels[3].setFont(GameClient.getFontLarge());
		m_categoryLabels[3].setZIndex(1000);
		m_categoryLabels[3].setSize(150,10);
		getContentPane().add(m_categoryLabels[3]);

		
		m_cancel = new Button("Cancel");
		m_cancel.setSize(300,56);
		m_cancel.setLocation(0,321);
		m_cancel.setFont(GameClient.getFontLarge());
		m_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for(int i=0;i<m_categoryLabels.length;i++){
					getContentPane().remove(m_categoryLabels[i]);
				}
				for(int i=0;i<m_categoryButtons.length;i++){
					getContentPane().remove(m_categoryButtons[i]);
				}
				getContentPane().remove(m_cancel);
				initGUI();
			}
		});
		getContentPane().add(m_cancel);
		
		this.getResizer().setVisible(false);
		getCloseButton().addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						cancelled();
					}
				});
		setTitle("PokeMart");
		setResizable(false);
		setHeight(400);
		setWidth(300);
		pack();
		setVisible(true);
	}
	
	private void initItems() {
		setCenter();
		for(int i=0;i<m_categoryButtons.length;i++){
			getContentPane().remove(m_categoryButtons[i]);
		}
		for(int i=0;i<m_categoryLabels.length;i++){
			getContentPane().remove(m_categoryLabels[i]);
		}
		getContentPane().remove(m_cancel);
		m_itemButtons = new Button[m_items.size()];
		m_itemPics = new Label[m_items.size()];
		m_itemLabels = new Label[m_items.size()];
		m_itemStockPics = new Label[m_items.size()];
		for(int i = 0;i<m_items.size();i++){
			final int itemChosen = m_items.get(i).getId();
			final int buttonNumber = i;
			m_itemButtons[i] = new Button("");
			m_itemButtons[i].setSize(300, 50);
			if(i>0)
				m_itemButtons[i].setLocation(0,(m_itemButtons[i-1].getY()+51));
			else
				m_itemButtons[i].setLocation(0,0);
			m_itemButtons[i].setZIndex(0);
			m_itemButtons[i].setFont(GameClient.getFontLarge());
			m_itemButtons[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					itemClicked(itemChosen);
				}
			});
			getContentPane().add(m_itemButtons[i]);
			String respath = System.getProperty("res.path");
			if(respath==null)
				respath="";
			try{
				LoadingList.setDeferredLoading(true);
				m_itemPics[i] = new Label(new Image(FileLoader.loadFile(respath+"res/items/24/" + m_items.get(i).getId() + ".png"),
						respath+"res/items/24/" + m_items.get(i).getId() + ".png", false));
				LoadingList.setDeferredLoading(false);
				m_itemPics[i].setGlassPane(true);
				m_itemPics[i].setSize(32,32);
				if(i>0)
					m_itemPics[i].setLocation(0,(m_itemPics[i-1].getY()+51));
				else
					m_itemPics[i].setLocation(0,12);
				m_itemPics[i].setZIndex(1000);
				getContentPane().add(m_itemPics[i]);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			try{
				LoadingList.setDeferredLoading(true);
				String stock = "empty";
				if(m_stock.get(m_items.get(i).getId()) >= 100 || m_items.size() == -1){
					stock = "full";
				} else if (m_stock.get(m_items.get(i).getId()) < 100 && m_stock.get(m_items.get(i).getId()) >= 60){
					stock = "half";
				} else if (m_stock.get(m_items.get(i).getId()) < 60 && m_stock.get(m_items.get(i).getId()) >= 30){
					stock = "halfempty";
				}
				m_itemStockPics[i] = new Label(new Image(FileLoader.loadFile(respath+"res/ui/shop/"+stock+".png"),
						respath+"res/ui/shop/"+stock+".png", false));
				LoadingList.setDeferredLoading(false);
				m_itemStockPics[i].setGlassPane(true);
				m_itemStockPics[i].setSize(32,32);
				if(i>0)
					m_itemStockPics[i].setLocation(260,(m_itemStockPics[i-1].getY()+51));
				else
					m_itemStockPics[i].setLocation(260,12);
				m_itemStockPics[i].setZIndex(1000);
				getContentPane().add(m_itemStockPics[i]);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			m_itemLabels[i] = new Label(m_items.get(i).getName()+" - $"+m_items.get(i).getPrice());
			m_itemLabels[i].setSize(200,50);
			m_itemLabels[i].setFont(GameClient.getFontLarge());
			m_itemLabels[i].setZIndex(1200);
			m_itemLabels[i].setHorizontalAlignment(0);
			m_itemLabels[i].addMouseListener(new MouseListener() {
				boolean entered = false;
				public void mouseReleased(MouseEvent arg0) {
					if(entered)
						itemClicked(itemChosen);
					m_itemButtons[buttonNumber].setEnabled(true);
				}
				
				public void mousePressed(MouseEvent arg0) {
					m_itemButtons[buttonNumber].setEnabled(false);
				}
				
				public void mouseMoved(MouseEvent arg0) {}
				
				public void mouseExited(MouseEvent arg0) {
					entered = false;
				}
				
				public void mouseEntered(MouseEvent arg0) {
					entered = true;		
				}
				
				public void mouseDragged(MouseEvent arg0) {}
			});
			if(i>0)
				m_itemLabels[i].setLocation(30,(m_itemLabels[i-1].getY()+51));
			else
				m_itemLabels[i].setLocation(30,0);
			m_itemLabels[i].updateAppearance();
			getContentPane().add(m_itemLabels[i]);
		}
		
		m_cancel = new Button("Cancel");
		m_cancel.setSize(300,40);
		m_cancel.setLocation(0,336);
		m_cancel.setFont(GameClient.getFontLarge());
		m_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for(int i=0;i<m_itemButtons.length;i++){
					getContentPane().remove(m_itemButtons[i]);
				}
				for(int i=0;i<m_itemPics.length;i++){
					getContentPane().remove(m_itemPics[i]);
				}
				for(int i=0;i<m_itemLabels.length;i++){
					getContentPane().remove(m_itemLabels[i]);
				}
				for(int i=0;i<m_itemStockPics.length;i++){
					getContentPane().remove(m_itemStockPics[i]);
				}
				getContentPane().remove(m_cancel);
				buyGUI();
			}
		});
		getContentPane().add(m_cancel);
		
		this.getResizer().setVisible(false);
		getCloseButton().addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						cancelled();
					}
				});
		
		setTitle("Potions");
		setResizable(false);
		setHeight(400);
		setWidth(300);
		pack();
//		for (int i = 0; i < m_itemButtons.length; i++) {
//			if (i > 0)
//				m_itemButtons[i].setLocation(0,m_itemButtons[i-1].getY() + m_itemButtons[i-1].getHeight());
//			m_itemButtons[i].setSize(getWidth(),(getHeight() - 60)/ m_itemButtons.length);
//		}
		setVisible(true);
	}
	
	public void cancelled() {
		GameClient.getInstance().getPacketGenerator().writeTcpMessage("Sf");
		GameClient.getInstance().getUi().stopShop();
	}
	
	public void itemClicked(int itemid) {
		GameClient.getInstance().getPacketGenerator().writeTcpMessage("Sb"+itemid+",1");
	}
	public void pack() {
		
	}
	
	/**
	 * Centers the frame
	 */
	public void setCenter() {
		int height = (int) GameClient.getInstance().getDisplay().getHeight();
		int width = (int) GameClient.getInstance().getDisplay().getWidth();
		int x = (width / 2) - 130;
		int y = (height / 2) - 238;
		this.setBounds(x, y, 259, 475);
	}
	
	@Override
	public void update(GUIContext container, int delta) {
		Timer.tick();
		if (m_timer.getTime() >= 3) {
			try {
				GameClient.getInstance().getUi().getNPCSpeech().advance();
				m_timer.pause();
			} catch (Exception e) {}
		}
	}
}

