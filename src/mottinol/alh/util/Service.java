package mottinol.alh.util;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;


/**
 * Agile Learning Helper
 * 
 * @version 1.1
 * @since 1.9
 * @author Loris Mottino
 *
 */
public class Service {
	
	public static final String[] OOP_COMMANDS = {"cd 2019/workshops/oop", "source setenv.sh", "./oop.sh"},
								 ECLIPSE_COMMANDS = {"cd 2019/workshops/oop", "source setenv.sh", "eclipse -data workspace"},
								 DASHBOARD_COMMAND = {"ssh", "-o", "ServerAliveInterval=120", "-N", "-L", "8080:im2ag-vteacher.ujf-grenoble.fr:80", "mandelbrot"},
								 
								 GIT_COMMANDS = {"cd 2019/workshops/oop", "source setenv.sh", "git add --all", "git commit", "git push --all"};
	
	private static final File HOME_DIRECTORY = new File(System.getProperty("user.home"));
	
	private final ProcessBuilder builder;
	private volatile Process process;
	
	private Thread waitThread;
	
	private volatile Listener<State> listener;
	
	
	public Service(final String... commands) {
		this(false, commands);
	}
	
	public Service(final boolean singleCommand, final String... commands) {
		if (commands == null || commands.length == 0)
			throw new NullPointerException("Cannot have empty command.");
		
		if (singleCommand)
			this.builder = new ProcessBuilder(commands);
		else {
			String fullCommand = commands[0];
			for (int i = 1; i < commands.length; i++)
				fullCommand += "; " + commands[i];
			
			this.builder = new ProcessBuilder(new String[] {"bash", "-c", fullCommand});
		}
		
		this.builder.directory(HOME_DIRECTORY).redirectOutput(Redirect.INHERIT).redirectError(Redirect.INHERIT);
		this.process = null;
		
		this.listener = null;
	}
	
	
	
	public synchronized boolean isRunning() {
		return this.process != null;// && this.process.isAlive()
	}
	
	
	public synchronized Process getProcess() {
		return this.process;
	}
	
	
	
	public synchronized Listener<State> getListener() {
		return this.listener;
	}
	
	
	public synchronized void setListener(final Listener<State> listener) {
		this.listener = listener;
	}
	
	
	public synchronized void removeListener() {
		this.listener = null;
	}
	
	
	
	public synchronized void start() {
		this.start(true);
	}
	
	public synchronized void start(final boolean sendUpdate) {
		if (this.isRunning())
			throw new IllegalStateException("Service is already running.");
		
		try {
			this.process = this.builder.start();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		this.waitThread = new Thread(() -> {
			try {
				this.process.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			this.process = null;
			
			if (sendUpdate && this.listener != null)
				listener.notify(State.STOPPED);
		});
		this.waitThread.setDaemon(true);
		this.waitThread.start();
		
		if (sendUpdate && this.listener != null)
			listener.notify(State.STARTED);
	}
	
	
	public synchronized void stop() {
		if (!this.isRunning())
			throw new IllegalStateException("Service is already stopped.");
		
		this.process.descendants().forEach(ProcessHandle::destroy);
		
		if (this.process != null)
			this.process.destroy();
	}
	
	
	
	
	
	public enum State {
		
		STARTED,
		STOPPED;
		
	}
	
}
