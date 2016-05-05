from pprint import pprint

# Build the tree somehow

def get_matrix(raw):
	separate = raw.split()

	rows = separate[3].split(";")

	return rows

def read(file):

	with open(file) as f:
		content = f.readlines()

	writer = open('game_print.txt','wb')

	count = 0

	states = []
	builder = []


	for i in range(0,len(content)):
		if(i < 6 ):
			writer.write(content[i])
		elif count == 12:
			count = 0
			states.append( builder )
			builder =[]
		else:
			count+=1
			builder.append(content[i])

	for i in range(0, len(states)):
		state = states[i]

		# ROUND HEADER
		_round = state[0].split()
		rnd = _round[3]
		_piece = state[1].split()
		piece = _piece[3]
		_nextpiece = state[2].split()
		nextpiece = _nextpiece[3]
		writer.write('\n' + 'round: ' + rnd + ' piece: ' + piece + ' nextpiece: ' + nextpiece )

		# ROUND MATRICES
		player_one = get_matrix(state[7])
		player_two = get_matrix(state[8])

		for i in range(20):
			row = player_one[i] + "     " + player_two[i] 
			writer.write('\n' + row.replace(","," "))

if __name__ == '__main__':
	read('/home/ams/Dropbox/GitHub/blockbattle-engine/bot.txt')