package frontlinesms2

import net.frontlinesms.messaging.ATDeviceDetector

class SmslibFconnection extends Fconnection {
	private def camelAddress = { "smslib:$port?debugMode=true&baud=$baud&pin=$pin&allMessages=$allMessages" }

	String port
	int baud
	String serial
	String imsi
	String pin
	boolean allMessages = true

	static constraints = {
		port(nullable: false, blank: false)
		imsi(nullable: true)
		pin(nullable: true)
		serial(nullable: true)
	}
	
	static namedQueries = {
		findForDetector { ATDeviceDetector d ->
			and {
				or {
					isNull('port')
					eq('port', d.portName)
				}
				or {
					isNull('serial')
					eq('serial', '')
					eq('serial', d.serial)
				}
				or {
					isNull('imsi')
					eq('imsi', '')
					eq('imsi', d.imsi)
				}
			}
		}
	}

	String type() { 'Phone/Modem' }
	
	String getCamelConsumerAddress() {
		camelAddress()
	}
	
	String getCamelProducerAddress() {
		camelAddress()
	}
}
