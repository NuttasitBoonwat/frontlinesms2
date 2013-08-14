package frontlinesms2

import grails.test.mixin.*
import spock.lang.*
import grails.buildtestdata.mixin.Build

@TestFor(Autoforward)
@Mock([Keyword, SmartGroup, Group, Contact, Fmessage, MessageSendService, AutoforwardService, CustomActivity])
@Build([Contact, Autoforward])
class AutoforwardSpec extends Specification {
	private static final String TEST_NUMBER = "+2345678"
	
	def setup() {
		// Not sure why this is necessary with Test Mixins, but it seems to be:
		Autoforward.metaClass.addToMessages = { m ->
			if(delegate.messages) delegate.messages << m
			else delegate.messages = [m]
			return delegate
		}
		Keyword.metaClass.validate = { -> true }
	}

	@Unroll
	def "Test Constraints #{valid} #{props}"() {
		when:
			def autoforward = new Autoforward()
			props.each { k, v -> autoforward[k] = v }
		then:
			autoforward.validate() == valid
		where:
			valid | props
			true  | [name:"name"]
			true  | [name:"name", keywords:[new Keyword(value:"keyword")]]
			true  | [name:"name", contacts:[new Contact(name:"name")]]
			true  | [name:"name", contacts:[new Contact(name:"name")], keywords:[new Keyword(value:"keyword")]]
			true  | [name:"name", smartGroups:[new SmartGroup(name:"SmartGroup", contactName:"contactName")], keywords:[new Keyword(value:"keyword")]]
			true  | [name:"name", groups:[new Group(name:"test")], keywords:[new Keyword(value:"keyword")]]
			true  | [name:"name", contacts:[new Contact(name:"name")], groups:[new Group(name:"test")], smartGroups:[new SmartGroup(name:"SmartGroup", contactName:"contactName")], keywords:[new Keyword(value:"keyword")]]
	}

	def 'processKeyword should call autoforwardService if addresses are available'() {
		given:
			def a = Autoforward.build()
			a.contacts = ['a contact']
			def m = Mock(Fmessage)
			def k = Mock(Keyword)
			def s = Mock(AutoforwardService)
			a.autoforwardService = s
		when:
			a.processKeyword(m, k)
		then:
			1 * s.doForward(_, _)
			0 * s._
	}

	def 'processKeyword should not call autoforwardService if no addresses are available'() {
		given:
			def a = Autoforward.build()
			def m = Mock(Fmessage)
			def k = Mock(Keyword)
			def s = Mock(AutoforwardService)
			a.autoforwardService = s
		when:
			a.processKeyword(m, k)
		then:
			0 * s._
	}

	@Unroll
	def 'getRecipientCount() should count group members and contacts'() {
		given:
			def a = new Autoforward(name:'recipient counter')
			if(contacts) a.contacts = (1..contacts).collect { Contact.build(mobile:"+123${Contact.count()}") }
			if(groups) a.groups = (1..groups).collect { mockGroup(groupMembers) }
			def sg = []
			if(smartGroups) sg = (1..smartGroups).collect { def smrt = mockSmartGroup(smartGroupMembers); println "smrt.members=$smrt.members"; return smrt }
			println "sg = $sg"
			println "sg.mem = ${sg*.members}"
			sg.each {
				println "it.members = $it.members"
			}
			a.smartGroups = sg
			if(smartGroups) println "membaz: " + a.smartGroups*.members
		expect:
			a.recipientCount == expectedRecipientCount
		where:
			expectedRecipientCount | contacts | groups | groupMembers | smartGroups | smartGroupMembers
			1                      | 1        | 0      | 0            | 0           | 0
			0                      | 0        | 1      | 0            | 0           | 0
			1                      | 0        | 1      | 1            | 0           | 0
			10                     | 0        | 2      | 5            | 0           | 0
			12                     | 2        | 2      | 5            | 0           | 0
			0                      | 0        | 0      | 0            | 1           | 0
			1                      | 0        | 0      | 0            | 1           | 1
			10                     | 0        | 0      | 0            | 5           | 2
			12                     | 2        | 0      | 0            | 5           | 2
			22                     | 2        | 2      | 5            | 5           | 2
	}

	private def mockGroup(int memberCount) {
		mockMembers(Group, memberCount)
	}

	private def mockSmartGroup(int memberCount) {
		mockMembers(SmartGroup, memberCount)
	}

	private def mockMembers(gClass, memberCount) {
		def g = Mock(gClass)
		if(memberCount) {
			def members = (1..memberCount).collect { Contact.build(mobile:"+123${Contact.count()}") }
			println "mockMembers() :: members=$members"
			g.members >> { members }
			println "mockMembers() :: g.members=$g.members"
			println "mockMembers() :: g.members=$g.members"
			println "mockMembers() :: g.members=$g.members"
			println "mockMembers() :: g.members=$g.members"
		}
		return g
	}

	private def mockFmessage(String messageText, String src=null, String ownerDetail=null) {
		Fmessage m = Mock()
		m.id >> 1
		m.text >> messageText
		m.src >> src
		m.setOwnerDetail >> ownerDetail
		def owner = Mock(CustomActivity)
		owner.addToMessages = { ms -> return "added message to messageOwner" }
		m.messageOwner >> owner
		return m
	}
}

