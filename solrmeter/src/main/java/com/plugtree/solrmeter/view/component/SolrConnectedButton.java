package com.plugtree.solrmeter.view.component;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.JButton;

import org.apache.log4j.Logger;

import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import com.plugtree.solrmeter.model.exception.PingNotConfiguredException;
import com.plugtree.solrmeter.model.operation.PingOperation;

/**
 * This is a regular Swing button with the only difference that it will try to validate that the server
 * where the ping operation is pointing is alive.
 * @author tflobbe
 *
 */
public class SolrConnectedButton extends JButton {
	
	private static final long serialVersionUID = -3480106669296431011L;
	
	private static final String DEFAULT_PING_INTERVAL = "-1";

	private String notConnectedToolTip;
	
	private long timeInterval;
	
	private PingOperation operation;
	
	private Pinger pinger;
	
	private Icon connectedIcon;
	private Icon notConnectedIcon;
	
	public SolrConnectedButton(String text, String tooltip, PingOperation operation, long timeInterval) {
		super(text);
		init(tooltip, operation, timeInterval);
	}
	
	public SolrConnectedButton(Icon conected, Icon notConected, String tooltip, PingOperation operation, long timeInterval) {
		super(conected);
		this.connectedIcon = conected;
		this.notConnectedIcon = notConected;
		init(tooltip, operation, timeInterval);
	}
	
	private void init(String tooltip, PingOperation operation, long timeInterval){
		this.notConnectedToolTip = tooltip;
		this.timeInterval = timeInterval;
		this.operation = operation;
		if(timeInterval > 0) {
			pinger = new Pinger();
			new Thread(pinger).start();
		}
	}
	
	public SolrConnectedButton(Icon conected, Icon notConected, String tooltip, PingOperation operation) {
		this(conected, notConected, tooltip, operation, Integer.valueOf(SolrMeterConfiguration.getProperty("solrConnectedButton.pingInterval", DEFAULT_PING_INTERVAL)));
	}
	
	public SolrConnectedButton(String text, String tooltip, PingOperation operation) {
		this(text, tooltip, operation, Integer.valueOf(SolrMeterConfiguration.getProperty("solrConnectedButton.pingInterval", DEFAULT_PING_INTERVAL)));
	}
	
	private void setConnectionOK() {
		this.setOpaque(false);
		this.setForeground(Color.black);
		this.setToolTipText(null);
		if(this.notConnectedIcon != null && this.connectedIcon != null){
			this.setIcon(this.connectedIcon);
		}
		repaint();
	}
	
	private void setConnectionFailed() {
		this.setOpaque(true);
		this.setForeground(Color.red);
		this.setToolTipText(notConnectedToolTip);
		if(this.notConnectedIcon != null && this.connectedIcon != null){
			this.setIcon(this.notConnectedIcon);
		}
		repaint();
	}
	
	
	
	
	private class Pinger implements Runnable {
		
		private boolean stop = false;
		
		@Override
		public synchronized void run() {
			boolean wasShownForFirstTime = false;
			while((isShowing() || !wasShownForFirstTime) && !stop) {
				if(isShowing()) {
					wasShownForFirstTime = true;
				}
				try {
					if(operation.execute()) {
						setConnectionOK();
					}else {
						setConnectionFailed();
					}
					this.wait(timeInterval);
				} catch (PingNotConfiguredException e) {
					Logger.getLogger(this.getClass()).info(e.getMessage());
					setConnectionOK();
					stop();
				}catch (InterruptedException e) {
					Logger.getLogger(this.getClass()).error("Error on SolrConnectedButton", e);
				}
			}
			
		}
		
		private void stop() {
			this.stop = true;
		}
		
	}

}
