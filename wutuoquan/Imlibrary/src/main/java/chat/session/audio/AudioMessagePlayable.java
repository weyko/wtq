package chat.session.audio;

import chat.media.Playable;
import chat.session.bean.IMChatAudioBody;
import chat.session.bean.MessageBean;

public class AudioMessagePlayable implements Playable {

	private MessageBean message;

	public MessageBean getMessage() {
		return message;
	}

	public AudioMessagePlayable(MessageBean playableMessage) {
		this.message = playableMessage;
	}

	@Override
	public long getDuration() {
		return ((IMChatAudioBody) message.getAttachment()).getAttr2();
	}

	@Override
	public String getPath() {
		return ((IMChatAudioBody) message.getAttachment()).getLocalUrl();
	}

	@Override
	public boolean isAudioEqual(Playable audio) {
		if (AudioMessagePlayable.class.isInstance(audio)) {
			return message.isTheSame(((AudioMessagePlayable) audio).getMessage());
		} else {
			return false;
		}
	}
}
