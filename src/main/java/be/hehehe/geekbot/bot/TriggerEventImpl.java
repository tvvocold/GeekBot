package be.hehehe.geekbot.bot;

import java.util.Collection;

public class TriggerEventImpl implements TriggerEvent {
	private String message;
	private String author;
	private String messageWithoutTrigger;
	private Collection<String> users;
	private String url;
	private boolean nickInMessage;
	private boolean botInMessage;
	private boolean startsWithTrigger;
	private MessageWriter messageWriter;

	/**
	 * use only in tests, some fields are not set
	 * 
	 * @param messageWithoutTrigger
	 */
	public TriggerEventImpl(String messageWithoutTrigger) {
		this(messageWithoutTrigger, null);
	}

	/**
	 * use only in tests, some fields are not set
	 * 
	 * @param messageWithoutTrigger
	 * @param url
	 */
	public TriggerEventImpl(String messageWithoutTrigger, String url) {
		this.messageWithoutTrigger = messageWithoutTrigger;
		this.url = url;
	}

	public TriggerEventImpl(String message, String author, String trigger,
			Collection<String> users, String url, boolean nickInMessage,
			boolean botInMessage, boolean startsWithTrigger,
			MessageWriter messageWriter) {
		this.message = message;
		this.author = author;
		this.users = users;
		this.url = url;
		this.nickInMessage = nickInMessage;
		this.botInMessage = botInMessage;
		this.startsWithTrigger = startsWithTrigger;

		if (message != null && trigger != null) {
			this.messageWithoutTrigger = message.replace(trigger, "");
		} else {
			this.messageWithoutTrigger = message;
		}
		this.messageWriter = messageWriter;

	}

	@Override
	public String getOriginalMessage() {
		return message;
	}

	@Override
	public String getAuthor() {
		return author;
	}

	@Override
	public Collection<String> getUsers() {
		return users;
	}

	@Override
	public boolean hasURL() {
		return url != null;
	}

	@Override
	public String getURL() {
		return url;
	}

	@Override
	public boolean isNickInMessage() {
		return nickInMessage;
	}

	@Override
	public boolean isBotInMessage() {
		return botInMessage;
	}

	@Override
	public String getMessage() {
		return messageWithoutTrigger;
	}

	@Override
	public boolean isStartsWithTrigger() {
		return startsWithTrigger;
	}

	@Override
	public void write(String message) {
		messageWriter.write(message);
	}

}
