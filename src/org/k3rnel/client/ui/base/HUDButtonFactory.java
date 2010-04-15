package org.k3rnel.client.ui.base;

import java.util.HashMap;

import org.k3rnel.client.GameClient;
import org.newdawn.slick.Font;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class HUDButtonFactory {
        private static HashMap<String, Image> rollovers;
        private static HashMap<String, Image> normals;
        private static HashMap<String, Image> downs;
        
        static {
                try {
                        //LoadingList.setDeferredLoading(true);
                        
                        normals = new HashMap<String, Image>();
                        rollovers = new HashMap<String, Image>();
                        downs = new HashMap<String, Image>();
                        
                        // -- Player Info Button --
                        normals.put("stats", getNormalImage("stats"));
                        rollovers.put("stats", getRolloverImage("stats"));
                        downs.put("stats", getDownImage("stats"));
                        
                        // -- Chat Buttons --
                        normals.put("chat", getNormalImage("chat"));
                        rollovers.put("chat", getRolloverImage("chat"));
                        downs.put("chat", getDownImage("chat"));

                        // -- Help Buttons --
                        normals.put("help", getNormalImage("help"));
                        rollovers.put("help", getRolloverImage("help"));
                        downs.put("help", getDownImage("help"));

                        // -- Requests Buttons --
                        normals.put("requests", getNormalImage("requests"));
                        rollovers.put("requests", getRolloverImage("requests"));
                        downs.put("requests", getDownImage("requests"));
                        
                        // -- Options Buttons --
                        normals.put("options", getNormalImage("options"));
                        rollovers.put("options", getRolloverImage("options"));
                        downs.put("options", getDownImage("options"));
                        
                        // -- Map Buttons --
                        normals.put("map", getNormalImage("map"));
                        rollovers.put("map", getRolloverImage("map"));
                        downs.put("map", getDownImage("map"));
                        
                        // -- Friends Buttons --
                        normals.put("friends", getNormalImage("friends"));
                        rollovers.put("friends", getRolloverImage("friends"));
                        downs.put("friends", getDownImage("friends"));
                        
                        font = GameClient.getFontSmall();
                        //LoadingList.setDeferredLoading(false);
                } catch (SlickException e) {
                    System.out.println("Failed to create HUD buttons");    
                	e.printStackTrace();
                        assert(false);
                }
        }
        
        private static Font font;
        
        private static Image getRolloverImage(String text) throws SlickException {
        	String respath = System.getProperty("res.path");
    		if(respath==null)
    			respath="";
            return new Image(respath+"res/ui/" + text + "_32x32.png", false);
        }
        
        private static Image getNormalImage(String text) throws SlickException {
        	String respath = System.getProperty("res.path");
    		if(respath==null)
    			respath="";
            return new Image(respath+"res/ui/" + text + "_32x32.png", false);
        }
        
        private static Image getDownImage(String text) throws SlickException {
        	String respath = System.getProperty("res.path");
    		if(respath==null)
    			respath="";
        	return new Image(respath+"res/ui/" + text + "Pressed_32x32.png", false);
        }
        
        public static ImageButton getButton(String text) {
                ImageButton out = new ImageButton(
                        normals.get(text.toLowerCase()),
                        rollovers.get(text.toLowerCase()),
                        downs.get(text.toLowerCase()));
                
                out.setFont(font);
                return out;
        }
}
