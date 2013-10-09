package nz.co.ahc.command;

/**
 * This lists all the available commands and maps them to ShellCommand implementations.
 * Doing this in an enum allows new commands to be added easier and in one place. It can also be useful
 * for automated documentation.
 * 
 * @author aimana
 *
 */

public enum SupportedCommands {
	LIST("ls", ListCommand.class),
	PRINT_WORKING_DIRECTORY("pwd", PrintWorkingDirectoryCommand.class),
	CHANGE_DIRECTORY("cd", ChangeDirectoryCommand.class),
	MAKE_DIRECTORY("mkdir", MakeDirectoryCommand.class),
	TOUCH("touch", TouchCommand.class);
	
	private final String name;
	private final Class<? extends ShellCommand> handlerClass;

	private SupportedCommands(String name, Class<? extends ShellCommand> handlerClass) {
		this.name = name;
		this.handlerClass = handlerClass;
	}
	
	public static ShellCommand getCommandHandlerInstance(String userCommand) {
		if(userCommand == null) { 
			return null;
		}
		for(SupportedCommands supportedCommand : SupportedCommands.values()) {
			if(userCommand.equals(supportedCommand.getCommandName())) {
				try {
					return supportedCommand.getHandlerClass().newInstance();
				} catch (Exception e) {
					System.err.println("Developer error, unable to instantiate class: " + supportedCommand.getHandlerClass());
				}
			}
		}
		return null;
	}

	private String getCommandName() {
		return name;
	}

	private Class<? extends ShellCommand> getHandlerClass() {
		return handlerClass;
	}
}