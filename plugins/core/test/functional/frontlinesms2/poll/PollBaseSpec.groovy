package frontlinesms2.poll

import frontlinesms2.*

class PollBaseSpec extends grails.plugin.geb.GebSpec {
	
	static createTestPolls() {
		[Poll.createPoll(name: 'Football Teams', choiceA: 'manchester', choiceB:'barcelona'),
				Poll.createPoll(name: 'Shampoo Brands', choiceA: 'pantene', choiceB: 'oriele'),
				Poll.createPoll(name: 'Rugby Brands', choiceA: 'newzealand', choiceB: 'britain')]*.save(failOnError:true, flush:true)
	}

	static createTestMessages() {
		[new Fmessage(src:'Bob', text:'I like manchester', date: new Date() - 4, starred: true),
			new Fmessage(src:'Alice', text:'go manchester', date: new Date() - 3),
				new Fmessage(src:'Joe', text:'pantene is the best',  date: new Date() - 2),
				new Fmessage(src:'Jill', text:'I fell down the hill',  date: new Date() - 1)].each() {
					it.inbound = true
					it.save(failOnError:true, flush:true)
				}
		def poll = Poll.findByName('Football Teams')	
		[PollResponse.findByValue('manchester').addToMessages(Fmessage.findBySrc('Bob')),
				PollResponse.findByValue('manchester').addToMessages(Fmessage.findBySrc('Alice')),
				PollResponse.findByValue('pantene').addToMessages(Fmessage.findBySrc('Joe'))]
		poll.save(failOnError:true, flush:true)
	}
	
	static createMoreTestMessages() {
		[new Fmessage(src:'Jill', text:'barcelona sucks!', date: new Date() - 4, starred: true),
			new Fmessage(src:'Tony', text:'Gormahia!', date: new Date() - 3)].each() {
					it.inbound = true
					it.save(failOnError:true, flush:true)
				}

		[PollResponse.findByValue('manchester').addToMessages(Fmessage.findByText('barcelona sucks!')),
				PollResponse.findByValue('barcelona').addToMessages(Fmessage.findBySrc('Tony'))]*.save(failOnError:true, flush:true)
	}

	static createTestFolders() {
		['Work', 'Projects'].each {
			new Folder(name: it).save(failOnError:true, flush:true)
		}
	}
}

