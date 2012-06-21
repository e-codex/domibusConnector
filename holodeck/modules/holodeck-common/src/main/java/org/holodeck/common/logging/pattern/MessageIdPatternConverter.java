package org.holodeck.common.logging.pattern;

import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.spi.LoggingEvent;
import org.holodeck.common.logging.model.MsgInfo;

public class MessageIdPatternConverter extends PatternConverter {
	@Override
	protected String convert(LoggingEvent event) {
		MsgInfo msg = (MsgInfo)event.getMessage();
		
		//An dieser Stelle k�nnen Daten abgefragt und an den Logger zur�ckgegeben werden.		
		return msg.getMessageId();
	}
}
