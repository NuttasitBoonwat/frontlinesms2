package frontlinesms2.domain

import frontlinesms2.*
import spock.lang.*

class JoinActionStepISpec extends grails.plugin.spock.IntegrationSpec {
	@Unroll
	def "Test dynamic constraints"() {
		when:
			def step = new JoinActionStep(type: 'joinAction')
			if(addStepProperty)
				step.addToStepProperties(new StepProperty(key:stepPropertyKey, value:"invaluable"))
		then:
			step.validate() == expectedOutcome
		where:
			addStepProperty | stepPropertyKey | expectedOutcome
			false           | null            | false
			true            | 'woteva'        | false
			true            | 'group'         | true
	}

	def "Can retrieve Contacts stored as StepProperties"() {
			given:
				def contactList = []
				(1..20).each {
					contactList << new Contact(name:"test-${it}", mobile:"number-${it}").save(failOnError:true)
				}
				def step = new JoinActionStep(type: 'joinAction')
				// to make it pass validation
				step.addToStepProperties(new StepProperty(key:"group", value:"invaluable")).save(failOnError:true)
				contactList.each {
					step.addToStepProperties(new StepProperty(key:'contactId', value:it.id))
				}
				(11121..11130).each {
					// add ids for non-existant contactIds to simulate deleted contacts
					step.addToStepProperties(new StepProperty(key:'contactId', value:it))
				}
				step.save(flush:true, failOnError:true)
			when:
				println "STEP PROPERTIES :::: ${step.stepProperties}"
				def retrievedContacts = step.getEntityList(Contact, "contactId")
			then:
				retrievedContacts.sort { it.id } == contactList
	   }
}