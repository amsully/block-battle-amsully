import random as r
from random import randint


from AbstractStrategy import AbstractStrategy


class NaiveStrategy(AbstractStrategy):
    def __init__(self, game):
        AbstractStrategy.__init__(self, game)

        self._actions = ['left', 'right', 'turnleft', 'turnright', 'down', 'drop']

    def choose(self):
        field_weight = self.fieldWt()

        left = r.randrange(field_weight[0])
        right = r.randrange(field_weight[1])

        # ind = [randint(0, 4) for _ in range(1, 10)]
        ind = [self.pick(left, right) for _ in range(1, 10)]

        moves = map(lambda x: self._actions[x], ind)
        moves.append('drop')


        return moves

    def pick(self, left, right):

    	if left < right:
    		return 0
    	else:
    		return 1

    def fieldWt(self):
    	field = self._game.me.field

    	arrTopVals = [0 for _ in range(10)]

    	i = -1
    	j = -1

    	for col in range(10):
    		for row in range(20):
    			if row == 1:
    				arrTopVals[col] = row
    				break

    	right = 0
    	left = 0
    	for val in range(10):
    		if val < 5:
    			left+=arrTopVals[val]
    		else:
    			right+=arrTopVals[val]

    	return [left, right]
