package mottinol.alh.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import mottinol.alh.util.Service;


/**
 * Agile Learning Helper
 * 
 * @version 1.1
 * @since 1.9
 * @author Loris Mottino
 *
 */
public class Gui extends JFrame implements WindowListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String ICON_PATH = "icon.png",
								BACKGROUND_PATH = "background.png";
	private static final Color BACKGROUND_COLOR = new Color(237, 236, 255),
							   INFO_LABEL_COLOR = new Color(39, 117, 199).darker().darker();
	
	
	private final Image backgroundImage;
	private final int backgroundImageWidth, backgroundImageHeight;
	
	
	private static BufferedImage getBufferedImage(final String path) {
		BufferedImage image = null;
		
		try {
			image = ImageIO.read(Gui.class.getClassLoader().getResourceAsStream(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return image;
	}
	
	
	private JPanel infoPanel,
				   processesPanel,
				   gitPanel;
	private JLabel infoLabel,
				   gitLabel;
	private JButton oopProcessButton, eclipseProcessButton, dashboardProcessButton,
					gitButton;
	private JTextField gitTextField;
	private GhostText gitGhostText;
	
	private final Service oopService, eclipseService, dashboardService;
	private volatile Service gitService;
	
	private volatile String infoText;
	
	
	public Gui() {
		this.infoText = "No service running";
		
		this.oopService = new Service(Service.OOP_COMMANDS);
		this.oopService.setListener(state -> {
			this.refreshInfoLabel();
			
			if (state == Service.State.STARTED) {
				oopProcessButton.setText("Stop OOP service");
				this.updateInfoLabel("OOP service launched", 2_000L);
			} else if (state == Service.State.STOPPED) {
				oopProcessButton.setText("Start OOP service");
				this.updateInfoLabel("OOP service killed", 2_000L);
			}
				
		});
		this.eclipseService = new Service(Service.ECLIPSE_COMMANDS);
		this.eclipseService.setListener(state -> {
			this.refreshInfoLabel();
			
			if (state == Service.State.STARTED) {
				eclipseProcessButton.setText("Kill Eclipse");
				this.updateInfoLabel("Eclipse is launching...", 2_000L);
			} else if (state == Service.State.STOPPED) {
				eclipseProcessButton.setText("Launch Eclipse");
				this.updateInfoLabel("Eclipse killed", 2_000L);
			}
				
		});
		
		this.dashboardService = new Service(true, Service.DASHBOARD_COMMAND);
		this.dashboardService.setListener(state -> {
			this.refreshInfoLabel();
			
			if (state == Service.State.STARTED) {
				dashboardProcessButton.setText("Close dashboard tunnel");
				this.updateInfoLabel("Dashboard tunnel opened", 2_000L);
			} else if (state == Service.State.STOPPED) {
				dashboardProcessButton.setText("Open dashboard tunnel");
				this.updateInfoLabel("Dashboard tunnel closed", 2_000L);
			}
		});
		
		this.gitService = null;
		
		
		this.setTitle("Agile Learning Helper");
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setResizable(false);
		
		JPanel contentPane = new JPanel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				Graphics2D g2 = (Graphics2D)g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				g2.drawImage(backgroundImage, (this.getWidth() - backgroundImageWidth) / 2, (this.getHeight() - backgroundImageHeight) / 2, null);
			}
		};
		contentPane.setBackground(BACKGROUND_COLOR);
		this.setContentPane(contentPane);
		this.setLayout(new BorderLayout());
		
		this.setIconImage(Gui.getBufferedImage(ICON_PATH));
		
		this.initialize();
		
		this.pack();
		this.setLocationRelativeTo(null);
		
		this.addWindowListener(this);
		
		gitTextField.setPreferredSize(gitTextField.getSize());
		this.disableOpacity();
		
		final BufferedImage backgroundImage = Gui.getBufferedImage(BACKGROUND_PATH);
		this.backgroundImageHeight = this.getHeight();
		this.backgroundImageWidth = (int)((double)backgroundImage.getWidth() * ((double)this.getHeight() / (double)backgroundImage.getHeight()));
		this.backgroundImage = backgroundImage.getScaledInstance(this.backgroundImageWidth, this.backgroundImageHeight, Image.SCALE_SMOOTH);
		
		contentPane.repaint();
		
		this.setVisible(true);
		
		infoLabel.setFocusable(true);
		infoLabel.requestFocusInWindow();
	}
	
	
	
	private void disableOpacity() {
		infoPanel.setOpaque(false);
		processesPanel.setOpaque(false);
		gitPanel.setOpaque(false);
		
		infoLabel.setOpaque(false);
		gitLabel.setOpaque(false);
		
		oopProcessButton.setOpaque(false);
		eclipseProcessButton.setOpaque(false);
		dashboardProcessButton.setOpaque(false);
		gitButton.setOpaque(false);
	}
	
	
	
	
	
	private void initialize() {
		infoPanel = new JPanel();
		infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 40, 30));
		
		infoLabel = new JLabel(infoText);
		infoLabel.setFont(infoLabel.getFont().deriveFont(Font.BOLD).deriveFont(30f));
		infoLabel.setForeground(INFO_LABEL_COLOR);
		
		infoPanel.add(infoLabel);
		
		this.getContentPane().add(infoPanel, BorderLayout.NORTH);
		
		
		
		
		processesPanel = new JPanel();
		processesPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(0, 30, 0, 30),
				BorderFactory.createTitledBorder("Launch/kill processes")
			));
		
		
		oopProcessButton = new JButton("Start OOP service");
		oopProcessButton.addActionListener(e -> this.oopAction());
		processesPanel.add(oopProcessButton);
		
		eclipseProcessButton = new JButton("Launch Eclipse");
		eclipseProcessButton.addActionListener(e -> this.eclipseAction());
		processesPanel.add(eclipseProcessButton);
		
		dashboardProcessButton = new JButton("Open dashboard tunnel");
		dashboardProcessButton.addActionListener(e -> this.dashboardAction());
		processesPanel.add(dashboardProcessButton);
		
		
		this.getContentPane().add(processesPanel, BorderLayout.CENTER);
		
		
		
		
		gitPanel = new JPanel();
		gitPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(40, 30, 20, 30),
				BorderFactory.createTitledBorder("Git commands")
			));
		
		
		gitLabel = new JLabel("Message to commit : ");
		gitPanel.add(gitLabel);
		
		
		gitTextField = new JTextField();
		gitTextField.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.LIGHT_GRAY),
				BorderFactory.createEmptyBorder(5, 10, 5, 10)
			));
		gitGhostText = new GhostText(gitTextField, "Sprint object.collections, Task4, Step3 completed");
		
		gitPanel.add(gitTextField);
		
		
		gitButton = new JButton("Add + Commit + Push");
		gitButton.addActionListener(e -> this.gitAction());
		
		gitPanel.add(gitButton);
		
		
		this.getContentPane().add(gitPanel, BorderLayout.SOUTH);
	}
	
	
	
	
	
	private void oopAction() {
		if (oopService.isRunning())
			oopService.stop();
		else
			oopService.start();
	}
	
	
	private void eclipseAction() {
		if (eclipseService.isRunning()) {
			if (JOptionPane.showConfirmDialog(this, "Are you sure you want to kill Eclipse?" + System.lineSeparator() + "It may be better to close it in a more regular way.", "Kill Eclipse?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION)
				eclipseService.stop();
		} else
			eclipseService.start();
	}
	
	
	private void dashboardAction() {
		if (dashboardService.isRunning())
			dashboardService.stop();
		else
			dashboardService.start();
	}
	
	
	
	private void gitAction() {
		final String[] gitCommands = Service.GIT_COMMANDS;
		
		if (gitGhostText.isEmpty()) {
			if (JOptionPane.showConfirmDialog(this, "Are you sure you want to commit without message?", "Commit without message?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.OK_OPTION)
				return;
		} else
			gitCommands[3] += " -m \"" + gitTextField.getText() + '"';
		
		gitService = new Service(gitCommands);
		
		gitService.start();
		
		this.updateInfoLabel("Commit in progress...");
		
		Thread waitThread = new Thread(() -> {
			this.freeze();
			
			try {
				gitService.getProcess().waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			gitService = null;
			this.unfreeze();
			this.updateInfoLabel("Commit performed !", 2_000L);
		});
		waitThread.setDaemon(true);
		waitThread.start();
	}
	
	
	
	
	
	private void updateInfoLabel(final String infoText) {
		infoLabel.setText(infoText);
	}
	
	private void updateInfoLabel(final String infoText, final long duration) {
		Thread t = new Thread(() -> {
			infoLabel.setText(infoText);
			
			try {
				Thread.sleep(duration);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (!infoLabel.getText().equals(this.infoText))
				infoLabel.setText(this.infoText);
		});
		t.setDaemon(true);
		t.start();
	}
	
	
	
	private void setInfoLabel(final String infoText) {
		this.infoText = infoText;
		infoLabel.setText(infoText);
	}
	
	
	
	private void refreshInfoLabel() {
		final boolean oopRunning = oopService.isRunning(),
					  eclipseRunning = eclipseService.isRunning(),
					  dashboardRunning = dashboardService.isRunning();
		
		if (oopRunning || eclipseRunning || dashboardRunning) {
			if (oopRunning && eclipseRunning && dashboardRunning)
				this.setInfoLabel("All services are running");
			else {
				String infoText = "Running :";
				if (oopRunning)
					infoText += " OOP service";
				if (eclipseRunning)
					infoText += (oopRunning ? " + " : " ") + "Eclipse";
				if (dashboardRunning)
					infoText += (oopRunning || eclipseRunning ? " + " : " ") + "Dashboard tunnel";
				this.setInfoLabel(infoText);
			}
		} else
			this.setInfoLabel("No service running");
	}
	
	
	
	
	
	private void freeze() {
		//oopProcessButton.setEnabled(false);
		//eclipseProcessButton.setEnabled(false);
		//dashboardProcessButton.setEnabled(false);
		
		gitTextField.setEnabled(false);
		gitButton.setEnabled(false);
	}
	
	
	private void unfreeze() {
		//oopProcessButton.setEnabled(true);
		//eclipseProcessButton.setEnabled(true);
		//dashboardProcessButton.setEnabled(true);
		
		gitTextField.setEnabled(true);
		gitButton.setEnabled(true);
	}
	
	
	
	
	
	@Override
	public void windowClosing(WindowEvent e) {
		if (!gitButton.isEnabled())
			return;
		
		if (oopService.isRunning() || eclipseService.isRunning() || dashboardService.isRunning()) {
			if (JOptionPane.showConfirmDialog(this, "Some services are still running, would you like" + System.lineSeparator() + "to kill them in order to quit the application?" + (eclipseService.isRunning() ? System.lineSeparator() + System.lineSeparator() + "Note: Eclipse is opened, and doing this will kill its process." + System.lineSeparator() + "It may be better to close it in a more regular way." : ""), "Exit the application anyway?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION) {
				if (oopService.isRunning())
					oopService.stop();
				if (eclipseService.isRunning())
					eclipseService.stop();
				if (dashboardService.isRunning())
					dashboardService.stop();
			} else
				return;
		}
		
		this.dispose();
		//System.exit(0);
	}
	
	
	public void windowOpened(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	
}
