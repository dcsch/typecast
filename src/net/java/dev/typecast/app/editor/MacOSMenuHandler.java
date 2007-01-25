/*
 * MacOSMenuHandler.java
 *
 * Created on January 25, 2007, 3:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.java.dev.typecast.app.editor;

import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;
import com.apple.eawt.Application;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author david
 */
public class MacOSMenuHandler extends Application {
    
    private Main _app;
    
    /** Creates a new instance of MacOSMenuHandler */
    public MacOSMenuHandler(Main app) {
        _app = app;
        setEnabledPreferencesMenu(true);
        addApplicationListener(new AboutBoxHandler());
    }
    
    class AboutBoxHandler extends ApplicationAdapter {
        
        public void handleAbout(ApplicationEvent e) {
            _app.showAbout();
            e.setHandled(true);
        }
        
        public void handleOpenApplication(ApplicationEvent e) {
        }
        
        public void handleOpenFile(ApplicationEvent e) {
        }
        
        public void handlePreferences(ApplicationEvent e) {
        }
        
        public void handlePrintFile(ApplicationEvent e) {
        }
        
        public void handleQuit(ApplicationEvent e) {
            _app.close();
        }
    }
}
